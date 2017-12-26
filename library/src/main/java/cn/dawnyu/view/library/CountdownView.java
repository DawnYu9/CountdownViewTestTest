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
import android.util.Log;
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
    private Paint mTimeTextPaint, mSuffixTextPaint;

    //----------------------------- attrs start ---------------------------//
    private boolean mIncludePad;
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
    private float mSuffixTextMargin;
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
    private float mTimeTextMeasuredHeight;
    private float mTimeTextTotalWidth;
    private float mTimeTextLetterMeasuredWidth;
    //    private float mSuffixTextTotalWidth;
    private float mTimeTextBaseline;
    private float mSuffixTextBaseline;
    private float mSuffixTextSingleWidth;
    private float mSuffixTextMeasuredHeight;
    private float mSuffixDayDeltY2Base;
    private float mSuffixHourDeltY2Base;
    private float mSuffixMinuteDeltY2Base;
    private float mSuffixSecondDeltY2Base;
    private float mSuffixDayBaseline;
    private float mSuffixHourBaseline;
    private float mSuffixMinuteBaseline;
    private float mSuffixSecondBaseline;
    private float mSuffixBaseline;

    private float drawTimeBackgroundWidth = 0;
    private float drawTimeBackgroundHeight = 0;
    private float drawTimeBackgroundPaddingLeft = 0;
    private float drawTimeBackgroundPaddingRight = 0;
    private float drawTimeBackgroundPaddingTop = 0;
    private float drawTimeBackgroundPaddingBottom = 0;

    private float drawSuffixBackgroundWidth = 0;
    private float drawSuffixBackgroundHeight = 0;
    private float drawSuffixBackgroundPaddingLeft = 0;
    private float drawSuffixBackgroundPaddingRight = 0;
    private float drawSuffixBackgroundPaddingTop = 0;
    private float drawSuffixBackgroundPaddingBottom = 0;
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
    private float mTimeBaseline;
    private float mTimeDeltY2Base;
    private Paint.FontMetrics mTimeTextPaintFontMetrics;
    private Paint.FontMetrics mSuffixTextPaintFontMetrics;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CountdownView, defStyleAttr, 0);

        mIncludePad = mTypedArray.getBoolean(R.styleable.CountdownView_includeFontPadding, false);
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

        mSuffixTextMargin = mTypedArray.getDimensionPixelSize(R.styleable.CountdownView_suffixTextMargin, Utils.getDip2Px(context, 0));

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

        getTimeByMillis(millisecond);

        init();

        stopCountdown();

        myCountDownTimer = new MyCountDownTimer(millisecond, 1000);
        myCountDownTimer.start();
    }

    private void getTimeByMillis(long millisecond) {
        mDays = (int) (millisecond / 1000 / (60 * 60 * 24));
        mHours = (int) ((millisecond / 1000 / (60 * 60)) % 24);
        mMinutes = (int) ((millisecond / 1000 / 60) % 60);
        mSeconds = (int) (millisecond / 1000 % 60);
    }

    private void init() {
        initPadding();

        initPaint();

        initSuffix();
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
//        mSuffixTextTotalWidth = 0;

        int mIndexDay = mTimeFormat.indexOf("dd");
        int mIndexHour = mTimeFormat.indexOf("hh");
        int mIndexMinute = mTimeFormat.indexOf("mm");
        int mIndexSecond = mTimeFormat.indexOf("ss");

        //Has day
        if (mIndexDay > -1 && mDays > 0) {
            mTimeTypeCount++;
            showDays = true;

            //Has day suffix
            if (mIndexDay + 2 < mIndexHour) {
                mSuffixDay = mTimeFormat.substring(mIndexDay + 2, mIndexHour);
                mSuffixDayTextWidth = mSuffixTextPaint.measureText(mSuffixDay);
//                mSuffixTextTotalWidth += mSuffixDayTextWidth;
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
//                mSuffixTextTotalWidth += mSuffixHourTextWidth;
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
//                mSuffixTextTotalWidth += mSuffixMinuteTextWidth;
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
//                mSuffixTextTotalWidth += mSuffixSecondTextWidth;
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

    private void measureDrawValues() {
        //Time values.
        drawTimeBackgroundPaddingLeft = mTimeBackgroundPaddingLeft;
        drawTimeBackgroundPaddingRight = mTimeBackgroundPaddingRight;
        drawTimeBackgroundPaddingTop = mTimeBackgroundPaddingTop;
        drawTimeBackgroundPaddingBottom = mTimeBackgroundPaddingBottom;

        drawSuffixBackgroundPaddingLeft = mSuffixBackgroundPaddingLeft;
        drawSuffixBackgroundPaddingRight = mSuffixBackgroundPaddingRight;
        drawSuffixBackgroundPaddingTop = mSuffixBackgroundPaddingTop;
        drawSuffixBackgroundPaddingBottom = mSuffixBackgroundPaddingBottom;

        //Time height values.
        if (mTimeBackgroundHeight > 0) { //If has specified the size of @timeBackgroundHeight.
            if (mTimeBackgroundHeight < mTimeTextMeasuredHeight) {
                drawTimeBackgroundHeight = mTimeTextMeasuredHeight;
                drawTimeBackgroundPaddingTop = drawTimeBackgroundPaddingBottom = 0;
            } else {
                /*
                 * If the size of @timeBackgroundHeight has been specified by users,
                 * the paddings will be recalculated regardless of the values specified by users.
                 */
                drawTimeBackgroundHeight = mTimeBackgroundHeight;
                drawTimeBackgroundPaddingTop = drawTimeBackgroundPaddingBottom = (mTimeBackgroundHeight - mTimeTextMeasuredHeight) / 2;
            }
        } else {
            /*
             * If the size of @timeBackgroundHeight has not been specified,
             * the @timeBackground will be drawn according to its @mTimeTextMeasuredHeight and @paddings.
             */
            drawTimeBackgroundHeight = mTimeTextMeasuredHeight + mTimeBackgroundPaddingTop + mTimeBackgroundPaddingBottom;
        }

        //Suffix height values.
        if (mSuffixBackgroundHeight > 0) {//If has specified the size of @suffixBackgroundHeight.
            if (mSuffixBackgroundHeight < mSuffixTextMeasuredHeight) {
                drawSuffixBackgroundHeight = mSuffixTextMeasuredHeight;
                drawSuffixBackgroundPaddingTop = drawSuffixBackgroundPaddingBottom = 0;
            } else {
                /*
                 * If the size of @suffixBackgroundHeight has been specified by users,
                 * the paddings will be recalculated regardless of the values specified by users.
                 */
                drawSuffixBackgroundHeight = mSuffixBackgroundHeight;
                drawSuffixBackgroundPaddingTop = drawSuffixBackgroundPaddingBottom = (mSuffixBackgroundHeight - mSuffixTextMeasuredHeight) / 2;
            }
        } else {
            /*
             * If the size of @suffixBackgroundHeight has not been specified,
             * the @suffixBackground will be drawn according to its @mSuffixTextMeasuredHeight and @paddings.
             */
            drawSuffixBackgroundHeight = mSuffixTextMeasuredHeight + mSuffixBackgroundPaddingTop + mSuffixBackgroundPaddingBottom;
        }
    }

    private void measureDrawWidthValuesWhenSplitting() {
        //If has specified the size of @timeBackgroundWidth.
        if (mTimeBackgroundWidth > 0) {
            if (mTimeBackgroundWidth < mTimeTextLetterMeasuredWidth) {
                drawTimeBackgroundWidth = mTimeTextLetterMeasuredWidth;
                drawTimeBackgroundPaddingLeft = drawTimeBackgroundPaddingRight = 0;
            } else {
                /*
                 * If the size of @timeBackgroundWidth has been specified by users,
                 * the paddings will be recalculated regardless of the values specified by users.
                 */
                drawTimeBackgroundWidth = mTimeBackgroundWidth;
                drawTimeBackgroundPaddingLeft = drawTimeBackgroundPaddingRight = (mTimeBackgroundWidth - mTimeTextLetterMeasuredWidth) / 2;
            }
        } else {
            /*
             * If the size of @timeBackgroundWidth has not been specified,
             * the @timeBackground will be drawn according to its @mTimeTextLetterMeasuredWidth and @paddings.
             */
            drawTimeBackgroundWidth = mTimeTextLetterMeasuredWidth + mTimeBackgroundPaddingLeft + mTimeBackgroundPaddingRight;
        }
    }

    private float getSuffixDeltY2Base(String suffixText) {
        float deltY = 0;

        if (!Utils.isNullOrEmpty(suffixText)) {
            Rect minRect = getTextBounds(suffixText, mSuffixTextPaint);

            /**
             * The size of Rect is different between different languages.
             */
            if (mSuffixTextMeasuredHeight < minRect.height()) {
                mSuffixTextMeasuredHeight = minRect.height();
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
        initTextBounds();

        int width = measureSize(1, getMeasuredTotalWidth(), widthMeasureSpec);
        int height = measureSize(2, getMeasuredTotalHeight(), heightMeasureSpec);

        setBaseline(height);

        measureDrawValues();

        setMeasuredDimension(width, height);
    }

    private void initTextBounds() {
        //Width of time letter.
        for (int i = 0; i < 10; i++) {
            Rect r = getTextBounds(String.valueOf(i), mTimeTextPaint);
            if (mTimeTextLetterMeasuredWidth < r.width()) {
                mTimeTextLetterMeasuredWidth = r.width();
            }
//            if (mTimeTextMeasuredHeight < r.height()) {
//                mTimeTextMeasuredHeight = r.height();
//            }
        }

        //Height.
        mTimeTextPaintFontMetrics = mTimeTextPaint.getFontMetrics();
        mSuffixTextPaintFontMetrics = mSuffixTextPaint.getFontMetrics();
        if (mIncludePad) {
            mTimeTextMeasuredHeight = mTimeTextPaintFontMetrics.bottom - mTimeTextPaintFontMetrics.top;
            mTimeTextBaseline = -mTimeTextPaintFontMetrics.top;

            mSuffixTextMeasuredHeight = mSuffixTextPaintFontMetrics.bottom - mSuffixTextPaintFontMetrics.top;
            mSuffixTextBaseline = -mSuffixTextPaintFontMetrics.top;
        } else {
            mTimeTextMeasuredHeight = mTimeTextPaintFontMetrics.descent - mTimeTextPaintFontMetrics.ascent;
            mTimeTextBaseline = -mTimeTextPaintFontMetrics.ascent;

            mSuffixTextMeasuredHeight = mSuffixTextPaintFontMetrics.descent - mSuffixTextPaintFontMetrics.ascent;
            mSuffixTextBaseline = -mSuffixTextPaintFontMetrics.ascent;
        }


//        Rect minRect = getTextBounds("4", mTimeTextPaint);
//        mTimeTextLetterMeasuredWidth = minRect.width();
//        mTimeTextMeasuredHeight = minRect.height();
//        mTimeDeltY2Base = minRect.height() / 2 + minRect.top;

        //Suffix.
//        mSuffixDayDeltY2Base = getSuffixDeltY2Base(mSuffixDay);
//        mSuffixHourDeltY2Base = getSuffixDeltY2Base(mSuffixHour);
//        mSuffixMinuteDeltY2Base = getSuffixDeltY2Base(mSuffixMinute);
//        mSuffixSecondDeltY2Base = getSuffixDeltY2Base(mSuffixSecond);
    }

    private void setBaseline(int height) {
//        mTimeBaseline = height / 2 - mTimeDeltY2Base;
        mTimeBaseline = (height + mTimeTextMeasuredHeight) / 2 - mTimeTextPaintFontMetrics.bottom;
        mSuffixBaseline = (height + mSuffixTextMeasuredHeight) / 2 - mSuffixTextPaintFontMetrics.bottom;

        float suffixDeltHeight = height / 2;
        mSuffixDayBaseline = suffixDeltHeight - mSuffixDayDeltY2Base;
        mSuffixHourBaseline = suffixDeltHeight - mSuffixHourDeltY2Base;
        mSuffixMinuteBaseline = suffixDeltHeight - mSuffixMinuteDeltY2Base;
        mSuffixSecondBaseline = suffixDeltHeight - mSuffixSecondDeltY2Base;

        mTimeTextBaseline += (height - mTimeTextMeasuredHeight) / 2;
        mSuffixTextBaseline += (height - mSuffixTextMeasuredHeight) / 2;
    }

    private float getMeasuredTotalWidth() {
        //Time.
        float totalTimeWidth = 0;
        int timeBackgroundCount = 0;
        if (mTimeTextLetterBackgroundSpacing > 0) {//Split timeBackground.
            measureDrawWidthValuesWhenSplitting();

            timeBackgroundCount = 6;
            if (showDays && mDays > 0) {
                timeBackgroundCount += getTimeString(mDays, 0).length();
                totalTimeWidth = timeBackgroundCount * drawTimeBackgroundWidth
                        + (getTimeString(mDays, 0).length() - 1 + 3) * mTimeTextLetterBackgroundSpacing;
            } else {
                totalTimeWidth = timeBackgroundCount * drawTimeBackgroundWidth
                        + 3 * mTimeTextLetterBackgroundSpacing;
            }
        } else {//Time text is a whole.
            timeBackgroundCount = 3;
            totalTimeWidth += timeBackgroundCount * measureTimeWidthWhenWhole(2);
            if (showDays && mDays > 0) {
                totalTimeWidth += measureTimeWidthWhenWhole(getTimeString(mDays, 0).length());
            }
        }

        //Suffix.
        int suffixTextMarginCount = mSuffixCount * 2;
        if (mTimeTypeCount == mSuffixCount) {
            suffixTextMarginCount--;
        }
        float totalSuffixWidth = measureSuffixWidth(mSuffixHourTextWidth)
                + measureSuffixWidth(mSuffixMinuteTextWidth)
                + measureSuffixWidth(mSuffixSecondTextWidth)
                + suffixTextMarginCount * mSuffixTextMargin;
        if (showDays && mDays > 0) {
            totalSuffixWidth += measureSuffixWidth(mSuffixDayTextWidth);
        }
        return totalTimeWidth + totalSuffixWidth;
    }

    private float measureSuffixWidth(float suffixWidth) {
        if (suffixWidth <= 0) {
            return 0;
        }

        if (mSuffixBackgroundWidth > 0) {//If has specified the size of @suffixBackgroundWidth.
            if (mSuffixBackgroundWidth < suffixWidth) {
                drawSuffixBackgroundWidth = suffixWidth;
                drawSuffixBackgroundPaddingLeft = drawSuffixBackgroundPaddingRight = 0;
            } else {
                /*
                 * If the size of @suffixBackgroundWidth has been specified by users,
                 * the paddings will be recalculated regardless of the values specified by users.
                 */
                drawSuffixBackgroundWidth = mSuffixBackgroundWidth;
                drawSuffixBackgroundPaddingLeft = drawSuffixBackgroundPaddingRight = (mSuffixBackgroundWidth - suffixWidth) / 2;
            }
        } else {
            /*
             * If the size of @suffixBackgroundWidth has not been specified,
             * the @suffixBackground will be drawn according to its @suffixTextWidth and @paddings.
             */
            drawSuffixBackgroundWidth = suffixWidth + mSuffixBackgroundPaddingLeft + mSuffixBackgroundPaddingRight;
        }

        return drawSuffixBackgroundWidth;
    }

    private float getMeasuredTotalHeight() {
        return Utils.getMaxNum(new float[]{
                drawTimeBackgroundHeight,
                drawSuffixBackgroundHeight});
    }

    /**
     * Measure view size.
     *
     * @param specType    1 width, 2 height
     * @param contentSize all content view size
     * @param measureSpec spec
     * @return measureSize
     */
    private int measureSize(int specType, float contentSize, int measureSpec) {
        double size;
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

        return (int) Math.ceil(size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float left = (canvas.getWidth() - getMeasuredTotalWidth()) / 2;
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
     *
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

    private float drawTimeSuffixItem(Canvas canvas, float left, String timeString, String suffix, float suffixTextWidth, float baseline) {
        //Draw time content.
        Paint.FontMetrics mTimeTextPaintFontMetrics = mTimeTextPaint.getFontMetrics();
        if (mTimeTextLetterBackgroundSpacing > 0) {//Split timeBackground.
            for (int i = 0; i < timeString.length(); i++) {
                //Draw time background.
                if (mTimeBackground != null) {
                    mTimeBackground.setBounds((int) left,
                            (int) (canvas.getHeight() - drawTimeBackgroundHeight) / 2,
                            (int) (left + drawTimeBackgroundWidth),
                            (int) (canvas.getHeight() + drawTimeBackgroundHeight) / 2);
                    mTimeBackground.draw(canvas);
                }

                //Draw time text.
                canvas.drawText(String.valueOf(timeString.charAt(i)),
                        left + drawTimeBackgroundPaddingLeft + (mTimeTextLetterMeasuredWidth) / 2,
//                        (canvas.getHeight() + mTimeTextMeasuredHeight) / 2,
                        mTimeTextBaseline,
                        mTimeTextPaint);

                left += drawTimeBackgroundWidth;
                if (i < timeString.length() - 1) {
                    left += mTimeTextLetterBackgroundSpacing;
                }
            }
        } else {//Time text is a whole.
            measureTimeWidthWhenWhole(timeString.length());

            //Draw time background.
            if (mTimeBackground != null) {
                mTimeBackground.setBounds((int) left,
                        (int) (canvas.getHeight() - drawTimeBackgroundHeight) / 2,
                        (int) (left + drawTimeBackgroundWidth),
                        (int) (canvas.getHeight() + drawTimeBackgroundHeight) / 2);
                mTimeBackground.draw(canvas);
            }

            //Draw time text.
            left += drawTimeBackgroundPaddingLeft;
            for (int i = 0; i < timeString.length(); i++) {
                canvas.drawText(String.valueOf(timeString.charAt(i)),
                        left + (mTimeTextLetterMeasuredWidth) / 2,
//                        (canvas.getHeight() + mTimeTextMeasuredHeight) / 2,
                        mTimeTextBaseline,
                        mTimeTextPaint);

                left += mTimeTextLetterMeasuredWidth;
                if (i < timeString.length() - 1) {
                    left += mTimeTextLetterSpacing;
                } else {//The last letter.
                    left += drawTimeBackgroundPaddingRight;
                }
            }
        }

        //Draw suffix content.
        left += mSuffixTextMargin;
        if (Utils.isNullOrEmpty(suffix)) {
            return left;
        }

        //Width values.
        if (mSuffixBackgroundWidth > 0) {//If has specified the size of @suffixBackgroundWidth.
            if (mSuffixBackgroundWidth < suffixTextWidth) {
                drawSuffixBackgroundWidth = suffixTextWidth;
                drawSuffixBackgroundPaddingLeft = drawSuffixBackgroundPaddingRight = 0;
            } else {
                /*
                 * If the size of @suffixBackgroundWidth has been specified by users,
                 * the paddings will be recalculated regardless of the values specified by users.
                 */
                drawSuffixBackgroundWidth = mSuffixBackgroundWidth;
                drawSuffixBackgroundPaddingLeft = drawSuffixBackgroundPaddingRight = (mSuffixBackgroundWidth - suffixTextWidth) / 2;
            }
        } else {
            /*
             * If the size of @suffixBackgroundWidth has not been specified,
             * the @suffixBackground will be drawn according to its @suffixTextWidth and @paddings.
             */
            drawSuffixBackgroundWidth = suffixTextWidth + mSuffixBackgroundPaddingLeft + mSuffixBackgroundPaddingRight;
        }

        //Draw suffix background.
        if (mSuffixBackground != null) {
            mSuffixBackground.setBounds((int) left,
                    (int) (canvas.getHeight() - drawSuffixBackgroundHeight) / 2,
                    (int) (left + drawSuffixBackgroundWidth),
                    (int) (canvas.getHeight() + drawSuffixBackgroundHeight) / 2);
            mSuffixBackground.draw(canvas);
        }

        //Draw suffix text.
        canvas.drawText(suffix,
                left + drawSuffixBackgroundPaddingLeft + suffixTextWidth / 2,
//                baseline,
                mSuffixTextBaseline,
                mSuffixTextPaint);

        left += drawSuffixBackgroundWidth + mSuffixTextMargin;

        return left;
    }

    private float measureTimeWidthWhenWhole(int letterCount) {
        //If has specified the size of @timeBackgroundWidth.
        if (mTimeBackgroundWidth > 0) {
            if (mTimeBackgroundWidth < letterCount * mTimeTextLetterMeasuredWidth + (letterCount - 1) * mTimeTextLetterSpacing) {
                drawTimeBackgroundWidth = letterCount * mTimeTextLetterMeasuredWidth + (letterCount - 1) * mTimeTextLetterSpacing;
                drawTimeBackgroundPaddingLeft = drawTimeBackgroundPaddingRight = 0;
            } else {
                /*
                 * If the size of @timeBackgroundWidth has been specified by users,
                 * the paddings will be recalculated regardless of the values specified by users.
                 */
                drawTimeBackgroundWidth = mTimeBackgroundWidth;
                drawTimeBackgroundPaddingLeft = drawTimeBackgroundPaddingRight
                        = (mTimeBackgroundWidth - letterCount * mTimeTextLetterMeasuredWidth - (letterCount - 1) * mTimeTextLetterSpacing) / 2;
            }
        } else {
            /*
             * If the size of @timeBackgroundWidth has not been specified,
             * the @timeBackground will be drawn according to its @mTimeTextLetterMeasuredWidth and @paddings.
             */
            drawTimeBackgroundWidth = letterCount * mTimeTextLetterMeasuredWidth
                    + (letterCount - 1) * mTimeTextLetterSpacing
                    + mTimeBackgroundPaddingLeft + mTimeBackgroundPaddingRight;
        }

        return drawTimeBackgroundWidth;
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
            getTimeByMillis(millisUntilFinished);

            refresh();
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