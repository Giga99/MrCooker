package mr.cooker.mrcooker.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_all_recipes.*
import kotlinx.android.synthetic.main.fragment_all_recipes.swipeRefreshLayout
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants
import mr.cooker.mrcooker.other.Constants.SEARCH_RECIPES_TIME_DELAY
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.activities.RecipeActivity
import mr.cooker.mrcooker.ui.adapters.RecipeAdapter
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel
import mr.cooker.mrcooker.ui.viewmodels.SearchViewModel
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AllRecipesFragment : Fragment(R.layout.fragment_all_recipes) {

    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        allRecipesViewModel.allRecipes.observe(viewLifecycleOwner, {
            observe(it)
        })

        recipeAdapter.setOnItemClickListener { recipe, iv ->
            showRecipe(recipe, iv)
        }

        swipeRefreshLayout.setOnRefreshListener {
            realtimeUpdate()
        }

        var job: Job? = null
        etSearch.editText?.addTextChangedListener { editable ->
            job?.cancel()
            if (editable.toString() != "") {
                job = CoroutineScope(Dispatchers.Main).launch {
                    delay(SEARCH_RECIPES_TIME_DELAY)
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            val recipes =
                                searchViewModel.getSearchedRecipes(
                                    editable.toString().toLowerCase(Locale.ROOT)
                                )
                            observe(recipes)
                        }
                    }
                }
            } else if (editable.toString() == "") {
                realtimeUpdate()
            }
        }
    }

    private fun realtimeUpdate() = CoroutineScope(Dispatchers.IO).launch {
        val data = allRecipesViewModel.getRealtimeRecipes()
        withContext(Dispatchers.Main) {
            observe(data)
        }
    }

    private fun observe(it: Resource<MutableList<Recipe>>?) {
        when (it) {
            is Resource.Loading -> {
                swipeRefreshLayout.isRefreshing = true
            }

            is Resource.Success -> {
                swipeRefreshLayout.isRefreshing = false
                recipeAdapter.submitList(it.data)
            }

            is Resource.Failure -> {
                Timber.e(it.throwable)
                Toast.makeText(
                    requireContext(),
                    "An error has occurred:${it.throwable.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupRecyclerView() = rvAllRecipes.apply {
        recipeAdapter = RecipeAdapter()
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

    override fun onResume() {
        super.onResume()

        realtimeUpdate()
    }
}