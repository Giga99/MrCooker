package mr.cooker.mrcooker.other

import android.content.SharedPreferences
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

fun getLastVersionRated():String? = SharedPrefUtils.sharedPreferences.getString("versionRated", null)