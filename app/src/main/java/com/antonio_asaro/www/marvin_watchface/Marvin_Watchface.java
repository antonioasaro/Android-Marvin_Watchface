package com.antonio_asaro.www.marvin_watchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationText;
import android.support.wearable.complications.SystemProviders;
import android.support.wearable.complications.rendering.ComplicationDrawable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class Marvin_Watchface extends CanvasWatchFaceService {
    private static final String TAG = "Android-Marvin_Watchface";
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    /**
     * Update rate in milliseconds for interactive mode. Defaults to one second
     * because the watch face needs to update seconds in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.MILLISECONDS.toMillis(500);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    // Complication declarations
    private static final int COMPLICATION_ID = 100;
    private static final int[] COMPLICATION_IDS = {COMPLICATION_ID};
    private static final int[][] COMPLICATION_SUPPORTED_TYPES = {
            {ComplicationData.TYPE_SHORT_TEXT}
    };

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<Marvin_Watchface.Engine> mWeakReference;

        public EngineHandler(Marvin_Watchface.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            Marvin_Watchface.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private float mXOffset;
        private float mYOffset;
        private Paint mBackgroundPaint;
        private Paint mTimePaint;
        private Paint mDatePaint;
        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;
        private boolean mAmbient;

        Bitmap mSpaceBitmap;
        Bitmap mDarkBitmap;
        Bitmap mMarsBitmap;
        Bitmap mEarthBitmap;
        Bitmap mSunBitmap;
        Bitmap mMoonBitmap;
        Bitmap mConnBitmap;
        Bitmap mFeetBitmap;
        Bitmap mFlagBitmap;
        Bitmap mMarvinBitmap;
        float mLineHeight;
        int abc = 0;

        float mBatteryLevel;
        Intent batteryStatus;
        Paint mBatteryPaint;

        Date mDate;
        SimpleDateFormat mDayOfWeekFormat;
        SimpleDateFormat mDayDateFormat;
        java.text.DateFormat mDateFormat;

        @Override
        public void onCreate(SurfaceHolder holder) {
            Log.d(TAG, "OnCreate()");
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(Marvin_Watchface.this)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();

            Resources resources = Marvin_Watchface.this.getResources();
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);

            //// Initialize resources
            Drawable mSpaceDrawable = getResources().getDrawable(R.drawable.space, null);
            Drawable mDarkDrawable = getResources().getDrawable(R.drawable.dark, null);
            Drawable mMarsDrawable = getResources().getDrawable(R.drawable.mars, null);
            Drawable mEarthDrawable = getResources().getDrawable(R.drawable.earth, null);
            Drawable mSunDrawable = getResources().getDrawable(R.drawable.sun, null);
            Drawable mMoonDrawable = getResources().getDrawable(R.drawable.moon, null);
            Drawable mConnDrawable = getResources().getDrawable(R.drawable.connect, null);
            Drawable mFeetDrawable = getResources().getDrawable(R.drawable.feet, null);
            Drawable mFlagDrawable = getResources().getDrawable(R.drawable.flag, null);
            Drawable mMarvinDrawable = getResources().getDrawable(R.drawable.marvin, null);
            mSpaceBitmap = ((BitmapDrawable) mSpaceDrawable).getBitmap();
            mDarkBitmap = ((BitmapDrawable) mDarkDrawable).getBitmap();
            mMarsBitmap = ((BitmapDrawable) mMarsDrawable).getBitmap();
            mEarthBitmap = ((BitmapDrawable) mEarthDrawable).getBitmap();
            mSunBitmap = ((BitmapDrawable) mSunDrawable).getBitmap();
            mMoonBitmap = ((BitmapDrawable) mMoonDrawable).getBitmap();
            mConnBitmap = ((BitmapDrawable) mConnDrawable).getBitmap();
            mFeetBitmap = ((BitmapDrawable) mFeetDrawable).getBitmap();
            mFlagBitmap = ((BitmapDrawable) mFlagDrawable).getBitmap();
            mMarvinBitmap = ((BitmapDrawable) mMarvinDrawable).getBitmap();
            mBatteryLevel = -1;
            mBatteryPaint = new Paint();
            mDate = new Date();
            initFormats();
            mDatePaint = new Paint();
            mDatePaint.setTypeface(NORMAL_TYPEFACE);
            mDatePaint.setAntiAlias(true);
            mDatePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_date));
            mLineHeight = resources.getDimension(R.dimen.digital_line_height);
            initializeComplications();


            // Initializes background.
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.background));

            // Initializes Watch Face.
            mTimePaint = new Paint();
            mTimePaint.setTypeface(NORMAL_TYPEFACE);
            mTimePaint.setAntiAlias(true);
            mTimePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_time));
        }

        private void initFormats() {
            mDayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            mDayOfWeekFormat.setCalendar(mCalendar);
            mDateFormat = DateFormat.getDateFormat(Marvin_Watchface.this);
            mDateFormat.setCalendar(mCalendar);
            mDayDateFormat = new SimpleDateFormat("EEE MMM d", Locale.getDefault());
            mDayDateFormat.setCalendar(mCalendar);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            Marvin_Watchface.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            Marvin_Watchface.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            Resources resources = Marvin_Watchface.this.getResources();
            mXOffset = resources.getDimension(R.dimen.digital_x_offset);
            float timeSize = resources.getDimension(R.dimen.digital_time_size);
            float dateSize = resources.getDimension(R.dimen.digital_date_size);
            mTimePaint.setTextSize(timeSize);
            mDatePaint.setTextSize(dateSize);
            Log.d(TAG, "offsets " + mXOffset + ", " + timeSize + " - " + dateSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            mAmbient = inAmbientMode;
            if (mLowBitAmbient) {
                mTimePaint.setAntiAlias(!inAmbientMode);
                mDatePaint.setAntiAlias(!inAmbientMode);
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
////                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT).show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
        Log.d(TAG, "OnDraw()");

            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            int hour = mCalendar.get(Calendar.HOUR);
            int minute = mCalendar.get(Calendar.MINUTE);
            int second = mCalendar.get(Calendar.SECOND);
            int millisec = mCalendar.get(Calendar.MILLISECOND);

            //// Draw the background.
            if (!isInAmbientMode()) {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
                Paint shading = new Paint();
                for (int i=0; i<bounds.height()/4; i++) {
                    shading.setARGB(0x80, 4*i/2, 4*i/2, 4*i/2);;
                    canvas.drawRect(0, 4*i, bounds.width(), 4*i+4, shading);
                }
                canvas.drawBitmap(mSpaceBitmap, 32, 36, null);
                canvas.drawBitmap(mMarsBitmap, 40, 332, null);
                canvas.drawBitmap(mEarthBitmap, 330, 120, null);
                canvas.drawBitmap(mFeetBitmap, 110, 88, null);
                canvas.drawBitmap(mFlagBitmap, 276, 276, null);
                canvas.drawBitmap(mMarvinBitmap, 42, 226, null);
            } else {
                mBackgroundPaint.setARGB(0xFF, 0x00, 0x00, 0x00);
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
                canvas.drawBitmap(mDarkBitmap, 20, 66, null);
                if ((mCalendar.get(Calendar.HOUR_OF_DAY)>=7) && (mCalendar.get(Calendar.HOUR_OF_DAY)<=(7+12))) {
                    canvas.drawBitmap(mSunBitmap, 324, 156, null);
                } else {
                    canvas.drawBitmap(mMoonBitmap, 324, 156, null);
                }
            }

            String time_str = String.format("%d:%02d", hour, minute);
            canvas.drawText(time_str, mXOffset, mYOffset, mTimePaint);

            int week_yoff, date_yoff;
            week_yoff = 0; date_yoff = 0;
            if (!isInAmbientMode()) {
                week_yoff = 176; date_yoff = 184;
            }
            if (getPeekCardPosition().isEmpty()) {
                if (!isInAmbientMode()) {
                    canvas.drawText(mDayDateFormat.format(mDate), mXOffset + 44, mYOffset + mLineHeight - week_yoff - 5, mDatePaint);
                } else {
                    canvas.drawText(mDayOfWeekFormat.format(mDate), mXOffset + 64, mYOffset + mLineHeight - week_yoff, mDatePaint);
                    canvas.drawText(mDateFormat.format(mDate), mXOffset + 64, mYOffset + mLineHeight * 2 - date_yoff - 4, mDatePaint);
                }
            }

            //// Draw step complication
            if (!isInAmbientMode()) {
                drawComplications(canvas, now);
            }

            if ((mBatteryLevel == -1) || ((minute % 15) == 0)) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                mBatteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            }

            //// Draw battery indicator
            if (!isInAmbientMode()) {
                int b_xoff, b_yoff;
                b_xoff = 18; b_yoff = 84;
                mBatteryPaint.setARGB(0xFF, 0x00, 0xFF, 0x00);
                if (mBatteryLevel <= 75) { mBatteryPaint.setARGB(0xFF, 0xFF, 0xFF, 0x00); }
                if (mBatteryLevel <= 50) { mBatteryPaint.setARGB(0xFF, 0xFF, 0xA5, 0x00); }
                if (mBatteryLevel <= 25) { mBatteryPaint.setARGB(0xFF, 0xFF, 0x00, 0x00); }
                canvas.drawRect(18 + b_xoff, 63 + b_yoff, 16 + b_xoff + 22, 63 + b_yoff + 10, mBatteryPaint);
                canvas.drawRect(13 + b_xoff, 68 + b_yoff, 13 + b_xoff + 30, 68 + b_yoff + 52, mBatteryPaint);
                mBatteryPaint.setARGB(0xFF, 0x00, 0x00, 0x00);
                canvas.drawRect(16 + b_xoff, 72 + b_yoff, 16 + b_xoff + 24, 72 + b_yoff + 46 * (100 - mBatteryLevel) / 100, mBatteryPaint);
            }

            //// Draw the sinusoidal bolt.
            if (!isInAmbientMode()) {
                int xoff, yoff, tsec, msec, toff;
                int xpos, ypos;
                Paint boltpaint = new Paint();

                xoff = 170; yoff = 294;
                boltpaint.setARGB(0xFF, 0x0F, 0xDD, 0xAF);
                boltpaint.setTextSize(24);
                tsec = second; msec = millisec/500;
                String tstr = Integer.toString(tsec);
                if (tsec < 10) { tstr = "0" + tstr; };
                if (tsec == 0) { tsec = 60; }

                toff = 2 * tsec + msec;
                xpos = xoff + toff;

                for (int i = 1; i <= toff; i++) {
                    ypos = yoff + (int) (10 * Math.cos((i/2) % (360 / 30)));
                    if (i != toff) {
                        canvas.drawCircle(xoff + i, ypos, 1.5f , boltpaint);
                    } else {
                        if (tsec != 60) {
                            canvas.drawText(tstr, xpos, ypos, boltpaint);
                        } else {
                            boltpaint.setStrokeWidth(6);
                            boltpaint.setARGB(0xFF, 0xFF, 0x00, 0x00);
                            xpos = xpos + 8; ypos = ypos - 12;
                            canvas.drawLine(xpos - 16, ypos + 0,  xpos + 16, ypos + 0,  boltpaint);
                            canvas.drawLine(xpos + 0,  ypos - 16, xpos + 0,  ypos + 16, boltpaint);
                            canvas.drawLine(xpos - 16, ypos - 16, xpos + 16, ypos + 16, boltpaint);
                            canvas.drawLine(xpos - 16, ypos + 16, xpos + 16, ypos - 16, boltpaint);
                        }
                    }
                }
            }
        }

        /**
             * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
             * or stops it if it shouldn't be running but currently is.
             */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        //// Complications
        Paint mComplicationPaint;
        private int mComplicationsX = 153;
        private int mComplicationsY = 112;
        private SparseArray<ComplicationData> mActiveComplicationDataSparseArray;
        private SparseArray<ComplicationDrawable> mComplicationDrawableSparseArray;

        private void initializeComplications() {
            Log.d(TAG, "initializeComplications()");

            mActiveComplicationDataSparseArray = new SparseArray<>(COMPLICATION_IDS.length);
            mComplicationPaint = new Paint();
            mComplicationPaint.setARGB(0xFF, 0xCC, 0xCC, 0xCC);
            mComplicationPaint.setTextSize(32);
            mComplicationPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            mComplicationPaint.setAntiAlias(true);

            setDefaultSystemComplicationProvider(COMPLICATION_ID, SystemProviders.STEP_COUNT, ComplicationData.TYPE_SHORT_TEXT);
            setActiveComplications(COMPLICATION_IDS);
        }

        @Override
        public void onComplicationDataUpdate(int complicationId, ComplicationData complicationData) {
            Log.d(TAG, "onComplicationDataUpdate() id: " + complicationId);
            mActiveComplicationDataSparseArray.put(complicationId, complicationData);
            invalidate();
        }

        private void drawComplications(Canvas canvas, long currentTimeMillis) {
            ComplicationData complicationData;

            for (int i = 0; i < COMPLICATION_IDS.length; i++) {
                complicationData = mActiveComplicationDataSparseArray.get(COMPLICATION_IDS[i]);
                if ((complicationData != null)
                        && (complicationData.isActive(currentTimeMillis))
                        && (complicationData.getType() == ComplicationData.TYPE_SHORT_TEXT)) {

                    ComplicationText mainText = complicationData.getShortText();
                    CharSequence complicationMessage = mainText.getText(getApplicationContext(), currentTimeMillis);
                    canvas.drawText(
                            complicationMessage,
                            0,
                            complicationMessage.length(),
                            mComplicationsX,
                            mComplicationsY,
                            mComplicationPaint);
                }
            }
        }
    }
}
