package cn.dawnyu.view.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * description: 自定义倒计时控件
 * 支持自定义时间后缀文案，目前只支持"天、小时、分钟、秒"的倒计时时间计算。
 * 支持自定义 单个时间字符 的背景样式
 * 支持设置 时间字符/时间后缀文字 的间距
 * <p>
 * 例："dd天hh:mm:ss", "hh小时mm分钟ss秒"
 * <p>
 * date: 17/11/24
 * version: 1.0.171124
 */

public class CountdownView extends View {
    private Context mContext;
    private Rect mTextBound;
    private Paint mTimeTextPaint, mSuffixTextPaint, mTimeBackgroundPaint, mSuffixBackgroundPaint;

    private int mTimeTextSize;
    private int mTimeTextColor;
    private int mTimeBackground;
    private boolean isTimeTextBold;
    private float mTimeTextHeight;
    private float mTimeTextCharBackgroundWidth;
    private float mTimeTextCharBackgroundHeight;
    private float mTimeTextCharMargin;
    private float mTimeTextTotalWidth;
    private float mTimeTextCharMeasuredWidth;

    private int mSuffixTextSize;
    private int mSuffixTextColor;
    private int mSuffixBackground;
    private boolean isSuffixTextBold;
    private float mSuffixTextHeight;
    private float mSuffixTextCharBackgroundWidth;
    private float mSuffixTextCharBackgroundHeight;
    private float mSuffixTextCharMargin;
    private float mSuffixTextTotalWidth;
    private float mSuffixTextSingleWidth;

    /**
     * 时间展示格式
     * 规范: day-dd, hour-hh, minute-mm, second-ss
     */
    private String mTimeFormat = "hh:mm:ss";
    private String mSuffixDay = "";
    private String mSuffixHour = "";
    private String mSuffixMinute = "";
    private String mSuffixSecond = "";

    private boolean showDays, showHours, showMinutes, showSeconds = false;

    private float mSuffixDayTextWidth = 0;
    private float mSuffixHourTextWidth = 0;
    private float mSuffixMinuteTextWidth = 0;
    private float mSuffixSecondTextWidth = 0;

    private int mTimeCount;
    private int mSuffixCount;

    public long mDay, mHour, mMinute, mSecond;

    private float mTimeTextCharActualWidth;
    private Bitmap mTimeBackgroundBitmap;
    private Bitmap mSuffixBackgroundBitmap;

