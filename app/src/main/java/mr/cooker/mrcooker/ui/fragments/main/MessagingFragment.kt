/*
 * Created by Igor Stevanovic on 11/28/20 9:24 PM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/28/20 9:24 PM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.fragments.main

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_messaging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Conversation
import mr.cooker.mrcooker.data.entities.Message
import mr.cooker.mrcooker.other.FirebaseUtils.currentUser
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.adapters.MessageAdapter
import mr.cooker.mrcooker.ui.viewmodels.MessagingViewModel
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class MessagingFragment : Fragment(R.layout.fragment_messaging) {

    private val messagingViewModel: MessagingViewModel by activityViewModels()
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var conversation: Conversation

    private val timer = object : CountDownTimer(10000000, 1000) {
        override fun onTick(p0: Long) {
            refreshConversation()
            seeMessages()
        }

        override fun onFinish() {
            refreshConversation()
            seeMessages()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE
        activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)?.visibility = View.GONE
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.GONE

        messagingLayout.visibility = View.GONE
        trailingLoaderMessaging.visibility = View.VISIBLE
        trailingLoaderMessaging.animate()

        setupRecyclerView()

        seeMessages()

        ivSend.setOnClickListener {
            if (isValidForm()) {
                try {
                    val text = etMessage.text.toString()
                    val message = Message(currentUser.uid, Date(System.currentTimeMillis()), text, false)
                    conversation.messages.add(message)
                    messageAdapter.notifyDataSetChanged()
                    messagingViewModel.updateMessages(
                        conversation.messages,
                        conversation.conversationId
                    )
                    if (messagingViewModel.status.throwable) messagingViewModel.status.throwException()
                    etMessage.setText("")
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    conversation.messages.removeAt(conversation.messages.size - 1)
                    messageAdapter.notifyDataSetChanged()
                }
            }
        }

        timer.start()
    }

    private fun refreshConversation() = CoroutineScope(Dispatchers.IO).launch {
        val data = messagingViewModel.refreshConversation(conversation.conversationId)
        withContext(Dispatchers.Main) {
            when (data) {
                is Resource.Loading -> { /* NO-OP */
                }
                is Resource.Success -> {
                    conversation = data.data
                    messageAdapter.submitList(conversation.messages)
                    messageAdapter.notifyDataSetChanged()
                }
                is Resource.Failure -> {

                    Toast.makeText(
                        requireContext(),
                        "An error has occurred:${data.throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun seeMessages() {
        for (message in conversation.messages.reversed()) {
            if (message.senderId == currentUser.uid) break
            message.seen = true
            try {
                messagingViewModel.updateMessages(
                    conversation.messages,
                    conversation.conversationId
                )
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() = rvMessages.apply {
        conversation = messagingViewModel.getConversation()!!
        messageAdapter = MessageAdapter(conversation.firstUser!!, conversation.secondUser!!)
        adapter = messageAdapter
        layoutManager = LinearLayoutManager(requireContext()).apply {
            //reverseLayout = true
            stackFromEnd = true
        }

        messageAdapter.submitList(conversation.messages)
        messageAdapter.notifyDataSetChanged()

        messagingLayout.visibility = View.VISIBLE
        trailingLoaderMessaging.visibility = View.GONE
    }

    private fun isValidForm(): Boolean =
        FormValidator.getInstance()
            .addField(etMessage, NonEmptyRule("Please, enter your message!"))
            .validate()

    override fun onResume() {
        super.onResume()
        timer.start()
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}