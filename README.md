# CalendarView
竖直滚动日历，目前支持设置可以设置加载几个月，可以选择单个日期，也可以选择日期区间；在选择日期区间的时候，需要可以设置最大区间。

* 采用Gradle方式集成：
> 1.在project项目build.gradle中添加

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
> 2.在app中的build.gradle中添加

```
	dependencies {
	        implementation 'com.github.muxiaosi:CalendarView:Tag'
	}
```


* 使用方式如下，只需要在布局中调用即可。


```
<com.mxs.calendarview.CalendarView
    android:id="@+id/cv_calendar"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:layout_constraintBottom_toTopOf="@+id/rl_calendar_btn_layout"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:max_range="31"
    app:max_month="4"/>
```

其中有两个参数：
* max_range：最值选择范围的最大区间（日期区间不能超个31天）,默认没有限制。
* max_month：需要显示几个月，默认显示3个月

