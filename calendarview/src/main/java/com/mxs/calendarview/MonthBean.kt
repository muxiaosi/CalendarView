package com.mxs.calendarview

/**
 * @author lijie
 * @date 2019-12-16 14:43
 * @description
 */
class MonthBean(
        /**
         * 属于哪年
         */
        val year: Int,
        /**
         * //哪一月
         */
        val month: Int,

        var dateList: List<DateBean> = arrayListOf()
)