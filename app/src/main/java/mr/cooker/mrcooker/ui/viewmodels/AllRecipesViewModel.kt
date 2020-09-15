package mr.cooker.mrcooker.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.data.repositories.MainRepository
import mr.cooker.mrcooker.other.Resource
import java.lang.Exception

class AllRecipesViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

}