package mr.cooker.mrcooker.data.repositories

import android.net.Uri
import mr.cooker.mrcooker.data.entities.FavoriteRecipe
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.entities.SmartRatingTracker
import mr.cooker.mrcooker.data.firebase.FirebaseDB
import mr.cooker.mrcooker.other.Resource
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

    suspend fun getSearchedMyRecipes(search: String) = firebaseDB.getSearchedMyRecipes(search)

    suspend fun addToFavoriteRecipes(favoriteRecipe: FavoriteRecipe) =
        firebaseDB.addToFavoriteRecipes(favoriteRecipe)

    suspend fun removeFavoriteRecipe(recipeID: String) = firebaseDB.removeFavoriteRecipe(recipeID)

    suspend fun isItFavoriteRecipe(recipeID: String): Boolean =
        firebaseDB.isItFavoriteRecipe(recipeID)

    suspend fun getFavoriteRecipes(): Resource<MutableList<Recipe>> =
        firebaseDB.getFavoriteRecipes()

    suspend fun getSearchedFavoriteRecipes(search: String): Resource<MutableList<Recipe>> =
        firebaseDB.getSearchedFavoriteRecipes(search)

    suspend fun getSmartRatingTracker(): SmartRatingTracker = firebaseDB.getSmartRatingTracker()
}