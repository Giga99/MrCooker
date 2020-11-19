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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.image_row.view.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Recipe

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Bitmap?>() {
        override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Bitmap?>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_row, parent, false))

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageBitmap = differ.currentList[position]

        holder.itemView.apply {
            if(position == 0) image.setImageResource(R.drawable.ic_add_photo)
            else Glide.with(context).load(imageBitmap).into(image)

            setOnClickListener {
                onItemClickListener?.let { it(Unit) }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((Unit) -> Unit)? = null

    fun setOnItemClickListener(listener: (Unit) -> Unit) {
        onItemClickListener = listener
    }
}