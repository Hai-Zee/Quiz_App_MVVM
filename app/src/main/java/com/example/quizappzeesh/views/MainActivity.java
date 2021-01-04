package com.example.quizappzeesh.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quizappzeesh.Broadcast.MyBroadCast;
import com.example.quizappzeesh.R;
import com.example.quizappzeesh.adapter.QuizListAdapter;

public class MainActivity extends AppCompatActivity implements MyBroadCast.ConnectionInterface {

    private LottieAnimationView lottieAnimationView;
    private ConstraintLayout constraintLayoutNavHost, constraintLayoutAnimation;
    private MyBroadCast myBroadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayoutNavHost = findViewById(R.id.frag_constraint_id);
        constraintLayoutAnimation = findViewById(R.id.constrainedAnim);

        myBroadCast = new MyBroadCast();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadCast, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myBroadCast);
    }

    @Override
    public void connectivity(boolean isConnected) {

        if (isConnected){
            constraintLayoutAnimation.setVisibility(View.GONE);
            constraintLayoutNavHost.setVisibility(View.VISIBLE);
        }
        else{

            constraintLayoutAnimation.setVisibility(View.VISIBLE);
            constraintLayoutNavHost.setVisibility(View.INVISIBLE);
        }
    }
}