package mr.cooker.mrcooker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import mr.cooker.mrcooker.data.db.entities.Recipe

@Database(
    entities = [Recipe::class],
    version = 1
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun getRecipeDao() : RecipeDAO
}