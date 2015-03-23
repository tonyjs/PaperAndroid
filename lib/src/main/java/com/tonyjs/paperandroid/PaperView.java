package com.tonyjs.paperandroid;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by tonyjs on 15. 2. 10..
 */
public class PaperView extends ViewGroup implements PaperViewAdapter.DataSetObserver{
    public PaperView(Context context) {
        super(context);
        init();
    }

    public PaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private PocketGestureDetector mGestureDetector;
    private void init() {setWillNotDraw(false);
        mGestureDetector = new PocketGestureDetector(getContext(), new PocketGestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int max = getChildCount();
        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    private PaperViewAdapter mAdapter;

    public PaperViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(PaperViewAdapter adapter) {
        mAdapter = adapter;
        adapter.registerDataSetObserver(this);
        adaptView();
    }

    private void adaptView() {
        int itemCount = mAdapter.getCount();
        if (itemCount <= 0) {
            return;
        }

        for (int i = 0; i < itemCount; i++) {
            View view = mAdapter.getView(i, this);
            addView(view);
        }
    }

    private boolean mFirstLayout = true;
    private int mFirstChildWidth;
    private int mFirstChildHeight;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }

        int height = b - t;
        int lastChildRight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (mFirstLayout) {
                mFirstChildWidth = childWidth;
                mFirstChildHeight = childHeight;
                mFirstLayout = false;
            }
            int childLeft = lastChildRight;
            int childRight = lastChildRight + childWidth;

            int childTop = height - childHeight;
            int childBottom = childTop + childHeight;
            child.layout(childLeft, childTop, childRight, childBottom);
            lastChildRight = childRight;
        }
    }

    @Override
    public void notifyDataSetChanged() {

    }

    @Override
    public void notifyItemAdded() {

    }

    @Override
    public void notifyItemRemoved(int position) {

    }

    public int getItemCount() {
        return mAdapter != null ? mAdapter.getCount() : 0;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getAdapter() == null || getAdapter().getCount() <= 0) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
    }

    private class PocketGestureDetector extends GestureDetectorCompat {
        private PocketGestureListener mListener;
        public PocketGestureDetector(Context context, PocketGestureListener listener) {
            super(context, listener);
            mListener = listener;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean handled = super.onTouchEvent(event);
            int action = event.getAction() & MotionEventCompat.ACTION_MASK;
            if (action == MotionEvent.ACTION_UP) {
                mListener.dispatchSingleTapUpIfNeed(event);
            }
            return handled;
        }
    }

    private class PocketGestureListener extends GestureDetector.SimpleOnGestureListener {

        public void dispatchSingleTapUpIfNeed(MotionEvent e) {
            if (getItemCount() > 0) {
                onSingleTapUp(e);
            }
        }

        private TouchTarget mTouchTarget;

        @Override
        public boolean onDown(MotionEvent e) {
            mTouchTarget = getChildByTouchPosition(e.getX(), e.getY());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mTouchTarget == null) {
                return;
            }

//            performItemLongClick(mTouchTarget);
            mTouchTarget = null;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
            if (mTouchTarget != null) {
                View target = mTouchTarget.getTarget();
                target.setPressed(true);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mTouchTarget != null) {
                View target = mTouchTarget.getTarget();
                target.setPressed(false);
//                performItemClick(mTouchTarget);
                mTouchTarget = null;
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                return false;
            }

            if (mTouchTarget != null) {
                View target = mTouchTarget.getTarget();
                target.setPressed(false);

                scale(target, distanceY);

                invalidate();

//                mTouchTarget = null;
            }

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mTouchTarget != null) {
                View target = mTouchTarget.getTarget();
                target.setPressed(false);
                mTouchTarget = null;
            }

            return true;
        }
    }

    private TouchTarget getChildByTouchPosition(float x, float y){
        int max = getChildCount();
        if (max <= 0) {
            return null;
        }

        TouchTarget target = null;
        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);
            float left = child.getX();
            float top = child.getY();
            int childWidth = child.getWidth();
            int childHeight = child.getHeight();
            float right = left + childWidth;
            float bottom = top + childHeight;
            if (x >= left && x <= right && y >= top && y <= bottom) {
                target = new TouchTarget(i, child);
                break;
            }
        }
        return target;
    }

    private void scale(View view, float distanceY) {
        if (distanceY > 0) {
            scaleUp(view, distanceY);
        } else {
            scaleDown(view, distanceY);
        }
    }

    private void scaleUp(View view, float distanceY) {
        LayoutParams params = view.getLayoutParams();
        int newWidth = Math.min(getWidth(), params.width + (int) distanceY);
        params.width = newWidth;
        int newHeight = Math.min(getHeight(), params.height + (int) distanceY);
        params.height = newHeight;
        view.setLayoutParams(params);
    }

    private void scaleDown(View view, float distanceY) {
        LayoutParams params = view.getLayoutParams();
        int newWidth = Math.max(mFirstChildWidth, params.width + (int) distanceY);
        params.width = newWidth;
        int newHeight = Math.max(mFirstChildHeight, params.height + (int) distanceY);
        params.height = newHeight;
        view.setLayoutParams(params);
    }

    private class TouchTarget {
        private int position;
        private View target;

        private TouchTarget() {
        }

        private TouchTarget(int position, View target) {
            this.position = position;
            this.target = target;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public View getTarget() {
            return target;
        }

        public void setTarget(View target) {
            this.target = target;
        }
    }
}
