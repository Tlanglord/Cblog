package com.qiang.cblog.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;

import com.qiang.cblog.R;

public class CustomSwitch extends CompoundButton {

	private Drawable mTrackOnDrawable;
	private Drawable mTrackOffDrawable;

	// Support minimum height
	private int mSwitchMinHeight;
	private TextPaint mTextPaint;
	private int mTouchSlop;
	private int mMinFlingVelocity;
	private int mSwitchPadding;
	private int mSwitchMinWidth;
	private int mThumbTextPadding;
	private CharSequence mTextOff;
	private Drawable mThumbDrawable;
	private CharSequence mTextOn;
	private Drawable mTrackDrawable;
	private Layout mOnLayout;
	private Layout mOffLayout;
	private Rect mTempRect;
	private int mThumbWidth;
	private int mSwitchWidth;
	private int mSwitchHeight;
	private int mSwitchLeft;
	private int mSwitchTop;
	private int mSwitchRight;
	private int mSwitchBottom;
	private float mThumbPosition;
	private ColorStateList mTextColors;

	public CustomSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomSwitch(Context context) {
		super(context);
	}

	public CustomSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		Resources res = getResources();
		mTextPaint.density = res.getDisplayMetrics().density;
		// float scaledDensity = res.getDisplayMetrics().scaledDensity;
		// mTextPaint.setCompatibilityScaling(res.getCompatibilityInfo().applicationScale);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Switch, defStyleAttr, 0);

		// off-on 模式： 图片模式或文字模式，图片模式是用Track背景图片表示off-on的状态，文字模式是用文字来表示off-on状态。
		mTrackOnDrawable = a.getDrawable(R.styleable.Switch_trackOn);
		mTrackOffDrawable = a.getDrawable(R.styleable.Switch_trackOff);
		if (checkTrackOffOnDrawable()) {
			// 如果设定图片模式，则默认显示off状态
			mTrackDrawable = mTrackOffDrawable;
		} else {
			mTrackDrawable = a.getDrawable(R.styleable.Switch_track);
		}

		mThumbDrawable = a.getDrawable(R.styleable.Switch_thumb);
		mTextOn = a.getText(R.styleable.Switch_textOn);
		mTextOff = a.getText(R.styleable.Switch_textOff);
		mThumbTextPadding = a.getDimensionPixelSize(
				R.styleable.Switch_thumbTextPadding, 0);
		mSwitchMinWidth = a.getDimensionPixelSize(
				R.styleable.Switch_switchMinWidth, 0);

		mSwitchMinHeight = a.getDimensionPixelSize(
				R.styleable.Switch_switchMinHeight, 0);

		mSwitchPadding = a.getDimensionPixelSize(
				R.styleable.Switch_switchPadding, 0);

		int appearance = a.getResourceId(
				R.styleable.Switch_switchTextAppearance, 0);
		if (appearance != 0) {
			// setSwitchTextAppearance(context, appearance);
		}
		a.recycle();

		ViewConfiguration config = ViewConfiguration.get(context);
		mTouchSlop = config.getScaledTouchSlop();
		mMinFlingVelocity = config.getScaledMinimumFlingVelocity();

