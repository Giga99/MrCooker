package mr.cooker.mrcooker.data.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.other.Resource

class RecipesRepo {

    suspend fun getRecipes(): Resource<MutableList<Recipe>> {
        val recipesList = mutableListOf<Recipe>()

        val documents = Firebase.firestore.collection("recipes").get().await()
        for(document in documents) {
            val recipe = document.toObject<Recipe>()
            recipesList.add(recipe)
        }

        return Resource.Success(recipesList)
    }
}