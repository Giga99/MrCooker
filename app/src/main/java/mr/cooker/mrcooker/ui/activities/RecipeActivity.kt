package mr.cooker.mrcooker.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants.postID
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

        val postId = intent.extras?.getInt(postID)
            ?: throw IllegalArgumentException("`postID` must be non-null")

        // TODO getRecipeById()
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