package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import mr.cooker.mrcooker.data.repositories.MainRepository

class AllRecipesViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
}