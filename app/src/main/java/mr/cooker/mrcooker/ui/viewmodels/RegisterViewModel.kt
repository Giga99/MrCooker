package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.repositories.AuthRepository
import mr.cooker.mrcooker.other.EventFirebaseAuth

class RegisterViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val status = MutableLiveData<EventFirebaseAuth<Exception>>()

    fun register(username: String, email: String, password: String) = viewModelScope.launch {
        try {
            authRepository.register(username, email, password)
            val event = EventFirebaseAuth(null)
            event.throwable = false
            status.postValue(event)
        } catch (e: Exception) {
            val event = EventFirebaseAuth(e)
            event.throwable = true
            status.postValue(event)
        }
    }
}