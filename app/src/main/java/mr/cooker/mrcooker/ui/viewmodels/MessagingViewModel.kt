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
            try {
                conversation = mainRepository.refreshConversation(conversationId)
                status.throwable = false
            } catch (e: Exception) {
                status.throwable = true
                status.exception = e
            }
        }.join()
        return conversation!!
    }

    suspend fun conversationNotExist(userId: String) = mainRepository.conversationNotExist(userId)

    suspend fun getConversation(userId: String): Resource<Conversation> {
        var conversation: Resource<Conversation>? = null
        viewModelScope.launch {
            try {
                conversation = mainRepository.getConversation(userId)
                status.throwable = false
            } catch (e: Exception) {
                status.throwable = true
                status.exception = e
            }
        }.join()
        return conversation!!
    }

    fun startConversation(user: User) = viewModelScope.launch {
        try {
            mainRepository.startConversation(user)
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