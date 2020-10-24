package mr.cooker.mrcooker.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.shreyaspatil.MaterialDialog.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.FavoriteRecipe
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants.postID
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.viewmodels.AddingViewModel
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel
import mr.cooker.mrcooker.ui.viewmodels.FavoriteRecipesViewModel
import java.lang.Exception

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {

    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
    private val addingViewModel: AddingViewModel by viewModels()
    private val favoriteRecipesViewModel: FavoriteRecipesViewModel by viewModels()
    private lateinit var recipe: Recipe
    private var favorite = false

    private var counter = 0

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
        isItFavoriteRecipe(postId)

        ivAddToFavorites.setOnClickListener {
            recipeLayout.visibility = View.GONE
            trailingLoaderRecipe.visibility = View.VISIBLE
            trailingLoaderRecipe.animate()

            if (favorite) removeFromFavorites(postId)
            else addToFavorites()
        }
    }

    private fun addToFavorites() = CoroutineScope(Dispatchers.IO).launch {
        try {
            favoriteRecipesViewModel.addToFavoriteRecipes(FavoriteRecipe(recipe.id!!)).join()
            if (favoriteRecipesViewModel.status.throwable) favoriteRecipesViewModel.status.throwException()
            withContext(Dispatchers.Main) {
                ivAddToFavorites.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@RecipeActivity,
                        R.drawable.ic_favorite
                    )
                )
                favorite = true
                recipeLayout.visibility = View.VISIBLE
                trailingLoaderRecipe.visibility = View.GONE
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@RecipeActivity,
                "An error has occurred:${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            recipeLayout.visibility = View.VISIBLE
            trailingLoaderRecipe.visibility = View.GONE
        }
    }

    private fun removeFromFavorites(postId: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            favoriteRecipesViewModel.removeFavoriteRecipe(postId).join()
            if (favoriteRecipesViewModel.status.throwable) favoriteRecipesViewModel.status.throwException()
            withContext(Dispatchers.Main) {
                ivAddToFavorites.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@RecipeActivity,
                        R.drawable.ic_not_favorite
                    )
                )
                favorite = false
                recipeLayout.visibility = View.VISIBLE
                trailingLoaderRecipe.visibility = View.GONE
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@RecipeActivity,
                "An error has occurred:${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            recipeLayout.visibility = View.VISIBLE
            trailingLoaderRecipe.visibility = View.GONE
        }
    }

    private fun getRecipe(postId: String) = CoroutineScope(Dispatchers.IO).launch {
        when (val data = allRecipesViewModel.getRecipeByID(postId)) {
            is Resource.Loading -> { /* NO-OP */
            }

            is Resource.Success -> {
                recipe = data.data
                withContext(Dispatchers.Main) {
                    tvName.text = recipe.name
                    tvTime.text = "${recipe.timeToCook}min"
                    tvIngredients.text = recipe.ingredients
                    tvInstructions.text = recipe.instructions
                    Glide.with(this@RecipeActivity).load(recipe.imgUrl).into(ivHeader)
                    ivHeader.setColorFilter(Color.parseColor("#4D000000"))
                    toolbar.menu.getItem(0).isVisible = recipe.ownerID.equals(currentUser.uid)
                    if(recipe.ownerID.equals(currentUser.uid)) ivAddToFavorites.visibility = View.GONE
                    else ivAddToFavorites.visibility = View.VISIBLE
                    counter++
                    if (counter == 2) {
                        recipeLayout.visibility = View.VISIBLE
                        trailingLoaderRecipe.visibility = View.GONE
                    }
                }
            }

            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RecipeActivity,
                        "An error has occurred:${data.throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@RecipeActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun isItFavoriteRecipe(postId: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            if (favoriteRecipesViewModel.isItFavoriteRecipe(postId)) {
                withContext(Dispatchers.Main) {
                    ivAddToFavorites.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@RecipeActivity,
                            R.drawable.ic_favorite
                        )
                    )
                    favorite = true
                    counter++
                    if (counter == 2) {
                        recipeLayout.visibility = View.VISIBLE
                        trailingLoaderRecipe.visibility = View.GONE
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    counter++
                    if (counter == 2) {
                        recipeLayout.visibility = View.VISIBLE
                        trailingLoaderRecipe.visibility = View.GONE
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@RecipeActivity,
                    "An error has occurred:${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@RecipeActivity, MainActivity::class.java))
                finish()
            }
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
                MaterialDialog.Builder(this)
                    .setTitle("Deleting recipe")
                    .setMessage("Are you sure you want to delete recipe?")
                    .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        toolbar.menu.getItem(0).isVisible = false
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        recipeLayout.visibility = View.GONE
                        trailingLoaderRecipe.visibility = View.VISIBLE
                        trailingLoaderRecipe.animate()
                        deleteRecipe()
                    }
                    .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                    .show()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteRecipe() = CoroutineScope(Dispatchers.IO).launch {
        try {
            addingViewModel.deleteRecipe(recipe).join()
            if (addingViewModel.status.throwable) addingViewModel.status.throwException()

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@RecipeActivity,
                    "Successfully deleted recipe!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                startActivity(Intent(this@RecipeActivity, MainActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                toolbar.menu.getItem(0).isVisible = true
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                recipeLayout.visibility = View.VISIBLE
                trailingLoaderRecipe.visibility = View.GONE
                Toast.makeText(this@RecipeActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}