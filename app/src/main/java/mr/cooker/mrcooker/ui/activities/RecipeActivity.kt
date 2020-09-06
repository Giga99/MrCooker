package mr.cooker.mrcooker.ui.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_recipe.tvName
import kotlinx.android.synthetic.main.activity_recipe.tvTime
import kotlinx.android.synthetic.main.recipe_row.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.ui.viewmodels.MainViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val postId = intent.extras?.getInt("postID")
            ?: throw IllegalArgumentException("`postID` must be non-null")

        viewModel.getRecipe(postId).observe(this, { recipe ->
            this@RecipeActivity.recipe = recipe
            tvName.text = recipe.name
            tvTime.text = "${recipe.timeToCook}min"
            tvIngredients.text = recipe.ingredients
            tvInstructions.text = recipe.instructions
            Glide.with(this).load(recipe.img).into(ivHeader)
        })
    }

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