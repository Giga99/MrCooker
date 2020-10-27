package mr.cooker.mrcooker.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import kotlinx.android.synthetic.main.smart_rating_dialog.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.SmartRating

class SmartRatingDialog(
    context: Context,
    private val appVersion: String
) : Dialog(context) {

    private val _rating = MutableLiveData<SmartRating>()
    val rating: LiveData<SmartRating> get() = _rating

    private var numOfStars = 0.0f
    private var review: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.smart_rating_dialog)

        ratingBar.setOnRatingBarChangeListener { ratingBar, fl, _ ->
            numOfStars = fl
            if (numOfStars < 4) etReview.visibility = View.VISIBLE
        }

        ivCancel.setOnClickListener {
            _rating.postValue(SmartRating(null, null, null))
            dismiss()
        }

        btnSubmit.setOnClickListener {
            when {
                numOfStars == 0.0f -> Toast.makeText(
                    context,
                    "Please set the number of stars!",
                    Toast.LENGTH_SHORT
                ).show()
                numOfStars < 4 -> {
                    if (isValidForm()) {
                        review = etReview.editText?.text.toString()
                        _rating.postValue(SmartRating(numOfStars, review, appVersion))
                        dismiss()
                    }
                }
                else -> {
                    _rating.postValue(SmartRating(numOfStars, null, null))
                    dismiss()
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etReview, NonEmptyRule("Please, enter your review"))
            .validate()
    }
}