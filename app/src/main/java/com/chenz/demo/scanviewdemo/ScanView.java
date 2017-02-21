package com.chenz.demo.scanviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chenz on 2017/2/20.
 */
public class ScanView extends View {


    /**
     * 背景圆
     */
    private Paint mCirclePaint;
    /**
     * 渐变
     */
    private Paint mSelctorPaint;
    /**
     * 直径
     */
    private int mDiameter;

    /**
     * 扫描线程
     */
    private ScanThread mScanThread;

    private boolean isStart;

    private Matrix mMatrix;

    /**
     * 逆时针
     */
    public static int ANTI_CLOCK_WISE = -1;

    /**
     * 顺时针
     */
    public static int CLOCK_WISE = 1;

    private int direction=CLOCK_WISE;
    /**
     * 旋转角度
     */
    private float angel = 0;

    public ScanView(Context context) {
        this(context, null);
    }

    public ScanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setAntiAlias(true);//消除锯齿
        mCirclePaint.setColor(0xff31C9F2);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mSelctorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelctorPaint.setAntiAlias(true);
        mSelctorPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取直径
        mDiameter = getMeasuredHeight() > getMeasuredWidth() ? getMeasuredWidth() : getMeasuredHeight();
        //渐变渲染
        SweepGradient sweepGradient = new SweepGradient(mDiameter / 2.0f, mDiameter / 2.0f, Color.TRANSPARENT, Color.WHITE);
        mSelctorPaint.setShader(sweepGradient);
        //PorterDuff.Mode.DST_OUT取下层绘制非交集部分。
//        mSelctorPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //画圆
        canvas.drawCircle(mDiameter / 2.0f, mDiameter / 2.0f, mDiameter / 2.0f, mCirclePaint);
        //把画布的多有对象与matrix联系起来
        if (mMatrix != null) {
            canvas.concat(mMatrix);
        }
        //画渐变圆
        canvas.drawCircle(mDiameter / 2.0f, mDiameter / 2.0f, mDiameter / 2.0f, mSelctorPaint);
        super.onDraw(canvas);
    }

    class ScanThread extends Thread {

        private View view;

        public ScanThread(View view) {
            super();
            this.view = view;
        }

        @Override
        public void run() {
            while (isStart) {
                angel += 1;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        mMatrix = new Matrix();
                        mMatrix.preRotate(angel * direction, mDiameter / 2.0f, mDiameter / 2.0f);
                        view.invalidate();
                    }
                });
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startScan(){
        mScanThread=new ScanThread(this);
        isStart=true;
        mScanThread.start();
    }

    public void stopScan(){
        isStart=false;
    }

    public void setDirection(int direction){
        this.direction=direction;
    }
}
