package mr.cooker.mrcooker.ui.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository

class AddingViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
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

    fun uploadAgain(recipe: Recipe, uri: Uri) = viewModelScope.launch {
        mainRepository.uploadAgain(recipe, uri)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        mainRepository.deleteRecipe(recipe)
    }

    suspend fun getBytes(imgUrl: String) = mainRepository.getBytes(imgUrl)
}