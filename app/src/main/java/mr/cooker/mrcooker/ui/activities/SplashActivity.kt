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

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.getNightMode
import mr.cooker.mrcooker.ui.viewmodels.LoginViewModel

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        MobileAds.initialize(this)

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

        checkPreviousLogin()
    }

    private fun checkPreviousLogin() = CoroutineScope(Dispatchers.IO).launch {
        try {
            if (loginViewModel.checkPrevLogging()) {
                withContext(Dispatchers.Main) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }, 1000)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(
                            Intent(
                                this@SplashActivity,
                                AuthenticationActivity::class.java
                            )
                        )
                        finish()
                    }, 1000)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@SplashActivity, e.message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
                finish()
            }
        }
    }
}