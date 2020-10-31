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
import mr.cooker.mrcooker.data.entities.FavoriteRecipe
import mr.cooker.mrcooker.data.entities.SmartRatingTracker
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.entities.SmartRating
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.other.exceptions.EmailNotVerifiedException
import timber.log.Timber
import java.lang.Exception
import java.util.*

class FirebaseDB {
    companion object {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val firebaseStorage = Firebase.storage.reference
        val firestoreRecipes = Firebase.firestore.collection("recipes")
        val firestoreUsers = Firebase.firestore.collection("users")
        val firestoreSmartRating = Firebase.firestore.collection("smartRating")
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
        if (!auth.currentUser!!.isEmailVerified) throw EmailNotVerifiedException()
        currentUser = auth.currentUser!!
        setFirstLoginOfTheDay()
    }

    suspend fun checkPrevLogging(): Boolean {
        return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            currentUser = auth.currentUser!!
            setFirstLoginOfTheDay()
            true
        } else false
    }

    private suspend fun setFirstLoginOfTheDay() {
        val documentQuery = firestoreUsers.whereEqualTo("userID", currentUser.uid).get().await()
        if (!documentQuery.isEmpty && getCountDaysPassed(currentUser.uid)) {
            for (document in documentQuery.documents) {
                val lastLogin = document.toObject<SmartRatingTracker>()
                val lastLoginTime =
                    GregorianCalendar.getInstance()
                        .also { it.timeInMillis = lastLogin!!.timeFirstLoginOfDay }
                val currTime = GregorianCalendar.getInstance()
                    .also { it.timeInMillis = System.currentTimeMillis() }

                val lastDay = lastLoginTime.get(Calendar.DAY_OF_MONTH)
                val lastMonth = lastLoginTime.get(Calendar.MONTH)
                val lastYear = lastLoginTime.get(Calendar.YEAR)

                val currDay = currTime.get(Calendar.DAY_OF_MONTH)
                val currMonth = currTime.get(Calendar.MONTH)
                val currYear = currTime.get(Calendar.YEAR)

                if (lastDay == currDay && lastMonth == currMonth && lastYear == currYear) continue

                val firstLogin = SmartRatingTracker(
                    currentUser.uid,
                    System.currentTimeMillis(),
                    lastLogin!!.daysPassed + 1,
                    true
                )
                firestoreUsers.document(document.id).set(firstLogin)
            }
        } else if(documentQuery.isEmpty) {
            val firstLogin = SmartRatingTracker(currentUser.uid, System.currentTimeMillis(), 1, true)
            firestoreUsers.add(firstLogin)
        }
    }

    suspend fun countDaysPassed(count: Boolean) {
        val documentQuery = firestoreUsers.whereEqualTo("userID", currentUser.uid).get().await()
        if (!documentQuery.isEmpty) {
            for (document in documentQuery.documents) {
                val smartRatingTracker = document.toObject<SmartRatingTracker>()!!
                firestoreUsers.document(document.id).set(
                    SmartRatingTracker(
                        smartRatingTracker.userID,
                        smartRatingTracker.timeFirstLoginOfDay,
                        smartRatingTracker.daysPassed,
                        count
                    )
                )
            }
        }
    }

    private suspend fun getCountDaysPassed(uid: String): Boolean {
        val documentQuery = firestoreUsers.whereEqualTo("userID", uid).get().await()
        if (!documentQuery.isEmpty) {
            for (document in documentQuery.documents) {
                val smartRatingTracker = document.toObject<SmartRatingTracker>()!!
                return smartRatingTracker.countDaysPassed
            }
        }
        return true
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun getSmartRatingTracker(): SmartRatingTracker {
        val documentQuery = firestoreUsers.whereEqualTo("userID", currentUser.uid).get().await()
        if (!documentQuery.isEmpty) {
            for (document in documentQuery.documents) {
                val smartRatingTracker = document.toObject<SmartRatingTracker>()
                return smartRatingTracker!!
            }
        }
        throw Exception("Unknown error...")
    }

    suspend fun resetDaysPassed() {
        val documentQuery = firestoreUsers.whereEqualTo("userID", currentUser.uid).get().await()
        if (!documentQuery.isEmpty) {
            for (document in documentQuery.documents) {
                val smartRatingTracker = document.toObject<SmartRatingTracker>()!!
                firestoreUsers.document(document.id).set(
                    SmartRatingTracker(
                        smartRatingTracker.userID,
                        smartRatingTracker.timeFirstLoginOfDay,
                        1,
                        smartRatingTracker.countDaysPassed
                    )
                )
            }
        }
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
        val userProfileChangeRequest =
            if (imgUri == currentUser.photoUrl) UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
            else UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(imgUri).build()
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

    suspend fun uploadProfilePhoto(imageUri: Uri): Uri? {
        val fileName = "${currentUser.uid}_${Calendar.getInstance().timeInMillis}"
        firebaseStorage.child("profileImages/$fileName").putFile(imageUri).await()

        return firebaseStorage.child("profileImages/$fileName").downloadUrl.await()
    }

    suspend fun deleteProfilePhoto() {
        val fileName = getFileName(folder = "profileImages", currentUser.photoUrl.toString())
        firebaseStorage.child("profileImages/$fileName").delete().await()
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
        val filename = getFileName(folder = "images", imgUrl)
        firebaseStorage.child("images/$filename").delete().await()
    }

    private fun getFileName(folder: String, imgUrl: String): String {
        val pom =
            imgUrl.removePrefix("https://firebasestorage.googleapis.com/v0/b/mrcooker-d0484.appspot.com/o/$folder%2F")
        val index = pom.indexOf('?')
        return pom.substring(0, index)
    }

    suspend fun getBytes(imgUrl: String): ByteArray {
        val downloadSize = 5L * 1024 * 1024
        val fileName = getFileName(folder = "images", imgUrl)
        return firebaseStorage.child("images/$fileName").getBytes(downloadSize).await()
    }

    suspend fun getAllRecipes(): Resource<MutableList<Recipe>> {
        val recipes = mutableListOf<Recipe>()
        val documentsList =
            firestoreRecipes.orderBy("timePosted", Query.Direction.DESCENDING).get().await()

        for (document in documentsList.documents) {
            val recipe = document.toObject<Recipe>()
            if (recipe!!.showToEveryone || recipe.ownerID.equals(currentUser.uid)) recipes.add(
                recipe
            )
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
                    && (recipe.showToEveryone || recipe.ownerID.equals(currentUser.uid))
                ) recipes.add(recipe)
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

    suspend fun addToFavoriteRecipes(favoriteRecipe: FavoriteRecipe) {
        Firebase.firestore.collection(currentUser.uid).add(favoriteRecipe).await()
        val query = firestoreRecipes.whereEqualTo("id", favoriteRecipe.recipeID).get().await()
        if (query.documents.isNotEmpty()) {
            for (document in query.documents) {
                var num = document["numOfFavorites"] as Long
                num++
                val map = mapOf<String, Any>("numOfFavorites" to num)
                firestoreRecipes.document(favoriteRecipe.recipeID).set(map, SetOptions.merge())
                    .await()
            }
        }
    }

    suspend fun removeFavoriteRecipe(recipeID: String) {
        val recipeQuery = Firebase.firestore.collection(currentUser.uid)
            .whereEqualTo("recipeID", recipeID).get().await()
        if (recipeQuery.documents.isNotEmpty()) {
            for (document in recipeQuery) Firebase.firestore.collection(currentUser.uid)
                .document(document.id).delete()
                .await()
        }

        val query = firestoreRecipes.whereEqualTo("id", recipeID).get().await()
        if (query.documents.isNotEmpty()) {
            for (document in query.documents) {
                var num = document["numOfFavorites"] as Long
                Timber.e("num=$num")
                num--
                Timber.e("num=$num")
                val map = mapOf<String, Any>("numOfFavorites" to num)
                firestoreRecipes.document(recipeID).set(map, SetOptions.merge()).await()
            }
        }
    }

    suspend fun isItFavoriteRecipe(recipeID: String): Boolean {
        val documentList =
            Firebase.firestore.collection(currentUser.uid).whereEqualTo("recipeID", recipeID).get()
                .await()

        return !documentList.isEmpty
    }

    suspend fun getFavoriteRecipes(): Resource<MutableList<Recipe>> {
        val documentList = Firebase.firestore.collection(currentUser.uid).get().await()
        val recipes = mutableListOf<Recipe>()

        if (!documentList.isEmpty) {
            for (document in documentList.documents) {
                val response = getRecipeByID(document["recipeID"].toString())
                if (response is Resource.Success && response.data.showToEveryone) recipes.add(
                    response.data
                )
            }
        }

        return Resource.Success(recipes)
    }

    suspend fun getSearchedFavoriteRecipes(search: String): Resource<MutableList<Recipe>> {
        val response = getFavoriteRecipes()
        val recipes = mutableListOf<Recipe>()

        if (response is Resource.Success) {
            for (recipe in response.data) {
                if (recipe.name.toLowerCase(Locale.ROOT).contains(search)) recipes.add(recipe)
            }
        }

        return Resource.Success(recipes)
    }

    suspend fun setSmartRating(smartRating: SmartRating) {
        firestoreSmartRating.document(currentUser.uid).set(smartRating).await()
    }
}