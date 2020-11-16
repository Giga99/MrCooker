/*
 * Created by Igor Stevanovic on 11/17/20 12:17 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/17/20 12:15 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.FavoriteRecipe
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.EventFirebase
import mr.cooker.mrcooker.other.Resource

class FavoriteRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val favoriteRecipes = liveData<Resource<MutableList<Recipe>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val recipes = mainRepository.getFavoriteRecipes()
            emit(recipes)
        } catch (e: Exception) {
            emit(Resource.Failure(e.cause!!))
        }
    }

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

    suspend fun getFavoriteRecipes(): Resource<MutableList<Recipe>> =
        mainRepository.getFavoriteRecipes()

    suspend fun getSearchedFavoriteRecipes(search: String): Resource<MutableList<Recipe>> =
        mainRepository.getSearchedFavoriteRecipes(search)
}