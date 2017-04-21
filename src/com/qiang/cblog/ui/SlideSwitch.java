package com.qiang.cblog.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qiang.cblog.R;

public class SlideSwitch extends View {

	private static final int SCALE = 4;

	private OnStateChangedListener onStateChangedListener;

	private boolean isOnState = false;
	private float mCurX = 0;
	private float mCenterY;
	private float mWidth;
	private float mRadius;
	private float mLineStart;
	private float mLineEnd;
	private float mLineWidth;

	private int mOnColor;
	private int mOnPotColor;
	private int mOffPotColor;
	private int mOffColor;

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mPotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public SlideSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.slide_switch, defStyle, 0);

		mOffColor = array.getColor(R.styleable.slide_switch_offColor,
				Color.GRAY);
		mOnColor = array
				.getColor(R.styleable.slide_switch_onColor, Color.BLACK);
		mOffPotColor = array.getColor(R.styleable.slide_switch_offPotColor,
				Color.GRAY);
		mOnPotColor = array.getColor(R.styleable.slide_switch_onPotColor,
				Color.BLACK);
		array.recycle();
	}

	public SlideSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.slide_switch);

		mOffColor = array.getColor(R.styleable.slide_switch_offColor,
				Color.GRAY);
		mOnColor = array
				.getColor(R.styleable.slide_switch_onColor, Color.BLACK);
		mOffPotColor = array.getColor(R.styleable.slide_switch_offPotColor,
				Color.GRAY);
		mOnPotColor = array.getColor(R.styleable.slide_switch_onPotColor,
				Color.BLACK);
		array.recycle();

	}

	public SlideSwitch(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mCurX = event.getX();
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (mCurX > mWidth / 2) {
				mCurX = mLineEnd;

				if (false == isOnState) {
					if (null != onStateChangedListener) {
						onStateChangedListener.onStateChanged(true);
					}
					isOnState = true;
				}
			} else {
				mCurX = mLineStart;
				if (true == isOnState) {
					if (null != onStateChangedListener) {
						onStateChangedListener.onStateChanged(false);
					}
					isOnState = false;
				}
			}
		}
		postInvalidate();
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(this.getMeasuredWidth(),
				this.getMeasuredWidth() * 2 / SCALE);
		mWidth = this.getMeasuredWidth();
		mRadius = mWidth / SCALE;
		mLineWidth = mRadius * 2f;
		mCurX = mRadius;
		mCenterY = this.getMeasuredWidth() / SCALE;
		mLineStart = mRadius;
		mLineEnd = (SCALE - 1) * mRadius;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mCurX = mCurX > mLineEnd ? mLineEnd : mCurX;
		mCurX = mCurX < mLineStart ? mLineStart : mCurX;
		mPaint.setStyle(Paint.Style.FILL);
		mPotPaint.setStyle(Paint.Style.FILL);
		if(isOnState){
			mPaint.setColor(mOnColor);
		}else{
			mPaint.setColor(mOffColor);
		}
		if(isOnState){
			mPotPaint.setColor(mOnPotColor);
		}else{
			mPotPaint.setColor(mOffPotColor);
		}
		
		RectF rectF = new RectF(getMeasuredWidth() / 6,
				getMeasuredHeight() / 6, getMeasuredWidth() / 6 * 5,
				getMeasuredHeight() - getMeasuredHeight() / 6);
		canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
//		mPaint.setColor(mOnColor);
		canvas.drawCircle(mCurX, mCenterY, mRadius, mPotPaint);
	}

	public void setOnStateChangedListener(OnStateChangedListener listener) {
		this.onStateChangedListener = listener;
	}

	public interface OnStateChangedListener {
		public void onStateChanged(boolean state);
	}

	// /* 限制滑动范围 */
	// mCurX = mCurX > mLineEnd ? mLineEnd : mCurX;
	// mCurX = mCurX < mLineStart ? mLineStart : mCurX;
	//
	// /* 划线 */
	// mPaint.setStyle(Paint.Style.FILL);
	// // mPaint.setStrokeWidth(mLineWidth / 2);
	// /* 左边部分的线，绿色 */
	// mPaint.setColor(mOnColor);
	// // canvas.drawLine(mLineStart, mCenterY, mCurX, mCenterY, mPaint);
	// /* 右边部分的线，灰色 */
	// // mPaint.setColor(Color.GRAY);
	// // canvas.drawLine(mCurX, mCenterY, mLineEnd, mCenterY, mPaint);
	// //
	// RectF rectF = new RectF(getMeasuredWidth() / 6,
	// getMeasuredHeight() / 6, getMeasuredWidth() / 6 * 5,
	// getMeasuredHeight() - getMeasuredHeight() / 6);
	// // canvas.drawRoundRect(rectF,rectF.centerX(), rectF.centerY(), mPaint);
	//
	// // canvas.drawOval(20, 20, 20, 20, mPaint);
	// // canvas.drawOval(rectF, mPaint);
	//
	// canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
	//
	// //
	// // /* 画圆 */
	// // /* 画最左和最右的圆，直径为直线段宽度， 即在直线段两边分别再加上一个半圆 */
	// // mPaint.setStyle(Paint.Style.FILL);
	// // mPaint.setColor(Color.GRAY);
	// // canvas.drawCircle(mLineEnd, mCenterY, mLineWidth / 2, mPaint); //
	// // mPaint.setColor(Color.BLUE);
	// // canvas.drawCircle(mLineStart, mCenterY, mLineWidth / 2, mPaint);//
	// /* 圆形滑块 */
	// mPaint.setColor(mOnColor);
	// canvas.drawCircle(mCurX, mCenterY, mRadius, mPaint);

}
