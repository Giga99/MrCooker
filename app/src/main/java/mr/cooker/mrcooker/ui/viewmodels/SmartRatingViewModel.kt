package mr.cooker.mrcooker.ui.viewmodels

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.SmartRatingTracker
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.EventFirebase
import java.lang.Exception

class SmartRatingViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val status = EventFirebase(null)

    suspend fun getSmartRatingTracker(): SmartRatingTracker? {
        return try {
            var smartRatingTracker: SmartRatingTracker? = null
            viewModelScope.launch {
                smartRatingTracker = mainRepository.getSmartRatingTracker()
            }.join()
            status.throwable = false
            smartRatingTracker
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
            null
        }
    }
}