package com.example.sendbirdcore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sendbirdcore.databinding.ActivityMainBinding
import com.example.sendbirdcore.model.Verified
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.DisconnectHandler
import com.sendbird.android.handler.MetaDataHandler
import com.sendbird.android.handler.SessionHandler
import com.sendbird.android.handler.SessionTokenRequester
import com.sendbird.android.params.GroupChannelCreateParams
import com.sendbird.android.params.UserUpdateParams
import com.sendbird.android.user.User

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickListener()
        listenSession()
        setupFirst()
    }

    private fun setupFirst() {
        binding.tvCurrentUserLoggedIn.text =
            "Current User Logged In: ${SendbirdChat.currentUser?.userId}"
    }

    private fun listenSession() {
        SendbirdChat.setSessionHandler(
            object : SessionHandler() {
                override fun onSessionTokenRequired(sessionTokenRequester: SessionTokenRequester) {
                    // A new session token is required in the SDK to refresh the session.
                    // Refresh the session token and pass it onto the SDK through sessionTokenRequester.onSuccess(NEW_TOKEN).
                    // If you do not want to refresh the session, pass on a null value through sessionTokenRequester.onSuccess(null).
                    // If any error occurred while refreshing the token, let the SDK know about it through sessionTokenRequester.onFail().
                    Log.d(TAG, "onSessionTokenRequired:")
                }

                override fun onSessionClosed() {
                    // The session refresh has been denied from the app.
                    // Client apps should guide the user to a login page to log in again.
                    Log.d(TAG, "onSessionClosed: ")
                }

                override fun onSessionRefreshed() {
                    // This is optional and no action is required.
                    // This is called when the session is refreshed.
                    Log.d(TAG, "onSessionRefreshed: ")
                }

                override fun onSessionError(sendbirdException: SendbirdException) {
                    // This is optional and no action is required.
                    // This is called when an error occurs during the session refresh.
                    sendbirdException.printStackTrace()
                    Log.d(TAG, "onSessionError: ${sendbirdException}")
                }
            }
        )
    }

    private fun onClickListener() {
        binding.btnConnectUser1.setOnClickListener {
            connectUser(AppConstant.USER_ID_1, onError = { e ->
                Log.e(TAG, "onCreate: ", e)
            }, onSuccess = { user ->
                Log.d(TAG, "onCreate: ${user.userId}")
                Log.d(TAG, "onCreate: SUCCESS")
                val metaDataUser = mapOf(Pair(MetaDataKey.IS_VERIFIED, Verified.NOT_VERIFIED.value))
                user.createMetaData(metaDataUser, handler = { metaDataMap, e ->
                    Log.d(TAG, "onClickListener: SUCCES METADATA")
                    val params = UserUpdateParams().apply {
                        nickname = "Anggit"
                        profileImageUrl =
                            "https://koanba-storage-stg.oss-cn-hongkong.aliyuncs.com/img/account/1631023794046.jpeg"
                    }
                    updateUserInfo(params)
                })
            })
        }

        binding.btnConnectUser2.setOnClickListener {
            connectUser(AppConstant.USER_ID_2, onError = { e ->
                Log.e(TAG, "onCreate: ", e)
            }, onSuccess = { user ->
                Log.d(TAG, "onCreate: ${user.userId}")
                Log.d(TAG, "onCreate: SUCCESS")
                val metaDataUser = mapOf(Pair(MetaDataKey.IS_VERIFIED, Verified.NOT_VERIFIED.value))
                user.createMetaData(metaDataUser, handler = { metaDataMap, e ->
                    Log.d(TAG, "onClickListener: SUCCES METADATA")
                    val params = UserUpdateParams().apply {
                        nickname = "Soju"
                        profileImageUrl =
                            "https://koanba-storage-test.oss-ap-southeast-5.aliyuncs.com/img/account/1685087937648.jpg"
                    }
                    updateUserInfo(params)
                })
            })
        }

        binding.btnConnectUser3.setOnClickListener {
            connectUser(AppConstant.USER_ID_3, onError = { e ->
                Log.e(TAG, "onCreate: ", e)
            }, onSuccess = { user ->
                Log.d(TAG, "onCreate: ${user.userId}")
                Log.d(TAG, "onCreate: SUCCESS")
                val metaDataUser = mapOf(Pair(MetaDataKey.IS_VERIFIED, Verified.VERIFIED.value))
                user.createMetaData(metaDataUser, handler = { metaDataMap, e ->
                    Log.d(TAG, "onClickListener: SUCCES METADATA")
                    val params = UserUpdateParams().apply {
                        nickname = "Yuya"
                        profileImageUrl =
                            "https://koanba-storage-test.oss-ap-southeast-5.aliyuncs.com/img/account/1686306823342.jpg"
                    }
                    updateUserInfo(params)
                })

            })
        }

        binding.btnDisconnect.setOnClickListener {
            SendbirdChat.disconnect(handler = {
                Log.d(TAG, "onCreate: DISCONNECTED")
                binding.tvCurrentUserLoggedIn.text = "Logout"
            })
        }

        binding.btnOpenListChat.setOnClickListener {
            startActivity(Intent(this, ListChatActivity::class.java))
        }

        binding.btnOpenListUser.setOnClickListener {
            startActivity(Intent(this, ListUserActivity::class.java))
        }

        binding.btnCreateChannel.setOnClickListener {
            createGroupChannel()
        }
    }

    private fun createGroupChannel() {
        val params = GroupChannelCreateParams().apply {
            name = "Channel User 1"
            coverUrl = "https://static.sendbird.com/sample/cover/cover_12.jpg"
            userIds = listOf(AppConstant.USER_ID_1, AppConstant.USER_ID_2)
            isDistinct = true
            customType = "${AppConstant.USER_ID_1}_${AppConstant.USER_ID_2}"
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
            }
        }
    }

    private fun updateUserInfo(params: UserUpdateParams) {
        SendbirdChat.updateCurrentUserInfo(params) { e ->
            if (e != null) {
                // Handle error.
                Log.e(TAG, "updateUserInfo: ", e)
            }

            // The current user's profile is successfully updated.
            // You could redraw the profile in a view in response to this operation.
            Log.d(TAG, "updateUserInfo: SUCCESS")
            binding.tvCurrentUserLoggedIn.text = params.nickname
        }
    }

    private fun connectUser(
        userId: String,
        onError: (e: SendbirdException?) -> Unit,
        onSuccess: (user: User) -> Unit
    ) {
        SendbirdChat.connect(userId) { user, e ->
            if (user == null) {
                // Handle error.
                onError(e)
                return@connect
            }

            if (e != null) {
                // Proceed in offline mode with the data stored in the local database.
                // Later, connection will be established automatically
                // and can be notified through ConnectionHandler.onReconnectSucceeded().
                onError(e)
                return@connect
            }

            // Proceed in online mode.
            onSuccess(user)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}