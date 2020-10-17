package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.Resource

class MyRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    val myRecipes = liveData<Resource<MutableList<Recipe>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val recipes = mainRepository.getMyRecipes()
            emit(recipes)
        } catch (e: Exception) {
            emit(Resource.Failure(e.cause!!))
        }
    }

    suspend fun getSearchedMyRecipes(search: String): Resource<MutableList<Recipe>> {
        var recipes: Resource<MutableList<Recipe>>? = null
        viewModelScope.launch {
            recipes = mainRepository.getSearchedMyRecipes(search)
        }.join()

        return recipes!!
    }

    suspend fun getRealtimeMyRecipes() = mainRepository.getRealtimeMyRecipes()
}