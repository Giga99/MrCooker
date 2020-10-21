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