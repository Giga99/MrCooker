package mr.cooker.mrcooker.data.repositories

import android.net.Uri
import com.bumptech.glide.load.engine.Resource
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.firebase.FirebaseDB
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseDB: FirebaseDB
) {
    suspend fun uploadImage(imageUri: Uri): Uri? = firebaseDB.uploadImage(imageUri)

    suspend fun uploadRecipe(recipe: Recipe) = firebaseDB.uploadRecipe(recipe)

    suspend fun getAllRecipes() = firebaseDB.getAllRecipes()

    suspend fun getRecipeByID(id: String) = firebaseDB.getRecipeByID(id)
}