package mr.cooker.mrcooker.ui.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.Resource

class AllRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val allRecipes = liveData<Resource<MutableList<Recipe>>>(Dispatchers.IO) {
        try {
            val recipes = mainRepository.getAllRecipes()
            emit(recipes)
        } catch (e: Exception) {
            emit(Resource.Failure(e.cause!!))
        }
    }

    suspend fun uploadImage(imageUri: Uri): Uri? {
        var downloadUrl: Uri? = null
        viewModelScope.launch {
            downloadUrl = mainRepository.uploadImage(imageUri)
        }.join()

        return downloadUrl
    }

    fun uploadRecipe(recipe: Recipe) = viewModelScope.launch {
        mainRepository.uploadRecipe(recipe)
    }

    suspend fun getRecipeByID(id: String): Resource<Recipe> {
        var recipe: Resource<Recipe>? = null

        viewModelScope.launch {
            recipe = mainRepository.getRecipeByID(id)
        }.join()

        return recipe!!
    }
}