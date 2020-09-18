package mr.cooker.mrcooker.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import java.util.*

class FirebaseDB {
    companion object {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val firebaseStorage = Firebase.storage.reference
        val firestoreRecipes = Firebase.firestore.collection("recipes")
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
        firestoreRecipes.add(recipe).await()
        val recipeQuery = firestoreRecipes
            .whereEqualTo("imgUrl", recipe.imgUrl)
            .whereEqualTo("name", recipe.name)
            .whereEqualTo("timeToCook", recipe.timeToCook)
            .whereEqualTo("ingredients", recipe.ingredients)
            .whereEqualTo("instructions", recipe.instructions)
            .whereEqualTo("ownerID", recipe.ownerID)
            .get().await()

        val map = mutableMapOf<String, Any>()

        if(recipeQuery.documents.isNotEmpty()) {
            for(document in recipeQuery) {
                map["id"] = document.id
                firestoreRecipes.document(document.id).set(map, SetOptions.merge()).await()
            }
        }
    }

    suspend fun getAllRecipes(): Resource<MutableList<Recipe>> {
        val recipes = mutableListOf<Recipe>()
        val documentsList = firestoreRecipes.get().await()

        for(document in documentsList.documents) {
            val recipe = document.toObject<Recipe>()
            recipes.add(recipe!!)
        }

        return Resource.Success(recipes)
    }

    suspend fun getMyRecipes(): Resource<MutableList<Recipe>> {
        val recipes = mutableListOf<Recipe>()
        val documentsList = firestoreRecipes.whereEqualTo("ownerID", currentUser.uid).get().await()

        for(document in documentsList.documents) {
            val recipe = document.toObject<Recipe>()
            recipes.add(recipe!!)
        }

        return Resource.Success(recipes)
    }

    suspend fun getRecipeByID(id: String): Resource<Recipe> {
        val document = firestoreRecipes.document(id).get().await()
        val recipe = document.toObject<Recipe>()

        return Resource.Success(recipe!!)
    }
}