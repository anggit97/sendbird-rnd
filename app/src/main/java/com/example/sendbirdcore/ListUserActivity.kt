package com.example.sendbirdcore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sendbirdcore.adapter.OnItemClickListener
import com.example.sendbirdcore.adapter.UserAdapter
import com.example.sendbirdcore.databinding.ActivityListUserBinding
import com.example.sendbirdcore.model.User
import com.example.sendbirdcore.seeder.UserSeeder
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.params.GroupChannelCreateParams

class ListUserActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityListUserBinding
    private val adapter: UserAdapter by lazy {
        UserAdapter().also {
            val currentUserId = SendbirdChat.currentUser?.userId
            it.setItems(
                UserSeeder.getUserList().filter { it.id != currentUserId }
                    .toMutableList()
            )
            it.setListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRv()
    }

    private fun setupRv() {
        with(binding) {
            rvListChat.layoutManager =
                LinearLayoutManager(this@ListUserActivity, LinearLayoutManager.VERTICAL, false)
            rvListChat.adapter = adapter
        }
    }

    override fun onItemClick(user: User) {
        SendbirdChat.currentUser?.userId?.let {
            val list = listOf(it, user.id)
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
                Log.e(TAG, "createGroupChannel: ", e)
            }

            if (channel != null) {
                // A group channel with the specified configuration is successfully created.
                // Through the channel parameter of the callback handler,
                // you can get the group channel's data from the result object
                // that the Sendbird server has passed to the callback method.
                val channelUrl = channel.url
                Log.d(TAG, "createGroupChannel: SUCCESS")
                Log.d(TAG, "createGroupChannel: $channelUrl")
                val intent = Intent(this, ChatDetailActivity::class.java).apply {
                    putExtra(ChatDetailActivity.CHANNEL_URL, channelUrl)
                }
                startActivity(intent)
            }
        }
    }

    companion object {
        const val TAG = "ListUserActivity"
    }
}
