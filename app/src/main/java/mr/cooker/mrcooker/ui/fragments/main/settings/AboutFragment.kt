/*
 * Created by Igor Stevanovic on 12/1/20 12:50 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 12/1/20 12:50 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.fragments.main.settings

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_about.*
import mr.cooker.mrcooker.R

class AboutFragment : Fragment(R.layout.fragment_about) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        with(requireActivity()) {
            tvVersion.text = packageManager.getPackageInfo(packageName, 0).versionName

            (this as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

            findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
            findViewById<BottomAppBar>(R.id.bottomAppBar).visibility = View.GONE
            findViewById<FloatingActionButton>(R.id.fab).visibility = View.GONE
            findViewById<ImageView>(R.id.ivFavorites).visibility = View.GONE
        }
    }
}