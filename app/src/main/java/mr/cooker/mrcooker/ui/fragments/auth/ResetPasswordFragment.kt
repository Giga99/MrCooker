/*
 * Created by Igor Stevanovic on 11/17/20 12:17 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/17/20 12:15 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.fragments.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_reset_password.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.viewmodels.ResetPasswordViewModel
import java.lang.Exception

@AndroidEntryPoint
class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
        }

        btnResetPassword.setOnClickListener {
            if (isValidForm()) resetPassword()
        }
    }

    private fun resetPassword() {
        resetPasswordLayout.visibility = View.GONE
        trailingLoaderResetPassword.visibility = View.VISIBLE
        trailingLoaderResetPassword.animate()

        val email = etEmailResetPass.editText?.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                resetPasswordViewModel.resetPassword(email).join()
                if (resetPasswordViewModel.status.throwable) resetPasswordViewModel.status.throwException()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Check your email!", Toast.LENGTH_SHORT).show()
                }
                findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resetPasswordLayout.visibility = View.VISIBLE
                    trailingLoaderResetPassword.visibility = View.GONE
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etEmailResetPass, NonEmptyRule("Please, enter your email!"))
            .validate()
    }
}