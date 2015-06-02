package com.xb.bubble.bubblelayout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

/**
 * 模仿小米天气<br>
 * Created by admin on 2015/5/21.<br>
 * <p>使用的时候请重写onPause和onResume<br></p>
 * <p>
 * protected void onPause() {
 * <blockquote><pre>super.onPause();</pre></blockquote>
 * <blockquote>rainLayout.setPause(true);</blockquote><br>
 * }<br>
 * <p/>
 * protected void onResume() {
 * <blockquote>super.onResume();</blockquote>
 * <blockquote>rainLayout.setPause(false);</blockquote>
 * }
 * </p>
 */
public class RainLayout extends View {

    private WeakHashMap<String, Context> mContext;

    private List<Bubble> bubbles = new ArrayList<Bubble>();
    private Random random = new Random();//生成随机数

    /**
     * View 的宽度*
     */
    //private int width;
    /**
     * 雨滴开始坐标
     */
    private float startY;
    private float startXRange;

    private boolean starting = false;

    /**
     * 偏移竖直方向的角度（例如下雨的时候有风）*
     */
    double gradient = 10 * Math.PI / 180;
    /**
     * 雨滴下落的速度（垂直方向）
     */
    float speedY = 10;
    /**
     * 雨滴下落的速度（水平方向）
     */
    //float speexX;
    /**
     * 雨的的粗细
     */
    float rainwidth = 5;
    /**
     * 雨的的长度
     */
    float rainlength = 30;

    /**
     * 雨滴下落的频率（雨滴的数量）
     */
    Fequence frequence = Fequence.Level5;

    /**
     * 风向(雨滴的走势)
     */
    Direction mDirection = Direction.RIGHT2LEFT;


    RainThread mThread;
    //boolean isPause;
    final Object lock = new Object();


    /**
     * 风向(雨滴的走势)
     */
    enum Direction {
        /**
         * 风向从左到右*
         */
        LEFT2RIGHT,
        /**
         * 风向从右到左*
         */
        RIGHT2LEFT;
    }

    enum Fequence {
        /**
         * 小雨,100粒/s*
         */
        Level1(1),
        /**
         * 中雨,200粒/s*
         */
        Level2(2),
        /**
         * 大雨,300粒/s*
         */
        Level3(3),
        /**
         * 暴雨,400粒/s*
         */
        Level4(4),
        /**
         * 特大暴雨,500粒/s*
         */
        Level5(5);

        Fequence(int frequence) {
            this.frequence = frequence;
        }

        private int frequence;
    }


    public RainLayout(Context context) {
        this(context, null);
    }

    public RainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = new WeakHashMap<>();
        mContext.put("Context", context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int[] location = new int[2];
        getLocationInWindow(location);
        //标题栏+状态栏的值
        int contentTop = ((Activity) mContext.get("Context")).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        //int titleBarHeight = contentTop - statusBarHeight ;
        startY = location[1] - contentTop;
        //
        if (bubbles == null || bubbles.isEmpty()) {
            return;
        }
        //
        List<Bubble> list = new ArrayList<>(bubbles);
        //依次绘制气泡
        for (Bubble bubble : list) {
            //碰到下边界从数组中移除
            // Log.i("XXX","bubble ==null?"+(bubble ==null)+"------bubble.getY ==null?"+(bubble.getY()));
            if (bubble == null) {
                return;
            }
            if (bubble.getY()/* + bubble.getSpeedY() */ > startY + getHeight()) {
                bubbles.remove(bubble);
            }
            //碰到左边界从数组中移除
            else if (bubble.getX() <= 0) {
                bubbles.remove(bubble);
            }
            //碰到右边界从数组中移除
            else if (bubble.getX() >= getWidth()) {
                bubbles.remove(bubble);
            }

            bubble.setX(bubble.getX() + bubble.getSpeedX());
            bubble.setY(bubble.getY() + bubble.getSpeedY());
            //canvas.drawCircle(bubble.getX(), bubble.getY(),bubble.getRadius(), paint);
            Bubble end = generate(bubble, rainlength);
            //canvas.drawLine(bubble.getX(), bubble.getY(), end.getX(), end.getY(), paint);
            canvas.drawLine(bubble.getX(), bubble.getY(), end.getX(), end.getY(), bubble.getPaint());
        }
    }

    public void onStartThread() {
        if (!starting) {
            starting = true;
            mThread = new RainThread();
            mThread.start();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.i("XXX", "onWindowVisibilityChanged");
        if (visibility == View.VISIBLE) {
            //isPause = false;
            if (starting) {
                mThread.setSuspend(false);
            }
        } else {
            //isPause = true;
            mThread.setSuspend(true);
        }
    }

    public void setPause(boolean pause) {
        if (mThread != null) {
            mThread.setSuspend(pause);
        }
    }

    public boolean isPause() {
        return mThread.isSuspend();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        Log.i("XXX", "onScreenStateChanged");
        if (screenState == SCREEN_STATE_OFF) {
            if (mThread.isSuspend()) {
                mThread.setSuspend(false);
            }
        } else {
            if (!mThread.isSuspend()) {
                mThread.setSuspend(true);
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.i("XXX", "onVisibilityChanged");
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        bubbles.clear();
        Log.i("XXX", "onDetachedFromWindow");
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onStartThread();
                return;
            }
        }, 1000);
        Log.i("XXX", "onAttachedToWindow");
    }

