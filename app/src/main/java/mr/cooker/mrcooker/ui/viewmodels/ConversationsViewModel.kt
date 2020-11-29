/*
 * Created by Igor Stevanovic on 11/26/20 11:59 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/26/20 11:59 PM
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
import androidx.lifecycle.liveData
import mr.cooker.mrcooker.data.entities.Conversation
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.Resource
import java.lang.Exception

class ConversationsViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val allConversations = liveData<Resource<List<Conversation>>> {
        emit(Resource.Loading())
        try {
            emit(mainRepository.getConversationList())
        } catch (e: Exception) {
            emit(Resource.Failure(e.cause!!))
        }
    }

    suspend fun getRealtimeConversations() = mainRepository.getConversationList()
}