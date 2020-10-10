package mr.cooker.mrcooker.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.fragment_all_recipes.*
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants.postID
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {

    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val postId = intent.extras?.getString(postID)
            ?: throw IllegalArgumentException("`postID` must be non-null")

        getRecipe(postId)
    }

    fun getRecipe(postId: String) = CoroutineScope(Dispatchers.IO).launch {
        val data = allRecipesViewModel.getRecipeByID(postId)
        when (data) {
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
        }
    }

    // Back button and for future menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}