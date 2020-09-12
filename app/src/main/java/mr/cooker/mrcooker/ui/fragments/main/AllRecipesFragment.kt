package mr.cooker.mrcooker.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_all_recipes.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.other.Constants
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.activities.RecipeActivity
import mr.cooker.mrcooker.ui.adapters.RecipeAdapter
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel

@AndroidEntryPoint
class AllRecipesFragment : Fragment(R.layout.fragment_all_recipes) {

    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_allRecipesFragment_to_addRecipeFragment)
        }

        allRecipesViewModel.recipesList.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    // Before try catch in viewmodel we can use emit(Resource.Loading()) to
                    // tell the view we started fetching results and this will be triggered
                    swipeRefreshLayout.isRefreshing = true
                }

                is Resource.Success -> {
                    recipeAdapter.submitList(it.data)
                }

                is Resource.Failure -> {
                    //Handle the failure
                    Toast.makeText(
                        requireContext(),
                        "An error has occurred:${it.throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        recipeAdapter.setOnItemClickListener { recipe, iv ->
            showRecipe(recipe, iv)
        }

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            allRecipesViewModel.recipesList.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Loading -> {
                        // Before try catch in viewmodel we can use emit(Resource.Loading()) to
                        // tell the view we started fetching results and this will be triggered
                        swipeRefreshLayout.isRefreshing = true
                    }

                    is Resource.Success -> {
                        recipeAdapter.submitList(it.data)
                        swipeRefreshLayout.isRefreshing = false
                    }

                    is Resource.Failure -> {
                        //Handle the failure
                        Toast.makeText(
                            requireContext(),
                            "An error has occurred:${it.throwable.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    private fun setupRecyclerView() = rvAllRecipes.apply {
        recipeAdapter = RecipeAdapter(true)
        adapter = recipeAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    // Transition to RecipeActivity
    private fun showRecipe(recipe: Recipe, imageView: ImageView) {
        val intent = Intent(context, RecipeActivity::class.java)
        intent.putExtra(Constants.postID, recipe.id)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            imageView,
            imageView.transitionName
        )

        startActivity(intent, options.toBundle())
    }
}