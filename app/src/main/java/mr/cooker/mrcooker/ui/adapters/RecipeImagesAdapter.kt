/*
 * Created by Igor Stevanovic on 11/20/20 12:38 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/20/20 12:38 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recipe_image_view_pager_item.view.*
import mr.cooker.mrcooker.R


class RecipeImagesAdapter : RecyclerView.Adapter<RecipeImagesAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<String>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recipe_image_view_pager_item, parent, false)
        )

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(context).load(imageUrl).into(imageViewPager)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}