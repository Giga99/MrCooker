package mr.cooker.mrcooker.ui.fragments.main

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.shreyaspatil.MaterialDialog.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.activities.AuthenticationActivity
import mr.cooker.mrcooker.ui.viewmodels.DeleteAccountViewModel
import mr.cooker.mrcooker.ui.viewmodels.MyRecipesViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val myRecipesViewModel: MyRecipesViewModel by viewModels()
    private val deleteAccountViewModel: DeleteAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUsername.text = currentUser.displayName

        myRecipesViewModel.myRecipes.observe(viewLifecycleOwner, Observer {
            when(it) {
                is Resource.Loading -> { /* NO-OP */ }

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

        tvDeleteAccount.setOnClickListener {
            MaterialDialog.Builder(requireActivity())
                .setTitle("Deleting account")
                .setMessage("Are you sure you want to delete account?")
                .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    profileLayout.visibility = View.GONE
                    trailingLoaderProfile.visibility = View.VISIBLE
                    trailingLoaderProfile.animate()
                    deleteAccount()
                }
                .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .build()
                .show()
        }
    }

    private fun deleteAccount() = CoroutineScope(Dispatchers.IO).launch {
        deleteAccountViewModel.deleteAccount().join()
        withContext(Dispatchers.Main) {
            profileLayout.visibility = View.VISIBLE
            trailingLoaderProfile.visibility = View.GONE
        }
        startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
        requireActivity().finish()
    }
}