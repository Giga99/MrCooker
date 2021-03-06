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

package mr.cooker.mrcooker.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_all_recipes.*
import kotlinx.android.synthetic.main.fragment_all_recipes.swipeRefreshLayout
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants
import mr.cooker.mrcooker.other.Constants.SEARCH_RECIPES_TIME_DELAY
import mr.cooker.mrcooker.other.Constants.ownerIDCode
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.activities.RecipeActivity
import mr.cooker.mrcooker.ui.adapters.RecipeAdapter
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel
import mr.cooker.mrcooker.ui.viewmodels.UserViewModel
import java.util.*

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AllRecipesFragment : Fragment(R.layout.fragment_all_recipes) {

    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
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

        var job: Job? = null
        etSearch.editText?.addTextChangedListener { editable ->
            job?.cancel()
            if (editable.toString() != "") {
                job = CoroutineScope(Dispatchers.Main).launch {
                    search(editable)
                }
            } else if (editable.toString() == "") {
                realtimeUpdate()
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            if (etSearch.editText?.editableText.toString() != "") {
                job = CoroutineScope(Dispatchers.Main).launch {
                    search(etSearch.editText?.editableText)
                }
            } else if (etSearch.editText?.editableText.toString() == "") {
                realtimeUpdate()
            }
        }
    }

    private suspend fun search(editable: Editable?) {
        delay(SEARCH_RECIPES_TIME_DELAY)
        editable.let {
            if (editable.toString().isNotEmpty()) {
                val recipes =
                    allRecipesViewModel.getSearchedRecipes(
                        editable.toString().toLowerCase(Locale.ROOT)
                    )
                observe(recipes)
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
                swipeRefreshLayout?.isRefreshing = true
            }

            is Resource.Success -> {
                swipeRefreshLayout?.isRefreshing = false
                recipeAdapter.submitList(it.data)
                recipeAdapter.notifyDataSetChanged()
            }

            is Resource.Failure -> {
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
        val intent = Intent(requireContext(), RecipeActivity::class.java)
        intent.putExtra(Constants.postID, recipe.id)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            imageView,
            imageView.transitionName
        )

        startActivityForResult(intent, ownerIDCode, options.toBundle())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ownerIDCode) {
            val ownerID = data?.getStringExtra(Constants.ownerID)
            if (ownerID != null) {
                if (ownerID == currentUser.uid) findNavController().navigate(R.id.profileFragment)
                else {
                    userViewModel.setUserID(ownerID)
                    findNavController().navigate(R.id.action_allRecipesFragment_to_otherProfileFragment)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        realtimeUpdate()
    }
}