package mr.cooker.mrcooker.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shreyaspatil.MaterialDialog.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.activities.AuthenticationActivity
import mr.cooker.mrcooker.ui.viewmodels.DeleteAccountViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val deleteAccountViewModel: DeleteAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDeleteAccount.setOnClickListener {
            MaterialDialog.Builder(requireActivity())
                .setTitle("Deleting account")
                .setMessage("Are you sure you want to delete account?")
                .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    settingsLayout.visibility = View.GONE
                    trailingLoaderSettings.visibility = View.VISIBLE
                    trailingLoaderSettings.animate()
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
            settingsLayout.visibility = View.VISIBLE
            trailingLoaderSettings.visibility = View.GONE
        }
        startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
        requireActivity().finish()
    }
}