package com.mxs.calendarview

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mxs.calendarview.DateBean.TYPE_DATE_BLANK
import com.mxs.calendarview.DateBean.TYPE_DATE_TITLE
import com.mxs.calendarview.databinding.ItemCalendarBlankBinding
import com.mxs.calendarview.databinding.ItemCalendarDateBinding
import com.mxs.calendarview.databinding.ItemCalendarTitleBinding

/**
 * @author lijie
 * @date 2019-12-16 18:04
 * @description 日期适配器
 */
class CalendarAdapter(private val mContext: Context, val items: MutableList<DateBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mListener: OnClickDayListener

    fun setOnClickDayListener(listener: OnClickDayListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_DATE_BLANK -> {
                //空白
                val binding = DataBindingUtil.inflate<ItemCalendarBlankBinding>(inflater, R.layout.item_calendar_blank, parent, false)
                return BlankViewHolder(binding)
            }
            TYPE_DATE_TITLE -> {
                //标题
                val binding = DataBindingUtil.inflate<ItemCalendarTitleBinding>(inflater, R.layout.item_calendar_title, parent, false)
                return TitleViewHolder(binding)
            }
            else -> {
                //日期
                val binding = DataBindingUtil.inflate<ItemCalendarDateBinding>(inflater, R.layout.item_calendar_date, parent, false)
                return DateViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = items[position]
        when (holder) {
            is BlankViewHolder -> holder.bind(bean)
            is TitleViewHolder -> holder.bind(bean)
            is DateViewHolder -> holder.bind(bean, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == RecyclerView.NO_POSITION){
            //处理异常 快速点击会返回-1的情况
            return RecyclerView.NO_POSITION
        }
        val dateBean = items[position]
        return dateBean.type
    }

    class BlankViewHolder(val binding: ItemCalendarBlankBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: DateBean) {
            binding.setVariable(BR.bean, bean)
            binding.executePendingBindings()
        }
    }

    class TitleViewHolder(val binding: ItemCalendarTitleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: DateBean) {
            binding.setVariable(BR.bean, bean)
            binding.executePendingBindings()
        }
    }

    inner class DateViewHolder(val binding: ItemCalendarDateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: DateBean, position: Int) {
            binding.setVariable(BR.bean, bean)
            binding.executePendingBindings()

            if (bean.isUnableClick) {
                binding.root.setOnClickListener {
                    mListener.let {
                        mListener.onClickDay(binding.root, bean, position)
                    }
                }
            }
        }
    }

    interface OnClickDayListener {

        fun onClickDay(view: View, bean: DateBean, position: Int)
    }
}