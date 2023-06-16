package com.example.sendbirdcore.utils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Anggit Prayogo on 15/06/23.
 */
object DateTimeUtils {
    fun convertEpochTime(epochTime: Long): String {
        val date = Date(epochTime)
        val format = SimpleDateFormat("HH:mm")
        return format.format(date)
    }
}