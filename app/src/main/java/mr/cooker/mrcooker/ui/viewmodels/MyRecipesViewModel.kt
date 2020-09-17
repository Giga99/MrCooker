package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mr.cooker.mrcooker.data.repositories.MainRepository

@ExperimentalCoroutinesApi
class MyRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

}