package mr.cooker.mrcooker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recipe_row.view.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.db.entities.Recipe
import mr.cooker.mrcooker.other.Converters.Companion.toBitmap

class RecipeAdapter(
    val allRecipes: Boolean
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Recipe>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recipe_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = differ.currentList[position]

        holder.itemView.apply {
            tvName.text = recipe.name
            tvTime.text = "${recipe.timeToCook}min"
            if(allRecipes) Glide.with(context).load(recipe.imgUrl).into(ivBackground)
            else Glide.with(context).load(toBitmap(recipe.img)).into(ivBackground)
            setOnClickListener {
                onItemClickListener?.let { it(recipe, ivBackground) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Recipe, ImageView) -> Unit)? = null

    fun setOnItemClickListener(listener: (Recipe, ImageView) -> Unit) {
        onItemClickListener = listener
    }
}