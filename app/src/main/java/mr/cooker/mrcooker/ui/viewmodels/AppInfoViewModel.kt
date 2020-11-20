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

package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobapphome.androidappupdater.tools.IUpdateInfoResolver
import com.mobapphome.androidappupdater.tools.ProgramInfo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mr.cooker.mrcooker.data.repositories.MainRepository

class AppInfoViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel(), IUpdateInfoResolver {

    override fun resolveInfo(): ProgramInfo = runBlocking {
        var programInfo: ProgramInfo? = null
        viewModelScope.launch {
            programInfo = mainRepository.getAppInfo().copy(isRunMode = true)
        }.join()

        programInfo!!
    }
}