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

package mr.cooker.mrcooker.data.entities

import java.io.Serializable

data class Recipe(
    var imgUrl: String = "",
    var name: String = "",
    var timeToCook: Int = -1,
    var ingredients: String = "",
    var instructions: String = "",
    var numOfFavorites: Long = -1,
    var showToEveryone: Boolean = true,
    var timePosted: Long = -1,
    var ownerID: String? = null,
    var imgUrls: List<String> = emptyList(),
    var id: String? = null
) : Serializable