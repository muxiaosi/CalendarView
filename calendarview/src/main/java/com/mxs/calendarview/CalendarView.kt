package com.mxs.calendarview

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import com.mxs.calendarview.utils.TimeUtils
import kotlinx.android.synthetic.main.calender_view.view.*
import java.util.*
import kotlin.math.abs

/**
 * @author lijie
 * @date 2019-12-16 12:09
 * @description 日历
 */
class CalendarView(val mContext: Context, attrs: AttributeSet?) : LinearLayout(mContext, attrs) {

    /**
     * 最多显示月
     */
    private var mMaxMonth: Int = 0
    /**
     * 最多能选择跨度区间
     */
    private var mMaxRange: Int = -1

    /**
     * 开始时间
     */
    var mStartDateBean: DateBean? = null
    /**
     * 结束时间
     */
    var mEndDateBean: DateBean? = null
    /**
     * 日期数据
     */
    private val mList = arrayListOf<DateBean>()

    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CalendarAdapter(mContext, mList)
    }

    private var mDateSelectedCallBack : DateSelectCallBack? = null

    /**
     * 选择日期回调
     */
    fun setDateSelectedCallBackListener(listener : DateSelectCallBack){
        mDateSelectedCallBack = listener
    }

    init {
        LayoutInflater.from(mContext).inflate(R.layout.calender_view, this, true)
        if (attrs != null) {
            val attributes = mContext.obtainStyledAttributes(attrs, R.styleable.CalendarView)
            val hasWeek = attributes.getBoolean(R.styleable.CalendarView_show_week, true)
            mMaxMonth = attributes.getInt(R.styleable.CalendarView_max_month, 3)
            mMaxRange = attributes.getInt(R.styleable.CalendarView_max_range, 3)
            if (hasWeek) {
                initWeekView()
            }
            attributes.recycle()
        }

        initData()

        val layoutManager = GridLayoutManager(mContext, 7)
        rv_calender_view.layoutManager = layoutManager
        rv_calender_view.adapter = mAdapter
        rv_calender_view.clipChildren = true
        rv_calender_view.clipToPadding = true
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                //设置每行item个数，若是title则占7个位置，若是空白或日期则占一个位置
                return if (mList[position].type == DateBean.TYPE_DATE_TITLE) {
                    7
                } else {
                    1
                }
            }
        }

        mAdapter.setOnClickDayListener(object : CalendarAdapter.OnClickDayListener {
            override fun onClickDay(view: View, bean: DateBean, position: Int) {
                //点击日期
                when (getSelectDayCount()) {
                    0 -> {
                        //尚未选择日期
                        clearAndSetStartDate(bean)
                    }
                    1 -> {
                        //已选择一天
                        val firstBean = getFirstSelectDay()
                        if (isSameDay(firstBean, bean)) {
                            //同一天则取消选择
                            mStartDateBean = null
                            firstBean?.isChooseDay = false
                            firstBean?.intervalType = 0
                            //暴露给外面回调
                            mDateSelectedCallBack?.onDateSelect(false)
                            mAdapter.notifyItemChanged(position)
                        } else {
                            when {
                                checkChooseDate(firstBean, bean) == 1 -> {
                                    //第一次选择之后的一天
                                    bean.isChooseDay = true
                                    refreshChooseUi(firstBean, bean)
                                }
                                checkChooseDate(firstBean, bean) == 0 -> {
                                    //第一次选择之前的一天
                                    bean.isChooseDay = true
                                    refreshChooseUi(bean, firstBean)
                                }
                                else -> Toast.makeText(mContext,String.format(mContext.getString(R.string.date_max_range), mMaxRange),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> {
                        //已存在区间
                        clearAndSetStartDate(bean)
                    }
                }
            }
        })

        rv_calender_view.addItemDecoration(CalendarDateDecoration(mContext, object : CalendarDateDecoration.ChooseCallback {
            override fun getGroupId(position: Int): String {
                if (position == -1) {
                    return ""
                }
                //返回年月栏数据，如2019年1月
                return if (position < mList.size) {
                    mList[position].groupName
                } else {
                    ""
                }
            }

        }))

        //滑动到当前月，即最后一项
        rv_calender_view.scrollToPosition(mList.size - 1)

    }

    private fun getFirstSelectDay(): DateBean? {
        for (bean in mList) {
            if (bean.type == DateBean.TYPE_DATE_NORMAL && bean.isChooseDay) {
                return bean
            }
        }
        return null
    }

    /**
     * 选择完起始、结束后更新UI
     */
    private fun refreshChooseUi(startBean: DateBean?, endBean: DateBean?) {
        for (bean in mList) {
            if (bean.type == DateBean.TYPE_DATE_NORMAL) {
                //调出日期
                if (bean.isChooseDay) {
                    if (isSameDay(startBean, bean)) {
                        //第一天
                        bean.intervalType = DateBean.TYPE_INTERVAL_START
                        mStartDateBean = bean
                    } else if (isSameDay(endBean, bean)) {
                        //最后一天
                        bean.intervalType = DateBean.TYPE_INTERVAL_END
                        mEndDateBean = endBean
                    }
                } else if (isInRange(startBean, endBean, bean)) {
                    //在两者之间
                    bean.isChooseDay = true
                    bean.intervalType = DateBean.TYPE_INTERVAL_MIDDLE
                }
            }
        }
        mDateSelectedCallBack?.onDateSelect(true)
        mAdapter.notifyDataSetChanged()
    }

    /**
     * 判断bean是否在start-end区间
     */
    private fun isInRange(startBean: DateBean?, endBean: DateBean?, bean: DateBean?): Boolean {
        if (null == startBean || null == endBean || null == bean) {
            return false
        }
        return checkChooseDate(startBean, bean) == 1 && checkChooseDate(bean, endBean) == 1
    }

    /**
     * 判断bean和firstBean日期前后
     * -1：无效或超出最大范围或同一天
     * 0: bean在firstBean之前
     * 1：bean在firstBean之后
     */
    private fun checkChooseDate(firstBean: DateBean?, bean: DateBean?): Int {
        if (firstBean == null || bean == null || isSameDay(firstBean, bean)) {
            return -1
        }
        val firstLongTime = TimeUtils.date2TimeStamp(firstBean.dateToString(), "yyyy-MM-dd")
        val selectLongTime = TimeUtils.date2TimeStamp(bean.dateToString(), "yyyy-MM-dd")
        val diffLongTime = selectLongTime - firstLongTime
        if (TimeUtils.diffTime2diffDay(abs(diffLongTime)) > mMaxRange) {
            return -1
        }
        return if (diffLongTime > 0) {
            1
        } else {
            0
        }
    }

    /**
     * 清除选中状态并设置起点
     */
    private fun clearAndSetStartDate(startDate: DateBean) {
        mStartDateBean = null
        mEndDateBean = null
        for (bean in mList) {
            if (bean.type == DateBean.TYPE_DATE_NORMAL && bean.isChooseDay) {
                //清除旧的选中区域
                bean.isChooseDay = false
                bean.intervalType = 0
            }
            if (isSameDay(bean, startDate)) {
                mStartDateBean = bean
                bean.isChooseDay = true
                bean.intervalType = DateBean.TYPE_INTERVAL_START
            }
        }
        mDateSelectedCallBack?.onDateSelect(true)
        mAdapter.notifyDataSetChanged()
    }

    /**
     * 判断是否同一天
     */
    private fun isSameDay(date1: DateBean?, date2: DateBean?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        }
        return date1.year == date2.year && date1.month == date2.month && date1.date == date2.date
    }

    /**
     * 获取选择的日期个数
     */
    private fun getSelectDayCount(): Int {
        val curList = Stream.of(mList)
                .filter { bean ->
                    bean.isChooseDay
                }
                .collect(Collectors.toList())
        return curList.size
    }

    /**
     * 初始化日期数据
     */
    private fun initData() {
        val monthList = arrayListOf<MonthBean>()
        //设置月份
        getMonthList(monthList)
        //获取日期
        getDateList(monthList)
    }

    /**
     * 获取月份数据
     */
    private fun getMonthList(monthList: ArrayList<MonthBean>) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -mMaxMonth + 1)
        for (index in 0 until mMaxMonth) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val monthBean = MonthBean(year, month)
            monthList.add(monthBean)

            calendar.add(Calendar.MONTH, 1)
        }
    }

    /**
     * 获取日期数据
     */
    private fun getDateList(monthList: ArrayList<MonthBean>) {
        val calendar = Calendar.getInstance()
        for ((index, bean) in monthList.withIndex()) {
            val dateList = arrayListOf<DateBean>()
            //设置每一个月的第一天
            calendar.set(bean.year, bean.month - 1, 1)
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            //每月第一天是周几
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            //第一天之前空几天
            val firstOffset = dayOfWeek - 1
            //每月开始空白
            for (i in 0 until firstOffset) {
                val dateBean = DateBean()
                dateBean.year = currentYear
                dateBean.month = currentMonth + 1
                dateBean.date = 0
                dateBean.type = DateBean.TYPE_DATE_BLANK
                dateBean.groupName = dateBean.monthToString()
                dateList.add(dateBean)
            }
            //每一个的最后一天
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.DATE, -1)
            val monthLastDate = calendar.get(Calendar.DATE)
            //每月日期
            for (i in 0 until monthLastDate) {
                val dateBean = DateBean()
                dateBean.year = currentYear
                dateBean.month = currentMonth + 1
                dateBean.date = i + 1
                dateBean.type = DateBean.TYPE_DATE_NORMAL
                dateBean.groupName = dateBean.monthToString()
                //设置今天
                checkRecentDay(dateBean)
                //设置周末
                checkWeekend(dateBean)
                //设置最后一个月不可点击
                val isLastMonth = index == monthList.size - 1
                checkDateUnAble(dateBean, isLastMonth)
                dateList.add(dateBean)
            }
            //每月结束空白
            val lastDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val lastOffSet = 7 - lastDayOfWeek
            for (i in 0 until lastOffSet) {
                val dateBean = DateBean()
                dateBean.year = currentYear
                dateBean.month = currentMonth + 1
                dateBean.date = 0
                dateBean.type = DateBean.TYPE_DATE_BLANK
                dateBean.groupName = dateBean.monthToString()
                dateList.add(dateBean)
            }
            bean.dateList = dateList
        }

        //初始化标题日期
        for (bean in monthList) {
            val titleBean = DateBean()
            titleBean.year = bean.year
            titleBean.month = bean.month
            titleBean.groupName = titleBean.monthToString()
            titleBean.type = DateBean.TYPE_DATE_TITLE
            //添加标题
            mList.add(titleBean)
            //日期数据
            mList.addAll(bean.dateList)
        }
    }

    /**
     * 控制今天之后的日期不可选择
     */
    private fun checkDateUnAble(bean: DateBean, lastMonth: Boolean) {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.get(Calendar.DATE)
        bean.isUnableClick = !(lastMonth && bean.date > currentDate)
    }

    /**
     * 控制是否显示日历
     */
    private fun initWeekView() {
        val weekArray = mContext.resources.getStringArray(R.array.week)
        val itemParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        itemParams.weight = 1f
        for (week in weekArray) {
            val textView = TextView(mContext)
            textView.layoutParams = itemParams
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.common_color_666))
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.resources.getDimensionPixelSize(R.dimen.common_size_12).toFloat())
            textView.gravity = Gravity.CENTER
            val marginTop = mContext.resources.getDimensionPixelSize(R.dimen.common_margin_15)
            textView.setPadding(marginTop, marginTop, marginTop, marginTop)
            textView.text = week

            ll_top_week.addView(textView)
        }
    }

    /**
     * 设置是否是今天
     */
    private fun checkRecentDay(bean: DateBean) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDate = calendar.get(Calendar.DATE)
        if (bean.year == currentYear && bean.month == currentMonth && bean.date == currentDate) {
            bean.isRecentDay = true
            bean.recentDayName = DateBean.STR_RECENT_TODAY
            //默认选择今天
//            bean.isChooseDay = true
//            bean.intervalType = DateBean.TYPE_INTERVAL_START
//            mStartDateBean = bean
        }
    }

    /**
     * 设置周末
     */
    private fun checkWeekend(bean: DateBean) {
        val calendar = Calendar.getInstance()
        calendar.set(bean.year, bean.month - 1, bean.date)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == 1 || dayOfWeek == 7) {
            //周日或者周六
            bean.isWeekend = true
        }
    }

    /**
     * 当日期被选择的时候，暴露给外面的操作
     */
    interface DateSelectCallBack {

        fun onDateSelect(isSelect: Boolean)
    }
}