package mr.cooker.mrcooker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.other.Converters

@Database(
    entities = [Recipe::class],
    version = 5
)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun getRecipeDao() : RecipeDAO
}