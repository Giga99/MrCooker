package mr.cooker.mrcooker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.RecipeAdapter
import mr.cooker.mrcooker.ui.viewmodels.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.recipes.observe(viewLifecycleOwner, Observer {
            recipeAdapter.submitList(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addRecipeFragment)
        }

        recipeAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("recipe", it)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_recipeFragment,
                bundle
            )
        }

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
                viewModel.deleteRecipe(recipe)
                Snackbar.make(view, "Successfully deleted recipe!", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.insertRecipe(recipe)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvRecipes)
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.recipes.observe(viewLifecycleOwner, Observer {
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
}
