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
        return try {
            var downloadUrl: Uri? = null
            viewModelScope.launch {
                downloadUrl = mainRepository.uploadImage(imageUri)
            }.join()
            status.throwable = false
            downloadUrl
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
            null
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

    fun editRecipe(
        id: String,
        name: String,
        time: String,
        ingredients: String,
        instructions: String,
        showToEveryone: Boolean
    ) = viewModelScope.launch {
        try {
            mainRepository.editRecipe(id, name, time, ingredients, instructions, showToEveryone)
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