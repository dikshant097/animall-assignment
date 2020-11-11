package com.animall.assignemnt.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import androidx.core.app.ActivityCompat
import com.animall.assignemnt.model.SMS
import java.util.*
import kotlin.collections.ArrayList

object Utility {

    const val ONE_SEC_MS: Long = 1000
    const val ONE_MIN_MS = 60 * ONE_SEC_MS
    const val ONE_HOUR_MS = 60 * ONE_MIN_MS
    const val ONE_DAY_MS = 24 * ONE_HOUR_MS
    const val ONE_WEEK_MS = 7 * ONE_DAY_MS
    const val ONE_MONTH_MS = 4 * ONE_WEEK_MS

    @SuppressLint("Recycle")
    fun getSms(context: Context, offset: Int, timeStamp: Long): ArrayList<SMS> {

        val PROJECTION_BUCKET: Array<String> = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.DATE,
            Telephony.Sms.BODY,
            Telephony.Sms.ADDRESS
        );

        val BUCKET_ORDER_BY = Telephony.Sms.DATE + " DESC limit 10 offset $offset";

        val SMSList = ArrayList<SMS>()
        val cursor: Cursor =
            context.contentResolver
                .query(
                    Uri.parse("content://sms/inbox"),
                    PROJECTION_BUCKET,
                    "date >= $timeStamp",
                    null,
                    BUCKET_ORDER_BY
                )
                ?: return SMSList

        try {
            while (cursor.moveToNext()) {

                SMSList.add(
                    SMS(
                        cursor.getString(0),
                        Date(cursor.getLong(1)),
                        cursor.getString(2),
                        cursor.getString(3)
                    )
                )
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            cursor.close()
        }

        return SMSList
    }

    fun isPermissionGranted(
        context: Context?,
        permission: String?
    ): Boolean {
        return if (context == null) {
            false
        } else try {
            ActivityCompat.checkSelfPermission(
                context,
                permission!!
            ) == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            e.stackTrace
            false
        }
    }

    fun requestPermission(activity: Activity?, requestCode: Int, permissions: Array<String>) {
        if (activity == null) {
            return
        }

        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun printDate(date: Date?): String? {
        if (date == null) return ""
        val out: String
        val diff = Date().time - date.time
        out = if (diff < ONE_MIN_MS) {
            "just now"
        } else if (diff < ONE_HOUR_MS) {
            val min = (diff / ONE_MIN_MS)
            min.toString() + "m ago"
        } else if (diff < ONE_DAY_MS) {
            val hours = (diff / ONE_HOUR_MS)
            hours.toString() + "h ago"
        } else if (diff < ONE_WEEK_MS) {
            val days = (diff / ONE_DAY_MS)
            days.toString() + "d ago"
        } else if (diff < ONE_MONTH_MS) {
            val days = (diff / ONE_WEEK_MS)
            days.toString() + "w ago"
        } else {
            "Received long time back"
        }
        return out
    }
}