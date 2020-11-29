/*
 * Created by Igor Stevanovic on 11/28/20 9:43 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/28/20 9:43 PM
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
import mr.cooker.mrcooker.data.entities.Conversation
import mr.cooker.mrcooker.data.entities.Message
import mr.cooker.mrcooker.data.entities.User
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.EventFirebase
import mr.cooker.mrcooker.other.Resource
import java.lang.Exception

class MessagingViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val status = EventFirebase(null)

    private var conversation: Conversation? = null

    suspend fun refreshConversation(conversationId: String): Resource<Conversation> {
        var conversation: Resource<Conversation>? = null
        viewModelScope.launch {
            conversation = mainRepository.refreshConversation(conversationId)
        }.join()
        return conversation!!
    }

    fun startConversation(user1: User, user2: User) = viewModelScope.launch {
        try {
            mainRepository.startConversation(Conversation(user1, user2))
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    fun updateMessages(messages: List<Message>, conversationId: String) = viewModelScope.launch {
        try {
            mainRepository.updateMessages(messages, conversationId)
            status.throwable = false
        } catch (e: Exception) {
            status.throwable = true
            status.exception = e
        }
    }

    fun setConversation(convrs: Conversation) {
        conversation = convrs
    }

    fun getConversation() = conversation
}