package com.mxs.calendarviewdemo

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.mxs.calendarview.CalendarView
import com.mxs.calendarviewdemo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //处理点击日期之后的回调
        cv_calendar.setDateSelectedCallBackListener(object : CalendarView.DateSelectCallBack {
            override fun onDateSelect(isSelect: Boolean) {
                mBinding.isEnable = isSelect
            }
        })

        btn_calendar.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_calendar -> {
                //确认
                if (checkDate()) {
                    sendData()
                }
            }
        }
    }

    private fun sendData() {
        val intent = Intent()
        intent.putExtra("start_time", cv_calendar.mStartDateBean?.dateToString())
        if (cv_calendar.mEndDateBean == null) {
            //如果没有选择结束时间
            intent.putExtra("end_time", cv_calendar.mStartDateBean?.dateToString())
        } else {
            intent.putExtra("end_time", cv_calendar.mEndDateBean?.dateToString())
        }
        setResult(Activity.RESULT_OK, intent)
        Toast.makeText(this,"日期：" + cv_calendar.mStartDateBean?.dateToString() + "~~" + cv_calendar.mEndDateBean?.dateToString(),Toast.LENGTH_SHORT).show()
    }

    /**
     * 检查是否合法
     */
    private fun checkDate(): Boolean {
        val startDateBean = cv_calendar.mStartDateBean
        if (startDateBean == null) {
            Toast.makeText(this,getString(R.string.please_select_calendar),Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
