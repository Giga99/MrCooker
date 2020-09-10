package mr.cooker.mrcooker.other

import com.google.firebase.auth.FirebaseUser
import java.util.*

fun isNight(): Boolean {
    val currHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return (currHours <= 7 || currHours >= 18)
}

object FirebaseUtils {
    lateinit var currentUser: FirebaseUser
}