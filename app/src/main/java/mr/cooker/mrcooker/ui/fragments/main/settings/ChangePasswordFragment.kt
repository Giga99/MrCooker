package mr.cooker.mrcooker.ui.fragments.main.settings

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
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_change_password.btnCancel
import kotlinx.android.synthetic.main.fragment_change_password.btnSubmit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.viewmodels.EditAccountViewModel

@AndroidEntryPoint
class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private val editAccountViewModel: EditAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        btnSubmit.setOnClickListener {
            if (isValidForm()) {
                changePasswordLayout.visibility = View.GONE
                trailingLoaderChangePassword.visibility = View.VISIBLE
                trailingLoaderChangePassword.animate()
                changePassword()
            }
        }
    }

    private fun changePassword() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val email = etEmail.text.toString()
            val oldPassword = etOldPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            editAccountViewModel.changePassword(email, oldPassword, newPassword).join()
            if (editAccountViewModel.status.throwable) editAccountViewModel.status.throwException()
            withContext(Dispatchers.Main) {
                changePasswordLayout.visibility = View.VISIBLE
                trailingLoaderChangePassword.visibility = View.GONE
            }
            findNavController().navigate(R.id.action_changePasswordFragment_to_settingsFragment)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                changePasswordLayout.visibility = View.VISIBLE
                trailingLoaderChangePassword.visibility = View.GONE
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etEmail, NonEmptyRule("Please, enter your email!"))
            .addField(etOldPassword, NonEmptyRule("Please, enter your current password!"))
            .addField(
                etNewPassword,
                PasswordRule(
                    PasswordPattern.ALPHA_NUMERIC,
                    "Please, provide strong password with at least one letter and one number!"
                ),
                MinLengthRule(6, "Please, provide strong password with minimum 6 length!"),
                NonEmptyRule("Please, enter your password!")
            )
            .addField(
                etConfirmNewPassword,
                NonEmptyRule("Please, confirm your password!"),
                EqualRule(
                    etNewPassword.text.toString(),
                    "Please, confirm your password, they are not same!"
                )
            )
            .validate()
    }
}