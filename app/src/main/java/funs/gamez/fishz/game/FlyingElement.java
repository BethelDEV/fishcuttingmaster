package funs.gamez.fishz.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;

import funs.gamez.fishz.helps.Utils;

/**
 * 界面上飞的：鱼、虾、金属锚
 */
public class FlyingElement {
    private static final Paint scorePaint;

    static {
        scorePaint = new Paint();
        scorePaint.setColor(Color.YELLOW);
        scorePaint.setShadowLayer(8, 8, 8, Color.WHITE);
        scorePaint.setTextSize(60);
    }

    private final Matrix mSpinMatrix = new Matrix(); // 旋转
    private Bitmap mBitmap;
    private double mX; // x坐标
    private double mY;
    private double mXv;
    private double mYv;
    private Paint mPaint;
    private double mScale; // 缩放比例
    private double mSpin; // 旋转角度
    private double mSpinv; // 旋转速度
    private boolean fade; // 渐变
    private boolean mHit; // 是否被碰
    private int mValue;
    private String mText;
    private boolean mBoom;

    public FlyingElement(Bitmap bitmap) {
        mBitmap = bitmap;

        mPaint = new Paint();
        mScale = (float) Utils.getRand(.1, .4);
    }


    public static FlyingElement getCopy(FlyingElement item) {
        FlyingElement item2 = new FlyingElement(item.mBitmap);

        item2.mX = item.mX;
        item2.mXv = item.mXv;
        item2.mY = item.mY;
        item2.mYv = item.mYv;
        item2.mSpinv = item.mSpinv;
        item2.mSpin = item.mSpin;
        item2.mBoom = item.mBoom;
        item2.mText = item.mText;
        item2.fade = item.fade;

        item2.mPaint = new Paint(item.mPaint);
        item2.mScale = item.mScale;
        item2.mHit = item.mHit;
        item2.mValue = item.mValue;

        return item2;
    }

    // 从Bundle创建
    public static FlyingElement create(Bundle bundle) {

        Bitmap bitmap = bundle.getParcelable("mBitmap");
        if (bitmap == null) {
            return null;
        }
        FlyingElement item2 = new FlyingElement(bitmap);

        item2.mX = bundle.getDouble("mX");
        item2.mXv = bundle.getDouble("mXv");
        item2.mY = bundle.getDouble("mY");
        item2.mYv = bundle.getDouble("mYv");
        item2.mSpinv = bundle.getDouble("mSpinv");
        item2.mSpin = bundle.getDouble("mSpin");
        item2.mBoom = bundle.getBoolean("mBoom");
        item2.mText = bundle.getString("mText");
        item2.fade = bundle.getBoolean("fade");

        item2.mPaint = new Paint();
        item2.mPaint.setColor(bundle.getInt("mPC"));
        item2.mPaint.setAlpha(bundle.getInt("mPA"));
        item2.mScale = bundle.getDouble("mScale");
        item2.mHit = bundle.getBoolean("mHit");
        item2.mValue = bundle.getInt("mValue");
        return item2;

    }

    // 冻结
    public void freeze(Bundle bundle) {

        if (mBitmap.getHeight() * mBitmap.getWidth() < 20000) {
            bundle.putParcelable("mBitmap", mBitmap);
        }
        bundle.putDouble("mX", mX);
        bundle.putDouble("mXv", mXv);
        bundle.putDouble("mY", mY);
        bundle.putDouble("mYv", mYv);
        bundle.putDouble("mSpinv", mSpinv);

        bundle.putDouble("mSpin", mSpin);
        bundle.putBoolean("mBoom", mBoom);
        bundle.putString("mText", mText);
        bundle.putBoolean("fade", fade);


        bundle.putInt("mPC", mPaint.getColor());
        bundle.putInt("mPA", mPaint.getAlpha());
        bundle.putDouble("mScale", mScale);
        bundle.putBoolean("mHit", mHit);
        bundle.putDouble("mValue", mValue);

    }

    int count = 0;

