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
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource

class UserRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    private var myRecipesBoolean: Boolean = false
    private var userID: String = ""

    val myRecipes = liveData<Resource<MutableList<Recipe>>>(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val recipes =
                if (myRecipesBoolean) mainRepository.getUserRecipes(currentUser.uid)
                else mainRepository.getUserRecipes(userID)
            emit(recipes)
        } catch (e: Exception) {
            emit(Resource.Failure(e.cause!!))
        }
    }

    suspend fun getSearchedUserRecipes(search: String): Resource<MutableList<Recipe>> {
        var recipes: Resource<MutableList<Recipe>>? = null
        viewModelScope.launch {
            recipes =
                if (myRecipesBoolean) mainRepository.getSearchedMyRecipes(search, currentUser.uid)
                else mainRepository.getSearchedMyRecipes(search, userID)
        }.join()

        return recipes!!
    }

    suspend fun getRealtimeUserRecipes() =
        if (myRecipesBoolean) mainRepository.getRealtimeUserRecipes(currentUser.uid)
        else mainRepository.getRealtimeUserRecipes(userID)

    fun setMyRecipesBoolean(areMyRecipes: Boolean) {
        myRecipesBoolean = areMyRecipes
    }

    fun getMyRecipesBoolean() = myRecipesBoolean

    fun setUserID(id: String) {
        userID = id
    }
}