/*
 * Created by Igor Stevanovic on 11/17/20 12:17 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/17/20 12:15 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_recipe.*
import kotlinx.coroutines.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.ui.adapters.ImageAdapter
import mr.cooker.mrcooker.ui.viewmodels.AddingViewModel
import java.lang.Exception
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddRecipeFragment : Fragment(R.layout.fragment_add_recipe) {
    private val addingViewModel: AddingViewModel by viewModels()
    private var imgUris = mutableListOf<Uri?>()
    private var imgBitmaps = mutableListOf<Bitmap?>()
    private var downloadUrls = mutableListOf<String>()

    private lateinit var imageAdapter: ImageAdapter

    private var showToEveryone = true
    private var lengthBefore = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        tvAdd.setOnClickListener {
            if (isValidForm() && imgUris.size > 1) {
                addRecipeLayout.visibility = View.GONE
                trailingLoaderAddRecipe.visibility = View.VISIBLE
                trailingLoaderAddRecipe.animate()

                val name = etName.editText?.text.toString()
                val time = etTime.editText?.text.toString()
                val ingredients = etIngredients.editText?.text.toString()
                val instructions = etInstructions.editText?.text.toString()

                upload(name, time, ingredients, instructions)

            } else if (imgUris.size == 1) {
                Toast.makeText(context, "Please select at least one image!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(context, "Please enter all the information!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        etIngredients.editText?.addTextChangedListener {
            if (it != null && it.length > lengthBefore) {
                if (etIngredients.editText?.text.toString().length == 1) {
                    etIngredients.editText?.setText("• ${etIngredients.editText?.text.toString()}")
                    etIngredients.editText?.setSelection(etIngredients.editText!!.text.length)
                }
                if (etIngredients.editText?.text.toString().endsWith("\n")) {
                    etIngredients.editText?.setText(
                        etIngredients.editText?.text.toString().replace("\n", "\n• ")
                    )
                    etIngredients.editText?.setText(
                        etIngredients.editText?.text.toString().replace("• •", "•")
                    )
                    etIngredients.editText?.setSelection(etIngredients.editText!!.text.length)
                }

                lengthBefore = it.length
            }
        }

        // ImagePicker
        imageAdapter.setOnItemClickListener {
            if (imgUris.size < 6) ImagePicker.with(this)
                .cropSquare().compress(1024)
                .maxResultSize(1080, 1080).start()
            else Toast.makeText(
                context,
                "You reached your maximum of 5 images!",
                Toast.LENGTH_SHORT
            ).show()
        }

        show.setOnClickListener {
            show.isChecked = !showToEveryone
            showToEveryone = !showToEveryone
        }

        tvCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() = rvImages.apply {
        imageAdapter = ImageAdapter()
        adapter = imageAdapter
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = RecyclerView.HORIZONTAL
        layoutManager = manager

        imgUris.add(null)
        imgBitmaps.add(null)
        imageAdapter.submitList(imgBitmaps)
    }

    private fun upload(name: String, time: String, ingredients: String, instructions: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (imgUri in imgUris) {
                    if (imgUri == null) continue
                    val downloadUrl = addingViewModel.uploadImage(imgUri)
                    if (addingViewModel.status.throwable) addingViewModel.status.throwException()
                    downloadUrls.add(downloadUrl.toString())
                }
                val recipe = Recipe(
                    downloadUrls.first(),
                    name,
                    time.toInt(),
                    ingredients,
                    instructions,
                    numOfFavorites = 0,
                    showToEveryone,
                    Calendar.getInstance().timeInMillis,
                    currentUser.uid,
                    downloadUrls
                )

                addingViewModel.uploadRecipe(recipe).join()

                withContext(Dispatchers.Main) {
                    Snackbar.make(
                        requireActivity().findViewById(R.id.rootView),
                        "Recipe saved successfully!",
                        Snackbar.LENGTH_LONG
                    ).show()

                    //findNavController().popBackStack()
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
                imgUris.add(data?.data)
                imgBitmaps.add(BitmapFactory.decodeFile(ImagePicker.getFilePath(data)!!))
                imageAdapter.submitList(imgBitmaps)
                imageAdapter.notifyDataSetChanged()
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