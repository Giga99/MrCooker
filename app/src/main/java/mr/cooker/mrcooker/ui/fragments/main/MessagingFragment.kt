/*
 * Created by Igor Stevanovic on 11/28/20 9:24 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/28/20 9:24 PM
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.ui.viewmodels.MessagingViewModel

@AndroidEntryPoint
class MessagingFragment : Fragment(R.layout.fragment_messaging) {

    private val messagingViewModel: MessagingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}