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
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository
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

    suspend fun getRealtimeRecipes() = mainRepository.getRealtimeRecipes()

    suspend fun getRecipeByID(id: String): Resource<Recipe>? {
        var recipe: Resource<Recipe>? = null

        viewModelScope.launch {
            recipe = mainRepository.getRecipeByID(id)
        }.join()

        return recipe
    }

    suspend fun getSearchedRecipes(search: String): Resource<MutableList<Recipe>> {
        var recipes: Resource<MutableList<Recipe>>? = null
        viewModelScope.launch {
            recipes = mainRepository.getSearchedRecipes(search)
        }.join()

        return recipes!!
    }
}