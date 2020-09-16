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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.ui.activities.MainActivity
import mr.cooker.mrcooker.ui.viewmodels.LoginViewModel
import java.lang.Exception

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener {
            if(isValidForm()) {
                loginUser()
            }
        }

        tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginUser() {
        loginLayout.visibility = View.GONE
        trailingLoaderLogin.visibility = View.VISIBLE
        trailingLoaderLogin.animate()

        val email = etLoginEmail.text.toString()
        val password = etLoginPassword.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                loginViewModel.login(email, password).join()
                withContext(Dispatchers.Main) {
                    loginLayout.visibility = View.VISIBLE
                    trailingLoaderLogin.visibility = View.GONE
                }
                startActivity(Intent(requireContext(), MainActivity::class.java))
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loginLayout.visibility = View.VISIBLE
                    trailingLoaderLogin.visibility = View.GONE
                }
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
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