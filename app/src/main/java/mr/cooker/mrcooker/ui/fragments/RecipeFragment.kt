package mr.cooker.mrcooker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipe.*
import kotlinx.android.synthetic.main.recipe_row.tvName
import kotlinx.android.synthetic.main.recipe_row.tvTime
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class RecipeFragment : Fragment(R.layout.fragment_recipe) {

    private val viewModel: MainViewModel by viewModels()
    private val args: RecipeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = args.recipe

        tvName.text = recipe.name
        tvTime.text = "${recipe.timeToCook}mins"
        tvIngredients.text = recipe.ingredients
        tvInstructions.text = recipe.instructions
    }
}