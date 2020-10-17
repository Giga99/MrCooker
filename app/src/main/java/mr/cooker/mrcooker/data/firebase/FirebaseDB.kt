package mr.cooker.mrcooker.data.firebase

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.Query
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

    fun signOut() {
        auth.signOut()
    }

    suspend fun register(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        auth.currentUser!!.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(username).build()
        )
        auth.currentUser!!.sendEmailVerification().await()
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun editAccount(name: String, imgUri: Uri?) {
        val userProfileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(imgUri)
            .build()
        currentUser.updateProfile(userProfileChangeRequest).await()
    }

    suspend fun changePassword(email: String, oldPassword: String, newPassword: String) {
        val credential: AuthCredential = EmailAuthProvider.getCredential(email, oldPassword)
        currentUser.reauthenticate(credential).await()
        currentUser.updatePassword(newPassword).await()
    }

    suspend fun deleteAccount() {
        val recipeQuery = firestoreRecipes
            .whereEqualTo("ownerID", currentUser.uid)
            .get().await()

        if (recipeQuery.documents.isNotEmpty()) {
            for (document in recipeQuery.documents) {
                val recipe = document.toObject<Recipe>()
                deleteRecipe(recipe!!)
            }
        }

        currentUser.delete().await()
    }

    fun checkPrevLogging(): Boolean {
        return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            currentUser = auth.currentUser!!
            true
        } else false
    }

    suspend fun uploadProfilePhoto(imageUri: Uri): Uri? {
        val fileName = "${currentUser.uid}_${Calendar.getInstance().timeInMillis}"
        firebaseStorage.child("profileImages/$fileName").putFile(imageUri).await()

        return firebaseStorage.child("profileImages/$fileName").downloadUrl.await()
    }

    suspend fun uploadImage(imageUri: Uri): Uri? {
        val fileName = "${currentUser.uid}_${Calendar.getInstance().timeInMillis}"
        firebaseStorage.child("images/$fileName").putFile(imageUri).await()

        return firebaseStorage.child("images/$fileName").downloadUrl.await()
    }

    suspend fun uploadRecipe(recipe: Recipe) {
        firestoreRecipes.add(recipe).await()
        val recipeQuery = firestoreRecipes
            .whereEqualTo("imgUrl", recipe.imgUrl)
            .whereEqualTo("name", recipe.name)
            .whereEqualTo("timeToCook", recipe.timeToCook)
            .whereEqualTo("ingredients", recipe.ingredients)
            .whereEqualTo("instructions", recipe.instructions)
            .whereEqualTo("showToEveryone", recipe.showToEveryone)
            .whereEqualTo("timePosted", recipe.timePosted)
            .whereEqualTo("ownerID", recipe.ownerID)
            .get().await()

        val map = mutableMapOf<String, Any>()

        if (recipeQuery.documents.isNotEmpty()) {
            for (document in recipeQuery) {
                map["id"] = document.id
                firestoreRecipes.document(document.id).set(map, SetOptions.merge()).await()
            }
        }
    }

    suspend fun uploadAgain(recipe: Recipe, uri: Uri) {
        firestoreRecipes.document(recipe.id!!).set(recipe).await()
        val downloadUrl = uploadImage(uri)

        val recipeQuery = firestoreRecipes
            .whereEqualTo("id", recipe.id)
            .whereEqualTo("name", recipe.name)
            .whereEqualTo("timeToCook", recipe.timeToCook)
            .whereEqualTo("ingredients", recipe.ingredients)
            .whereEqualTo("instructions", recipe.instructions)
            .whereEqualTo("showToEveryone", recipe.showToEveryone)
            .whereEqualTo("timePosted", recipe.timePosted)
            .whereEqualTo("ownerID", recipe.ownerID)
            .get().await()

        val map = mutableMapOf<String, Any>()

        if (recipeQuery.documents.isNotEmpty()) {
            for (document in recipeQuery) {
                map["imgUrl"] = downloadUrl.toString()
                firestoreRecipes.document(document.id).set(map, SetOptions.merge()).await()
            }
        }
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        deleteImage(recipe.imgUrl)
        val recipeQuery = firestoreRecipes.whereEqualTo("id", recipe.id).get().await()
        if (recipeQuery.documents.isNotEmpty()) {
            for (document in recipeQuery) firestoreRecipes.document(document.id).delete().await()
        }
    }

    private suspend fun deleteImage(imgUrl: String) {
        val filename = getFileName(imgUrl)
        firebaseStorage.child("images/$filename").delete().await()
    }

    private fun getFileName(imgUrl: String): String {
        val pom =
            imgUrl.removePrefix("https://firebasestorage.googleapis.com/v0/b/mrcooker-d0484.appspot.com/o/images%2F")
        val index = pom.indexOf('?')
        return pom.substring(0, index)
    }

    suspend fun getBytes(imgUrl: String): ByteArray {
        val downloadSize = 5L * 1024 * 1024
        val fileName = getFileName(imgUrl)
        return firebaseStorage.child("images/$fileName").getBytes(downloadSize).await()
    }

    suspend fun getAllRecipes(): Resource<MutableList<Recipe>> {
        val recipes = mutableListOf<Recipe>()
        val documentsList =
            firestoreRecipes.orderBy("timePosted", Query.Direction.DESCENDING).get().await()

        for (document in documentsList.documents) {
            val recipe = document.toObject<Recipe>()
            if(recipe!!.showToEveryone || recipe.ownerID.equals(currentUser.uid)) recipes.add(recipe)
        }

        return Resource.Success(recipes)
    }

    suspend fun getMyRecipes(): Resource<MutableList<Recipe>> {
        val recipes = mutableListOf<Recipe>()
        val documentsList =
            firestoreRecipes.whereEqualTo("ownerID", currentUser.uid)
                .orderBy("timePosted", Query.Direction.DESCENDING).get().await()

        if (!documentsList.isEmpty) {
            for (document in documentsList.documents) {
                val recipe = document.toObject<Recipe>()
                recipes.add(recipe!!)
            }
        }

        return Resource.Success(recipes)
    }

    suspend fun getRecipeByID(id: String): Resource<Recipe> {
        val document = firestoreRecipes.document(id).get().await()
        val recipe = document.toObject<Recipe>()

        return Resource.Success(recipe!!)
    }

    suspend fun getSearchedRecipes(search: String): Resource<MutableList<Recipe>> {
        val recipes = mutableListOf<Recipe>()
        val documentList = firestoreRecipes.get().await()

        if (!documentList.isEmpty) {
            for (document in documentList.documents) {
                val recipe = document.toObject<Recipe>()
                if (recipe!!.name.toLowerCase(Locale.ROOT).contains(search)
                    && (recipe.showToEveryone || recipe.ownerID.equals(currentUser.uid))) recipes.add(recipe)
            }
        }

        return Resource.Success(recipes)
    }

    suspend fun getSearchedMyRecipes(search: String): Resource<MutableList<Recipe>> {
        val recipes = mutableListOf<Recipe>()
        val documentList = firestoreRecipes.whereEqualTo("ownerID", currentUser.uid).get().await()

        if (!documentList.isEmpty) {
            for (document in documentList.documents) {
                val recipe = document.toObject<Recipe>()
                if (recipe!!.name.toLowerCase(Locale.ROOT).contains(search)) recipes.add(recipe)
            }
        }

        return Resource.Success(recipes)
    }

    fun getRealtimeRecipes(): Resource<MutableList<Recipe>> {
        val recipes = mutableListOf<Recipe>()

        firestoreRecipes.orderBy("timePosted", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    for (document in querySnapshot.documents) {
                        val recipe = document.toObject<Recipe>()
                        recipes.add(recipe!!)
                    }
                }
            }

        return Resource.Success(recipes)
    }
}