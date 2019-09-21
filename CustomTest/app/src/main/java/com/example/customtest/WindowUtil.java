package com.example.customtest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * create by WenJinG on 2019/7/26
 */
public class WindowUtil {
    private WindowManager mManager;
    private View mView;
    private  WindowManager.LayoutParams mParams;
    private static final String TAG = "window";
    public WindowUtil(Activity context,int resouceId){
        Log.d(TAG,"windowManager null:"+(mManager==null));
        if(mManager==null){
            mManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Log.d(TAG,"windowManager null:"+(mManager==null));
        }
        mView = LayoutInflater.from(context).inflate(R.layout.window_children,
                (ViewGroup) context.getWindow().getDecorView(),false);
        mParams = new WindowManager.LayoutParams();
        mParams.token = context.getWindow().getDecorView().getWindowToken();
        mParams.x = 0;
        mParams.y=0;
        mParams.gravity = Gravity.CENTER;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
    }

    public void show(){
        Log.d(TAG,"windowManager null:"+(mManager==null));
        if(mManager!=null)
        mManager.addView(mView,mParams);
    }
}
