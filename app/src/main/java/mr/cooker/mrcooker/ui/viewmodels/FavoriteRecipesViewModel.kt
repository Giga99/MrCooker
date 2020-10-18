package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.FavoriteRecipe
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.EventFirebase

class FavoriteRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val status = EventFirebase(null)

    fun addToFavoriteRecipes(favoriteRecipe: FavoriteRecipe) = viewModelScope.launch {
        try {
            mainRepository.addToFavoriteRecipes(favoriteRecipe)
            status.throwable = false
        } catch (e: java.lang.Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    fun removeFavoriteRecipe(recipeID: String) = viewModelScope.launch {
        try {
            mainRepository.removeFavoriteRecipe(recipeID)
            status.throwable = false
        } catch (e: java.lang.Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    suspend fun isItFavoriteRecipe(recipeID: String): Boolean =
        mainRepository.isItFavoriteRecipe(recipeID)
}