package mr.cooker.mrcooker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "recipes_table")
data class Recipe(
    var name: String,
    var timeToCook: Int,
    var ingredients: String,
    var instructions: String,
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}