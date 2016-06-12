package com.showjoy.tashow.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.showjoy.tashow.R;

public class SwipeListView extends ListView {

	private Boolean mIsHorizontal;

	private View mPreItemView;

	private View mCurrentItemView;

	private float mFirstX;

	private float mFirstY;

	private int mRightViewWidth = 0;

	// private boolean mIsInAnimation = false;
	private final int mDuration = 100;

	private int mDurationStep = 10;

	private boolean flag = true;

	private  boolean mIsShown;

	private float down_x = 0;

	private float up_x = 0;

	
	int motionPosition;
	
	public SwipeListView(Context context) {
		this(context, null);
	}

	public SwipeListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	/**
	 * 当前展开的view
	 * @return
	 */
	public View getCurrentItemView(){
		return mPreItemView;
	}
	
	public boolean isShowns(){
		return mIsShown;
	}

	public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.swipelistviewstyle);

		// 获取自定义属性和默认值
		// mRightViewWidth = (int) mTypedArray.getDimension(
		// R.styleable.swipelistviewstyle_right_width, 200);

		mTypedArray.recycle();
	}

	/**
	 * return true, deliver to listView. return false, deliver to child. if
	 * move, return true
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		float lastX = ev.getX();
		float lastY = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsHorizontal = null;
			System.out.println("onInterceptTouchEvent----->ACTION_DOWN");
			mFirstX = lastX;
			mFirstY = lastY;
			motionPosition = pointToPosition((int) mFirstX, (int) mFirstY);

			if (motionPosition==-1) {//内容未撑满全屏，点击其他空白地方恢复初始
				if (mIsShown) {
					hiddenRight(mCurrentItemView, true);
				}
				break;
			}
			if (motionPosition >= 0) {
				View currentItemView = getChildAt(motionPosition
						- getFirstVisiblePosition());
				//按下去获取当前右边组件的宽度
				LinearLayout id = (LinearLayout) currentItemView.findViewById(R.id.item_right);
				mRightViewWidth =  id.getWidth();
				/////////////////////////////
				mPreItemView = mCurrentItemView;
				mCurrentItemView = currentItemView;
			}
			if (mIsShown&& (mPreItemView != mCurrentItemView)) {
				hiddenRight(mPreItemView, true);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = lastX - mFirstX;
			float dy = lastY - mFirstY;

			if (Math.abs(dx) >= 20 && Math.abs(dy) >= 20) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			// System.out.println("onInterceptTouchEvent----->ACTION_UP");
			if (mIsShown
					&& (mPreItemView != mCurrentItemView || isHitCurItemLeft(lastX))) {
				// System.out.println("1---> hiddenRight");
				/**
				 * 情况一：
				 * <p>
				 * 一个Item的右边布局已经显示，
				 * <p>
				 * 这时候点击任意一个item, 那么那个右边布局显示的item隐藏其右边布局
				 */
				hiddenRight(mPreItemView, true);
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	private boolean isHitCurItemLeft(float x) {
		return x < getWidth() - mRightViewWidth;
	}

	/**
	 * @param dx
	 * @param dy
	 * @return judge if can judge scroll direction
	 */
	private boolean judgeScrollDirection(float dx, float dy) {
		boolean canJudge = true;

		if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
			mIsHorizontal = true;
			// System.out.println("mIsHorizontal---->" + mIsHorizontal);
		} else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
			mIsHorizontal = false;
			// System.out.println("mIsHorizontal---->" + mIsHorizontal);
		} else {
			canJudge = false;
		}

		return canJudge;
	}

	/**
	 * return false, can't move any direction. return true, cant't move
	 * vertical, can move horizontal. return super.onTouchEvent(ev), can move
	 * both.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		float lastX = ev.getX();
		float lastY = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			down_x = lastX;
			System.out.println("---->ACTION_DOWN");
			break;

		case MotionEvent.ACTION_MOVE:
			if (motionPosition==-1) {
				break;
			}
			float dx = lastX - mFirstX;
			float dy = lastY - mFirstY;
				
			// confirm is scroll direction
			if (mIsHorizontal == null) {
				if (!judgeScrollDirection(dx, dy)) {
					break;
				}
			}

			if (mIsHorizontal) {
				if (mIsShown && mPreItemView != mCurrentItemView) {
					// System.out.println("2---> hiddenRight");
					/**
					 * 情况二：
					 * <p>
					 * 一个Item的右边布局已经显示，
					 * <p>
					 * 这时候左右滑动另外一个item,那个右边布局显示的item隐藏其右边布局
					 * <p>
					 * 向左滑动只触发该情况，向右滑动还会触发情况五
					 */
					hiddenRight(mPreItemView, true);
				}

				if (mIsShown && mPreItemView == mCurrentItemView) {
					dx = dx - mRightViewWidth;
					// System.out.println("======dx " + dx);
				}

				// can't move beyond boundary
				if (dx < 0 && dx > -mRightViewWidth) {
					mCurrentItemView.scrollTo((int) (-dx), 0);
				}

				return true;
			} else {
				if (mIsShown) {
					// System.out.println("3---> hiddenRight");
					/**
					 * 情况三：
					 * <p>
					 * 一个Item的右边布局已经显示，
					 * <p>
					 * 这时候上下滚动ListView,那么那个右边布局显示的item隐藏其右边布局
					 */
					hiddenRight(mPreItemView, false);
				}
			}

			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			up_x = lastX;
			System.out.println("============ACTION_UP");
			clearPressedState();
			if (mIsShown) {
				// System.out.println("4---> hiddenRight");
				/**
				 * 情况四：
				 * <p>
				 * 一个Item的右边布局已经显示，
				 * <p>
				 * 这时候右滑动当前这个item,右边布局显示的item隐藏其右边布局
				 */
				if (up_x - down_x > 8) {
					hiddenRight(mPreItemView, true);
				}
			}

			if (mIsHorizontal != null && mIsHorizontal) {
				if (mFirstX - lastX > mRightViewWidth / 8) {
					showRight(mCurrentItemView);
				} else {
					// System.out.println("5---> hiddenRight");
					/**
					 * 情况五：
					 * <p>
					 * 向右滑动一个item,且滑动的距离超过了右边View的宽度的一半，隐藏之。
					 */
					mIsShown = true;
					hiddenRight(mCurrentItemView, true);
				}
				return true;
			}
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void clearPressedState() {
		if(mCurrentItemView != null) {
			// TODO current item is still has background, issue
			mCurrentItemView.setPressed(false);
			setPressed(false);
			refreshDrawableState();
			// invalidate();
		}
	}

	private void showRight(View view) {
		// System.out.println("=========showRight");

		if (!mIsShown) {
			Message msg = new MoveHandler().obtainMessage();
			msg.obj = view;
			msg.arg1 = view.getScrollX();
			msg.arg2 = mRightViewWidth;
			msg.sendToTarget();

			mIsShown = true;
		}
	}

	public void hiddenRight(View view, boolean flag) {
		this.flag = flag;
		if (mCurrentItemView == null) {
			return;
		}
		if (view==null) {
			return;
		}
		// System.out.println("=========hiddenRight");
		if (mIsShown) {
			Message msg = new MoveHandler().obtainMessage();//
			msg.obj = view;
			msg.arg1 = view.getScrollX();
			msg.arg2 = 0;

			msg.sendToTarget();

			mIsShown = false;
		}
	}

	/**
	 * show or hide right layout animation
	 */
	@SuppressLint("HandlerLeak")
	class MoveHandler extends Handler {
		int stepX = 0;

		int fromX;

		int toX;

		View view;

		private boolean mIsInAnimation = false;

		private void animatioOver() {
			mIsInAnimation = false;
			stepX = 0;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (stepX == 0) {
				if (mIsInAnimation) {
					return;
				}
				mIsInAnimation = true;
				view = (View) msg.obj;
				fromX = msg.arg1;
				toX = msg.arg2;
				if (flag) {
					mDurationStep = 10;
				} else {
					mDurationStep = 100;
				}
				stepX = (int) ((toX - fromX) * mDurationStep * 1.0 / mDuration);
				flag = true;
				mDurationStep = 10;
				if (stepX < 0 && stepX > -1) {
					stepX = -1;
				} else if (stepX > 0 && stepX < 1) {
					stepX = 1;
				}
				if (Math.abs(toX - fromX) < 10) {
					view.scrollTo(toX, 0);
					animatioOver();
					return;
				}
			}

			fromX += stepX;
			boolean isLastStep = (stepX > 0 && fromX > toX)
					|| (stepX < 0 && fromX < toX);
			if (isLastStep) {
				fromX = toX;
			}
			view.scrollTo(fromX, 0);
			invalidate();

			if (!isLastStep) {
				this.sendEmptyMessageDelayed(0, mDurationStep);
			} else {
				animatioOver();
			}
		}
	}

	public int getRightViewWidth() {
		return mRightViewWidth;
	}

	public void setRightViewWidth(int mRightViewWidth) {
		this.mRightViewWidth = mRightViewWidth;
	}
}
