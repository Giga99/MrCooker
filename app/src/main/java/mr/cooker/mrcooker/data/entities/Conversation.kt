/*
 * Created by Igor Stevanovic on 11/25/20 11:41 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/25/20 11:41 PM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.data.entities

import java.io.Serializable

data class Conversation(
    var firstUser: User? = null,
    var secondUser: User? = null,
    var messages: MutableList<Message> = mutableListOf(),
    var conversationId: String = ""
) : Serializable