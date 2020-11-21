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

package mr.cooker.mrcooker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import mr.cooker.mrcooker.other.SharedPrefUtils
import mr.cooker.mrcooker.other.getNightMode
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        SharedPrefUtils.sharedPreferences = getSharedPreferences("nightMode", MODE_PRIVATE)

        if (getNightMode() != 0) AppCompatDelegate.setDefaultNightMode(getNightMode())
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}