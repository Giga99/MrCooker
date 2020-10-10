package mr.cooker.mrcooker.ui.fragments.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_recipe.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.ui.viewmodels.AddingViewModel
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddRecipeFragment : Fragment(R.layout.fragment_add_recipe) {
    private val addingViewModel: AddingViewModel by viewModels()
    private var imgBitmap: Bitmap? = null
    private var imgUri: Uri? = null
    private var downloadUrl: Uri? = null

    private var lengthBefore = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvAdd.setOnClickListener {
            if (isValidForm() && imgBitmap != null) {
                addRecipeLayout.visibility = View.GONE
                trailingLoaderAddRecipe.visibility = View.VISIBLE
                trailingLoaderAddRecipe.animate()

                val name = etName.editText?.text.toString()
                val time = etTime.editText?.text.toString()
                val ingredients = etIngredients.editText?.text.toString()
                val instructions = etInstructions.editText?.text.toString()

                upload(name, time, ingredients, instructions)

            } else if (imgBitmap == null) {
                Toast.makeText(context, "Please select the image!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter all the information!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        etIngredients.editText?.addTextChangedListener {
            if (it != null) {
                if (it.length > lengthBefore) {
                    if (etIngredients.editText?.text.toString().length == 1) {
                        etIngredients.editText?.setText("• " + etIngredients.editText?.text.toString())
                        etIngredients.editText?.setSelection(etIngredients.editText!!.text.length)
                    }
                    if (etIngredients.editText?.text.toString().endsWith("\n")) {
                        etIngredients.editText?.setText(etIngredients.editText?.text.toString().replace("\n", "\n• "))
                        etIngredients.editText?.setText(etIngredients.editText?.text.toString().replace("• •", "•"))
                        etIngredients.editText?.setSelection(etIngredients.editText!!.text.length)
                    }
                }

                lengthBefore = it.length
            }
        }

        // ImagePicker
        ivAddImage.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        tvCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun upload(name: String, time: String, ingredients: String, instructions: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                downloadUrl = addingViewModel.uploadImage(imgUri!!)
                if (addingViewModel.status.throwable) addingViewModel.status.throwException()
                val recipe = Recipe(
                    downloadUrl.toString(),
                    name,
                    time.toInt(),
                    ingredients,
                    instructions,
                    Calendar.getInstance().timeInMillis,
                    currentUser.uid
                )

                addingViewModel.uploadRecipe(recipe).join()

                withContext(Dispatchers.Main) {
                    Snackbar.make(
                        requireActivity().findViewById(R.id.rootView),
                        "Recipe saved successfully!",
                        Snackbar.LENGTH_LONG
                    ).show()

                    addRecipeLayout.visibility = View.VISIBLE
                    trailingLoaderAddRecipe.visibility = View.GONE

                    findNavController().navigate(R.id.action_addRecipeFragment_to_allRecipesFragment)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addRecipeLayout.visibility = View.VISIBLE
                    trailingLoaderAddRecipe.visibility = View.GONE
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    // Picked image and now saving it
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                imgUri = data?.data
                imgBitmap = BitmapFactory.decodeFile(ImagePicker.getFilePath(data)!!)
                Glide.with(this).load(imgBitmap).into(ivAddImage)
            }

            ImagePicker.RESULT_ERROR -> Toast.makeText(
                context,
                ImagePicker.getError(data),
                Toast.LENGTH_SHORT
            ).show()

            else -> Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etName, NonEmptyRule("Please, enter a recipe name!"))
            .addField(etTime, NonEmptyRule("Please, enter a time to cook!"))
            .addField(etIngredients, NonEmptyRule("Please, enter ingredients!"))
            .addField(etInstructions, NonEmptyRule("Please, enter instructions!"))
            .validate()
    }
}