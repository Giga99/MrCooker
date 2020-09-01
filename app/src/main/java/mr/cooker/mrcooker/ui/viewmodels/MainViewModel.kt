package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
    val recipes = mainRepository.getAllRecipes()

    fun insertRecipe(recipe: Recipe) = viewModelScope.launch {
        mainRepository.insertRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        mainRepository.deleteRecipe(recipe)
    }
}