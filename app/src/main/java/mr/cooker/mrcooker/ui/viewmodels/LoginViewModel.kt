package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.repositories.AuthRepository
import mr.cooker.mrcooker.other.EventFirebaseAuth

class LoginViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val status = MutableLiveData<EventFirebaseAuth<Exception>>()

    fun login(email: String, password: String) = viewModelScope.launch {
        try {
            authRepository.login(email, password)
            val event = EventFirebaseAuth(null)
            event.throwable = false
            status.postValue(event)
        } catch (e: Exception) {
            val event = EventFirebaseAuth(e)
            event.throwable = true
            status.postValue(event)
        }
    }

    fun checkPrevLogging(): Boolean = authRepository.checkPrevLogging()
}