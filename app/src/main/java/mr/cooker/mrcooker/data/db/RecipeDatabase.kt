package mr.cooker.mrcooker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mr.cooker.mrcooker.data.db.entities.Recipe

@Database(
    entities = [Recipe::class],
    version = 3
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun getRecipeDao() : RecipeDAO
}