package mr.cooker.mrcooker.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.getNightMode
import mr.cooker.mrcooker.ui.viewmodels.LoginViewModel

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Fix for API 23 and lower
        if (getNightMode() == AppCompatDelegate.MODE_NIGHT_YES) splashImage.setImageDrawable(
            AppCompatResources.getDrawable(this, R.drawable.ic_splash_dark)
        )
        else splashImage.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_splash
            )
        )

        try {
            if (loginViewModel.checkPrevLogging()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 2000)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                    finish()
                }, 2000)
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}