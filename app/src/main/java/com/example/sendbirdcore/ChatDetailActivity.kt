package com.example.sendbirdcore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sendbirdcore.adapter.ChatDetailAdapter
import com.example.sendbirdcore.databinding.ActivityChatDetailBinding
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.collection.GroupChannelContext
import com.sendbird.android.collection.MessageCollection
import com.sendbird.android.collection.MessageCollectionInitPolicy
import com.sendbird.android.collection.MessageContext
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.MessageCollectionHandler
import com.sendbird.android.handler.MessageCollectionInitHandler
import com.sendbird.android.handler.UserMessageHandler
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.params.MessageCollectionCreateParams
import com.sendbird.android.params.MessageListParams
import com.sendbird.android.params.UserMessageCreateParams
import com.sendbird.android.user.User

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var collection: MessageCollection
    private var channel: GroupChannel? = null
    var channelUrl: String? = null
    private var currentUser: User? = null
    private val adapter: ChatDetailAdapter by lazy {
        ChatDetailAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        channelUrl = intent.getStringExtra(CHANNEL_URL)
        setContentView(binding.root)
        currentUser = SendbirdChat.currentUser
        currentUser?.let { adapter.setCurrentUser(it) }
        createMessageCollection()
        clickListener()
        setupRv()
    }

    private fun setupRv() {
        binding.recyclerViewChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewChat.adapter = adapter
    }

    private fun clickListener() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            val userMessageParams = UserMessageCreateParams().apply {
                this.message = message
                this.data = "coin 1"
            }
            channel?.sendUserMessage(userMessageParams, handler = { message, e ->
                if (e != null) {
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    return@sendUserMessage
                }
                binding.etMessage.setText("")
            })
        }
    }

    private fun createMessageCollection() {
        channelUrl?.let {
            GroupChannel.getChannel(it) { channel, e ->
                if (e != null) {
                    // Handle error.
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
                // Through the channel parameter of the callback handler,
                // the group channel object identified with CHANNEL_URL is returned by the Sendbird server,
                // and you can get the group channel's data from the result object.

                this.channel = channel

                channel?.markAsRead(handler = { e ->
                    if (e != null) {
                        // Handle error.
                        Log.e(TAG, "createMessageCollection: ", e)
                        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                    Log.d(TAG, "createMessageCollection: read")
                })

                // Create a MessageListParams to be used in the MessageCollection.
                val params = MessageListParams().apply {
                    reverse = false
                }


                channel?.let {
                    collection = SendbirdChat.createMessageCollection(
                        MessageCollectionCreateParams(channel, params).apply {
                            startingPoint = Long.MAX_VALUE
                        }
                    )

                    listenCollection()

                    initialize()
                }
            }
        }
    }

    private fun listenCollection() {
        collection.messageCollectionHandler = object : MessageCollectionHandler {
            override fun onChannelDeleted(context: GroupChannelContext, channelUrl: String) {
                Log.d(TAG, "onChannelDeleted: $channelUrl")
            }

            override fun onChannelUpdated(context: GroupChannelContext, channel: GroupChannel) {
                Log.d(TAG, "onChannelUpdated: $channel")
            }

            override fun onHugeGapDetected() {
                Log.d(TAG, "onHugeGapDetected: ")
            }

            override fun onMessagesAdded(
                context: MessageContext,
                channel: GroupChannel,
                messages: List<BaseMessage>
            ) {
                Log.d(TAG, "onMessagesAdded: $messages")
                adapter.addItem(messages.last())
                binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
            }

            override fun onMessagesDeleted(
                context: MessageContext,
                channel: GroupChannel,
                messages: List<BaseMessage>
            ) {
                Log.d(TAG, "onMessagesDeleted: $messages")
            }

            override fun onMessagesUpdated(
                context: MessageContext,
                channel: GroupChannel,
                messages: List<BaseMessage>
            ) {
                Log.d(TAG, "onMessagesUpdated: $messages")
            }
        }
    }

    // Initialize messages from the startingPoint.
    private fun initialize() {
        collection.initialize(
            MessageCollectionInitPolicy.CACHE_AND_REPLACE_BY_API,
            object : MessageCollectionInitHandler {
                override fun onCacheResult(cachedList: List<BaseMessage>?, e: SendbirdException?) {
                    // Messages are retrieved from the local cache.
                    // They can be outdated or far from the startingPoint.
                    Log.d(TAG, "onCacheResult: $cachedList")
                    cachedList?.let {
                        adapter.setItems(it)
                        binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                }

                override fun onApiResult(apiResultList: List<BaseMessage>?, e: SendbirdException?) {
                    // Messages are retrieved through API calls from the Sendbird server.
                    // According to MessageCollectionInitPolicy.CACHE_AND_REPLACE_BY_API,
                    // the existing data source needs to be cleared
                    // before adding retrieved messages to the local cache.
                    Log.d(TAG, "onApiResult: $apiResultList")
                    apiResultList?.let {
                        adapter.setItems(it)
                        binding.recyclerViewChat.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
        )
    }

    companion object {
        const val CHANNEL_URL = "channel_url"
        const val TAG = "ChatDetailActivity"
    }
}