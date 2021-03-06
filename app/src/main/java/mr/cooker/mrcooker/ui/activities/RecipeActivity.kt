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

package mr.cooker.mrcooker.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.google.android.gms.ads.AdRequest
import com.shreyaspatil.MaterialDialog.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_recipe.etIngredients
import kotlinx.android.synthetic.main.activity_recipe.etInstructions
import kotlinx.android.synthetic.main.activity_recipe.etName
import kotlinx.android.synthetic.main.activity_recipe.etTime
import kotlinx.android.synthetic.main.activity_recipe.show
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.FavoriteRecipe
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants
import mr.cooker.mrcooker.other.Constants.ownerIDCode
import mr.cooker.mrcooker.other.Constants.postID
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.adapters.RecipeImagesAdapter
import mr.cooker.mrcooker.ui.viewmodels.AddingViewModel
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel
import mr.cooker.mrcooker.ui.viewmodels.FavoriteRecipesViewModel
import mr.cooker.mrcooker.ui.viewmodels.UserViewModel
import java.lang.Exception

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {

    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
    private val addingViewModel: AddingViewModel by viewModels()
    private val favoriteRecipesViewModel: FavoriteRecipesViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var recipe: Recipe
    private var favorite = false

    private lateinit var recipeImagesAdapter: RecipeImagesAdapter

    private var counter = 0

    private var editRecipe = false
    private var lengthBefore = 0

    private var showEveryone = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recipeLayout.visibility = View.GONE
        trailingLoaderRecipe.visibility = View.VISIBLE
        trailingLoaderRecipe.animate()

        val recipeId = intent.extras?.getString(postID)
            ?: throw IllegalArgumentException("`postID` must be non-null")

        getRecipe(recipeId)
        isItFavoriteRecipe(recipeId)

        ivAddToFavorites.setOnClickListener {
            recipeLayout.visibility = View.GONE
            trailingLoaderRecipe.visibility = View.VISIBLE
            trailingLoaderRecipe.animate()

            if (favorite) removeFromFavorites(recipeId)
            else addToFavorites()
        }

        btnCancel.setOnClickListener {
            editRecipe = false
            recipeLayout.visibility = View.VISIBLE
            editRecipeLayout.visibility = View.GONE
        }

        btnEdit.setOnClickListener {
            if(isValidForm()) {
                toolbar.menu.getItem(0).isVisible = false
                toolbar.menu.getItem(1).isVisible = false
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                editRecipeLayout.visibility = View.GONE
                trailingLoaderRecipe.visibility = View.VISIBLE
                trailingLoaderRecipe.animate()
                editRecipe()
            }
        }

        show.setOnClickListener {
            show.isChecked = !showEveryone
            showEveryone = !showEveryone
        }

        etIngredients.addTextChangedListener {
            if (it != null && it.length > lengthBefore) {
                if (etIngredients.text.toString().length == 1) {
                    etIngredients.setText("• ${etIngredients.text}")
                    etIngredients.setSelection(etIngredients.text.length)
                }
                if (etIngredients.text.toString().endsWith("\n")) {
                    etIngredients.setText(
                        etIngredients.text.toString().replace("\n", "\n• ")
                    )
                    etIngredients.setText(
                        etIngredients.text.toString().replace("• •", "•")
                    )
                    etIngredients.setSelection(etIngredients.text.length)
                }

                lengthBefore = it.length
            }
        }

        tvOwnerName.setOnClickListener {
            val intent = Intent(this.applicationContext, MainActivity::class.java)
            intent.putExtra(Constants.ownerID, recipe.ownerID)
            setResult(ownerIDCode, intent)
            finish()
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
        val data = allRecipesViewModel.getRecipeByID(postId)
        if (data == null) onBackPressed()
        when (data) {
            is Resource.Loading -> { /* NO-OP */
            }

            is Resource.Success -> {
                recipe = data.data
                userViewModel.setUserID(recipe.ownerID!!)
                val userInfo = userViewModel.getUserInfo()
                while(userInfo == null);
                withContext(Dispatchers.Main) {
                    with(recipe) {
                        tvName.text = name
                        tvTime.text = "${timeToCook}min"
                        tvIngredients.text = ingredients
                        tvInstructions.text = instructions
                        tvOwnerName.text = "by\n${userInfo.username}"
                        recipeImagesAdapter = RecipeImagesAdapter()
                        vpImages.adapter = recipeImagesAdapter
                        recipeImagesAdapter.submitList(imgUrls)
                        if (ownerID.equals(currentUser.uid)) {
                            toolbar.menu.getItem(0).isVisible = true
                            toolbar.menu.getItem(1).isVisible = true
                            ivAddToFavorites.visibility = View.GONE
                            etName.setText(recipe.name)
                            etTime.setText("" + recipe.timeToCook)
                            etIngredients.setText(recipe.ingredients)
                            etInstructions.setText(recipe.instructions)
                            show.isChecked = recipe.showToEveryone
                            showEveryone = recipe.showToEveryone
                        } else {
                            toolbar.menu.getItem(0).isVisible = false
                            toolbar.menu.getItem(1).isVisible = false
                            ivAddToFavorites.visibility = View.VISIBLE
                        }

                        counter++
                        if (counter == 2) {
                            recipeLayout.visibility = View.VISIBLE
                            trailingLoaderRecipe.visibility = View.GONE
                        }
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
                if (!editRecipe) supportFinishAfterTransition()
                else {
                    MaterialDialog.Builder(this)
                        .setTitle("Editing recipe")
                        .setMessage("Are you sure you want to exit editing mode?")
                        .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            supportFinishAfterTransition()
                        }
                        .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                        .build()
                        .show()
                }
                true
            }

            R.id.editRecipe -> {
                editRecipe = true
                recipeLayout.visibility = View.GONE
                editRecipeLayout.visibility = View.VISIBLE
                true
            }

            R.id.deleteRecipe -> {
                MaterialDialog.Builder(this)
                    .setTitle("Deleting recipe")
                    .setMessage("Are you sure you want to delete recipe?")
                    .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        toolbar.menu.getItem(0).isVisible = false
                        toolbar.menu.getItem(1).isVisible = false
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
                ).show()
                startActivity(Intent(this@RecipeActivity, MainActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                toolbar.menu.getItem(0).isVisible = true
                toolbar.menu.getItem(1).isVisible = true
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                recipeLayout.visibility = View.VISIBLE
                trailingLoaderRecipe.visibility = View.GONE
                Toast.makeText(this@RecipeActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editRecipe() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val name = etName.text.toString()
            val time = etTime.text.toString()
            val ingredients = etIngredients.text.toString()
            val instructions = etInstructions.text.toString()
            addingViewModel.editRecipe(
                recipe.id!!,
                name,
                time,
                ingredients,
                instructions,
                showEveryone
            ).join()
            if (addingViewModel.status.throwable) addingViewModel.status.throwException()

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@RecipeActivity,
                    "Successfully edited recipe!",
                    Toast.LENGTH_SHORT
                ).show()

                tvName.text = name
                tvTime.text = "${time}min"
                tvIngredients.text = ingredients
                tvInstructions.text = instructions

                toolbar.menu.getItem(0).isVisible = true
                toolbar.menu.getItem(1).isVisible = true
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                editRecipe = false
                recipeLayout.visibility = View.VISIBLE
                trailingLoaderRecipe.visibility = View.GONE
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                toolbar.menu.getItem(1).isVisible = true
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                recipeLayout.visibility = View.VISIBLE
                trailingLoaderRecipe.visibility = View.GONE
                Toast.makeText(this@RecipeActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etName, NonEmptyRule("Please, enter a recipe name!"))
            .addField(etTime, NonEmptyRule("Please, enter a time to cook!"))
            .addField(etIngredients, NonEmptyRule("Please, enter ingredients!"))
            .addField(etInstructions, NonEmptyRule("Please, enter instructions!"))
            .validate()
    }
}