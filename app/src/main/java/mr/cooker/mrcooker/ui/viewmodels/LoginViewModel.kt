package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.repositories.AuthRepository
import mr.cooker.mrcooker.other.EventFirebase

class LoginViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val status = EventFirebase(null)

    fun login(email: String, password: String) = viewModelScope.launch {
        try {
            authRepository.login(email, password)
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    suspend fun checkPrevLogging(): Boolean = authRepository.checkPrevLogging()
}