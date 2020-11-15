package mr.cooker.mrcooker.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.update_app_dialog.*
import mr.cooker.mrcooker.R

class UpdateAppDialog(
    context: Context,
    private val appVersion: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_app_dialog)

        ivCancel.setOnClickListener {
            dismiss()
        }

        btnUpdate.setOnClickListener {
            startActivity(
                context,
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=mr.cooker.mrcooker")
                ),
                null
            )
        }
    }
}