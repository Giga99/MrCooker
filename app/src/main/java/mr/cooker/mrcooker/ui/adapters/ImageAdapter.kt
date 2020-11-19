/*
 * Created by Igor Stevanovic on 11/19/20 9:52 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/19/20 9:52 PM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.image_row.view.*
import mr.cooker.mrcooker.R

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Uri>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_row, parent, false))

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = differ.currentList[position]

        holder.itemView.apply {
            if(position == 0) image.setImageResource(R.drawable.ic_add_photo)
            else image.setImageURI(imageUri)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}