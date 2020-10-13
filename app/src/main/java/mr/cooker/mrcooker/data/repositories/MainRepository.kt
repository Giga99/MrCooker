package mr.cooker.mrcooker.data.repositories

import android.net.Uri
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.firebase.FirebaseDB
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseDB: FirebaseDB
) {
    suspend fun uploadImage(imageUri: Uri): Uri? = firebaseDB.uploadImage(imageUri)

    suspend fun uploadRecipe(recipe: Recipe) = firebaseDB.uploadRecipe(recipe)

    suspend fun uploadAgain(recipe: Recipe, uri: Uri) = firebaseDB.uploadAgain(recipe, uri)

    suspend fun deleteRecipe(recipe: Recipe) = firebaseDB.deleteRecipe(recipe)

    suspend fun getBytes(imgUrl: String) = firebaseDB.getBytes(imgUrl)

    suspend fun getAllRecipes() = firebaseDB.getAllRecipes()

    suspend fun getRealtimeRecipes() = firebaseDB.getAllRecipes()

    suspend fun getRealtimeMyRecipes() = firebaseDB.getMyRecipes()

    suspend fun getMyRecipes() = firebaseDB.getMyRecipes()

    suspend fun getRecipeByID(id: String) = firebaseDB.getRecipeByID(id)

    suspend fun getSearchedRecipes(search: String) = firebaseDB.getSearchedRecipes(search)
}