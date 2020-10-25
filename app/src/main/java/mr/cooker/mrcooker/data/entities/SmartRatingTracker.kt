package mr.cooker.mrcooker.data.entities

data class SmartRatingTracker(
    val userID: String,
    val timeFirstLoginOfDay: Long,
    val daysPassed: Int
)