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

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.cooker.mrcooker.data.entities.SmartRating
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

    fun resetDaysPassed() = viewModelScope.launch {
        try {
            mainRepository.resetDaysPassed()
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    fun setSmartRating(smartRating: SmartRating) = viewModelScope.launch {
        try {
            mainRepository.setSmartRating(smartRating)
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    fun countDaysPassed(count: Boolean) = viewModelScope.launch {
        try {
            mainRepository.countDaysPassed(count)
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }
}