package com.example.customtest;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private RefreshWithHeader refreshWithHeader;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
                return new RecyclerView.ViewHolder(view) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
        refreshWithHeader = findViewById(R.id.hr);
        refreshWithHeader.addRefreshListener(new RefreshWithHeader.RefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(),"我刷新了",Toast.LENGTH_SHORT).show();
            }
        });

        editText = findViewById(R.id.et);
        WrapperView view = new WrapperView(editText);
        ObjectAnimator animator = ObjectAnimator.ofInt(view,"MarginTop",0,200,100,-300);
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }

    public void showWindow(View view) {
        WindowUtil windowUtil = new WindowUtil(this,0);
        windowUtil.show();
    }

    class WrapperView{
        private View mView;
        public WrapperView(View view){
            mView = view;
        }

        public void setMarginTop(int margin){
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mView.getLayoutParams();
            if(params!=null){
                params.topMargin = margin;
                mView.requestLayout();
            }
        }

        public int getMarginTop(){
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)mView.getLayoutParams();
            if(params!=null){
               return params.topMargin;
            }
            return -1;
        }
    }
}
