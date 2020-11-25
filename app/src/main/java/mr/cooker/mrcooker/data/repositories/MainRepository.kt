/*
 * Created by Igor Stevanovic on 11/17/20 12:17 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/17/20 12:15 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.data.repositories

import android.net.Uri
import com.mobapphome.androidappupdater.tools.ProgramInfo
import mr.cooker.mrcooker.data.entities.*
import mr.cooker.mrcooker.data.firebase.FirebaseDB
import mr.cooker.mrcooker.other.Resource
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseDB: FirebaseDB
) {
    suspend fun uploadImage(imageUri: Uri): Uri? = firebaseDB.uploadImage(imageUri)

    suspend fun uploadRecipe(recipe: Recipe) = firebaseDB.uploadRecipe(recipe)

    suspend fun editRecipe(
        id: String,
        name: String,
        time: String,
        ingredients: String,
        instructions: String,
        showToEveryone: Boolean
    ) = firebaseDB.editRecipe(id, name, time, ingredients, instructions, showToEveryone)

    suspend fun deleteRecipe(recipe: Recipe) = firebaseDB.deleteRecipe(recipe)

    suspend fun getBytes(imgUrl: String) = firebaseDB.getBytes(imgUrl)

    suspend fun getAllRecipes() = firebaseDB.getAllRecipes()

    suspend fun getRealtimeRecipes() = firebaseDB.getAllRecipes()

    suspend fun getRealtimeMyRecipes() = firebaseDB.getMyRecipes()

    suspend fun getMyRecipes() = firebaseDB.getMyRecipes()

    suspend fun getRecipeByID(id: String) = firebaseDB.getRecipeByID(id)

    suspend fun getSearchedRecipes(search: String) = firebaseDB.getSearchedRecipes(search)

    suspend fun getSearchedMyRecipes(search: String) = firebaseDB.getSearchedMyRecipes(search)

    suspend fun addToFavoriteRecipes(favoriteRecipe: FavoriteRecipe) =
        firebaseDB.addToFavoriteRecipes(favoriteRecipe)

    suspend fun removeFavoriteRecipe(recipeID: String) = firebaseDB.removeFavoriteRecipe(recipeID)

    suspend fun isItFavoriteRecipe(recipeID: String): Boolean =
        firebaseDB.isItFavoriteRecipe(recipeID)

    suspend fun getFavoriteRecipes(): Resource<MutableList<Recipe>> =
        firebaseDB.getFavoriteRecipes()

    suspend fun getSearchedFavoriteRecipes(search: String): Resource<MutableList<Recipe>> =
        firebaseDB.getSearchedFavoriteRecipes(search)

    suspend fun getSmartRatingTracker(): SmartRatingTracker = firebaseDB.getSmartRatingTracker()

    suspend fun resetDaysPassed() = firebaseDB.resetDaysPassed()

    suspend fun setSmartRating(smartRating: SmartRating) = firebaseDB.setSmartRating(smartRating)

    suspend fun countDaysPassed(count: Boolean) = firebaseDB.countDaysPassed(count)

    suspend fun getAppInfo(): ProgramInfo = firebaseDB.getAppInfo()

    suspend fun startConversation(conversation: Conversation) =
        firebaseDB.startConversation(conversation)

    suspend fun sendMessage(messages: List<Message>, conversationId: String) =
        firebaseDB.sendMessage(messages, conversationId)

    suspend fun getConversation(conversationId: String): Resource<Conversation> =
        firebaseDB.getConversation(conversationId)

    suspend fun getConversationList(): Resource<List<Conversation>> =
        firebaseDB.getConversationList()
}