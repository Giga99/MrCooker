package mr.cooker.mrcooker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_my_recipes.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.other.Constants.postID
import mr.cooker.mrcooker.ui.adapters.RecipeAdapter
import mr.cooker.mrcooker.ui.activities.RecipeActivity
import mr.cooker.mrcooker.ui.viewmodels.MyRecipesViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MyRecipesFragment : Fragment(R.layout.fragment_my_recipes) {

    private val myRecipesViewModel: MyRecipesViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        myRecipesViewModel.recipes.observe(viewLifecycleOwner, Observer {
            recipeAdapter.submitList(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addRecipeFragment)
        }

        recipeAdapter.setOnItemClickListener { recipe, iv ->
            showRecipe(recipe, iv)
        }

        // Deleting on swipe left or right and undo if change mind
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val recipe = recipeAdapter.differ.currentList[position]
                myRecipesViewModel.deleteRecipe(recipe)
                Snackbar.make(view, "Successfully deleted recipe!", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        myRecipesViewModel.insertRecipe(recipe)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvRecipes)
        }

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            myRecipesViewModel.recipes.observe(viewLifecycleOwner, Observer {
                recipeAdapter.submitList(it)
                swipeRefreshLayout.isRefreshing = false
            })
        }
    }

    private fun setupRecyclerView() = rvRecipes.apply {
        recipeAdapter = RecipeAdapter()
        adapter = recipeAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    // Transition to RecipeActivity
    private fun showRecipe(recipe: Recipe, imageView: ImageView) {
        val intent = Intent(context, RecipeActivity::class.java)
        intent.putExtra(postID, recipe.id)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            imageView,
            imageView.transitionName
        )

        startActivity(intent, options.toBundle())
    }
}
