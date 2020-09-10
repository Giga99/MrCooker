package mr.cooker.mrcooker.ui.fragments.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.form_validation.constant.PasswordPattern
import com.github.dhaval2404.form_validation.rule.EqualRule
import com.github.dhaval2404.form_validation.rule.MinLengthRule
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.rule.PasswordRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_recipe.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.data.db.entities.RecipeFirebase
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.ui.viewmodels.AllRecipesViewModel
import mr.cooker.mrcooker.ui.viewmodels.MyRecipesViewModel
import timber.log.Timber
import java.lang.Exception
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddRecipeFragment: Fragment(R.layout.fragment_add_recipe) {

    private val myRecipesViewModel: MyRecipesViewModel by viewModels()
    private val allRecipesViewModel: AllRecipesViewModel by viewModels()
    private var imgBitmap: Bitmap? = null
    private var imgUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvAdd.setOnClickListener {
            val name = etName.text.toString()
            val time = etTime.text.toString()
            val ingredients = etIngredients.text.toString()
            val instructions = etInstructions.text.toString()

            if(isValidForm() && imgBitmap != null) {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        val fileName = allRecipesViewModel.uploadImage(imgUri!!)
                        val imgUrl = allRecipesViewModel.getImageUrl(fileName)
                        val recipe = Recipe(imgBitmap!!, name, time.toInt(), ingredients, instructions)
                        val recipeFirebase = RecipeFirebase(imgUrl, name, time.toInt(), ingredients, instructions, currentUser.uid)
                        myRecipesViewModel.insertRecipe(recipe)
                        allRecipesViewModel.addRecipeToFirestore(recipeFirebase).join()

                        withContext(Dispatchers.Main) {
                            Snackbar.make(
                                requireActivity().findViewById(R.id.rootView),
                                "Recipe saved successfully!",
                                Snackbar.LENGTH_LONG
                            ).show()

                            findNavController().navigate(R.id.action_addRecipeFragment_to_allRecipesFragment)
                        }
                    }
                } catch (e : Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            } else if(imgBitmap == null){
                Toast.makeText(context, "Please select the image!", Toast.LENGTH_SHORT).show()
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
            findNavController().navigate(R.id.action_addRecipeFragment_to_allRecipesFragment)
        }
    }

    // Picked image and now saving it
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode) {
            Activity.RESULT_OK -> {
                data?.let {
                    imgBitmap = BitmapFactory.decodeFile(ImagePicker.getFilePath(it))
                    Glide.with(this).load(imgBitmap).into(ivAddImage)
                    imgUri = it.data
                }
            }

            ImagePicker.RESULT_ERROR -> Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()

            else -> Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidForm(): Boolean {
        return FormValidator.getInstance()
            .addField(etName, NonEmptyRule("Please, enter recipe name!"))
            .addField(etTime, NonEmptyRule("Please, enter time for cooking!"))
            .addField(etIngredients, NonEmptyRule("Please, enter recipe ingredients!"))
            .addField(etInstructions, NonEmptyRule("Please, enter instructions for cooking!"))
            .validate()
    }
}