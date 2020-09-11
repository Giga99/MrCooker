package mr.cooker.mrcooker.data.repositories

import androidx.lifecycle.LiveData
import mr.cooker.mrcooker.data.db.RecipeDAO
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.data.firebase.RecipesRepo
import mr.cooker.mrcooker.other.Resource
import javax.inject.Inject

class MainRepository @Inject constructor(
    val recipeDao : RecipeDAO,
    private val recipesRepo: RecipesRepo
) {
    suspend fun insertRecipe(recipe : Recipe) = recipeDao.insertRecipe(recipe)

    suspend fun deleteRecipe(recipe : Recipe) = recipeDao.deleteRecipe(recipe)

    fun getAllMyRecipes() = recipeDao.getAllRecipes()

    fun getRecipeByID(id: Int) : LiveData<Recipe> = recipeDao.getRecipeByID(id)

    suspend fun getAllRecipesFirebase(): Resource<MutableList<Recipe>> = recipesRepo.getRecipes()
}