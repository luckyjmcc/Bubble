package com.xb.bubble.bubblelayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//模拟海底气泡上升的自定义控件
//修改了一些细节
//原作者QQ：719055805
public class BubbleLayout extends View {

	private List<Bubble> bubbles = new ArrayList<Bubble>();
	private Random random = new Random();//生成随机数
	private int width, height;
	private boolean starting = false;

	public BubbleLayout(Context context) {
		super(context);
	}

	public BubbleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BubbleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		width = getWidth();
		height = getHeight();
		if (!starting) {
			starting = true;
			new Thread() {
				public void run() {
					while (true) {
						try {
							Thread.sleep(random.nextInt(3) * 300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Bubble bubble = new Bubble();
						int radius = random.nextInt(30);
						while (radius == 0) {
							radius = random.nextInt(30);
						}
						float speedY = random.nextFloat()*5;
						while (speedY < 1) {
							speedY = random.nextFloat()*5;
						}
						bubble.setRadius(radius);
						bubble.setSpeedY(speedY);
						bubble.setX(width / 2);
						bubble.setY(height);
						float speedX = random.nextFloat()-0.5f;
						while (speedX == 0) {
							speedX = random.nextFloat()-0.5f;
						}
						bubble.setSpeedX(speedX*2);
						bubbles.add(bubble);
					}
				};
			}.start();
		}
		Paint paint = new Paint();
		// 绘制渐变正方形作为背景
		// 我更倾向于用图片做背景
		/*
		Shader shader = new LinearGradient(0, 0, 0, height, new int[] {
				getResources().getColor(android.R.color.holo_blue_bright),
				getResources().getColor(android.R.color.holo_blue_light),
				getResources().getColor(android.R.color.holo_blue_dark) },
				null, Shader.TileMode.REPEAT);
		paint.setShader(shader);
		canvas.drawRect(0, 0, width, height, paint);
		*/
		//绘制气泡
		paint.reset();
		paint.setColor(0X669999);//灰白色
		paint.setAlpha(45);//设置不透明度：透明为0，完全不透明为255
		List<Bubble> list = new ArrayList<Bubble>(bubbles);
		//依次绘制气泡
		for (Bubble bubble : list) 
		{
			//碰到上边界从数组中移除
			if (bubble.getY() - bubble.getSpeedY() <= 0) 
			{
				bubbles.remove(bubble);
			}
			//碰到左边界从数组中移除
			else if(bubble.getX() - bubble.getRadius() <= 0)
			{
				bubbles.remove(bubble);
			}
			//碰到右边界从数组中移除
			else if(bubble.getX() + bubble.getRadius() >= width)
			{
				bubbles.remove(bubble);
			}
			else 
			{
				int i = bubbles.indexOf(bubble);
				if (bubble.getX() + bubble.getSpeedX() <= bubble.getRadius()) {
					bubble.setX(bubble.getRadius());
				} else if (bubble.getX() + bubble.getSpeedX() >= width
						- bubble.getRadius()) {
					bubble.setX(width - bubble.getRadius());
				} else {
					bubble.setX(bubble.getX() + bubble.getSpeedX());
				}
				bubble.setY(bubble.getY() - bubble.getSpeedY());
				
				//海底溢出的甲烷上升过程越来越大（气压减小）
				//鱼类和潜水员吐出的气体却会越变越小（被海水和藻类吸收）
				//如果考虑太多现实情景的话，代码量就会变得很大，也容易出现bug
				//感兴趣的读者可以自行添加
				//bubble.setRadius(bubble.getRadius());
				
				bubbles.set(i, bubble);
				canvas.drawCircle(bubble.getX(), bubble.getY(),
						bubble.getRadius(), paint);
			}
		}
		//刷新屏幕
		invalidate();
	}

	//内部VO，不需要太多注释吧？
	private class Bubble {
		//气泡半径 
		private float radius;
		//上升速度
		private float speedY;
		//平移速度
		private float speedX;
		//气泡x坐标
		private float x;
		// 气泡y坐标
		private float y;

		public float getRadius() {
			return radius;
		}

		public void setRadius(float radius) {
			this.radius = radius;
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

	}
}