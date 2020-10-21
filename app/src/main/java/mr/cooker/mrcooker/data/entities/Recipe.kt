package mr.cooker.mrcooker.data.entities

import java.io.Serializable

data class Recipe(
    var imgUrl: String = "",
    var name: String = "",
    var timeToCook: Int = -1,
    var ingredients: String = "",
    var instructions: String = "",
    var numOfFavorites: Int = -1,
    var showToEveryone: Boolean = true,
    var timePosted: Long = -1,
    var ownerID: String? = null,
    var id: String? = null
) : Serializable