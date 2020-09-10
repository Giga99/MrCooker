package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository

@ExperimentalCoroutinesApi
class MyRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    val recipes = mainRepository.getAllMyRecipes()

    fun insertRecipe(recipe: Recipe) = viewModelScope.launch {
        mainRepository.insertRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        mainRepository.deleteRecipe(recipe)
    }

    fun getRecipeByID(id: Int) = mainRepository.getRecipeByID(id)
}