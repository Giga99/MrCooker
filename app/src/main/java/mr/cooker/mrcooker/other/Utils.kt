package mr.cooker.mrcooker.other

import java.util.*

fun isNight(): Boolean {
    val currHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return (currHours <= 7 || currHours >= 18)
}