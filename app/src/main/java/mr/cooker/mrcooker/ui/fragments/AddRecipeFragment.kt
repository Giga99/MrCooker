package mr.cooker.mrcooker.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_recipe.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class AddRecipeFragment: Fragment(R.layout.fragment_add_recipe) {

    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvAdd.setOnClickListener {
            val name = etName.text.toString()
            val time = etTime.text.toString()
            val ingredients = etIngredients.text.toString()
            val instructions = etInstructions.text.toString()

            if(name.isNotEmpty() && time.isNotEmpty() && ingredients.isNotEmpty() && instructions.isNotEmpty()) {
                val recipe = Recipe(name, time.toInt(), ingredients, instructions)
                viewModel.insertRecipe(recipe)

                Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    "Recipe saved successfully!",
                    Snackbar.LENGTH_LONG
                ).show()

                findNavController().navigate(R.id.action_addRecipeFragment_to_homeFragment)
            } else {
                Toast.makeText(context, "Please enter all the information!", Toast.LENGTH_SHORT).show()
            }
        }

        tvCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addRecipeFragment_to_homeFragment)
        }
    }
}