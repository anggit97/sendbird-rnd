package com.example.sendbirdcore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sendbirdcore.adapter.ChatAdapter
import com.example.sendbirdcore.adapter.ChatClickListener
import com.example.sendbirdcore.adapter.UserAdapter
import com.example.sendbirdcore.databinding.ActivityListChatBinding
import com.example.sendbirdcore.model.Chat
import com.example.sendbirdcore.model.Verified
import com.example.sendbirdcore.seeder.UserSeeder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.SuperChannelFilter
import com.sendbird.android.channel.query.GroupChannelListQueryOrder
import com.sendbird.android.channel.query.PublicChannelFilter
import com.sendbird.android.collection.GroupChannelCollection
import com.sendbird.android.collection.GroupChannelContext
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.GroupChannelCollectionHandler
import com.sendbird.android.handler.GroupChannelsCallbackHandler
import com.sendbird.android.params.GroupChannelCollectionCreateParams
import com.sendbird.android.params.GroupChannelCreateParams
import com.sendbird.android.params.GroupChannelListQueryParams
import com.sendbird.android.user.User

class ListChatActivity : AppCompatActivity(), ChatClickListener {

    private lateinit var binding: ActivityListChatBinding
    private val adapter: ChatAdapter by lazy {
        ChatAdapter().also {
            it.setListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRv()
        initQueryGroup()
    }

    private fun setupRv() {
        with(binding) {
            rvListChat.layoutManager =
                LinearLayoutManager(this@ListChatActivity, LinearLayoutManager.VERTICAL, false)
            rvListChat.adapter = adapter
        }
    }


    private fun initQueryGroup() {
        // Create a GroupChannelListQuery instance in GroupChannelCollection.
        val query = GroupChannel.createMyGroupChannelListQuery(
            GroupChannelListQueryParams().apply {
                includeEmpty = false
                order = GroupChannelListQueryOrder.CHRONOLOGICAL
                publicChannelFilter =
                    PublicChannelFilter.PRIVATE            // Retrieve public group channels.
                superChannelFilter =
                    SuperChannelFilter.NONSUPER_CHANNEL_ONLY  // Retrieve Supergroup channels.
                includeMetadata = true
                // You can add other params setters.
            }
        )
        val collection: GroupChannelCollection = SendbirdChat.createGroupChannelCollection(
            GroupChannelCollectionCreateParams(query)
        )

        collection.groupChannelCollectionHandler = object : GroupChannelCollectionHandler {
            override fun onChannelsAdded(
                context: GroupChannelContext,
                channels: List<GroupChannel>
            ) {
                //TODO("Not yet implemented")
            }

            override fun onChannelsDeleted(
                context: GroupChannelContext,
                deletedChannelUrls: List<String>
            ) {
                //TODO("Not yet implemented")
            }

            override fun onChannelsUpdated(
                context: GroupChannelContext,
                channels: List<GroupChannel>
            ) {
//                val result = channels.map {
//                    val currentUserId = SendbirdChat.currentUser?.userId
//                    val otherUser = it.members.filter { member -> member.userId != currentUserId }
//                    val isVerifiedUser =
//                        otherUser.firstOrNull()?.metaData?.get(MetaDataKey.IS_VERIFIED)
//                            ?: Verified.NOT_VERIFIED.value
//                    Chat(
//                        it.name,
//                        otherUser.map { it.nickname }
//                            .firstOrNull() ?: "-",
//                        it.lastMessage?.message ?: "-",
//                        otherUser.map { it.profileUrl }
//                            .firstOrNull() ?: "-",
//                        otherUser.filter { it.userId != currentUserId }.map { it.userId }
//                            .firstOrNull() ?: "-",
//                        it.unreadMessageCount,
//                        it.createdAt,
//                        isVerifiedUser,
//                        it.members
//                    )
//                }.filter { it.userId != SendbirdChat.currentUser?.userId }.toMutableList()
//                adapter.updateItems(result)
            }
        }

        collection.loadMore(handler = GroupChannelsCallbackHandler { list, e ->
            if (e != null) {
                // Error!
                return@GroupChannelsCallbackHandler
            }
            Log.d(TAG, "initQueryGroup: ")
            val currentUserId = SendbirdChat.currentUser?.userId
            val chatMapper = list?.map {
                val otherUser = it.members.filter { member -> member.userId != currentUserId }
                val isVerifiedUser =
                    otherUser.firstOrNull()?.metaData?.get(MetaDataKey.IS_VERIFIED)
                        ?: Verified.NOT_VERIFIED.value
                Chat(
                    it.name,
                    otherUser.map { it.nickname }
                        .firstOrNull() ?: "-",
                    it.lastMessage?.message ?: "-",
                    otherUser.map { it.profileUrl }
                        .firstOrNull() ?: "-",
                    otherUser.filter { it.userId != currentUserId }.map { it.userId }
                        .firstOrNull() ?: "-",
                    it.unreadMessageCount,
                    it.createdAt,
                    isVerifiedUser,
                    it.members
                )
            }?.toList() ?: emptyList()
//                ?.filter { it.userId != currentUserId }?.toList() ?: emptyList()

            adapter.setItems(chatMapper.filter { it.userId != currentUserId }.toMutableList())
        })
    }

    override fun onItemClick(chat: Chat) {
        Log.d(TAG, "onItemClick: ${chat.name}")
        SendbirdChat.currentUser?.userId?.let {
            val list = listOf(
                it,
                chat.member.
                filter { member -> it != member.userId }
                    .map { it.userId }.first()
            )
            createGroupChannel(list)
        }
    }

    private fun createGroupChannel(list: List<String>) {
        val params = GroupChannelCreateParams().apply {
            userIds = list
            channelUrl = list.sorted().joinToString("_")
            isDistinct = true
            customType = "dm"
        }
        GroupChannel.createChannel(params) { channel, e ->
            if (e != null) {
                // Handle error.
                Log.e(ListUserActivity.TAG, "createGroupChannel: ", e)
            }

            if (channel != null) {
                // A group channel with the specified configuration is successfully created.
                // Through the channel parameter of the callback handler,
                // you can get the group channel's data from the result object
                // that the Sendbird server has passed to the callback method.
                val channelUrl = channel.url
                Log.d(ListUserActivity.TAG, "createGroupChannel: SUCCESS")
                Log.d(ListUserActivity.TAG, "createGroupChannel: $channelUrl")
                val intent = Intent(this, ChatDetailActivity::class.java).apply {
                    putExtra(ChatDetailActivity.CHANNEL_URL, channelUrl)
                }
                startActivity(intent)
            }
        }
    }

    companion object {
        const val TAG = "ListChatActivity"
    }
}