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

package mr.cooker.mrcooker.other

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseUser
import java.util.*

fun isNight(): Boolean {
    val currHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return (currHours <= 7 || currHours >= 18)
}

object FirebaseUtils {
    lateinit var currentUser: FirebaseUser
}

object SharedPrefUtils {
    lateinit var sharedPreferences: SharedPreferences
}

fun getNightMode(): Int = SharedPrefUtils.sharedPreferences.getInt("mode", 0)

fun setNightMode(mode: Int) = SharedPrefUtils.sharedPreferences.edit {
    putInt("mode", mode)
    commit()
}

fun getLastVersionRated(): String? =
    SharedPrefUtils.sharedPreferences.getString("versionRated", null)

fun setLastVersionRated(appVersion: String) = SharedPrefUtils.sharedPreferences.edit {
    putString("versionRated", appVersion)
    commit()
}