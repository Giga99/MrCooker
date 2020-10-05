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
import timber.log.Timber

class SearchViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    suspend fun getSearchedRecipes(search: String): Resource<MutableList<Recipe>> {
        var recipes: Resource<MutableList<Recipe>>? = null
        viewModelScope.launch {
            recipes = mainRepository.getSearchedRecipes(search)
        }.join()

        return recipes!!
    }
}