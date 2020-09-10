package mr.cooker.mrcooker.data.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.data.db.entities.RecipeFirebase
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import java.util.*

class FirebaseDatabase {
    private val recipesCollectionRef: CollectionReference = Firebase.firestore.collection("Recipes")
    private val storageReference = Firebase.storage.reference

    suspend fun add(recipe: RecipeFirebase) {
        recipesCollectionRef.add(recipe)
    }

    fun getAllRecipes(): LiveData<List<RecipeFirebase>> {
        val querySnapshot: QuerySnapshot = recipesCollectionRef.get().result!!
        val recipeList = MutableLiveData<List<RecipeFirebase>>()
        val list = mutableListOf<RecipeFirebase>()

        for(document in querySnapshot.documents) {
            val recipe = document.toObject<RecipeFirebase>()!!
            list.add(recipe)
        }

        recipeList.postValue(list)
        return recipeList
    }

    suspend fun uploadImage(imageUri: Uri): String {
        val filename = "${currentUser.uid}_${Calendar.getInstance().timeInMillis}"
        storageReference.child("images/$filename").putFile(imageUri).await()
        return filename
    }

    suspend fun getImageUrl(fileName: String): Uri {
        return storageReference.child("images/$fileName").downloadUrl.await()
    }
}