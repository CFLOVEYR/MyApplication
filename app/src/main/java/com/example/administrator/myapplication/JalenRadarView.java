package com.example.administrator.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 *
 */
public class JalenRadarView extends View {
    private Context mContext;
    private int count = 6;                //数据个数  
    private float angle = (float) (Math.PI * 2 / count);
    private float radius;                   //网格最大半径  
    private int centerX;                  //中心X  
    private int centerY;                  //中心Y  
    private String[] titles = {"财会", "公/法/计数/其他", "英语", "金融", "行测", "经济"};//科目固死的标题
    private double[] data = {50, 100, 50, 54.5, 77.5, 50}; //各维度分值
    private double[] data1 = {0, 0, 0, 0, 0, 0}; //各维度分值
    private float maxValue = 100;             //数据最大值
    private Paint mainPaint;                //雷达区画笔  
    private Paint valuePaint;               //数据区画笔  
    private Paint textPaint;                //文本画笔      
    private Paint textPaintCenter;                //中心分数文本画笔
    public static final float RADIUS = 50f;

    private Redar currentPoint;

    private Redar mPaint;

    public JalenRadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public JalenRadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public JalenRadarView(Context context) {
        super(context);
        mContext = context;
        init();
    }



    /**
     * 初始化画笔的默认状态值
     */
    private void init() {
        count = Math.min(data.length, titles.length);

        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setColor(Color.GRAY);
        mainPaint.setStyle(Paint.Style.STROKE);

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(Color.YELLOW);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint();
        textPaint.setTextSize(DensityUtils.sp2px(mContext, 12));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);

