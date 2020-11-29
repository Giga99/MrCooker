/*
 * Created by Igor Stevanovic on 11/25/20 11:38 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/25/20 11:38 PM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.data.entities

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable

data class Message(
    var senderId: String = "",
    @ServerTimestamp
    var timestamp: Long = 0L,
    var text: String = "",
    var seen: Boolean = false
) : Serializable