    private MyCountDownTimer myCountDownTimer;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CountdownView, defStyleAttr, 0);

        mTimeTextSize = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeTextSize, Utils.getSp2Px(context, 13));
        mTimeTextColor = mTypedArray.getColor(R.styleable.CountdownView_timeTextColor, Color.BLACK);

        mTimeBackground = mTypedArray.getResourceId(R.styleable.CountdownView_timeBackground, 0);

        mTimeTextCharMargin = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeTextCharMargin, Utils.getDip2Px(context, 0));
        mTimeTextCharBackgroundWidth = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeTextCharBackgroundWidth, Utils.getDip2Px(context, 0));
        mTimeTextCharBackgroundHeight = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeTextCharBackgroundHeight, Utils.getDip2Px(context, 0));
        isTimeTextBold = mTypedArray.getBoolean(R.styleable.CountdownView_isTimeTextBold, false);

        mSuffixTextSize = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixTextSize, Utils.getSp2Px(context, 16));
        mSuffixTextColor = mTypedArray.getColor(R.styleable.CountdownView_suffixTextColor, Color.BLACK);
        mSuffixBackground = mTypedArray.getResourceId(R.styleable.CountdownView_suffixBackground, 0);
        mSuffixTextCharMargin = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixTextCharMargin, Utils.getDip2Px(context, 2));
        mSuffixTextCharBackgroundWidth = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixTextCharBackgroundWidth, Utils.getDip2Px(context, 0));
        mSuffixTextCharBackgroundHeight = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixTextCharBackgroundHeight, Utils.getDip2Px(context, 0));
        isSuffixTextBold = mTypedArray.getBoolean(R.styleable.CountdownView_isSuffixTextBold, false);

        mTypedArray.recycle();
    }

    /**
     * 开始倒计时
     *
     * @param millisecond 倒计时时间长度
     * @param mTimeFormat 时间展示格式
     *                    例："dd天hh:mm:ss", "hh小时mm分钟ss秒"
     */
    public void start(long millisecond, String mTimeFormat) {
        if (millisecond <= 0) {
            return;
        }

        if (!Utils.isNullOrEmpty(mTimeFormat)) {
            this.mTimeFormat = mTimeFormat;
        }

        mDay = (int) (millisecond / 1000 / (60 * 60 * 24));
        mHour = (int) ((millisecond / 1000 / (60 * 60)) % 24);
        mMinute = (int) ((millisecond / 1000 / 60) % 60);
        mSecond = (int) (millisecond / 1000 % 60);

        init();

        stopCountdown();

        myCountDownTimer = new MyCountDownTimer(millisecond, 1000);
        myCountDownTimer.start();
    }

    private void init() {
        initPaint();

        initBackground();

        initTimeTextBounds();

        initSuffix();
        initSuffixTextBounds();
    }

    private void initPaint() {
        //Time text paint
        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setColor(mTimeTextColor);
        mTimeTextPaint.setTextSize(mTimeTextSize);
        mTimeTextPaint.setTextAlign(Paint.Align.CENTER);
        if (isTimeTextBold) {
            mTimeTextPaint.setFakeBoldText(true);
        }
        mTimeBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeBackgroundPaint.setStyle(Paint.Style.FILL);

        //Suffix text paint
        mSuffixTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSuffixTextPaint.setColor(mSuffixTextColor);
        mSuffixTextPaint.setTextSize(mSuffixTextSize);
        mSuffixTextPaint.setTextAlign(Paint.Align.CENTER);
        if (isSuffixTextBold) {
            mSuffixTextPaint.setFakeBoldText(true);
        }
    }

    private void initTimeTextBounds() {
        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds("0", 0, 1, rect);
        mTimeTextCharMeasuredWidth = rect.width();
        mTimeTextHeight = rect.height();
    }

    private void initSuffixTextBounds() {
        Rect rect = new Rect();
        mSuffixTextPaint.getTextBounds("00", 0, 2, rect);
        mSuffixTextHeight = rect.height();
    }

    private void initBackground() {
        mTimeBackgroundBitmap = getBitmap(mTimeBackground);
        mSuffixBackgroundBitmap = getBitmap(mSuffixBackground);
    }

    private void initSuffix() {
        mTimeCount = 0;
        mSuffixCount = 0;
        mSuffixTextTotalWidth = 0;

        int mIndexDay = mTimeFormat.indexOf("dd");
        int mIndexHour = mTimeFormat.indexOf("hh");
        int mIndexMinute = mTimeFormat.indexOf("mm");
        int mIndexSecond = mTimeFormat.indexOf("ss");

        //Has day
        if (mIndexDay > -1) {
            showDays = true;

            //Has day suffix
            if (mIndexDay + 2 < mIndexHour) {
                mSuffixDay = mTimeFormat.substring(mIndexDay + 2, mIndexHour);
                mSuffixDayTextWidth = mSuffixTextPaint.measureText(mSuffixDay);
                mSuffixTextTotalWidth += mSuffixDayTextWidth;
                mSuffixCount++;
            }
        } else {
            showDays = false;
            mSuffixDay = "";
        }

        //Has hour
        if (mIndexHour > -1) {
            mTimeCount++;
            showHours = true;

            //Has hour suffix
            if (mIndexHour + 2 < mIndexMinute) {
                mSuffixHour = mTimeFormat.substring(mIndexHour + 2, mIndexMinute);
                mSuffixHourTextWidth = mSuffixTextPaint.measureText(mSuffixHour);
                mSuffixTextTotalWidth += mSuffixHourTextWidth;
                mSuffixCount++;
            }
        } else {
            showHours = false;
            mSuffixHour = "";
        }

        //Has Minute
        if (mIndexMinute > -1) {
            mTimeCount++;
            showMinutes = true;

            //Has minute suffix
            if (mIndexMinute + 2 < mIndexSecond) {
                mSuffixMinute = mTimeFormat.substring(mIndexMinute + 2, mIndexSecond);
                mSuffixMinuteTextWidth = mSuffixTextPaint.measureText(mSuffixMinute);
                mSuffixTextTotalWidth += mSuffixMinuteTextWidth;
                mSuffixCount++;
            }
        } else {
            showMinutes = false;
            mSuffixMinute = "";
        }

        //Has second
        if (mIndexSecond > -1) {
            mTimeCount++;
            showSeconds = true;

            //Has second suffix
            if (mIndexSecond + 2 < mTimeFormat.length()) {
                mSuffixSecond = mTimeFormat.substring(mIndexSecond + 2, mTimeFormat.length());
                mSuffixSecondTextWidth = mSuffixTextPaint.measureText(mSuffixSecond);
                mSuffixTextTotalWidth += mSuffixSecondTextWidth;
                mSuffixCount++;
            }
        } else {
            showSeconds = false;
            mSuffixSecond = "";
        }
    }

    private int getTotalWidth() {
        float suffixWidth = mSuffixTextTotalWidth + mSuffixTextCharMargin * mSuffixCount * 2;

        float timeWidth;
        if (mTimeTextCharBackgroundWidth == 0) {
            mTimeTextCharActualWidth = mTimeTextCharMeasuredWidth;
        } else {
            mTimeTextCharActualWidth = mTimeTextCharBackgroundWidth;
        }

        float daysTextWidth = 0;
        if (showDays && mDay > 0) {
            int daysTextCount = getTimeString(mDay, 0).length();
            daysTextWidth = mTimeTextCharActualWidth * daysTextCount + mTimeTextCharMargin * (daysTextCount - 1);
        }
        timeWidth = daysTextWidth + (mTimeTextCharActualWidth * 2 + mTimeTextCharMargin) * mTimeCount;

        return (int) Math.ceil(suffixWidth + timeWidth);//todo
    }

    private int getTotalHeight() {
        return (int) Math.ceil(Math.max(Math.max(mTimeTextHeight, mSuffixTextHeight), mTimeBackgroundBitmap == null ? 0 : mTimeBackgroundBitmap.getHeight()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = measureSize(1, getTotalWidth(), widthMeasureSpec);
        int height = measureSize(2, getTotalHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    /**
     * Measure view Size
     *
     * @param specType    1 width 2 height
     * @param contentSize all content view size
     * @param measureSpec spec
     * @return measureSize
     */
    private int measureSize(int specType, int contentSize, int measureSpec) {
        int size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            size = Math.max(contentSize, specSize);
        } else {
            size = contentSize;

            if (specType == 1) {
                // width
                size += (getPaddingLeft() + getPaddingRight());
            } else {
                // height
                size += (getPaddingTop() + getPaddingBottom());
            }
        }

        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float left = 0;
        if (showDays && mDay > 0) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mDay, 0), mSuffixDay, mSuffixDayTextWidth);
        }

        //hour
        if (showHours) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mHour, 2), mSuffixHour, mSuffixHourTextWidth);
        }

        //Minute
        if (showMinutes) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mMinute, 2), mSuffixMinute, mSuffixMinuteTextWidth);
        }

        //Second
        if (showSeconds) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mSecond, 2), mSuffixSecond, mSuffixSecondTextWidth);
        }
    }

    private String getTimeString(long time, int minLength) {
        String timeString = String.valueOf(time);

        if (timeString.length() < minLength) {
            for (int i = 0; i < minLength - timeString.length(); i++) {
                timeString = "0" + timeString;
            }
        }

        return timeString;
    }

    private float drawTimeSuffixItem(Canvas canvas, float left, String timeString, String timeSuffix, float suffixTextWidth) {
        for (int i = 0; i < timeString.length(); i++) {
            //Draw time background
            if (mTimeBackgroundBitmap != null) {
                canvas.drawBitmap(mTimeBackgroundBitmap, left, 0, mTimeBackgroundPaint);
            }

            //Draw time text
            canvas.drawText(String.valueOf(timeString.charAt(i)), left + (mTimeTextCharActualWidth) / 2, (getTotalHeight() + mTimeTextHeight) / 2, mTimeTextPaint);

            if (i < timeString.length() - 1) {
                left += mTimeTextCharActualWidth + mTimeTextCharMargin;
            } else {
                left += mTimeTextCharActualWidth;
            }
        }

        //Draw suffix
        left += mSuffixTextCharMargin;
        canvas.drawText(timeSuffix, left + suffixTextWidth / 2, (getTotalHeight() + mSuffixTextHeight) / 2, mSuffixTextPaint);
        left += suffixTextWidth + mSuffixTextCharMargin;

        return left;
    }

    private Bitmap getBitmap(int drawableRes) {
        try {
            Drawable drawable = getResources().getDrawable(drawableRes);
            Canvas canvas = new Canvas();
            Bitmap bitmap = Bitmap.createBitmap((int) mTimeTextCharBackgroundWidth, (int) mTimeTextCharBackgroundHeight, Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, (int) mTimeTextCharBackgroundWidth, (int) mTimeTextCharBackgroundHeight);
            drawable.draw(canvas);

            return bitmap;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    class MyCountDownTimer extends CountDownTimer {

        long diffTime = 0;

        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            //todo
            resetZero();
            refresh();
            cancel();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            diffTime = millisUntilFinished / 1000;

            mDay = diffTime / (60 * 60 * 24);
            mHour = (diffTime / (60 * 60)) % 24;
            mMinute = (diffTime / 60) % 60;
            mSecond = diffTime % 60;

            refresh();
        }
    }

    private void refresh() {
        postInvalidate();
        requestLayout();
    }

    private void resetZero() {
        mDay = 0;
        mHour = 0;
        mMinute = 0;
        mSecond = 0;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopCountdown();
    }

    private void stopCountdown() {
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
        }
    }
}