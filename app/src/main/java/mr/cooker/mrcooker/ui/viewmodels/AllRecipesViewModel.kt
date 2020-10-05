package mr.cooker.mrcooker.ui.viewmodels

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.Constants
import mr.cooker.mrcooker.other.NetworkUtils
import mr.cooker.mrcooker.other.Resource

class AllRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    val allRecipes = liveData<Resource<MutableList<Recipe>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val recipes = mainRepository.getAllRecipes()
            emit(recipes)
        } catch (e: Exception) {
            emit(Resource.Failure(e.cause!!))
        }
    }

    fun getRealtimeRecipes() = mainRepository.getRealtimeRecipes()

    suspend fun getRecipeByID(id: String): Resource<Recipe> {
        var recipe: Resource<Recipe>? = null

        viewModelScope.launch {
            recipe = mainRepository.getRecipeByID(id)
        }.join()

        return recipe!!
    }
}