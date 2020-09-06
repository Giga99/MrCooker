package mr.cooker.mrcooker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import mr.cooker.mrcooker.data.db.entities.Recipe

@Dao
interface RecipeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes_table")
    fun getAllRecipes() : LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes_table WHERE ID = :postID")
    fun getRecipe(postID: Int) : LiveData<Recipe>
}