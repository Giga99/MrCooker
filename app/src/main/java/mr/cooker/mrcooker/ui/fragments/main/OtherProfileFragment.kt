/*
 * Created by Igor Stevanovic on 11/28/20 12:24 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/28/20 12:24 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.fragments.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.abdularis.civ.AvatarImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_other_profile.btnShowRecipes
import kotlinx.android.synthetic.main.fragment_other_profile.ivProfileImage
import kotlinx.android.synthetic.main.fragment_other_profile.profileLayout
import kotlinx.android.synthetic.main.fragment_other_profile.trailingLoaderProfile
import kotlinx.android.synthetic.main.fragment_other_profile.tvNumOfRecipes
import kotlinx.android.synthetic.main.fragment_other_profile.tvUsername
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.viewmodels.UserRecipesViewModel
import mr.cooker.mrcooker.ui.viewmodels.UserViewModel
import java.lang.Exception

@AndroidEntryPoint
class OtherProfileFragment : Fragment(R.layout.fragment_other_profile) {

    private val userRecipesViewModel: UserRecipesViewModel by activityViewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileLayout.visibility = View.GONE
        trailingLoaderProfile.visibility = View.VISIBLE
        trailingLoaderProfile.animate()

        userRecipesViewModel.setMyRecipesBoolean(false)
        userViewModel.userID.observe(viewLifecycleOwner, {
            userRecipesViewModel.setUserID(it)
        })

        getUserInfo()

        btnShowRecipes.setOnClickListener { findNavController().navigate(R.id.action_otherProfileFragment_to_userRecipesFragment) }
    }

    private fun getUserInfo() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val user = userViewModel.getUserInfo()
            if(userViewModel.status.throwable || user == null) userViewModel.status.throwException()

            userRecipesViewModel.userRecipes.observe(viewLifecycleOwner, {
                when (it) {
                    is Resource.Loading -> { /* NO-OP */
                    }

                    is Resource.Success -> {
                        tvUsername.text = user!!.username
                        tvNumOfRecipes.text = "${it.data.size}"
                        if (user.profileImage == null) {
                            ivProfileImage.text = user.username.substring(0, 1)
                            ivProfileImage.state = AvatarImageView.SHOW_INITIAL
                        } else {
                            Glide.with(this@OtherProfileFragment)
                                .load(user.profileImage)
                                .into(ivProfileImage)
                            ivProfileImage.state = AvatarImageView.SHOW_IMAGE
                        }

                        profileLayout.visibility = View.VISIBLE
                        trailingLoaderProfile.visibility = View.GONE
                    }

                    is Resource.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "An error has occurred:${it.throwable.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "An error has occurred:${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}