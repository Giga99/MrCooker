package mr.cooker.mrcooker.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_recipe.tvName
import kotlinx.android.synthetic.main.activity_recipe.tvTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.other.Constants.postID
import mr.cooker.mrcooker.ui.viewmodels.MyRecipesViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {

    private val myRecipesViewModel: MyRecipesViewModel by viewModels()
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val postId = intent.extras?.getInt(postID)
            ?: throw IllegalArgumentException("`postID` must be non-null")

        myRecipesViewModel.getRecipeByID(postId).observe(this, { recipe ->
            this@RecipeActivity.recipe = recipe
            tvName.text = recipe.name
            tvTime.text = "${recipe.timeToCook}min"
            tvIngredients.text = recipe.ingredients
            tvInstructions.text = recipe.instructions
            Glide.with(this).load(recipe.img).into(ivHeader)
        })
    }

    // Back button and for future menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}