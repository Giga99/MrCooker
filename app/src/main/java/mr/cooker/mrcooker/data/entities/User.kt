/*
 * Created by Igor Stevanovic on 11/27/20 12:43 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/27/20 12:43 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.data.entities

import java.io.Serializable

data class User(
    val username: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val userId: String = ""
) : Serializable
