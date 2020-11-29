/*
 * Created by Igor Stevanovic on 11/26/20 12:40 AM
 * Copyright (c) 2020 MrCooker. All rights reserved.
 * Last modified 11/26/20 12:40 AM
 * Licensed under the GPL-3.0 License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mr.cooker.mrcooker.ui.fragments.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_conversations.*
import kotlinx.android.synthetic.main.fragment_conversations.swipeRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.cooker.mrcooker.R
import mr.cooker.mrcooker.data.entities.Conversation
import mr.cooker.mrcooker.other.Resource
import mr.cooker.mrcooker.ui.adapters.ConversationAdapter
import mr.cooker.mrcooker.ui.viewmodels.ConversationsViewModel
import mr.cooker.mrcooker.ui.viewmodels.MessagingViewModel

@AndroidEntryPoint
class ConversationsFragment : Fragment(R.layout.fragment_conversations) {

    private val conversationsViewModel: ConversationsViewModel by viewModels()
    private val messagingViewModel: MessagingViewModel by activityViewModels()
    private lateinit var conversationAdapter: ConversationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE
        activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)?.visibility = View.VISIBLE
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.visibility = View.VISIBLE

        setupRecyclerView()
        conversationsViewModel.allConversations.observe(viewLifecycleOwner, {
            observe(it)
        })

        conversationAdapter.setOnItemClickListener {
            showConversation(it)
        }

        swipeRefreshLayout.setOnRefreshListener {
            realtimeUpdate()
        }
    }

    private fun showConversation(conversation: Conversation) {
        messagingViewModel.setConversation(conversation)
        findNavController().navigate(R.id.action_conversationsFragment_to_messagingFragment)
    }

    private fun realtimeUpdate() = CoroutineScope(Dispatchers.IO).launch {
        val data = conversationsViewModel.getRealtimeConversations()
        withContext(Dispatchers.Main) {
            observe(data)
        }
    }

    private fun observe(it: Resource<List<Conversation>>) {
        when (it) {
            is Resource.Loading -> {
                swipeRefreshLayout?.isRefreshing = true
            }

            is Resource.Success -> {
                swipeRefreshLayout?.isRefreshing = false
                conversationAdapter.submitList(it.data)
                conversationAdapter.notifyDataSetChanged()
            }

            is Resource.Failure -> {
                Toast.makeText(
                    requireContext(),
                    "An error has occurred:${it.throwable.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupRecyclerView() = rvConversations.apply {
        conversationAdapter = ConversationAdapter()
        adapter = conversationAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()

        realtimeUpdate()
    }
}