    /**
     * 根据起点产生终点
     * start(x0,y0)--->end(x1,y1) = (x0+l*sin(α),y0+l*cos(α))，α= gradient
     *
     * @return
     */
    private Bubble generate(Bubble bubble, float length) {
        Bubble end = new Bubble();
        if (mDirection == Direction.LEFT2RIGHT) {
            end.setX((float) (bubble.getX() + length * Math.sin(gradient)));
        } else {
            end.setX((float) (bubble.getX() - length * Math.sin(gradient)));
        }
        end.setY((float) (bubble.getY() + length * Math.cos(gradient)));
        return end;
    }

    //内部VO，不需要太多注释吧？
    private class Bubble implements Cloneable {
        //雨滴粗细种子
        private float width;
        //上升速度
        private float speedY;
        //平移速度
        private float speedX;
        //气泡x坐标
        private float x;
        // 气泡y坐标
        private float y;

        private Paint paint;

        /**
         * 雨滴的粗细
         *
         * @return
         */
        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getSpeedY() {
            return speedY;
        }

        public void setSpeedY(float speedY) {
            this.speedY = speedY;
        }

        public float getSpeedX() {
            return speedX;
        }

        public void setSpeedX(float speedX) {
            this.speedX = speedX;
        }

        public Paint getPaint() {
            return paint;
        }

        public void setPaint(Paint paint) {
            this.paint = paint;
        }

        @Override
        protected Bubble clone() throws CloneNotSupportedException {
            Bubble o = null;
            try {
                o = (Bubble) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return o;
        }
        public List<Bubble> clone(int num){
            List<Bubble> clones = null;
            Bubble clone;
            try {
                clones = new ArrayList<>();
                for(int i=0;i<num;i++){
                    clone = clone();
                    clone.getPaint().setStrokeWidth((float) Math.random() * (clone.getWidth() - 2) + 2);
                    clone.setX(random.nextInt(RainLayout.this.getWidth()));
                    clones.add(clone);
                }
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
            return clones;
        }
    }


    public double getGradient() {
        return gradient;
    }

    public void setGradient(double gradient) {
        this.gradient = gradient * Math.PI / 180;
    }

    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public float getRainwidth() {
        return rainwidth;
    }

    public void setRainwidth(float rainwidth) {
        this.rainwidth = rainwidth;
    }

    public float getRainlength() {
        return rainlength;
    }

    public void setRainlength(float rainlength) {
        this.rainlength = rainlength;
    }

    public Fequence getFrequence() {
        return frequence;
    }

    public void setFrequence(Fequence frequence) {
        this.frequence = frequence;
    }

    public Direction getDirection() {
        return mDirection;
    }

    public void setDirection(Direction mDirection) {
        this.mDirection = mDirection;
    }

    class RainThread extends IRainThread {
        @Override
        protected void runPersonllLogic() {
            // while (!suspend) {
            Log.i("XXX", "suspend=" + !suspend);
            try {
                //int rainCount = 50 / (frequence.ordinal() + 1);//50ms
                //Log.i("XXX","rainCount="+rainCount);
                //Thread.sleep(random.nextInt(3) * rainCount);
                Thread.sleep(random.nextInt(3) * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Bubble bubble = new Bubble();
            bubble.setWidth(rainwidth);
            bubble.setSpeedY(speedY);
            Paint paint = new Paint(); //为了产生雨滴粗细不一的效果我们不用同一个Paint
            paint.reset();
            paint.setColor(0X669999);//灰白色
            paint.setAlpha(45);//设置不透明度：透明为0，完全不透明为255
            //paint.setStrokeWidth((float) random.nextInt((int) rainwidth));//雨滴的粗细,//产生1000到9999的随机数
            paint.setStrokeWidth((float) Math.random() * (rainwidth - 2) + 2);//雨滴的粗细,//产生rainwidth-2---rainwidth+2的随机数
            bubble.setPaint(paint);
            bubble.setX(random.nextInt(getWidth()));
            bubble.setY(startY);
            float speedX;
            if (mDirection == Direction.RIGHT2LEFT) {
                speedX = -(float) (Math.tan(gradient) * speedY);
            } else {
                speedX = (float) (Math.tan(gradient) * speedY);
            }
            bubble.setSpeedX(speedX);
            //bubbles.add(bubble);
            bubbles.addAll(bubble.clone(frequence.ordinal() + 1));
            //刷新屏幕
            Message msg = mHandler.obtainMessage();
            mHandler.sendMessage(msg);
        }
    }

    //
    abstract class IRainThread extends Thread {
        protected boolean suspend = false;
        protected String control = ""; // 只是需要一个对象而已，这个对象没有实际意义

        /**
         * setSuspend(true)是当前线程暂停/ 等待，调用setSuspend(false)让当前线程恢复/唤醒
         *
         * @param suspend
         */
        public void setSuspend(boolean suspend) {
            if (!suspend) {
                synchronized (control) {
                    control.notifyAll();
                }
            }
            this.suspend = suspend;
        }

        public boolean isSuspend() {
            return this.suspend;
        }

        public void run() {
            while (true) {
                synchronized (control) {
                    if (suspend) {
                        try {
                            control.wait();
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.i("XXX", "..........................");
                this.runPersonllLogic();
            }
        }

        protected abstract void runPersonllLogic();
    }
}
