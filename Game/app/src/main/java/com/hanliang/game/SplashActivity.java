package com.hanliang.game;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends FragmentActivity {

    @BindView(R.id.start_img)
    ImageView startImg;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );

        ButterKnife.bind(this);
        startImg.post(new Runnable() {
            @Override
            public void run() {
                setBaseEvent();
            }
        });
    }




    private void setBaseEvent() {
        //动画-渐变
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(startImg, "alpha", 0f, 1f);
        alpha.setDuration(3000);
        alpha.start();

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                alpha.cancel();
            }
        }, 4000);
    }


}
