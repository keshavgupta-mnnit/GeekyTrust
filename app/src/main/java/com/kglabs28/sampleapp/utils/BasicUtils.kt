package com.kglabs28.sampleapp.utils

import android.util.Log
import com.kglabs28.sampleapp.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object BasicUtils {
    fun parseTimestampStringToLong(dateString: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = format.parse(dateString)
            date?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    fun parseTimestampLongToString(timestamp: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(Date(timestamp))
    }

    fun isNetworkConnected(): Boolean {
        return true
    }

    fun log(tag:String,message: String) {
        if(BuildConfig.DEBUG) Log.d(tag,message)
    }
}