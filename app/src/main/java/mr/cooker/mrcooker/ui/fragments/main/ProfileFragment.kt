package mr.cooker.mrcooker.ui.fragments.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.viewmodels.MyRecipesViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val myRecipesViewModel: MyRecipesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUsername.text = currentUser.displayName

        myRecipesViewModel.myRecipes.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> { /* NO-OP */
                }

                is Resource.Success -> {
                    tvNumOfRecipes.text = "${it.data.size}"
                }

                is Resource.Failure -> {
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