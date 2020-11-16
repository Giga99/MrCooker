package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobapphome.androidappupdater.tools.IUpdateInfoResolver
import com.mobapphome.androidappupdater.tools.ProgramInfo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mr.cooker.mrcooker.data.repositories.MainRepository
import timber.log.Timber

class AppInfoViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel(), IUpdateInfoResolver {

    override fun resolveInfo(): ProgramInfo = runBlocking {
        var programInfo: ProgramInfo? = null
        viewModelScope.launch {
            programInfo = mainRepository.getAppInfo().copy(isRunMode = true)
        }.join()

        Timber.e(programInfo.toString())
        programInfo!!
    }
}