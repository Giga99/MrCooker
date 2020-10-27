package mr.cooker.mrcooker.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.res.Configuration
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
import com.shreyaspatil.MaterialDialog.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.Constants.ANIMATION_DURATION
import mr.cooker.mrcooker.other.NetworkUtils
import mr.cooker.mrcooker.other.getLastVersionRated
import mr.cooker.mrcooker.other.setLastVersionRated
import mr.cooker.mrcooker.other.setNightMode
import mr.cooker.mrcooker.ui.dialogs.SmartRatingDialog
import mr.cooker.mrcooker.ui.viewmodels.SignOutViewModel
import mr.cooker.mrcooker.ui.viewmodels.SmartRatingViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val signOutViewModel: SignOutViewModel by viewModels()
    private val smartRatingViewModel: SmartRatingViewModel by viewModels()

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

                R.id.myRecipesFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_myRecipesFragment_to_addRecipeFragment)

                R.id.profileFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_profileFragment_to_addRecipeFragment)

                R.id.settingsFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_settingsFragment_to_addRecipeFragment)
            }
        }

        ivFavorites.setOnClickListener {
            when (navHostFragment.findNavController().currentDestination?.id) {
                R.id.allRecipesFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_allRecipesFragment_to_favoriteRecipesFragment)

                R.id.myRecipesFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_myRecipesFragment_to_favoriteRecipesFragment)

                R.id.profileFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_profileFragment_to_favoriteRecipesFragment)

                R.id.settingsFragment ->
                    navHostFragment.findNavController()
                        .navigate(R.id.action_settingsFragment_to_favoriteRecipesFragment)
            }
        }

        checkNetworkConnectivity()

        smartRating()
    }

    private fun smartRating() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            val lastRatedVersion = getLastVersionRated()
            if (version != lastRatedVersion) {
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
                    it.numOfStars == null -> {
                        smartRatingViewModel.resetDaysPassed()
                    }
                    it.numOfStars < 4 -> {
                        smartRatingViewModel.setSmartRating(it)
                        if (smartRatingViewModel.status.throwable) smartRatingViewModel.status.throwException()
                        setLastVersionRated(appVersion)
                    }
                    else -> {
                        // TODO redirect to PlayStore
                        setLastVersionRated(appVersion)
                    }
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
}