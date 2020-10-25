package mr.cooker.mrcooker.data.entities

import java.io.Serializable

data class SmartRatingTracker(
    val userID: String = "",
    val timeFirstLoginOfDay: Long = 0,
    val daysPassed: Int = 0
): Serializable