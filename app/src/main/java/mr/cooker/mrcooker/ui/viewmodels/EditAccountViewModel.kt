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
import mr.cooker.mrcooker.data.repositories.AuthRepository
import mr.cooker.mrcooker.other.EventFirebase
import java.lang.Exception

class EditAccountViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val status = EventFirebase(null)

    fun editAccount(name: String, imgUri: Uri?) = viewModelScope.launch {
        try {
            authRepository.editAccount(name, imgUri)
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    suspend fun uploadProfilePhoto(imageUri: Uri): Uri? {
        return try {
            var downloadUrl: Uri? = null
            viewModelScope.launch {
                downloadUrl = authRepository.uploadProfilePhoto(imageUri)
            }.join()
            status.throwable = false
            downloadUrl
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
            null
        }
    }

    suspend fun deleteProfilePhoto() = viewModelScope.launch {
        try {
            authRepository.deleteProfilePhoto()
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    fun changePassword(email: String, oldPassword: String, newPassword: String) =
        viewModelScope.launch {
            try {
                authRepository.changePassword(email, oldPassword, newPassword)
                status.throwable = false
            } catch (e: Exception) {
                status.throwable = true
                status.exception = e
            }
        }

    fun changeEmail(oldEmail: String, password: String, newEmail: String) =
        viewModelScope.launch {
            try {
                authRepository.changeEmail(oldEmail, password, newEmail)
                status.throwable = false
            } catch (e: Exception) {
                status.throwable = true
                status.exception = e
            }
        }

    fun deleteAccount() = viewModelScope.launch {
        try {
            authRepository.deleteAccount()
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }
}