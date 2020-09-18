package mr.cooker.mrcooker.data.entities

import java.io.Serializable

data class Recipe(
    var imgUrl: String = "",
    var name: String = "",
    var timeToCook: Int = -1,
    var ingredients: String = "",
    var instructions: String = "",
    var ownerID: String? = null,
    var id: String? = null
) : Serializable {}