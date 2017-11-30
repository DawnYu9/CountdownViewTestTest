package cn.dawnyu.view.library;

import android.content.Context;
import android.content.res.TypedArray;
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
     * Margin between time text letters.
     */
    private float mTimeTextLetterSpacing;
    private float mTimeTextLetterBackgroundSpacing;
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
    private float mSuffixTextLetterSpacing;
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
    private float mTimeTextLetterMeasuredWidth;
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

    private int mTimeTypeCount;
    private int mSuffixCount;

    public long mDays, mHours, mMinutes, mSeconds;

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

        mTimeTextLetterSpacing = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeTextLetterSpacing, Utils.getDip2Px(context, 0));
        mTimeTextLetterBackgroundSpacing = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_timeTextLetterBackgroundSpacing, Utils.getDip2Px(context, 0));

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

        mSuffixTextLetterSpacing = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixTextLetterSpacing, Utils.getDip2Px(context, 2));

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

        mDays = (int) (millisecond / 1000 / (60 * 60 * 24));
        mHours = (int) ((millisecond / 1000 / (60 * 60)) % 24);
        mMinutes = (int) ((millisecond / 1000 / 60) % 60);
        mSeconds = (int) (millisecond / 1000 % 60);

        init();

        stopCountdown();

        myCountDownTimer = new MyCountDownTimer(millisecond, 1000);
        myCountDownTimer.start();
    }

    private void init() {
        initPadding();

        initPaint();

        initSuffix();

        initTimeTextBounds();
        initSuffixTextBounds();
    }

    private void initPadding() {
        mTimeBackgroundPaddingLeft = getTimePadding(mTimeBackgroundPaddingLeft, "x");
        mTimeBackgroundPaddingRight = getTimePadding(mTimeBackgroundPaddingRight, "x");
        mTimeBackgroundPaddingTop = getTimePadding(mTimeBackgroundPaddingTop, "y");
        mTimeBackgroundPaddingBottom = getTimePadding(mTimeBackgroundPaddingBottom, "y");

        mSuffixBackgroundPaddingLeft = getSuffixPadding(mSuffixBackgroundPaddingLeft, "x");
        mSuffixBackgroundPaddingRight = getSuffixPadding(mSuffixBackgroundPaddingRight, "x");
        mSuffixBackgroundPaddingTop = getSuffixPadding(mSuffixBackgroundPaddingTop, "y");
        mSuffixBackgroundPaddingBottom = getSuffixPadding(mSuffixBackgroundPaddingBottom, "y");
    }

    private float getTimePadding(float padding, String type) {
        if (padding == 0)
            switch (type) {
                case "x":
                    return mTimeBackgroundPaddingX == 0 ? mTimeBackgroundPadding : mTimeBackgroundPaddingX;
                case "y":
                    return mTimeBackgroundPaddingY == 0 ? mTimeBackgroundPadding : mTimeBackgroundPaddingY;
            }

        return padding;
    }

    private float getSuffixPadding(float padding, String type) {
        if (padding == 0)
            switch (type) {
                case "x":
                    return mSuffixBackgroundPaddingX == 0 ? mSuffixBackgroundPadding : mSuffixBackgroundPaddingX;
                case "y":
                    return mSuffixBackgroundPaddingY == 0 ? mSuffixBackgroundPadding : mSuffixBackgroundPaddingY;
            }

        return padding;
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

    //todo test 缺少时间
    private void initSuffix() {
        mTimeTypeCount = 0;
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
            mTimeTypeCount++;
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
            mTimeTypeCount++;
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
            mTimeTypeCount++;
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

    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    private void initTimeTextBounds() {
        Rect rect = getTextBounds("0", mTimeTextPaint);
        mTimeTextLetterMeasuredWidth = rect.width();
        mTimeTextHeight = rect.height();

        if (mTimeBackgroundHeight == 0) {
            mTimeBackgroundHeight = mTimeTextHeight + mTimeBackgroundPaddingTop + mTimeBackgroundPaddingBottom;
        }
    }

    private void initSuffixTextBounds() {
        mSuffixDayDeltY2Base = getSuffixDeltY2Base(mSuffixDay);
        mSuffixHourDeltY2Base = getSuffixDeltY2Base(mSuffixHour);
        mSuffixMinuteDeltY2Base = getSuffixDeltY2Base(mSuffixMinute);
        mSuffixSecondDeltY2Base = getSuffixDeltY2Base(mSuffixSecond);

        if (mSuffixBackgroundHeight == 0) {
            mSuffixBackgroundHeight = mSuffixTextHeight + mSuffixBackgroundPaddingTop + mSuffixBackgroundPaddingBottom;
        }

        float deltHeight = getTotalContentHeight() / 2;

        mSuffixDayBaseline = deltHeight - mSuffixDayDeltY2Base;
        mSuffixHourBaseline = deltHeight - mSuffixHourDeltY2Base;
        mSuffixMinuteBaseline = deltHeight - mSuffixMinuteDeltY2Base;
        mSuffixSecondBaseline = deltHeight - mSuffixSecondDeltY2Base;
    }

    private float getSuffixDeltY2Base(String suffixText) {
        float deltY = 0;

        if (!Utils.isNullOrEmpty(suffixText)) {
            Rect minRect = getTextBounds(suffixText, mSuffixTextPaint);

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = measureSize(1, getTotalContentWidth(), widthMeasureSpec);
        int height = measureSize(2, (int) getTotalContentHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    private int getTotalContentWidth() {
        //Total width of the content of all suffix.
        float suffixContentWidth = mSuffixTextTotalWidth
                + mSuffixCount * 2 * (mSuffixTextLetterSpacing + mSuffixBackgroundPaddingLeft + mSuffixBackgroundPaddingRight);

        //Total width of the content of all time.
        float timeContentWidth;
        float daysTextWidth = 0;
        float timeLetterSpacing = (mTimeTextLetterSpacing == 0 ? mTimeTextLetterBackgroundSpacing : mTimeTextLetterSpacing);
        int paddingPairs = 1;

        //Width of day time text.
        if (showDays && mDays > 0) {
            int dayLetterCount = getTimeString(mDays, 0).length();
            if (mTimeTextLetterBackgroundSpacing > 0) {
                paddingPairs = dayLetterCount;
            }

            daysTextWidth = dayLetterCount * mTimeTextLetterMeasuredWidth
                    + paddingPairs * (mTimeBackgroundPaddingLeft + mTimeBackgroundPaddingRight)
                    + (dayLetterCount - 1) * timeLetterSpacing;
        }

        /*
         * Width of the rest time text.
         */
        paddingPairs = 1;
        if (mTimeTextLetterBackgroundSpacing > 0) {
            paddingPairs = 2;
        }
        timeContentWidth = daysTextWidth
                + mTimeTypeCount * (mTimeTextLetterMeasuredWidth * 2 + timeLetterSpacing + paddingPairs * (mTimeBackgroundPaddingLeft + mTimeBackgroundPaddingRight));

        return (int) Math.ceil(suffixContentWidth + timeContentWidth);
    }

    private float getTotalContentHeight() {
        return Utils.getMaxNum(new float[]{
                mTimeTextHeight,
                mTimeBackgroundHeight,
                mSuffixTextHeight,
                mSuffixBackgroundHeight});
    }

    /**
     * Measure view size.
     *
     * @param specType    1 width, 2 height
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
        //Day
        if (showDays && mDays > 0) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mDays, 0), mSuffixDay, mSuffixDayTextWidth, mSuffixDayBaseline);
        }

        //Hour
        if (showHours) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mHours, 2), mSuffixHour, mSuffixHourTextWidth, mSuffixHourBaseline);
        }

        //Minute
        if (showMinutes) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mMinutes, 2), mSuffixMinute, mSuffixMinuteTextWidth, mSuffixMinuteBaseline);
        }

        //Second
        if (showSeconds) {
            left = drawTimeSuffixItem(canvas, left, getTimeString(mSeconds, 2), mSuffixSecond, mSuffixSecondTextWidth, mSuffixSecondBaseline);
        }
    }

    /**
     * Get the String of @time.
     * @param time
     * @param minLength The minimum length of @time String.
     * @return
     */
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
        //Draw time content.
        float timeBgWidth = mTimeBackgroundWidth;

        if (mTimeTextLetterBackgroundSpacing > 0) {//Split timeBackground.
            if (timeBgWidth < mTimeTextLetterMeasuredWidth) {
                timeBgWidth = mTimeTextLetterMeasuredWidth;
            }
            timeBgWidth += mTimeBackgroundPaddingLeft + mTimeBackgroundPaddingRight;

            for (int i = 0; i < timeString.length(); i++) {
                //Draw time background.
                if (mTimeBackground != null) {
                    mTimeBackground.setBounds((int) left,
                            (int) (getTotalContentHeight() - mTimeBackgroundHeight) / 2,
                            (int) (left + timeBgWidth),
                            (int) (getTotalContentHeight() + mTimeBackgroundHeight) / 2);
                    mTimeBackground.draw(canvas);
                }

                //Draw time text.
                canvas.drawText(String.valueOf(timeString.charAt(i)),
                        left + mTimeBackgroundPaddingLeft + (mTimeTextLetterMeasuredWidth) / 2,
                        (int) (getTotalContentHeight() + mTimeTextHeight) / 2, //Baseline of numeric character is the bottom of it.
                        mTimeTextPaint);

                if (i < timeString.length() - 1) {
                    left += mTimeBackgroundPaddingLeft + mTimeTextLetterMeasuredWidth + mTimeBackgroundPaddingRight + mTimeTextLetterBackgroundSpacing;
                } else {//The last character
                    left += mTimeBackgroundPaddingLeft + mTimeTextLetterMeasuredWidth + mTimeBackgroundPaddingRight;
                }
            }
        } else {//Time text is a whole.
            //Draw time background.
            if (timeBgWidth < mTimeTextLetterMeasuredWidth * timeString.length()) {
                timeBgWidth = mTimeTextLetterMeasuredWidth * timeString.length();
            }
            timeBgWidth += mTimeBackgroundPaddingLeft
                    + mTimeBackgroundPaddingRight
                    + (timeString.length() - 1) * mTimeTextLetterSpacing;

            if (mTimeBackground != null) {
                mTimeBackground.setBounds((int) left,
                        (int) (getTotalContentHeight() - mTimeBackgroundHeight) / 2,
                        (int) (left + timeBgWidth),
                        (int) (getTotalContentHeight() + mTimeBackgroundHeight) / 2);
                mTimeBackground.draw(canvas);
            }

            //Draw time text.
            for (int i = 0; i < timeString.length(); i++) {
                float temp_left = left;
                if (i == 0) {
                    temp_left += mTimeBackgroundPaddingLeft;
                }
                canvas.drawText(String.valueOf(timeString.charAt(i)),
                        temp_left + (mTimeTextLetterMeasuredWidth) / 2,
                        (int) (getTotalContentHeight() + mTimeTextHeight) / 2, //Baseline of numeric character is the bottom of it.
                        mTimeTextPaint);

                if (i == 0) {
                    left += mTimeBackgroundPaddingLeft + mTimeTextLetterMeasuredWidth + mTimeTextLetterSpacing;
                } else if (i < timeString.length() - 1) {
                    left += mTimeTextLetterMeasuredWidth + mTimeTextLetterSpacing;
                } else {//The last character
                    left += mTimeTextLetterMeasuredWidth + mTimeBackgroundPaddingRight;
                }
            }
        }

        //Draw suffix content.
        left += mSuffixTextLetterSpacing;
        float suffixBgWidth = mSuffixBackgroundWidth;
        if (suffixBgWidth < suffixTextWidth) {
            suffixBgWidth = suffixTextWidth;
        }
        suffixBgWidth += mSuffixBackgroundPaddingLeft + mSuffixBackgroundPaddingRight;

        //Draw suffix background.
        if (mSuffixBackground != null) {
            mSuffixBackground.setBounds((int) left,
                    (int) (getTotalContentHeight() - mSuffixBackgroundHeight) / 2,
                    (int) (left + suffixBgWidth),
                    (int) (getTotalContentHeight() + mSuffixBackgroundHeight) / 2);
            mSuffixBackground.draw(canvas);
        }

        //Draw suffix text.
        canvas.drawText(timeSuffix,
                left + mSuffixBackgroundPaddingLeft + suffixTextWidth / 2,
                (int) baseline,
                mSuffixTextPaint);

        left += mSuffixBackgroundPaddingLeft + suffixTextWidth + mSuffixBackgroundPaddingRight + mSuffixTextLetterSpacing;

        return left;
    }

   /* private Bitmap getBitmap(int drawableRes) {
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
    }*/

    class MyCountDownTimer extends CountDownTimer {

        long diffTime = 0;

        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            refresh();
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
//            diffTime = millisUntilFinished / 1000;
//
//            mDays = diffTime / (60 * 60 * 24);
//            mHours = (diffTime / (60 * 60)) % 24;
//            mMinutes = (diffTime / 60) % 60;
//            mSeconds = diffTime % 60;
//
//            refresh();//todo
        }
    }

    private void refresh() {
        postInvalidate();
        requestLayout();
    }

    private void resetZero() {
        mDays = 0;
        mHours = 0;
        mMinutes = 0;
        mSeconds = 0;
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