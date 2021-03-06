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
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.activities.MainActivity
import mr.cooker.mrcooker.ui.viewmodels.LoginViewModel
import java.lang.Exception

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener {
            if (isValidForm()) {
                loginUser()
            }
        }

        tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        tvResetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }

    private fun loginUser() {
        loginLayout.visibility = View.GONE
        trailingLoaderLogin.visibility = View.VISIBLE
        trailingLoaderLogin.animate()

        val email = etLoginEmail.editText?.text.toString()
        val password = etLoginPassword.editText?.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                loginViewModel.login(email, password).join()
                if (loginViewModel.status.throwable) loginViewModel.status.throwException()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loginLayout.visibility = View.VISIBLE
                    trailingLoaderLogin.visibility = View.GONE
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etLoginEmail, NonEmptyRule("Please, enter your email!"))
            .addField(etLoginPassword, NonEmptyRule("Please, enter your password!"))
            .validate()
    }
}