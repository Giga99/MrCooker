package mr.cooker.mrcooker.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import java.util.*

class FirebaseDB {
    companion object {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val firebaseStorage = Firebase.storage.reference
        val firestore = Firebase.firestore.collection("recipes")
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
        currentUser = auth.currentUser!!
    }

    suspend fun register(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        auth.currentUser!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(username).build())
        currentUser = auth.currentUser!!
    }

    fun checkPrevLogging(): Boolean {
        return if(auth.currentUser != null) {
            currentUser = auth.currentUser!!
            true
        } else false
    }

    suspend fun uploadImage(imageUri: Uri): Uri? {
        val fileName = "${currentUser.uid}_${Calendar.getInstance().timeInMillis}"
        firebaseStorage.child("images/$fileName").putFile(imageUri).await()
        val downloadUrl = firebaseStorage.child("images/$fileName").downloadUrl.await()

        return downloadUrl
    }

    suspend fun uploadRecipe(recipe: Recipe) {
        firestore.add(recipe).await()
        val recipeQuery = firestore
            .whereEqualTo("imgUrl", recipe.imgUrl)
            .whereEqualTo("name", recipe.name)
            .whereEqualTo("timeToCook", recipe.timeToCook)
            .whereEqualTo("ingredients", recipe.ingredients)
            .whereEqualTo("instructions", recipe.instructions)
            .get().await()

        val map = mutableMapOf<String, Any>()

        if(recipeQuery.documents.isNotEmpty()) {
            for(document in recipeQuery) {
                map["id"] = document.id
                firestore.document(document.id).set(map, SetOptions.merge()).await()
            }
        }
    }
}