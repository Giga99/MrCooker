package mr.cooker.mrcooker.ui.fragments.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_settings.*
import mr.cooker.mrcooker.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_editProfileFragment)
        }

        btnChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
        }
    }
}