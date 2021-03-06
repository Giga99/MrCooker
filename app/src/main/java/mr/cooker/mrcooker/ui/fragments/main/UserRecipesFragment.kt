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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_recipes.*
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants
import mr.cooker.mrcooker.other.Constants.ownerIDCode
import mr.cooker.mrcooker.other.Constants.postID
import mr.cooker.mrcooker.other.FirebaseUtils
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.adapters.RecipeAdapter
import mr.cooker.mrcooker.ui.activities.RecipeActivity
import mr.cooker.mrcooker.ui.viewmodels.AddingViewModel
import mr.cooker.mrcooker.ui.viewmodels.UserRecipesViewModel
import mr.cooker.mrcooker.ui.viewmodels.UserViewModel
import java.lang.Exception
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UserRecipesFragment : Fragment(R.layout.fragment_user_recipes) {

    private val userRecipesViewModel: UserRecipesViewModel by activityViewModels()
    private val addingViewModel: AddingViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        userRecipesViewModel.userRecipes.observe(viewLifecycleOwner, {
            observe(it)
        })

        var job: Job? = null
        etSearchMy.editText?.addTextChangedListener { editable ->
            job?.cancel()
            if (editable.toString() != "") {
                job = CoroutineScope(Dispatchers.Main).launch {
                    search(editable)
                }
            } else if (editable.toString() == "") {
                realtimeUpdate()
            }
        }

        recipeAdapter.setOnItemClickListener { recipe, iv ->
            showRecipe(recipe, iv)
        }

        // Deleting on swipe left or right and undo if change mind, only if currentUser is owner of recipes
        if(userRecipesViewModel.getMyRecipesBoolean()) {
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
                    swipeRefreshLayout?.isRefreshing = true
                    deleteRecipe(viewHolder)
                }
            }

            ItemTouchHelper(itemTouchHelperCallback).apply {
                attachToRecyclerView(rvRecipes)
            }
        }

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            if (etSearchMy.editText?.editableText.toString() != "") {
                job = CoroutineScope(Dispatchers.Main).launch {
                    search(etSearchMy.editText?.editableText)
                }
            } else if (etSearchMy.editText?.editableText.toString() == "") {
                realtimeUpdate()
            }
        }
    }

    private suspend fun search(editable: Editable?) {
        delay(Constants.SEARCH_RECIPES_TIME_DELAY)
        editable.let {
            if (editable.toString().isNotEmpty()) {
                val recipes =
                    userRecipesViewModel.getSearchedUserRecipes(
                        editable.toString().toLowerCase(Locale.ROOT)
                    )
                observe(recipes)
            }
        }
    }

    private fun realtimeUpdate() = CoroutineScope(Dispatchers.IO).launch {
        val data = userRecipesViewModel.getRealtimeUserRecipes()
        withContext(Dispatchers.Main) {
            observe(data)
        }
    }

    private fun deleteRecipe(viewHolder: RecyclerView.ViewHolder) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val position = viewHolder.adapterPosition
                val recipe = recipeAdapter.differ.currentList[position]

                addingViewModel.deleteRecipe(recipe).join()
                if (addingViewModel.status.throwable) addingViewModel.status.throwException()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Successfully deleted recipe!",
                        Toast.LENGTH_SHORT
                    ).show()
                    realtimeUpdate()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
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

        startActivityForResult(intent, ownerIDCode, options.toBundle())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ownerIDCode) {
            val ownerID = data?.getStringExtra(Constants.ownerID)
            if (ownerID != null) {
                if (ownerID == FirebaseUtils.currentUser.uid) findNavController().navigate(R.id.profileFragment)
                else {
                    userViewModel.setUserID(ownerID)
                    findNavController().navigate(R.id.action_userRecipesFragment_to_otherProfileFragment)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        realtimeUpdate()
    }
}
