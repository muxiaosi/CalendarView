package com.mxs.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.text.TextUtils
import android.view.View

/**
 * @author lijie
 * @date 2019-12-16 17:05
 * @description 日历控件分割线
 */
class CalendarDateDecoration(val context: Context, private val mCallback: ChooseCallback) :
    RecyclerView.ItemDecoration() {

    /**
     * 悬停栏背景画笔
     */
    private val mPaint by lazy(LazyThreadSafetyMode.NONE) {
        Paint()
    }

    /**
     * 悬停栏文字画笔
     */
    private val mTextPaint by lazy(LazyThreadSafetyMode.NONE) {
        TextPaint()
    }

    /**
     * 分割线
     */
    private val mDividerPaint by lazy(LazyThreadSafetyMode.NONE) {
        Paint()
    }

    private var mTop: Int = 0

    private var mTopPadding: Float = 0.0f

    private var mDividerHeight: Int = 0

    init {
        mPaint.color = ContextCompat.getColor(context, R.color.common_color_background)

        mTextPaint.isAntiAlias = true
        mTextPaint.textSize = sp2px(context, 14.0F).toFloat()
        mTextPaint.color = ContextCompat.getColor(context, R.color.common_color_333)
        mTextPaint.textAlign = Paint.Align.CENTER
        val fontMetrics = mTextPaint.fontMetrics
        mTop = dp2px(context, 40.0f)
        mTopPadding = -((fontMetrics.bottom - fontMetrics.top) / 2 + fontMetrics.top)

        mDividerPaint.color = ContextCompat.getColor(context, R.color.common_color_divider)
        mDividerHeight = 2
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //设置item之间的padding
        outRect?.bottom = mDividerHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        //画分割线
        val childCount = parent?.childCount
        childCount?.let {
            val adapter = parent.adapter
            for (i in 0 until it) {
                val view = parent.getChildAt(i)
                val position = parent.getChildAdapterPosition(view)
                if (adapter?.getItemViewType(position) == DateBean.TYPE_DATE_TITLE) {
                    //title下不画
                    continue
                }
                if (isLastInGroup(position)) {
                    //最后一行下不画
                    continue
                }
                val top = view.bottom.toFloat()
                val bottom = (view.bottom + mDividerHeight).toFloat()
                val right = parent.width - parent.paddingRight
                val left = parent.paddingLeft
                c?.drawRect(left.toFloat(), top, right.toFloat(), bottom, mDividerPaint)
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val manager = parent.layoutManager as GridLayoutManager
        val position = manager.findFirstVisibleItemPosition()
        if (position == RecyclerView.NO_POSITION) {
            return
        }
        val viewHolder = parent.findViewHolderForAdapterPosition(position)
        val child = viewHolder?.itemView

        child?.let {
            var flag = false
            if (isLastInGroup(position)) {
                if (child.height + child.top < mTop) {
                    c.save()
                    flag = true
                    c.translate(0f, (child.height + child.top - mTop).toFloat())
                }
            }

            val rect = RectF(
                0f,
                parent.paddingTop.toFloat(),
                parent.right.toFloat(),
                (parent.paddingTop + mTop).toFloat()
            )
            c.drawRect(rect, mPaint)

            c.drawText(
                mCallback.getGroupId(position),
                rect.centerX(),
                rect.centerY() + mTopPadding,
                mTextPaint
            )

            if (flag) {
                c.restore()
            }

        }
    }

    private fun isLastInGroup(position: Int): Boolean {
        return !TextUtils.equals(mCallback.getGroupId(position), mCallback.getGroupId(position + 7))
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    private fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    private fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    interface ChooseCallback {

        fun getGroupId(position: Int): String
    }
}