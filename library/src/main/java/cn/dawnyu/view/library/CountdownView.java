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
 * version: 0.1.0
 */

public class CountdownView extends View {
    private Context mContext;
    private Rect mTextBound;
    private Paint mTimeTextPaint, mSuffixTextPaint, mTimeBackgroundPaint, mSuffixBackgroundPaint;

    //----------------------------- attrs start ---------------------------//
    private int mTimeTextSize;
    private int mTimeTextColor;
    private boolean isTimeTextBold;
    /**
     * Margin between time text characters.
     */
    private float mTimeTextCharMargin;
    private Drawable mTimeBackground;
    private float mTimeBackgroundWidth;
    private float mTimeBackgroundHeight;
    private float mTimeBackgroundPadding;
    private float mTimeBackgroundPaddingX;
    private float mTimeBackgroundPaddingY;
    private float mTimeBackgroundPaddingLeft;
    private float mTimeBackgroundPaddingRight;
    private float mTimeBackgroundPaddingTop;
    private float mTimeBackgroundPaddingBottom;

    private int mSuffixTextSize;
    private int mSuffixTextColor;
    private boolean isSuffixTextBold;
    /**
     * Margin between the adjacent time text and suffix text.
     */
    private float mSuffixTextCharMargin;
    private Drawable mSuffixBackground;
    private float mSuffixBackgroundWidth;
    private float mSuffixBackgroundHeight;
    private float mSuffixBackgroundPadding;
    private float mSuffixBackgroundPaddingX;
    private float mSuffixBackgroundPaddingY;
    private float mSuffixBackgroundPaddingLeft;
    private float mSuffixBackgroundPaddingRight;
    private float mSuffixBackgroundPaddingTop;
    private float mSuffixBackgroundPaddingBottom;
    //----------------------------- attrs end ---------------------------//

    //---------------------- calculate values start --------------------//
    private float mTimeTextHeight;
    private float mTimeTextTotalWidth;
    private float mTimeTextCharMeasuredWidth;
    private float mSuffixTextTotalWidth;
    private float mSuffixTextSingleWidth;
    private float mSuffixTextHeight;
    private float mSuffixDayDeltY2Base;
    private float mSuffixHourDeltY2Base;
    private float mSuffixMinuteDeltY2Base;
    private float mSuffixSecondDeltY2Base;
    private float mSuffixDayBaseline;
    private float mSuffixHourBaseline;
    private float mSuffixMinuteBaseline;
    private float mSuffixSecondBaseline;
    //---------------------- calculate values end --------------------//

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
        isTimeTextBold = mTypedArray.getBoolean(R.styleable.CountdownView_isTimeTextBold, false);

