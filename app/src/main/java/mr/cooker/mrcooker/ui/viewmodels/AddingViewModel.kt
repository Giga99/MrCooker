package mr.cooker.mrcooker.ui.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.EventFirebase
import java.lang.Exception

class AddingViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val status = EventFirebase(null)

    suspend fun uploadImage(imageUri: Uri): Uri? {
        try {
            var downloadUrl: Uri? = null
            viewModelScope.launch {
                downloadUrl = mainRepository.uploadImage(imageUri)
            }.join()
            status.throwable = false
            return downloadUrl
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
            return null
        }
    }

    fun uploadRecipe(recipe: Recipe) = viewModelScope.launch {
        try {
            mainRepository.uploadRecipe(recipe)
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    fun uploadAgain(recipe: Recipe, uri: Uri) = viewModelScope.launch {
        try {
            mainRepository.uploadAgain(recipe, uri)
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        try {
            mainRepository.deleteRecipe(recipe)
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    suspend fun getBytes(imgUrl: String) = mainRepository.getBytes(imgUrl)
}