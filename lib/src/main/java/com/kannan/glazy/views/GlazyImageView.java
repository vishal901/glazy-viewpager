package com.kannan.glazy.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.support.v7.graphics.Palette;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kannan.glazy.Utils;

import java.util.ArrayList;


public class GlazyImageView extends View {

    private String TAG = "glazy"; //GlazyImageView.class.getSimpleName();

    private Context mContext;

    private int mImageRes = -1;
    private Bitmap mImageBitmap;
    private Paint mBitmapPaint;
    private Shader mGradientShader;
    private Paint mGradientPaint;
    private Paint mTintPaint;
    private TextPaint mTitleTextPaint;
    private String mTitleText;
    private int mTitleTextColor;
    private int mTitleTextSize;
    private TextPaint mSubTitleTextPaint;
    private String mSubTitleText;
    private int mSubTitleTextColor;
    private int mSubTitleTextSize;
    private int mTextMargin;
    private int mTitleTextX;
    private int mTitleTextY;
    private int mSubTitleTextX;
    private int mSubTitleTextY;
    private ArrayList<Path> mPathsFull;
    private ArrayList<Path> mPathsScaled;
    private Matrix mScaleMatrix;

    private boolean mAutoTint;
    private int mTintColor;
    private int mTintAlpha;

    private float mHeight;
    private float mWidth;

    private ImageCutType mCutType;
    private int mCutAngle;
    private int mCutCount;
    private int mCutHeight;
    private int mCutPhaseShift;
    private float mOpenFactor;

    private RectF mBitmapScaleRectOriginal;
    private RectF mBitmapScaleRect;

    private boolean flag = false;

    public enum ImageCutType {
        LINE(0),
        ARC(1),
        WAVE(2);

        int mType;
        ImageCutType(int type) {
            mType = type;
        }
    }

    public GlazyImageView(Context context) {
        super(context);
        init(context, null);
    }

    public GlazyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GlazyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mContext = context;

//        if(attrs != null){
//            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GlazyImageView);
//            try {
//                if (array.hasValue(R.styleable.GlazyImageView_src))
//                    mImageRes = array.getResourceId(R.styleable.GlazyImageView_src, -1);
//                if (array.hasValue(R.styleable.GlazyImageView_coverHeight))
//                    mCoverHeight = array.getDimensionPixelSize(R.styleable.GlazyImageView_coverHeight, 100);
//                if (array.hasValue(R.styleable.GlazyImageView_slopeHeight))
//                    mSlopeHeight = array.getDimensionPixelSize(R.styleable.GlazyImageView_slopeHeight, 100);
//                if (array.hasValue(R.styleable.GlazyImageView_tintColor))
//                    mCoverTint = array.getColor(R.styleable.GlazyImageView_tintColor, Color.YELLOW);
//            } finally {
//                array.recycle();
//            }
//        }

        setLayerType(LAYER_TYPE_NONE, null);

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setStyle(Paint.Style.FILL);

        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGradientPaint.setStyle(Paint.Style.FILL);

        mTintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTintPaint.setStyle(Paint.Style.FILL);

        mTitleTextColor = Color.WHITE;
        mTitleText = "";
        mTitleTextSize = Utils.dpToPx(20, mContext);
        mTextMargin = Utils.dpToPx(10, mContext);
        mTitleTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTitleTextPaint.setTextSize(mTitleTextSize);
        mTitleTextPaint.setTextAlign(Paint.Align.LEFT);
        mTitleTextPaint.setTypeface(Typeface.create("Helvetica", Typeface.BOLD));
//        mTitleTextPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
        mTitleTextPaint.setStyle(Paint.Style.FILL);
        mTitleTextPaint.setColor(mTitleTextColor);

