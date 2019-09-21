package com.example.customtest;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * create by WenJinG on 2019/7/19
 */
public class RefreshWithHeader extends ViewGroup {
    private static final String TAG = "HeaderRefresh";

    private View mHeader;
    private TextView mState;
    private List<RefreshListener>mRefreshListeners;
    private RecyclerView mChild;
    private boolean firstLoad = true;
    private int touchSlop;
    private float mLastInterceptY = 0;
    private float mLastY ;
    private float distance ;

    private static final int PULL_TO_REFRESH = 1;
    private static final int RELEASE_TO_REFRESH = 2;
    private static final int REFRESHING = 0;
    private int mFlags = -1 ;

    public RefreshWithHeader(Context context) {
        super(context);
    }

    public RefreshWithHeader(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RefreshWithHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(true);
        mHeader = LayoutInflater.from(this.getContext()).inflate(R.layout.header,this,false);
        mState = mHeader.findViewById(R.id.state);
        mState.setText("下拉刷新");
        addView(mHeader,0);
        touchSlop = ViewConfiguration.getTouchSlop();
        mRefreshListeners = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = 0;
        int measureHeight = 0;
        final int childrenCount = getChildCount();

        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpaceMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpaceMode = MeasureSpec.getMode(heightMeasureSpec);

        if(childrenCount == 0){
            setMeasuredDimension(0,0);
        }else if(widthSpaceMode == MeasureSpec.AT_MOST && heightSpaceMode == MeasureSpec.AT_MOST ){
            for(int i = 0;i<childrenCount;i++){
                if(getChildAt(i)!=null) {
                    measureHeight += getChildAt(i).getMeasuredHeight();
                    measureWidth = Math.max(measureWidth, getChildAt(i).getMeasuredWidth());
                }
            }
            Log.d(TAG,"heightww :"+measureHeight);
            setMeasuredDimension(measureWidth,measureHeight);
        }else if(widthSpaceMode == MeasureSpec.AT_MOST){
            for (int i = 0;i < childrenCount;i++){
                if(getChildAt(i)!=null)
                measureWidth = Math.max(measureWidth,getChildAt(i).getMeasuredWidth());
            }
            setMeasuredDimension(measureWidth,heightSpaceSize);
        }else if(heightSpaceMode == MeasureSpec.AT_MOST){
            for(int i = 0; i<childrenCount;i++){
                if(getChildAt(i)!=null)
                measureHeight += getChildAt(i).getMeasuredHeight();
            }
            setMeasuredDimension(widthSpaceSize,measureHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childrenCount = getChildCount();
        Log.d(TAG,"intercept :" + "子View个数"+childrenCount);
        int childrenTop = -mHeader.getMeasuredHeight();

        for(int i = 0;i < childrenCount;i++){
            final View child = getChildAt(i);
            if(child.getVisibility() != GONE){
                child.layout(0,childrenTop,child.getMeasuredWidth(),
                        childrenTop+child.getMeasuredHeight());
                childrenTop += child.getMeasuredHeight();
            }
        }

        if(changed && firstLoad){
            firstLoad = false;
            //mHeader.layout(0,-mHeader.getHeight(),mHeader.getWidth(),0);
            mChild = (RecyclerView) getChildAt(childrenCount-1);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        float deltaY = ev.getY() - mLastInterceptY;
        Log.d(TAG,"intercept :" + "准备拦截"+deltaY+ (mHeader.getY()>-mHeader.getHeight()));
       switch (ev.getAction()){
           case MotionEvent.ACTION_DOWN:
               intercepted = false;
               break;
           case MotionEvent.ACTION_MOVE:
               if (deltaY>touchSlop && !mChild.canScrollVertically(-1)){
                   Log.d(TAG,"intercept :" + "滑到顶部:拦截");
                   intercepted = true;
               }else if(deltaY < 0 && mHeader.getY()>-mHeader.getHeight()) {
                   Log.d(TAG,"intercept :" + "拦截"+deltaY);
                   intercepted = true;
               }else {
                   intercepted = false;
               }
               break;
           case MotionEvent.ACTION_UP:
               intercepted = false;
               break;
       }
        mLastInterceptY = ev.getY();
       return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount()>1){
            mFlags = -1;
            mLastY = 0;
            mHeader.layout(0, -mHeader.getHeight(), mHeader.getWidth(), 0);
            mChild.layout(0, 0, getWidth(), mChild.getHeight());
            return false;
        }
        float y = event.getY();
        float deltaX = y - mLastY;
        boolean result = false;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                Log.d(TAG,"down y:"+y);
                Log.d(TAG,"down deltaX:"+deltaX);
                Log.d(TAG,"down distance:"+distance);
                result = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG,"move y:"+event.getY());
                Log.d(TAG,"move mLastY:"+mLastY);
                Log.d(TAG,"move deltaX:"+deltaX);
                Log.d(TAG,"move distance:"+distance);
                Log.d(TAG,"move headerTop:" + mHeader.getY());
                if(mFlags==REFRESHING){
                    return false;
                }
                if(deltaX<touchSlop){
                   result = false;
                    if(mHeader.getY() < 0){
                        mFlags = PULL_TO_REFRESH;
                        updateHeaderTitle("下拉刷新");
                        Log.d(TAG,"move PULL_TO_REFRESH:" + mHeader.getY());
                        result = false;
                    }
                }else  if(mFlags != REFRESHING&& mHeader.getY()>0) {
                    Log.d(TAG,"move RELEASE_TO_REFRESH:" + mHeader.getY());
                    updateHeaderTitle("松手刷新");
                    mFlags = RELEASE_TO_REFRESH;
                }
                if(mLastY!=0&&event.getPointerCount()==1) {
                    mHeader.layout(0, (int) mHeader.getY() + (int) deltaX/2, mHeader.getWidth()
                            , (int) mHeader.getY() + (int) deltaX/2 + mHeader.getHeight());
                    mChild.layout(0, (int) mChild.getY() + (int) deltaX/2, mChild.getWidth()
                            , (int) mChild.getY() + (int) deltaX/2 + mChild.getHeight());
                }
                mLastY = y;
                result = false;
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG,"up RELEASE_TO_REFRESH:" + mFlags);
               if (mFlags == RELEASE_TO_REFRESH){
                   updateHeaderTitle("松手刷新");
                   refresh();
                   result = true;
                }
               if(!result) {
                   mHeader.layout(0, -mHeader.getHeight(), mHeader.getWidth(), 0);
                   mChild.layout(0, 0, getWidth(), mChild.getHeight());
               }
                mFlags = -1;
                mLastY = 0;
                Log.d(TAG,"up 数据初始化了:" );
               break;
        }
        return result;
    }

    public void addRefreshListener(RefreshListener listener){
        if (listener!=null)
        mRefreshListeners.add(listener);
    }

    public void clearRefreshListener(){
        if (mRefreshListeners!=null&&mRefreshListeners.size()>0){
            mRefreshListeners.clear();
        }
    }

    public void addHeader(View header){
        if(header!=null)
        this.mHeader = header;
    }

    public void addHeader(@LayoutRes int header){
            this.mHeader = LayoutInflater.from(this.getContext())
                    .inflate(header,this,false);
    }

    private void updateHeaderTitle(String s){
        mState.setText(s);
    }

    private void refresh(){
        mFlags = REFRESHING;
        refreshAnimation();
        if(mRefreshListeners!=null&&mRefreshListeners.size()>0){
            for(RefreshListener refreshListener :mRefreshListeners){
                refreshListener.onRefresh();
            }
        }
        updateHeaderTitle("正在刷新");
        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
               initState();
                    Log.d(TAG,"refresh 正在刷新:" +distance);
                }
            }
        };
        handler.sendEmptyMessageDelayed(1,2000);
    }

    private void refreshAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mHeader,"Y",
                mHeader.getY(),0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mChild,"Y",
                mChild.getY(),mHeader.getHeight());
        AnimatorSet set = new AnimatorSet();
        set.play(animator).with(animator2);
        set.setDuration(500);
        set.start();
        mHeader.layout(0,0, mHeader.getWidth(), mHeader.getHeight());
        mChild.layout(0, mHeader.getHeight(),getWidth(),mChild.getHeight()+ mHeader.getHeight());
    }

    private void initState(){
        mFlags = -1;
        mLastY = 0;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mHeader,"Y",
                mHeader.getY(),-mHeader.getHeight());
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mChild,"Y",
                mChild.getY(),0);
        AnimatorSet set = new AnimatorSet();
        set.play(animator).with(animator2);
        set.setDuration(500);
        set.start();
        mHeader.layout(0,-mHeader.getHeight(), mHeader.getWidth(),0);
        mChild.layout(0,0,getWidth(),mChild.getHeight());
    }

    interface RefreshListener{
        void onRefresh();
    }
}