        mTimeTextCharMargin = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeTextCharMargin, Utils.getDip2Px(context, 0));

        mTimeBackground = mTypedArray.getDrawable(R.styleable.CountdownView_timeBackground);
        mTimeBackgroundWidth = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundWidth, Utils.getDip2Px(context, 0));
        mTimeBackgroundHeight = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundHeight, Utils.getDip2Px(context, 0));

        mTimeBackgroundPadding = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundPadding, Utils.getDip2Px(context, 0));
        mTimeBackgroundPaddingX = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundPaddingX, Utils.getDip2Px(context, 0));
        mTimeBackgroundPaddingY = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundPaddingY, Utils.getDip2Px(context, 0));
        mTimeBackgroundPaddingLeft = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundPaddingLeft, Utils.getDip2Px(context, 0));
        mTimeBackgroundPaddingRight = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundPaddingRight, Utils.getDip2Px(context, 0));
        mTimeBackgroundPaddingTop = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundPaddingTop, Utils.getDip2Px(context, 0));
        mTimeBackgroundPaddingBottom = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeBackgroundPaddingBottom, Utils.getDip2Px(context, 0));

        mSuffixTextSize = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixTextSize, Utils.getSp2Px(context, 16));
        mSuffixTextColor = mTypedArray.getColor(R.styleable.CountdownView_suffixTextColor, Color.BLACK);
        isSuffixTextBold = mTypedArray.getBoolean(R.styleable.CountdownView_isSuffixTextBold, false);

        mSuffixTextCharMargin = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixTextCharMargin, Utils.getDip2Px(context, 2));

        mSuffixBackground = mTypedArray.getDrawable(R.styleable.CountdownView_suffixBackground);
        mSuffixBackgroundWidth = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundWidth, Utils.getDip2Px(context, 0));
        mSuffixBackgroundHeight = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundHeight, Utils.getDip2Px(context, 0));

        mSuffixBackgroundPadding = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundPadding, Utils.getDip2Px(context, 0));
        mSuffixBackgroundPaddingX = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundPaddingX, Utils.getDip2Px(context, 0));
        mSuffixBackgroundPaddingY = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundPaddingY, Utils.getDip2Px(context, 0));
        mSuffixBackgroundPaddingLeft = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundPaddingLeft, Utils.getDip2Px(context, 0));
        mSuffixBackgroundPaddingRight = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundPaddingRight, Utils.getDip2Px(context, 0));
        mSuffixBackgroundPaddingTop = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundPaddingTop, Utils.getDip2Px(context, 0));
        mSuffixBackgroundPaddingBottom = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixBackgroundPaddingBottom, Utils.getDip2Px(context, 0));

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

        initSuffix();

        initTimeTextBounds();
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

    private void initSuffix() {
        mTimeCount = 0;
        mSuffixCount = 0;
//        mSuffixTextTotalWidth = 0;

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

    private Rect getTextRectBound(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    private void initTimeTextBounds() {
        Rect rect = getTextRectBound("0", mTimeTextPaint);
        mTimeTextCharMeasuredWidth = rect.width();
        mTimeTextHeight = rect.height();

        if (mTimeBackgroundHeight == 0) {
            mTimeBackgroundHeight = mTimeTextHeight;
        }
    }

    private void initSuffixTextBounds() {
        mSuffixDayDeltY2Base = getSuffixDeltY2Base(mSuffixDay);
        mSuffixHourDeltY2Base = getSuffixDeltY2Base(mSuffixHour);
        mSuffixMinuteDeltY2Base = getSuffixDeltY2Base(mSuffixMinute);
        mSuffixSecondDeltY2Base = getSuffixDeltY2Base(mSuffixSecond);

        if (mSuffixBackgroundHeight == 0) {
            mSuffixBackgroundHeight = mSuffixTextHeight;//todo add padding
        }

        float deltHeight = getTotalHeight() / 2;

        mSuffixDayBaseline = deltHeight - mSuffixDayDeltY2Base;
        mSuffixHourBaseline = deltHeight - mSuffixHourDeltY2Base;
        mSuffixMinuteBaseline = deltHeight - mSuffixMinuteDeltY2Base;
        mSuffixSecondBaseline = deltHeight - mSuffixSecondDeltY2Base;
    }

    private float getSuffixDeltY2Base(String suffixText) {
        float deltY = 0;

        if (!Utils.isNullOrEmpty(suffixText)) {
            Rect minRect = getTextRectBound(suffixText, mSuffixTextPaint);

            /**
             * The size of Rect is different between different languages.
             */
            if (mSuffixTextHeight < minRect.height()) {
                mSuffixTextHeight = minRect.height();
            }

            /**
             * The position of baseline is different between different languages.
             */
            deltY = minRect.height() / 2 + minRect.top;
        }

        return deltY;
    }

    private int getTotalWidth() {
        float suffixWidth = mSuffixTextTotalWidth + mSuffixTextCharMargin * mSuffixCount * 2;

        float timeWidth;
        if (mTimeBackgroundWidth == 0) {
            mTimeTextCharActualWidth = mTimeTextCharMeasuredWidth;
        } else {
            mTimeTextCharActualWidth = mTimeBackgroundWidth;
        }

        float daysTextWidth = 0;
        if (showDays && mDay > 0) {
            int daysTextCount = getTimeString(mDay, 0).length();
            daysTextWidth = mTimeTextCharActualWidth * daysTextCount + mTimeTextCharMargin * (daysTextCount - 1);
        }
        timeWidth = daysTextWidth + (mTimeTextCharActualWidth * 2 + mTimeTextCharMargin) * mTimeCount;

        return (int) Math.ceil(suffixWidth + timeWidth);//todo
    }

    private float getTotalHeight() {
        return Utils.getMaxNum(new float[]{
                mTimeTextHeight,
                mTimeBackgroundHeight,
                mSuffixTextHeight,
                mSuffixBackgroundHeight});
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = measureSize(1, getTotalWidth(), widthMeasureSpec);
        int height = measureSize(2, (int) getTotalHeight(), heightMeasureSpec);

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
            left = drawTimeSuffixItem(canvas, left, getTimeString(mDay, 0), mSuffixDay, mSuffixDayTextWidth, mSuffixDayBaseline);
        }

        //hour
        if (showHours) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mHour, 2), mSuffixHour, mSuffixHourTextWidth, mSuffixHourBaseline);
        }

        //Minute
        if (showMinutes) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mMinute, 2), mSuffixMinute, mSuffixMinuteTextWidth, mSuffixMinuteBaseline);
        }

        //Second
        if (showSeconds) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mSecond, 2), mSuffixSecond, mSuffixSecondTextWidth, mSuffixSecondBaseline);
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

    private float drawTimeSuffixItem(Canvas canvas, float left, String timeString, String timeSuffix, float suffixTextWidth, float baseline) {
        //Draw time
        for (int i = 0; i < timeString.length(); i++) {
            //Draw time background
            if (mTimeBackground != null) {
                mTimeBackground.setBounds((int) left, (int) (getTotalHeight() - mTimeBackgroundHeight) / 2, (int) (left + mTimeBackgroundWidth), (int) (getTotalHeight() + mTimeBackgroundHeight) / 2);
                mTimeBackground.draw(canvas);
            }

            //Draw time text
            canvas.drawText(String.valueOf(timeString.charAt(i)), left + (mTimeTextCharActualWidth) / 2, (int) (getTotalHeight() + mTimeTextHeight) / 2, mTimeTextPaint);

            if (i < timeString.length() - 1) {
                left += mTimeTextCharActualWidth + mTimeTextCharMargin;
            } else {
                left += mTimeTextCharActualWidth;
            }
        }

        //Draw suffix
        left += mSuffixTextCharMargin;

        //Draw suffix background
        if (mSuffixBackground != null) {
            mSuffixBackground.setBounds((int) left, 0, (int) (left + suffixTextWidth), (int) mSuffixBackgroundHeight);
            mSuffixBackground.draw(canvas);
        }

        //Draw suffix text
        canvas.drawText(timeSuffix, left + suffixTextWidth / 2, (int) baseline, mSuffixTextPaint);

        left += suffixTextWidth + mSuffixTextCharMargin;

        return left;
    }

    private Bitmap getBitmap(int drawableRes) {
        try {
            Drawable drawable = getResources().getDrawable(drawableRes);
            Canvas canvas = new Canvas();
            Bitmap bitmap = Bitmap.createBitmap((int) mTimeBackgroundWidth, (int) mTimeBackgroundHeight, Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, (int) mTimeBackgroundWidth, (int) mTimeBackgroundHeight);
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