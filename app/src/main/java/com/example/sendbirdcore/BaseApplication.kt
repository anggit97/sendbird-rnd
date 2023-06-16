package com.example.sendbirdcore

import android.app.Application
import android.util.Log
import com.sendbird.android.SendbirdChat
import com.sendbird.android.caching.LocalCacheConfig
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.InitParams

/**
 * Created by Anggit Prayogo on 13/06/23.
 */
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SendbirdChat.init(
            InitParams(AppConstant.APP_ID, applicationContext, useCaching = true),
            object : InitResultHandler {
                override fun onMigrationStarted() {
                    Log.i("Application", "Called when there's an update in Sendbird server.")
                }

                override fun onInitFailed(e: SendbirdException) {
                    Log.i(
                        "Application",
                        "Called when initialize failed. SDK will still operate properly as if useLocalCaching is set to false."
                    )
                }

                override fun onInitSucceed() {
                    Log.i("Application", "Called when initialization is completed.")
                }
            }
        )
    }
}