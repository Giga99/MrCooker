package mr.cooker.mrcooker.ui.fragments.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader
import com.github.dhaval2404.form_validation.constant.PasswordPattern
import com.github.dhaval2404.form_validation.rule.*
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.BaseApplication
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.ui.viewmodels.RegisterViewModel
import java.lang.Exception

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRegister.setOnClickListener {
            if(isValidForm()) {
                registerUser()
            }
        }

        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun registerUser() {
        registerLayout.visibility = View.GONE
        trailingLoaderRegister.visibility = View.VISIBLE
        trailingLoaderRegister.animate()

        val username = etUsername.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        try {
            registerViewModel.register(username, email, password)
            registerLayout.visibility = View.VISIBLE
            trailingLoaderRegister.visibility = View.GONE
            Toast.makeText(requireContext(), "Successfully registered!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        } catch (e: Exception) {
            registerLayout.visibility = View.VISIBLE
            trailingLoaderRegister.visibility = View.GONE
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etEmail, NonEmptyRule("Please, enter your email!"))
            .addField(etUsername, NonEmptyRule("Please, enter your username!"))
            .addField(
                etPassword,
                PasswordRule(PasswordPattern.ALPHA_NUMERIC, "Please, provide strong password with at least one letter and one number!"),
                MinLengthRule(6, "Please, provide strong password with minimum 6 length!"),
                NonEmptyRule("Please, enter your password!"))
            .addField(
                etConfirmPassword,
                NonEmptyRule("Please, confirm your password!"),
                EqualRule(etPassword.text.toString(), "Please, confirm your password, they are not same!")
            )
            .validate()
    }
}