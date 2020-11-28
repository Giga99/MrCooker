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
import mr.cooker.mrcooker.data.entities.Conversation
import mr.cooker.mrcooker.data.repositories.MainRepository

class MessagingViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private var conversation: Conversation? = null

    suspend fun getConversation(conversationId: String) = mainRepository.getConversation(conversationId)

    fun setConversation(convrs: Conversation) {
        conversation = convrs
    }
}