        mSubTitleTextColor = Color.GRAY;
        mSubTitleText = "ACTORACTORACTORACTORACTORACTORACTORACTORACTORACTORACTOR";
        mSubTitleTextSize = Utils.dpToPx(10, mContext);
        mSubTitleTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSubTitleTextPaint.setTextSize(mSubTitleTextSize);
        mSubTitleTextPaint.setTextAlign(Paint.Align.LEFT);
        mSubTitleTextPaint.setTypeface(Typeface.create("Helvetica", Typeface.BOLD));
//        mTitleTextPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
        mSubTitleTextPaint.setStyle(Paint.Style.FILL);
        mSubTitleTextPaint.setColor(mSubTitleTextColor);
//        mTitleTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));



        mBitmapScaleRectOriginal = new RectF();
        mPathsFull = new ArrayList<>();
        mPathsScaled = new ArrayList<>();
        mScaleMatrix = new Matrix();

        mAutoTint = false;
        mTintColor = Color.parseColor("#cc000000");
        mTintAlpha = 100;

        mCutCount = 3;
        mCutAngle = 10;
        mCutHeight = 100;
        mOpenFactor = 0f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        Log.i(TAG, "onMeasure");
        prepareDrawingElements();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i(TAG, "layout");
//        update(mOpenFactor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mImageRes != -1 && mImageBitmap != null) {
            mTintPaint.setAlpha(mTintAlpha);
            for (int i = 1; i < mPathsScaled.size(); i++) {
                canvas.drawPath(mPathsScaled.get(i), mTintPaint);
            }
            if (mPathsScaled.size() > 0) {
                canvas.clipPath(mPathsScaled.get(0));
                canvas.drawBitmap(mImageBitmap, null, mBitmapScaleRect, mBitmapPaint);
//                canvas.clipRect(mBitmapScaleRect);
                canvas.drawPath(mPathsScaled.get(0), mGradientPaint);
                mTintPaint.setAlpha((int) (255 * (1-mOpenFactor)));
                canvas.drawPath(mPathsScaled.get(0), mTintPaint);
//                canvas.drawPaint(mGradientPaint);
            }
            float f = mOpenFactor - 0.5f;
            if (f > 0) {
                mTitleTextPaint.setAlpha((int) (255 * (f * 2)));
                mSubTitleTextPaint.setAlpha((int) (255 * (f * 2)));
                canvas.drawText(mTitleText, mTitleTextX, mTitleTextY, mTitleTextPaint);
                canvas.drawText(mSubTitleText, mSubTitleTextX, mSubTitleTextY, mSubTitleTextPaint);
            }
        }
    }

    public void update(float factor) {
//        Log.i(TAG, "update");
//        if (Math.abs(mOpenFactor - factor) < 0.001) {
//            return;
//        }
        mOpenFactor = factor;
        mScaleMatrix.setScale(1f, factor);
        mPathsScaled.clear();
        for (int i = 0; i < mPathsFull.size(); i++) {
            Path path = new Path();
            mPathsFull.get(i).transform(mScaleMatrix, path);
            mPathsScaled.add(i, path);
        }

        mBitmapScaleRect = new RectF(mBitmapScaleRectOriginal);
        mBitmapScaleRect.offsetTo(
                mBitmapScaleRectOriginal.left,
                mBitmapScaleRectOriginal.top - (mBitmapScaleRectOriginal.height() / 2 * (1 - mOpenFactor))
        );

        RectF bound = new RectF();
        mPathsScaled.get(0).computeBounds(bound, true);

        mSubTitleTextX = mTextMargin;
        mSubTitleTextY = (int) (bound.height() -
                (mCutHeight * mOpenFactor + mSubTitleTextSize));

        mTitleTextX = mTextMargin;
        mTitleTextY = mSubTitleTextY - 2 * mSubTitleTextSize;
//        mTitleTextY = (int) (bound.height() - ((bound.height() / 5) * (1 )));

        postInvalidate();
    }

    private void prepareDrawingElements() {
        if (mWidth != 0 && mHeight != 0) {
            prepareBitmap();
            createPaths();
            prepareTints();
            prepareText();
        }
    }

    private void createPaths() {
        Log.i(TAG, "createPaths" + mWidth + " " + mHeight + " " + mOpenFactor);
        mPathsFull.clear();
        float angleIncrement = mCutAngle / ((float) 1.5 * mCutCount);
        float cutHeightIncrement = mCutHeight / ((float) 1.5 * mCutCount);

        switch (mCutType) {
            case LINE:
                for (int i = 0; i < mCutCount; i += 1) {
                    mPathsFull.add(
                            Utils.getLinePath(
                                    mWidth,
                                    mHeight,
                                    mCutAngle - angleIncrement * i
                            )
                    );
                }
                break;
            case ARC:
                for (int i = 0; i < mCutCount; i += 1) {
                    mPathsFull.add(
                            Utils.getWavePath(
                                    mWidth,
                                    mHeight,
                                    mCutHeight - cutHeightIncrement * i,
                                    0.05f,
                                    mCutPhaseShift
                            )
                    );
                }
                break;
            case WAVE:
                for (int i = 0; i < mCutCount; i += 1) {
                    mPathsFull.add(
                            Utils.getWavePath(
                                    mWidth,
                                    mHeight,
                                    (mCutHeight - cutHeightIncrement * i) / 2,
                                    0.1f,
                                    mCutPhaseShift
                            )
                    );
                }
                break;
            default:
                Log.e(TAG, "Unknown ImageCutType enum : " + mCutType + "\n"
                + "switching to default value : " + "");
        }
//            RectF bound = new RectF();
//            mPathsFull.get(0).computeBounds(bound, true);
//            Log.i("app", bound.toString());

    }

    private void prepareTints() {
        if (mAutoTint) {
            pickColorFromBitmapAsync();
        }
        mGradientShader = Utils.getLinearGradient(
                mWidth, mHeight, Color.parseColor("#00000000"), mTintColor);
        mGradientPaint.setShader(mGradientShader);
        mGradientPaint.setAlpha(255);
        mTintPaint.setColor(mTintColor);
        mTintPaint.setAlpha(255);
    }

    private void prepareText() {
        if (mTitleText != null && !mTitleText.trim().equals("")) {
            float availableSpace = (mWidth - 2 * mTextMargin) * 0.75f;
            mTitleText = TextUtils.ellipsize(
                    mTitleText,
                    mTitleTextPaint,
                    availableSpace,
                    TextUtils.TruncateAt.END
            ).toString();
        }
        if (mSubTitleText != null && !mSubTitleText.trim().equals("")) {
            float availableSpace = (mWidth - 2 * mTextMargin) * 0.5f;
            mSubTitleText = TextUtils.ellipsize(
                    mSubTitleText,
                    mSubTitleTextPaint,
                    availableSpace,
                    TextUtils.TruncateAt.END
            ).toString();
        }
    }

    private void prepareBitmap() {
        Log.i(TAG, "prepare bitmap");
        if (mImageRes != -1) {
            try {
                mImageBitmap = decodeSampledBitmapFromResource(mContext.getResources(), mImageRes, mWidth, mHeight);
            } catch(OutOfMemoryError e) {
                mImageBitmap = null;
                Log.e(TAG, "Image too large to load: \n" + e.getMessage());
            } catch(Exception e) {
                Log.e(TAG, "Could not load bitmap image : \n" + e.getMessage());
            }

//            prepare scaleRect for drawing bitmap
            float scaleRatio = 1;
            if (mWidth != mImageBitmap.getWidth()) {
                scaleRatio = mWidth / mImageBitmap.getWidth();
            }
            if (scaleRatio * mImageBitmap.getHeight() < mHeight) {
                scaleRatio = mHeight / mImageBitmap.getHeight();
            }
            float requiredHeight = mImageBitmap.getHeight() * scaleRatio;
            float requiredWidth = mImageBitmap.getWidth() * scaleRatio;
            int y = (int) ((requiredHeight / 2) - (mHeight / 2));
            int x = (int) ((requiredWidth / 2) - (mWidth / 2));
            if (x > 0) x = -x;
            if (y > 0) y = -y;

            mBitmapScaleRectOriginal.set(x, y, x + requiredWidth, y + requiredHeight);
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(
            Resources res, int resId, float reqWidth, float reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, float reqWidth, float reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private Bitmap squareCropBitmap(Bitmap bitmap) {
        int sideDimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
        return ThumbnailUtils.extractThumbnail(
                bitmap, sideDimension, sideDimension, ThumbnailUtils.OPTIONS_RECYCLE_INPUT
        );
    }

    private void pickColorFromBitmapAsync() {
        if (mImageBitmap != null) {
            Palette.from(mImageBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int defaultColor = 0x000000;
                    if (palette.getDominantColor(defaultColor) != 0) {
                        mTintColor = Math.abs(palette.getDominantColor(defaultColor));
                    } else if (palette.getDarkVibrantColor(defaultColor) != 0) {
                        mTintColor = Math.abs(palette.getDarkVibrantColor(defaultColor));
                    } else if (palette.getDarkMutedColor(defaultColor) != 0) {
                        mTintColor = Math.abs(palette.getDarkMutedColor(defaultColor));
                    } else if (palette.getMutedColor(defaultColor) != 0) {
                        mTintColor = Math.abs(palette.getMutedColor(defaultColor));
                    } else {
                        Log.i(TAG, "Could not pick color from bitmap, using default tint : " + "");
                    }
                }
            });
        }
    }

    private void pickColorFromBitmap() {
        if (mImageBitmap != null) {
            Palette palette = Palette.from(mImageBitmap).generate();

            int defaultColor = 0x000000;
            if (palette.getDominantColor(defaultColor) != 0) {
                mTintColor = Math.abs(palette.getDominantColor(defaultColor));
            } else if (palette.getDarkVibrantColor(defaultColor) != 0) {
                mTintColor = Math.abs(palette.getDarkVibrantColor(defaultColor));
            } else if (palette.getDarkMutedColor(defaultColor) != 0) {
                mTintColor = Math.abs(palette.getDarkMutedColor(defaultColor));
            } else if (palette.getMutedColor(defaultColor) != 0) {
                mTintColor = Math.abs(palette.getMutedColor(defaultColor));
            } else {
                Log.i(TAG, "Could not pick color from bitmap, using default tint : " + "");
            }
        }
    }

    public void setImageRes(int imgRes) {
        mImageRes = imgRes;
//        postInvalidate();
    }

    public void setCutType(ImageCutType cutType) {
        mCutType = cutType;
    }

    public void setCutAngle(int angle) {
        angle = Math.abs(angle) % 180;
        if ((angle >= 0 && angle <= 45) || (angle >= 135 && angle <= 180)) {
            mCutAngle = angle;
        } else {
            mCutAngle = 15;
        }
    }

    public void setCutCount(int count) {
        count = Math.abs(count);
        if (count <= 0) count = 0;
        if (count > 4)  count = 4;
        mCutCount = count;
    }

    public void setCutHeight(int height) {
        mCutHeight = height;
    }

    public void setCutPhaseShift(int phaseShift) {
        mCutPhaseShift = phaseShift;
    }

    public void setTintColor(int tintColor) {
        mTintColor = tintColor;
        mAutoTint = false;
    }

    public void setTitleText(String title) {
        mTitleText = title;
    }

    public void setTitleTextColor(int color) {
        mTitleTextColor = color;
    }

    public void setTitleTextSize(int size) {
        mTitleTextSize = size;
    }

    public void setSubTitleText(String title) {
        mSubTitleText = title;
    }

    public void setSubTitleTextColor(int color) {
        mSubTitleTextColor = color;
    }

    public void setSubTitleTextSize(int size) {
        mSubTitleTextSize = size;
    }

}
