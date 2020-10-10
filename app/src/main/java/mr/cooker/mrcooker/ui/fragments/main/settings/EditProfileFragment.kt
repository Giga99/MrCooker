package mr.cooker.mrcooker.ui.fragments.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.shreyaspatil.MaterialDialog.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.ui.activities.AuthenticationActivity
import mr.cooker.mrcooker.ui.viewmodels.EditAccountViewModel

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val editAccountViewModel: EditAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEditName.setText(currentUser.displayName)

        btnSubmit.setOnClickListener {
            if (isValidForm()) {
                editProfileLayout.visibility = View.GONE
                trailingLoaderEditProfile.visibility = View.VISIBLE
                trailingLoaderEditProfile.animate()
                editAccount()
            }
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        tvDeleteAccount.setOnClickListener {
            MaterialDialog.Builder(requireActivity())
                .setTitle("Deleting account")
                .setMessage("Are you sure you want to delete account?")
                .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    editProfileLayout.visibility = View.GONE
                    trailingLoaderEditProfile.visibility = View.VISIBLE
                    trailingLoaderEditProfile.animate()
                    deleteAccount()
                }
                .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .build()
                .show()
        }
    }

    private fun editAccount() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val name = etEditName.text.toString()
            editAccountViewModel.editAccount(name).join()
            if (editAccountViewModel.status.throwable) editAccountViewModel.status.throwException()
            withContext(Dispatchers.Main) {
                editProfileLayout.visibility = View.VISIBLE
                trailingLoaderEditProfile.visibility = View.GONE
            }
            findNavController().navigate(R.id.action_editProfileFragment_to_settingsFragment)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                editProfileLayout.visibility = View.VISIBLE
                trailingLoaderEditProfile.visibility = View.GONE
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAccount() = CoroutineScope(Dispatchers.IO).launch {
        try {
            editAccountViewModel.deleteAccount().join()
            withContext(Dispatchers.Main) {
                editProfileLayout.visibility = View.VISIBLE
                trailingLoaderEditProfile.visibility = View.GONE
            }
            startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
            requireActivity().finish()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                editProfileLayout.visibility = View.VISIBLE
                trailingLoaderEditProfile.visibility = View.GONE
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etEditName, NonEmptyRule("Please, enter a new name!"))
            .validate()
    }
}