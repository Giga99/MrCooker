package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.repositories.AuthRepository

class ResetPasswordViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun resetPassword(email: String) = viewModelScope.launch {
        authRepository.resetPassword(email)
    }
}