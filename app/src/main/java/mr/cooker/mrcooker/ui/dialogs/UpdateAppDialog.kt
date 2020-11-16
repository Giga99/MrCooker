package mr.cooker.mrcooker.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.update_app_dialog.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.other.Constants.PLAY_STORE_URI

class UpdateAppDialog(
    context: Context,
    private val appVersion: String
) : Dialog(context) {

    private val _navigation = MutableLiveData<Boolean>()
    val navigation: LiveData<Boolean> get() = _navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_app_dialog)

        _navigation.postValue(false)

        ivCancel.setOnClickListener {
            _navigation.postValue(true)
            dismiss()
        }

        btnUpdate.setOnClickListener {
            _navigation.postValue(true)
            startActivity(
                context,
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(PLAY_STORE_URI)
                ),
                null
            )
            dismiss()
        }
    }
}