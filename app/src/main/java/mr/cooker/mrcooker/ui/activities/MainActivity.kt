package mr.cooker.mrcooker.ui.activities


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.shreyaspatil.MaterialDialog.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.NetworkUtils
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        //bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP */ }

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.homeFragment -> bottomNavigationView.visibility = View.VISIBLE
                    R.id.addRecipeFragment -> bottomNavigationView.visibility = View.GONE
                }
            }

        checkNetworkConnectivity()
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

                    animate().alpha(1f).setStartDelay(1000L).setDuration(1000L)
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
        MaterialDialog.Builder(this)
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()
            .show()
    }
}