package mr.cooker.mrcooker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import mr.cooker.mrcooker.other.SharedPrefUtils
import mr.cooker.mrcooker.other.getNightMode
import mr.cooker.mrcooker.other.isNight
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