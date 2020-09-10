package mr.cooker.mrcooker.ui.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.data.db.entities.RecipeFirebase
import mr.cooker.mrcooker.data.repositories.MainRepository

class AllRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    //val recipes = mainRepository.getAllRecipes()

    fun addRecipeToFirestore(recipe: RecipeFirebase) = viewModelScope.launch {
        mainRepository.addRecipeToFirestore(recipe)
    }

    fun uploadImage(imageUri: Uri): String {
        var fileName: String? = null

        runBlocking {
            fileName = mainRepository.uploadImage(imageUri)
        }

        return fileName!!
    }

    fun getImageUrl(fileName: String): Uri {
        var imgUrl: Uri? = null

        runBlocking {
            imgUrl = mainRepository.getImageUrl(fileName)
        }

        return imgUrl!!
    }
}