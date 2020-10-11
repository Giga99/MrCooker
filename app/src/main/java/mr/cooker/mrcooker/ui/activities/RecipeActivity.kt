package mr.cooker.mrcooker.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.fragment_all_recipes.*
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants.postID
import mr.cooker.mrcooker.other.Converters
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.other.SharedPrefUtils
import mr.cooker.mrcooker.ui.viewmodels.AddingViewModel
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel
import java.io.ByteArrayOutputStream
import java.lang.Exception

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {

    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
    private val addingViewModel: AddingViewModel by viewModels()
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recipeLayout.visibility = View.GONE
        trailingLoaderRecipe.visibility = View.VISIBLE
        trailingLoaderRecipe.animate()

        val postId = intent.extras?.getString(postID)
            ?: throw IllegalArgumentException("`postID` must be non-null")

        getRecipe(postId)
    }

    private fun getRecipe(postId: String) = CoroutineScope(Dispatchers.IO).launch {
        when (val data = allRecipesViewModel.getRecipeByID(postId)) {
            is Resource.Loading -> {
                swipeRefreshLayout.isRefreshing = true
            }

            is Resource.Success -> {
                recipe = data.data
            }

            is Resource.Failure -> {
                Toast.makeText(
                    this@RecipeActivity,
                    "An error has occurred:${data.throwable.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        withContext(Dispatchers.Main) {
            tvName.text = recipe.name
            tvTime.text = "${recipe.timeToCook.toString()}min"
            tvIngredients.text = recipe.ingredients
            tvInstructions.text = recipe.instructions
            Glide.with(this@RecipeActivity).load(recipe.imgUrl).into(ivHeader)
            toolbar.menu.getItem(0).isVisible = recipe.ownerID.equals(currentUser.uid)
            recipeLayout.visibility = View.VISIBLE
            trailingLoaderRecipe.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_recipe_menu, menu)

        return true
    }

    // Back button and for future menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }

            R.id.deleteRecipe -> {
                recipeLayout.visibility = View.GONE
                trailingLoaderRecipe.visibility = View.VISIBLE
                trailingLoaderRecipe.animate()
                deleteRecipe()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteRecipe() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val byteArray = addingViewModel.getBytes(recipe.imgUrl)
            val bitmap = Converters.toBitmap(byteArray)

            // Making uri from bitmap, deprecated TODO
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media
                .insertImage(this@RecipeActivity.contentResolver, bitmap, "Title", null)
            val uri = Uri.parse(path.toString())

            addingViewModel.deleteRecipe(recipe).join()
            if (addingViewModel.status.throwable) addingViewModel.status.throwException()
            Snackbar.make(tvInstructions, "Successfully deleted recipe!", Snackbar.LENGTH_LONG)
                .apply {
                    setAction("Undo") {
                        addingViewModel.uploadAgain(recipe, uri!!)
                    }
                    show()
                }

            withContext(Dispatchers.Main) {
                startActivity(Intent(this@RecipeActivity, MainActivity::class.java))
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                recipeLayout.visibility = View.VISIBLE
                trailingLoaderRecipe.visibility = View.GONE
                Toast.makeText(this@RecipeActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}