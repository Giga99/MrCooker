package mr.cooker.mrcooker.ui.fragments.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_recipe.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.other.Converters.Companion.toByteArray
import mr.cooker.mrcooker.ui.viewmodels.MyRecipesViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddRecipeFragment: Fragment(R.layout.fragment_add_recipe) {

    private val myRecipesViewModel: MyRecipesViewModel by viewModels()
    private var imgBitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvAdd.setOnClickListener {
            val name = etName.text.toString()
            val time = etTime.text.toString()
            val ingredients = etIngredients.text.toString()
            val instructions = etInstructions.text.toString()

            if(name.isNotEmpty() && time.isNotEmpty() && ingredients.isNotEmpty() && instructions.isNotEmpty() && imgBitmap != null) {
                val recipe = Recipe(toByteArray(imgBitmap!!), name, time.toInt(), ingredients, instructions)
                myRecipesViewModel.insertRecipe(recipe)

                Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    "Recipe saved successfully!",
                    Snackbar.LENGTH_LONG
                ).show()

                findNavController().navigate(R.id.action_addRecipeFragment_to_allRecipesFragment)
            } else if(imgBitmap == null){
                Toast.makeText(context, "Please select the image!", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(context, "Please enter all the information!", Toast.LENGTH_SHORT).show()
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
                imgBitmap = BitmapFactory.decodeFile(ImagePicker.getFilePath(data)!!)
                Glide.with(this).load(imgBitmap).into(ivAddImage)
            }

            ImagePicker.RESULT_ERROR -> Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()

            else -> Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}