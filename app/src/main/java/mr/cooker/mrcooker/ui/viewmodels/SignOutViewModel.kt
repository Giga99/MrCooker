package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import mr.cooker.mrcooker.data.repositories.AuthRepository

class SignOutViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun signOut() = authRepository.signOut()
}