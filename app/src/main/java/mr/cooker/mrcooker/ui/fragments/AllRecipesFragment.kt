package mr.cooker.mrcooker.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel

@AndroidEntryPoint
class AllRecipesFragment : Fragment(R.layout.fragment_all_recipes) {

    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
}