        textPaintCenter = new Paint();
        textPaintCenter.setTextSize(DensityUtils.sp2px(mContext, 30));
        textPaintCenter.setStyle(Paint.Style.FILL);
        textPaintCenter.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(h, w) / 2 * 0.8f;//改变六边形的半径
        centerX = w / 2;
        centerY = h / 2;
        postInvalidate();//通知绘制
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawPolygon(canvas);
        drawLines(canvas);
        drawText(canvas);
        drawRegion(canvas);
        drawTextCenter(canvas);
    }

    /**
     * 绘制正多边形
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        float r = radius / (count - 1);
        for (int i = 1; i < count; i++) {
            float curR = r * i;
            path.reset();
            //限制只绘制最中心的点和外边的框
            if (i == count - 1) {
                for (int j = 0; j < count; j++) {
                    if (j == 0) {
                        path.moveTo(centerX + curR, centerY);
                    } else {
                        float x = (float) (centerX + curR * Math.cos(angle * j));
                        float y = (float) (centerY + curR * Math.sin(angle * j));
                        path.lineTo(x, y);
                    }
                }
            }

            path.close();
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制直线
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            float x = (float) (centerX + radius * Math.cos(angle * i));
            float y = (float) (centerY + radius * Math.sin(angle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;

        Paint.FontMetrics fontMetrics1 = textPaint.getFontMetrics();
        float height = fontMetrics1.ascent + fontMetrics.descent;
        for (int i = 0; i < count; i++) {
            float x = (float) (centerX + (radius + fontHeight / 2) * Math.cos(angle * i));
            float y = (float) (centerY + (radius + fontHeight / 2) * Math.sin(angle * i));
            float dis = textPaint.measureText(titles[i]);//文本长度
            if (angle * i == 0) {                                   //X轴-->>公/法/计数/其他
                canvas.drawText(titles[i], x, centerY - height / 2, textPaint);
            } else if (angle * i > 0 && angle * i <= Math.PI / 2) {//第4象限-->>财会
                canvas.drawText(titles[i], x - dis / 2, y - height, textPaint);
            } else if (angle * i > Math.PI / 2 && angle * i <= Math.PI) {//第2象限-->>英语
                canvas.drawText(titles[i], x - dis / 2, y - height, textPaint);
            } else if (angle * i > Math.PI && angle * i < 6 * Math.PI / 5) {//第1象限-->>金融
                canvas.drawText(titles[i], x - dis, centerY - height / 2, textPaint);
            } else if (angle * i > 6 * Math.PI / 5 && angle * i < 3 * Math.PI / 2) {//-->>行测
                canvas.drawText(titles[i], x - dis / 2, y, textPaint);
            } else if (angle * i >= 3 * Math.PI / 2 && angle * i <= Math.PI * 2) {//第3象限-->经济
                canvas.drawText(titles[i], x - dis / 2, y, textPaint);
            }
        }
    }

    /**
     * 绘制中心文字
     *
     * @param canvas
     */
    private void drawTextCenter(Canvas canvas) {
        Paint.FontMetrics fontMetrics = textPaintCenter.getFontMetrics();
        float fontHeight = fontMetrics.ascent + fontMetrics.descent;
        Log.e("---", "--" + fontHeight / 2);
        textPaintCenter.setTextAlign(Paint.Align.CENTER);
//        float dis = textPaint.measureText("460");//文本长度
//        canvas.drawText("460", centerX-dis/2, centerY+fontHeight/2, textPaint);
        canvas.drawText("460", centerX, centerY - fontHeight / 2, textPaintCenter);

    }
    private void drawCircle(Canvas canvas) {
        Path path = new Path();
        valuePaint.setAlpha(255);
        for (int i = 0; i < count; i++) {
            double percent = data[i] / maxValue;
            float x = (float) (centerX + radius * Math.cos(angle * i) * percent);
            float y = (float) (centerY + radius * Math.sin(angle * i) * percent);
            if (i == 0) {
                path.moveTo(x, centerY);
            } else {
                path.lineTo(x, y);
            }
            //绘制小圆点
            canvas.drawCircle(x, y, 1, valuePaint);
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }
    private void startAnimation() {
        Redar startPoint = new Redar(data1);
        Redar endPoint = new Redar(data);
        ValueAnimator anim = ValueAnimator.ofObject(new RegionEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Redar) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.setDuration(5000);
        anim.start();
    }
    /**
     * 绘制区域
     *
     * @param canvas
     */
    private void drawRegion(Canvas canvas) {

        if (currentPoint == null) {
            currentPoint = new Redar(data1);
            drawCircle(canvas);
            startAnimation();
        } else {
            drawCircle(canvas);
        }

//        Path path = new Path();
//        valuePaint.setAlpha(255);
//        for (int i = 0; i < count; i++) {
//            double percent = data[i] / maxValue;
//            float x = (float) (centerX + radius * Math.cos(angle * i) * percent);
//            float y = (float) (centerY + radius * Math.sin(angle * i) * percent);
//            if (i == 0) {
//                path.moveTo(x, centerY);
//            } else {
//                path.lineTo(x, y);
//            }
//            //绘制小圆点
//            canvas.drawCircle(x, y, 1, valuePaint);
//        }
//        valuePaint.setStyle(Paint.Style.STROKE);
//        canvas.drawPath(path, valuePaint);
//        valuePaint.setAlpha(127);
//        //绘制填充区域
//        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        canvas.drawPath(path, valuePaint);
    }

    //设置标题
    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public double[] getData() {
        return data;
    }

    //设置数值
    public void setData(double[] data) {
        this.data = data;
    }


    public float getMaxValue() {
        return maxValue;
    }

    //设置最大数值
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    //设置蜘蛛网颜色
    public void setMainPaintColor(int color) {
        mainPaint.setColor(color);
    }

    //设置标题颜色
    public void setTextPaintColor(int color) {
        textPaint.setColor(color);
    }

    //设置覆盖局域颜色
    public void setValuePaintColor(int color) {
        valuePaint.setColor(color);
    }

    //设置文本颜色
    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    //设置文本字体大小
    public void setTextSize(int size) {
        textPaint.setColor(size);
    }


}  