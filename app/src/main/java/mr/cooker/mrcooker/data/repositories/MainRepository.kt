package mr.cooker.mrcooker.data.repositories

import android.net.Uri
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.data.firebase.FirebaseDB
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseDB: FirebaseDB
) {
    suspend fun uploadImage(imageUri: Uri): Uri? = firebaseDB.uploadImage(imageUri)

    suspend fun uploadRecipe(recipe: Recipe) = firebaseDB.uploadRecipe(recipe)
}