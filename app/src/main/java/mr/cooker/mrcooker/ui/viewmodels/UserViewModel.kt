/*
 * Created by Igor Stevanovic on 11/28/20 12:29 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/28/20 12:29 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.User
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.EventFirebase
import java.lang.Exception

class UserViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _userID = MutableLiveData<String>()
    val userID: LiveData<String> get() = _userID

    val status = EventFirebase(null)

    suspend fun getUserInfo(): User? =
        try {
            var user: User? = null
            viewModelScope.launch {
                user = mainRepository.getUserInfo(userID.value!!)
            }.join()
            status.throwable = false
            user
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
            null
        }
}