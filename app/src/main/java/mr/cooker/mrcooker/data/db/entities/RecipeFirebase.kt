package mr.cooker.mrcooker.data.db.entities

import android.net.Uri
import java.io.Serializable

data class RecipeFirebase(
    var imgUrl: Uri,
    var name: String,
    var timeToCook: Int,
    var ingredients: String,
    var instructions: String,
    var ownerUid: String
) : Serializable {
}