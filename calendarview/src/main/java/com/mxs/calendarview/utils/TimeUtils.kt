package com.mxs.calendarview.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author lijie
 * @date 2019-12-19 15:04
 * @description 时间工具类
 */
class TimeUtils {

    companion object {
        /**
         * 日期格式字符串转换成时间戳
         *
         * @param date   字符串日期
         * @param format 如：yyyy-MM-dd HH:mm:ss
         * @return
         */
        fun date2TimeStamp(date: String, format: String): Long {
            try {
                val sdf = SimpleDateFormat(format, Locale.CHINA)
                return sdf.parse(date).time
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return 0
        }

        /**
         * 获取当天零点时间戳 单位秒
         *
         * @return
         */
        fun getCurDateTime(): Long {
            val timeZone = TimeZone.getTimeZone("GMT+8")
            val calendar = Calendar.getInstance(timeZone)
            calendar.time = Date()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            return calendar.timeInMillis / 1000
        }

        /**
         * 时间戳差值转为天数差
         *
         * @param diffTime
         * @return
         */
        fun diffTime2diffDay(diffTime: Long): Int {
            return (diffTime / 1000 / 3600 / 24).toInt()
        }
    }
}