package com.mxs.calendarview;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;


/**
 * @author lijie
 * @date 2019-12-16 16:06
 * @description
 */
public class DateBean {

    /**
     * view类型：空白，年月标题，日期
     */
    public static final int TYPE_DATE_BLANK = 0;
    public static final int TYPE_DATE_TITLE = 1;
    public static final int TYPE_DATE_NORMAL = 2;
    /**
     * 选择区间类型：开始、中间、结束
     */
    public static final int TYPE_INTERVAL_START = 1;
    public static final int TYPE_INTERVAL_MIDDLE = 2;
    public static final int TYPE_INTERVAL_END = 3;
    /**
     * 最近几天
     */
    public static final String STR_RECENT_TODAY = "今天";
    public static final String STR_RECENT_TOMORROW = "明天";
    public static final String STR_RECENT_ACQUIRED = "后天";

    private int year = 2018;
    private int month;
    private int date;
    /**
     * 控制view的类型
     */
    private int type;
    /**
     * 分组
     */
    private String groupName;
    /**
     * 周末
     */
    private boolean isWeekend;
    /**
     * 节日
     */
    private String festival;
    /**
     * 是否是今天
     */
    private boolean isRecentDay;
    private String recentDayName;
    /**
     * 选择日期
     */
    private ObservableBoolean isChooseDay = new ObservableBoolean();
    /**
     * 区间类型
     */
    private ObservableInt intervalType = new ObservableInt();
    /**
     * 不可选择点击
     */
    private boolean isUnableClick = true;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isWeekend() {
        return isWeekend;
    }

    public void setWeekend(boolean weekend) {
        isWeekend = weekend;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public boolean isRecentDay() {
        return isRecentDay;
    }

    public void setRecentDay(boolean recentDay) {
        isRecentDay = recentDay;
    }

    public String getRecentDayName() {
        return recentDayName;
    }

    public void setRecentDayName(String recentDayName) {
        this.recentDayName = recentDayName;
    }

    public boolean isChooseDay() {
        return isChooseDay.get();
    }

    public void setChooseDay(boolean chooseDay) {
        isChooseDay.set(chooseDay);
    }

    public int getIntervalType() {
        return intervalType.get();
    }

    public void setIntervalType(int intervalType) {
        this.intervalType.set(intervalType);
    }

    public boolean isUnableClick() {
        return isUnableClick;
    }

    public void setUnableClick(boolean unableClick) {
        isUnableClick = unableClick;
    }

    public String dateToString() {
        String sMonth = month < 10 ? String.format("0%d", month) : String.format("%d", month);
        String sDate = date < 10 ? String.format("0%d", date) : String.format("%d", date);
        return year + "-" + sMonth + "-" + sDate;
    }

    public String monthToString() {
        String sMonth = month < 10 ? String.format("0%d", month) : String.format("%d", month);
        return year + "年" + sMonth + "月";
    }

    /**
     * 获取开始和结束标签
     *
     * @return
     */
    public String dateStatusMsg() {
        if (isChooseDay() && getIntervalType() == TYPE_INTERVAL_START) {
            return "开始";
        } else if (isChooseDay() && getIntervalType() == TYPE_INTERVAL_END) {
            return "结束";
        }
        return "";
    }

    /**
     * 是否显示状态
     *
     * @return
     */
    public boolean showDateStatus() {
        return isChooseDay() && (getIntervalType() == TYPE_INTERVAL_START || getIntervalType() == TYPE_INTERVAL_END);
    }

    /**
     * 设置选择俺日期的颜色
     *
     * @return
     */
    public int setChooseItemBg() {
        int color = R.color.white;
        if (isChooseDay()) {
            switch (getIntervalType()) {
                case TYPE_INTERVAL_START:
                case TYPE_INTERVAL_END:
                    color = R.color.color_red_FC5F46;
                    break;
                case TYPE_INTERVAL_MIDDLE:
                    color = R.color.color_red_FFE8E3;
                    break;
                default:
                    color = R.color.white;
                    break;
            }
        }
        return color;
    }

    /**
     * 设置日期颜色
     *
     * @return
     */
    public int setDateTextColor() {
        int color;
        switch (getIntervalType()) {
            case TYPE_INTERVAL_START:
            case TYPE_INTERVAL_END:
                color = R.color.white;
                break;
            default:
                if (!isUnableClick) {
                    //判断不可点击
                    color = R.color.color_gray_C2C2C2;
                } else if (isWeekend) {
                    //判断周末
                    color = R.color.color_red_FC5F46;
                } else {
                    color = R.color.common_color_333;
                }
                break;
        }
        return color;
    }

    /**
     * 设置日期文字，如果是今天的话，设置为今天
     *
     * @return
     */
    public String setDateMsg() {
        if (isRecentDay) {
            return STR_RECENT_TODAY;
        } else {
            return String.valueOf(date);
        }
    }
}
