package mr.cooker.mrcooker.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_main.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.Constants
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.NetworkUtils

class AuthenticationActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        auth = FirebaseAuth.getInstance()

        checkNetworkConnectivity()
    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser != null) {
            currentUser = auth.currentUser!!
            startActivity(Intent(this, MainActivity::class.java))
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

                    animate().alpha(1f).setStartDelay(Constants.ANIMATION_DURATION).setDuration(
                        Constants.ANIMATION_DURATION
                    )
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