		refreshDrawableState();
		setChecked(isChecked());
	}

	private boolean checkTrackOffOnDrawable() {
		return mTrackOnDrawable != null && mTrackOffDrawable != null;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mOnLayout == null) {
			mOnLayout = makeLayout(mTextOn);
		}
		if (mOffLayout == null) {
			mOffLayout = makeLayout(mTextOff);
		}
		mTrackDrawable.getPadding(mTempRect);
		final int maxTextWidth = Math.max(mOnLayout.getWidth(),
				mOffLayout.getWidth());
		final int switchWidth = Math.max(mSwitchMinWidth, maxTextWidth * 2
				+ mThumbTextPadding * 4 + mTempRect.left + mTempRect.right);

		// final int switchHeight = mTrackDrawable.getIntrinsicHeight();
		int switchHeight;
		if (mSwitchMinHeight <= 0) {
			switchHeight = mTrackDrawable.getIntrinsicHeight();
		} else {
			switchHeight = Math.max(mSwitchMinHeight, mTempRect.top
					+ mTempRect.bottom);
		}

		mThumbWidth = maxTextWidth + mThumbTextPadding * 2;

		mSwitchWidth = switchWidth;
		mSwitchHeight = switchHeight;

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int measuredHeight = getMeasuredHeight();
		if (measuredHeight < switchHeight) {
			if (Build.VERSION.SDK_INT >= 11) {
				setMeasuredDimension(getMeasuredWidthAndState(), switchHeight);
			} else {
				setMeasuredDimension(getMeasuredWidth(), switchHeight);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int switchLeft = mSwitchLeft;
		int switchTop = mSwitchTop;
		int switchRight = mSwitchRight;
		int switchBottom = mSwitchBottom;

		if (checkTrackOffOnDrawable()) {
			mTrackDrawable = getTargetCheckedState()
					? mTrackOnDrawable
					: mTrackOffDrawable;
			refreshDrawableState();
		}

		mTrackDrawable.setBounds(switchLeft, switchTop, switchRight,
				switchBottom);
		mTrackDrawable.draw(canvas);

		canvas.save();

		mTrackDrawable.getPadding(mTempRect);
		int switchInnerLeft = switchLeft + mTempRect.left;
		int switchInnerTop = switchTop + mTempRect.top;
		int switchInnerRight = switchRight - mTempRect.right;
		int switchInnerBottom = switchBottom - mTempRect.bottom;
		canvas.clipRect(switchInnerLeft, switchTop, switchInnerRight,
				switchBottom);

		mThumbDrawable.getPadding(mTempRect);
		final int thumbPos = (int) (mThumbPosition + 0.5f);
		int thumbLeft = switchInnerLeft - mTempRect.left + thumbPos;
		int thumbRight = switchInnerLeft + thumbPos + mThumbWidth
				+ mTempRect.right;

		mThumbDrawable
				.setBounds(thumbLeft, switchTop, thumbRight, switchBottom);
		mThumbDrawable.draw(canvas);

		// mTextColors should not be null, but just in case
		if (mTextColors != null) {
			mTextPaint.setColor(mTextColors.getColorForState(
					getDrawableState(), mTextColors.getDefaultColor()));
		}
		mTextPaint.drawableState = getDrawableState();

		Layout switchText = getTargetCheckedState() ? mOnLayout : mOffLayout;

		if (switchText != null) {
			canvas.translate(
					(thumbLeft + thumbRight) / 2 - switchText.getWidth() / 2,
					(switchInnerTop + switchInnerBottom) / 2
							- switchText.getHeight() / 2);
			switchText.draw(canvas);
		}

		// boolean allCaps =
		// appearance.getBoolean(com.android.internal.R.styleable.
		// TextAppearance_textAllCaps, false);
		// if (allCaps) {
		// mSwitchTransformationMethod = new
		// AllCapsTransformationMethod(getContext());
		// mSwitchTransformationMethod.setLengthChangesAllowed(true);
		// } else {
		// mSwitchTransformationMethod = null;
		// }
		canvas.restore();
	}

	private int getThumbScrollRange() {
		if (mTrackDrawable == null) {
			return 0;
		}
		mTrackDrawable.getPadding(mTempRect);
		return mSwitchWidth - mThumbWidth - mTempRect.left - mTempRect.right;
	}

	// public void setSwitchTextAppearance(Context context, int resid) {
	// TypedArray appearance = context.obtainStyledAttributes(resid,
	// com.android.internal.R.styleable.TextAppearance);
	//
	// ColorStateList colors;
	// int ts;
	//
	// colors = appearance
	// .getColorStateList(com.android.internal.R.styleable.TextAppearance_textColor);
	// if (colors != null) {
	// mTextColors = colors;
	// } else {
	// // If no color set in TextAppearance, default to the view's
	// // textColor
	// mTextColors = getTextColors();
	// }
	//
	// ts = appearance.getDimensionPixelSize(
	// com.android.internal.R.styleable.TextAppearance_textSize, 0);
	// if (ts != 0) {
	// if (ts != mTextPaint.getTextSize()) {
	// mTextPaint.setTextSize(ts);
	// requestLayout();
	// }
	// }
	//
	// int typefaceIndex, styleIndex;
	//
	// typefaceIndex = appearance.getInt(
	// com.android.internal.R.styleable.TextAppearance_typeface, -1);
	// styleIndex = appearance.getInt(
	// com.android.internal.R.styleable.TextAppearance_textStyle, -1);
	//
	// setSwitchTypefaceByIndex(typefaceIndex, styleIndex);
	//
	// appearance.recycle();
	// }

	// private int getThumbScrollRange() {
	// if (mTrackDrawable != null) {
	// final Rect padding = mTempRect;
	// mTrackDrawable.getPadding(padding);
	//
	// final Insets insets;
	// if (mThumbDrawable != null) {
	// insets = mThumbDrawable.getOpticalInsets();
	// } else {
	// insets = Insets.NONE;
	// }
	//
	// return mSwitchWidth - mThumbWidth - padding.left - padding.right
	// - insets.left - insets.right;
	// } else {
	// return 0;
	// }
	// }

	private boolean getTargetCheckedState() {
		return mThumbPosition >= getThumbScrollRange() / 2;
	}

	private Layout makeLayout(CharSequence text) {
		return new StaticLayout(text, mTextPaint, (int) Math.ceil(Layout
				.getDesiredWidth(text, mTextPaint)),
				Layout.Alignment.ALIGN_NORMAL, 1.f, 0, true);
	}

	//
	// private Layout makeLayout(CharSequence text) {
	// final CharSequence transformed = (mSwitchTransformationMethod != null)
	// ? mSwitchTransformationMethod.getTransformation(text, this)
	// : text;
	//
	// return new StaticLayout(transformed, mTextPaint,
	// (int) Math.ceil(Layout.getDesiredWidth(transformed, mTextPaint)),
	// Layout.Alignment.ALIGN_NORMAL, 1.f, 0, true);
	// }

}
