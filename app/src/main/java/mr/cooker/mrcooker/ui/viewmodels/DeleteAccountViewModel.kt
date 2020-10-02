package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.repositories.AuthRepository

class DeleteAccountViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun deleteAccount() = viewModelScope.launch {
        authRepository.deleteAccount()
    }
}