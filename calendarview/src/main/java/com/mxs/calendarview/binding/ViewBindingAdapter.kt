package com.mxs.calendarview.binding

import android.databinding.BindingAdapter
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @author lijie
 * @date 2019-12-19 14:55
 * @description 控件绑定
 */
object ViewBindingAdapter {

    @BindingAdapter("android:layout_marginTop")
    @JvmStatic
    fun setLayoutMargin(view: View, `val`: Float) {
        val marginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParams.topMargin = `val`.toInt()
        view.layoutParams = marginLayoutParams
    }

    @BindingAdapter("android:background")
    @JvmStatic
    fun setBackgroundResource(view: View, resourceId: ResourceId) {
        view.setBackgroundResource(resourceId.unboxed)
    }

    @BindingAdapter("android:textColor")
    @JvmStatic
    fun setText(view: TextView, resourceId: ResourceId) {
        view.setTextColor(ContextCompat.getColor(view.context, resourceId.unboxed))
    }

    @BindingAdapter("android:visibility")
    @JvmStatic
    fun setViewVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
}