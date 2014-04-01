package com.example.test1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

public class DigitalClock2  extends TextView {

Calendar mCalendar;
private String mformat = "yyyy年M月d日(EE)";

private Runnable mTicker;
private Handler mHandler;
private Locale locale;

private boolean mTickerStopped = false;

String mFormat;

public DigitalClock2(Context context) {
super(context);
locale = Locale.JAPAN;
initClock(context);
}

public DigitalClock2(Context context, Locale setLocale) {
super(context);
locale = setLocale;
initClock(context);
}

public DigitalClock2(Context context, AttributeSet attrs) {
super(context, attrs);
locale = Locale.JAPAN;
initClock(context);
}

public DigitalClock2(Context context, AttributeSet attrs, Locale setLocale) {
super(context, attrs);
locale =setLocale;
initClock(context);
}

private void initClock(Context context) {

if (mCalendar == null) {
mCalendar = Calendar.getInstance(locale);
}
}

@Override
protected void onAttachedToWindow() {
mTickerStopped = false;
super.onAttachedToWindow();
mHandler = new Handler();

/**
* requests a tick on the next hard-second boundary
*/
mTicker = new Runnable() {
@Override
public void run() {
if (mTickerStopped) return;
mCalendar.setTimeInMillis(System.currentTimeMillis());
Date date1 = mCalendar.getTime();

//引数に表示フォーマットを指定して
//SimpleDateFormatオブジェクトを生成しています。
SimpleDateFormat sdf1 = new SimpleDateFormat(mformat,Locale.JAPAN);
//formatメソッドで該当のフォーマットに変更しています。
setText(sdf1.format(date1));

invalidate();
long now = SystemClock.uptimeMillis();
long next = now + (1000 - now % 1000);
mHandler.postAtTime(mTicker, next);
}
};
mTicker.run();
}

@Override
protected void onDetachedFromWindow() {
super.onDetachedFromWindow();
mTickerStopped = true;
}}
