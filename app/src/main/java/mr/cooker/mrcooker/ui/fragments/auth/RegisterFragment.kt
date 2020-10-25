package mr.cooker.mrcooker.ui.fragments.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.form_validation.constant.PasswordPattern
import com.github.dhaval2404.form_validation.rule.EqualRule
import com.github.dhaval2404.form_validation.rule.MinLengthRule
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.rule.PasswordRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.viewmodels.RegisterViewModel


@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val registerViewModel: RegisterViewModel by viewModels()

    private var termsAccepted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRegister.setOnClickListener {
            if (isValidForm() && termsAccepted) {
                registerUser()
            } else if (!termsAccepted) {
                Toast.makeText(
                    requireContext(),
                    "You need to accept Terms and Conditions!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        checkboxTerms.setOnClickListener {
            termsAccepted = !termsAccepted
            checkboxTerms.isChecked = termsAccepted
        }

        tvTerms.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://mrcooker.flycricket.io/terms.html")
                )
            )
        }
    }

    private fun registerUser() {
        registerLayout.visibility = View.GONE
        trailingLoaderRegister.visibility = View.VISIBLE
        trailingLoaderRegister.animate()

        val username = etUsername.editText?.text.toString()
        val email = etEmail.editText?.text.toString()
        val password = etPassword.editText?.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                registerViewModel.register(username, email, password).join()
                if (registerViewModel.status.throwable) registerViewModel.status.throwException()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Successfully registered!", Toast.LENGTH_SHORT)
                        .show()
                }
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    registerLayout.visibility = View.VISIBLE
                    trailingLoaderRegister.visibility = View.GONE
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etEmail, NonEmptyRule("Please, enter your email!"))
            .addField(etUsername, NonEmptyRule("Please, enter your username!"))
            .addField(
                etPassword,
                PasswordRule(
                    PasswordPattern.ALPHA_NUMERIC,
                    "Please, provide strong password with at least one letter and one number!"
                ),
                MinLengthRule(6, "Please, provide strong password with minimum 6 length!"),
                NonEmptyRule("Please, enter your password!")
            )
            .addField(
                etConfirmPassword,
                NonEmptyRule("Please, confirm your password!"),
                EqualRule(
                    etPassword.editText?.text.toString(),
                    "Please, confirm your password, they are not same!"
                )
            )
            .validate()
    }
}