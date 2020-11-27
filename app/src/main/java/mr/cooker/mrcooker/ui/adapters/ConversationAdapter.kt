/*
 * Created by Igor Stevanovic on 11/26/20 11:25 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/26/20 11:25 PM
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
import com.github.abdularis.civ.AvatarImageView
import kotlinx.android.synthetic.main.conversation_row.view.*
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Conversation
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import java.util.concurrent.TimeUnit

class ConversationAdapter() : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
            oldItem.conversationId == newItem.conversationId

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Conversation>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder =
        ConversationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.conversation_row, parent, false)
        )

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = differ.currentList[position]

        holder.itemView.apply {
            if (conversation.firstUserId == currentUser.uid) {
                tvConvUsername.text = conversation.secondUserId
                ivConvProfileImage.text = conversation.secondUserId.substring(0, 1)
                ivConvProfileImage.state = AvatarImageView.SHOW_INITIAL
            } else {
                tvConvUsername.text = conversation.firstUserId
                ivConvProfileImage.text = conversation.firstUserId.substring(0, 1)
                ivConvProfileImage.state = AvatarImageView.SHOW_INITIAL
            }

            val lastMessage = conversation.messages.last()
            tvConvLastMessage.text = lastMessage.text
            val timePassed = System.currentTimeMillis() - lastMessage.timestamp
            tvConvTimeOfLastMessage.text = calculateTime(timePassed)

            setOnClickListener {
                onItemClickListener?.let { it(conversation) }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((Conversation) -> Unit)? = null

    fun setOnItemClickListener(listener: (Conversation) -> Unit) {
        onItemClickListener = listener
    }

    private fun calculateTime(timePassed: Long): String =
        when {
            TimeUnit.MILLISECONDS.toSeconds(timePassed) < 60 -> "${
                TimeUnit.MILLISECONDS.toSeconds(
                    timePassed
                )
            } ago"
            TimeUnit.MILLISECONDS.toMinutes(timePassed) < 60 -> "${
                TimeUnit.MILLISECONDS.toMinutes(
                    timePassed
                )
            } ago"
            TimeUnit.MILLISECONDS.toHours(timePassed) < 24 -> "${
                TimeUnit.MILLISECONDS.toHours(
                    timePassed
                )
            } ago"
            TimeUnit.MILLISECONDS.toDays(timePassed) < 7 -> "${
                TimeUnit.MILLISECONDS.toDays(
                    timePassed
                )
            } ago"
            else -> {
                val weeks = TimeUnit.MILLISECONDS.toDays(timePassed) / 7
                "$weeks ago"
            }
        }
}