    // 更新 位置
    public void updatePosition(double gravity, double airresit) {
        mYv -= Math.signum(mYv) * airresit;
        mXv -= Math.signum(mXv) * airresit;

        mSpinv -= Math.signum(mSpinv) * airresit * 3;


        mYv += gravity;

        mX += mXv;
        mY += mYv;
        mSpin += mSpinv;

        if (!wasHit()) {
            mScale += (1 - mScale) / 33;
        }
        count++;

    }

    public boolean isHit(float x, float y) {
        if (mHit) return false;

        int width2 = mBitmap.getWidth() / 2;
        int height2 = mBitmap.getHeight() / 2;

        if (isBoom()) {
            width2 *= .7;
            height2 *= .7;
        }

        return (x > mX - width2 && x < mX + width2 && y > mY - height2 && y < mY + height2);
    }

    public boolean wasHit() {
        return mHit;
    }

    // 切割
    public FlyingElement[] cut(float x0, float y0) {
        int max = Math.max(mBitmap.getHeight(), mBitmap.getWidth());
        Bitmap spun = Bitmap.createBitmap(max, max, Bitmap.Config.ARGB_8888);
        Canvas rot = new Canvas(spun);
        mSpinMatrix.reset();
        mSpinMatrix.setRotate((float) mSpin, max / 2, max / 2);
        rot.drawBitmap(mBitmap, mSpinMatrix, null);


        Bitmap[] bmparts = new Bitmap[2];
        bmparts[0] = Bitmap.createBitmap(spun, 0, 0, max / 2, max);
        bmparts[1] = Bitmap.createBitmap(spun, max / 2, 0, max / 2, max);

        FlyingElement[] parts = new FlyingElement[2];
        for (int i = 0; i < parts.length; i++) {

            parts[i] = getCopy(this);
            parts[i].setBitmap(bmparts[i]);
            parts[i].mHit = true;
            if (i == 0) {
                parts[i].setYv(Math.max(Utils.getRand(1, 4), mYv));
            } else {
                parts[i].setYv(mYv < 0 ? mYv * .75 : mYv * 1.5);
            }
            parts[i].setXv(mXv * (i * 2 + 1));
            double d = Utils.getRand(-7, 7);
            parts[i].setSpinv((d == 0 ? 1 : d));
        }

        mText = "" + mValue;
        mHit = true;
        fade = true;
        return parts;
    }

    // 绘制
    public void draw(Canvas c) {
        int max = 50;
        if (mBitmap != null) {
            max = Math.max(mBitmap.getHeight(), mBitmap.getWidth());


            mSpinMatrix.reset();
            //mSpinMatrix.setTranslate(0, bmHeight/2);
            mSpinMatrix.setRotate((float) mSpin, max / 2, max / 2);
            // mSpinMatrix.setTranslate(max/2, max/2);
            mSpinMatrix.postScale((float) mScale, (float) mScale);


            Bitmap bm = Bitmap.createBitmap(max, max, Bitmap.Config.ARGB_8888);
            Canvas rot = new Canvas(bm);
            rot.drawBitmap(mBitmap, mSpinMatrix, null);


            if (fade) {
                int step = 10;
                int a = mPaint.getAlpha();
                if (a > step) {
                    mPaint.setAlpha(a - step);
                }

            }

            c.drawBitmap(bm, (int) mX - max / 2, (int) mY - max / 2, mPaint);
        }
        if (mText != null) {
            c.drawText(mText, (int) mX - max / 2, (int) mY - max / 3, scorePaint);
        }
    }


    public int getValue() {
        return mValue;
    }

    public void setValue(int mValue) {
        this.mValue = mValue;
    }

    public void setHit() {
        mHit = true;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public double getX() {
        return mX;
    }

    public void setX(double x) {
        this.mX = x;
    }

    public double getXv() {
        return mXv;
    }

    public void setXv(double mXv) {
        this.mXv = mXv;
    }

    public double getY() {
        return mY;
    }

    public void setY(double y) {
        this.mY = y;
    }

    public double getYv() {
        return mYv;
    }

    public void setYv(double mYv) {
        this.mYv = mYv;
    }

    public void setSpinv(double spinv) {
        mSpinv = spinv;
    }

    public boolean isBoom() {
        return mBoom;
    }

    public void setBoom(boolean boom) {
        this.mBoom = boom;
    }
}
