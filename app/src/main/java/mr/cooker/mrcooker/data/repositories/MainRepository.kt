package mr.cooker.mrcooker.data.repositories

import mr.cooker.mrcooker.data.db.RecipeDAO
import mr.cooker.mrcooker.data.db.entities.Recipe
import javax.inject.Inject

class MainRepository @Inject constructor(
    val recipeDao : RecipeDAO
) {
    suspend fun insertRecipe(recipe : Recipe) = recipeDao.insertRecipe(recipe)

    suspend fun deleteRecipe(recipe : Recipe) = recipeDao.deleteRecipe(recipe)

    fun getAllRecipes() = recipeDao.getAllRecipes()
}