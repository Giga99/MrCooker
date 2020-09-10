package mr.cooker.mrcooker.data.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mr.cooker.mrcooker.data.db.RecipeDAO
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.data.db.entities.RecipeFirebase
import mr.cooker.mrcooker.data.firebase.FirebaseDatabase
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val recipeDao : RecipeDAO,
    private val firebaseDB : FirebaseDatabase
) {
    suspend fun insertRecipe(recipe : Recipe) = recipeDao.insertRecipe(recipe)

    suspend fun deleteRecipe(recipe : Recipe) = recipeDao.deleteRecipe(recipe)

    fun getAllMyRecipes() = recipeDao.getAllRecipes()

    fun getRecipeByID(id: Int) = recipeDao.getRecipeByID(id)

    suspend fun addRecipeToFirestore(recipe: RecipeFirebase) = firebaseDB.add(recipe)

    fun getAllRecipes() = firebaseDB.getAllRecipes()

    suspend fun uploadImage(imageUri: Uri) = firebaseDB.uploadImage(imageUri)

    suspend fun getImageUrl(fileName: String) = firebaseDB.getImageUrl(fileName)
}