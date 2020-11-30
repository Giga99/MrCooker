/*
 * Created by Igor Stevanovic on 11/28/20 10:20 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/28/20 10:20 PM
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
import com.github.abdularis.civ.AvatarImageView
import kotlinx.android.synthetic.main.message_row_left.view.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Message
import mr.cooker.mrcooker.data.entities.User
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser

class MessageAdapter(
    private val user1: User,
    private val user2: User
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Message>) = differ.submitList(list)

    override fun getItemViewType(position: Int): Int =
        if(differ.currentList[position].senderId == currentUser.uid) 0
        else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder =
        if(viewType == 0) MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_row_right, parent, false))
        else MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_row_left, parent, false))

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = differ.currentList[position]

        holder.itemView.apply {
            if(user1.userId == message.senderId) {
                if (user1.profileImage == null) {
                    ivMessageProfile.text = user1.username.substring(0, 1)
                    ivMessageProfile.state = AvatarImageView.SHOW_INITIAL
                } else {
                    Glide.with(context)
                        .load(user1.profileImage)
                        .into(ivMessageProfile)
                    ivMessageProfile.state = AvatarImageView.SHOW_IMAGE
                }
            } else {
                if (user2.profileImage == null) {
                    ivMessageProfile.text = user2.username.substring(0, 1)
                    ivMessageProfile.state = AvatarImageView.SHOW_INITIAL
                } else {
                    Glide.with(context)
                        .load(user2.profileImage)
                        .into(ivMessageProfile)
                    ivMessageProfile.state = AvatarImageView.SHOW_IMAGE
                }
            }

            messageText.text = message.text
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}