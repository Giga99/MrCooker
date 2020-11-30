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

package mr.cooker.mrcooker.ui.fragments.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.abdularis.civ.AvatarImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.viewmodels.UserRecipesViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val userRecipesViewModel: UserRecipesViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileLayout.visibility = View.GONE
        trailingLoaderProfile.visibility = View.VISIBLE
        trailingLoaderProfile.animate()

        userRecipesViewModel.setMyRecipesBoolean(true)

        userRecipesViewModel.userRecipes.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> { /* NO-OP */
                }

                is Resource.Success -> {
                    tvUsername.text = currentUser.displayName
                    tvNumOfRecipes.text = "${it.data.size}"
                    if (currentUser.photoUrl == null) {
                        ivProfileImage.text = currentUser.displayName?.substring(0, 1)
                        ivProfileImage.state = AvatarImageView.SHOW_INITIAL
                    } else {
                        Glide.with(this).load(currentUser.photoUrl).into(ivProfileImage)
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

        btnShowRecipes.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_userRecipesFragment) }
    }
}