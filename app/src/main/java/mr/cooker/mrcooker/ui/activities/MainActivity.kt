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

package mr.cooker.mrcooker.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mobapphome.androidappupdater.tools.AAUpdaterController
import com.shreyaspatil.MaterialDialog.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.SmartRating
import mr.cooker.mrcooker.other.*
import mr.cooker.mrcooker.other.Constants.ANIMATION_DURATION
import mr.cooker.mrcooker.other.Constants.PLAY_STORE_URI
import mr.cooker.mrcooker.ui.dialogs.SmartRatingDialog
import mr.cooker.mrcooker.ui.viewmodels.AppInfoViewModel
import mr.cooker.mrcooker.ui.viewmodels.SignOutViewModel
import mr.cooker.mrcooker.ui.viewmodels.SmartRatingViewModel
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val signOutViewModel: SignOutViewModel by viewModels()
    private val smartRatingViewModel: SmartRatingViewModel by viewModels()
    private val appInfoViewModel: AppInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        setSupportActionBar(toolbar)

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        fab.setOnClickListener {
            when (navHostFragment.findNavController().currentDestination?.id) {
                R.id.allRecipesFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_allRecipesFragment_to_addRecipeFragment)

                R.id.conversationsFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_conversationsFragment_to_addRecipeFragment)

                R.id.userRecipesFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_userRecipesFragment_to_addRecipeFragment)

                R.id.profileFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_profileFragment_to_addRecipeFragment)

                R.id.settingsFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_settingsFragment_to_addRecipeFragment)
                R.id.favoriteRecipesFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_favoriteRecipesFragment_to_addRecipeFragment)

                R.id.otherProfileFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_otherProfileFragment_to_addRecipeFragment)
            }
        }

        ivFavorites.setOnClickListener {
            when (navHostFragment.findNavController().currentDestination?.id) {
                R.id.allRecipesFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_allRecipesFragment_to_favoriteRecipesFragment)

                R.id.conversationsFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_conversationsFragment_to_favoriteRecipesFragment)

                R.id.userRecipesFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_userRecipesFragment_to_favoriteRecipesFragment)

                R.id.profileFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_profileFragment_to_favoriteRecipesFragment)

                R.id.settingsFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_settingsFragment_to_favoriteRecipesFragment)

                R.id.otherProfileFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_otherProfileFragment_to_favoriteRecipesFragment)
            }
        }

        checkNetworkConnectivity()

        appUpdate()

        smartRating()
    }

    private fun appUpdate() = CoroutineScope(Dispatchers.IO).launch {
        try {
            AAUpdaterController.init(this@MainActivity, null, appInfoViewModel, false)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun smartRating() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            val lastRatedVersion = getLastVersionRated()
            if (version != lastRatedVersion) {
                setCountDaysPassed(true)
                val smartRatingTracker = smartRatingViewModel.getSmartRatingTracker()
                if (smartRatingViewModel.status.throwable) smartRatingViewModel.status.throwException()

                if (smartRatingTracker!!.daysPassed == 5) showSmartRatingDialog(version)
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun showSmartRatingDialog(appVersion: String) = withContext(Dispatchers.Main) {
        try {
            val smartRatingDialog = SmartRatingDialog(this@MainActivity, appVersion)
            smartRatingDialog.show()

            smartRatingDialog.rating.observe(this@MainActivity, {
                when {
                    it.numOfStars == null -> { /* NO-OP */
                    }
                    it.numOfStars < 4 -> {
                        setSmartRating(it)
                        setCountDaysPassed(false)
                        setLastVersionRated(appVersion)
                    }
                    else -> {
                        setCountDaysPassed(false)
                        setLastVersionRated(appVersion)
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(PLAY_STORE_URI)
                            )
                        )
                    }
                }

                smartRatingViewModel.resetDaysPassed()
                if (smartRatingViewModel.status.throwable) smartRatingViewModel.status.throwException()
            })
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSmartRating(rating: SmartRating) = CoroutineScope(Dispatchers.IO).launch {
        try {
            smartRatingViewModel.setSmartRating(rating)
            if (smartRatingViewModel.status.throwable) smartRatingViewModel.status.throwException()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setCountDaysPassed(count: Boolean) = CoroutineScope(Dispatchers.IO).launch {
        try {
            smartRatingViewModel.countDaysPassed(count)
            if (smartRatingViewModel.status.throwable) smartRatingViewModel.status.throwException()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Timber.e("back")
                navHostFragment.findNavController().popBackStack()
                true
            }

            R.id.signOut -> {
                MaterialDialog.Builder(this)
                    .setTitle(getString(R.string.exit_dialog_title))
                    .setMessage(getString(R.string.exit_dialog_message))
                    .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                        try {
                            dialogInterface.dismiss()
                            signOutViewModel.signOut()
                            startActivity(Intent(this, AuthenticationActivity::class.java))
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                    .show()

                true
            }

            R.id.nightMode -> {
                val mode =
                    if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_NO
                    ) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    }

                setNightMode(mode)

                // Change UI Mode
                AppCompatDelegate.setDefaultNightMode(mode)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkNetworkConnectivity() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this, { isConnected ->
            if (!isConnected) {
                tvNetworkStatus.text = getString(R.string.status_offline)
                networkStatusLayout.apply {
                    visibility = View.VISIBLE
                    setBackgroundColor(getColor(R.color.errorColor))
                }
            } else {
                tvNetworkStatus.text = getString(R.string.status_online)
                networkStatusLayout.apply {
                    setBackgroundColor(getColor(R.color.onlineColor))

                    animate().alpha(1f).setStartDelay(ANIMATION_DURATION)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                visibility = View.GONE
                            }
                        })
                }
            }
        })
    }

    override fun onBackPressed() {
        if (navHostFragment.findNavController().previousBackStackEntry == null) {
            MaterialDialog.Builder(this)
                .setTitle(getString(R.string.exit_dialog_title))
                .setMessage(getString(R.string.exit_dialog_message))
                .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                    try {
                        dialogInterface.dismiss()
                        signOutViewModel.signOut()
                        startActivity(Intent(this, AuthenticationActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .build()
                .show()
        } else {
            navHostFragment.findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AAUpdaterController.end()
    }
}