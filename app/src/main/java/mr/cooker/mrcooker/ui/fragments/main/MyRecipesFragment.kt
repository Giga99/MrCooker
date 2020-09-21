package mr.cooker.mrcooker.ui.fragments.main

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
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
import kotlinx.android.synthetic.main.fragment_my_recipes.fab
import kotlinx.android.synthetic.main.fragment_my_recipes.swipeRefreshLayout
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.Constants.postID
import mr.cooker.mrcooker.other.Converters.Companion.toBitmap
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.adapters.RecipeAdapter
import mr.cooker.mrcooker.ui.activities.RecipeActivity
import mr.cooker.mrcooker.ui.viewmodels.AddingViewModel
import mr.cooker.mrcooker.ui.viewmodels.MyRecipesViewModel
import timber.log.Timber
import java.io.*
import java.lang.Exception
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MyRecipesFragment : Fragment(R.layout.fragment_my_recipes) {

    private val myRecipesViewModel: MyRecipesViewModel by viewModels()
    private val addingViewModel: AddingViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        myRecipesViewModel.myRecipes.observe(viewLifecycleOwner, Observer {
            observe(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_myRecipesFragment_to_addRecipeFragment)
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
                deleteRecipe(viewHolder)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvRecipes)
        }

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            myRecipesViewModel.myRecipes.observe(viewLifecycleOwner, Observer {
                observe(it)
                swipeRefreshLayout.isRefreshing = false
            })
        }
    }

    private fun deleteRecipe(viewHolder: RecyclerView.ViewHolder) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val position = viewHolder.adapterPosition
            val recipe = recipeAdapter.differ.currentList[position]
            val byteArray = addingViewModel.getBytes(recipe.imgUrl)
            val bitmap = toBitmap(byteArray)

            // Making uri from bitmap, deprecated TODO
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media
                .insertImage(context?.contentResolver, bitmap, "Title", null)
            val uri = Uri.parse(path.toString())

            addingViewModel.deleteRecipe(recipe).join()
            Snackbar.make(requireView(), "Successfully deleted recipe!", Snackbar.LENGTH_LONG)
                .apply {
                    setAction("Undo") {
                        CoroutineScope(Dispatchers.IO).launch {
                            addingViewModel.uploadAgain(recipe, uri!!)
                        }
                    }
                    show()
                }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observe(it: Resource<MutableList<Recipe>>?) {
        when(it) {
            is Resource.Loading -> {
                swipeRefreshLayout.isRefreshing = true
            }

            is Resource.Success -> {
                recipeAdapter.submitList(it.data)
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

        startActivity(intent, options.toBundle())
    }
}
