package mr.cooker.mrcooker.ui.viewmodels

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

    fun editAccount(name: String) = viewModelScope.launch {
        try {
            authRepository.editAccount(name)
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