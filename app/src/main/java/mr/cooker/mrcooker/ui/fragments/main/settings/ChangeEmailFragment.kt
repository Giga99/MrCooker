/*
 * Created by Igor Stevanovic on 11/30/20 11:40 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/30/20 11:40 PM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_change_email.*
import kotlinx.android.synthetic.main.fragment_change_email.btnCancel
import kotlinx.android.synthetic.main.fragment_change_email.btnSubmit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.activities.AuthenticationActivity
import mr.cooker.mrcooker.ui.viewmodels.EditAccountViewModel
import mr.cooker.mrcooker.ui.viewmodels.SignOutViewModel

@AndroidEntryPoint
class ChangeEmailFragment : Fragment(R.layout.fragment_change_email) {

    private val editAccountViewModel: EditAccountViewModel by viewModels()
    private val signOutViewModel: SignOutViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        btnSubmit.setOnClickListener {
            if (isValidForm()) {
                changeEmailLayout.visibility = View.GONE
                trailingLoaderChangeEmail.visibility = View.VISIBLE
                trailingLoaderChangeEmail.animate()
                changeEmail()
            }
        }
    }

    private fun changeEmail() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val oldEmail = etOldEmail.editText?.text.toString()
            val password = etPassword.editText?.text.toString()
            val newEmail = etNewEmail.editText?.text.toString()
            editAccountViewModel.changeEmail(oldEmail, password, newEmail)
            if (editAccountViewModel.status.throwable) editAccountViewModel.status.throwException()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Successfully changed the email, please verify new email!",
                    Toast.LENGTH_SHORT
                ).show()
                signOutViewModel.signOut()
                with(requireActivity()) {
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                    finish()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                changeEmailLayout.visibility = View.VISIBLE
                trailingLoaderChangeEmail.visibility = View.GONE
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etOldEmail, NonEmptyRule("Please, enter your email!"))
            .addField(etPassword, NonEmptyRule("Please, enter your current password!"))
            .addField(etNewEmail, NonEmptyRule("Please, enter your new email!"))
            .validate()
    }
}