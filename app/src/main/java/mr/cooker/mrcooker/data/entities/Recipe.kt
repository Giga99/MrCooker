package mr.cooker.mrcooker.data.entities

import java.io.Serializable

data class Recipe(
    var imgUrl: String,
    var name: String,
    var timeToCook: Int,
    var ingredients: String,
    var instructions: String,
    var id: String? = null
) : Serializable {}