package com.tdt.project.wearhelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FindPhoneActivity extends Activity implements
        DelayedConfirmationView.DelayedConfirmationListener {

    private static final int CONFIRMATION_REQUEST_CODE = 0;

    private boolean mCanceled = false;
    private DelayedConfirmationView mDelayedView;
    private View mButton;
    private TextView mtext;
    private ResponseReceiver receiver = new ResponseReceiver();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONFIRMATION_REQUEST_CODE) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_phone);
        registerReceiver(receiver, new IntentFilter(FindPhoneService.ACTION_TOGGLE_ALARM));

        mDelayedView = (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
        mtext = (TextView) findViewById(R.id.textView);
        mDelayedView.setVisibility(View.GONE);
        mDelayedView.setTotalTimeMs(2000);
        mDelayedView.setListener(this);
        mButton = findViewById(R.id.trigger_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateInDelayedViewAndStartTimer();
            }
        });
    }

    @Override
    public void onTimerFinished(View view) {
        if (mCanceled) {
            return;
        }
        Intent serviceIntent = new Intent(this, FindPhoneService.class);
        serviceIntent.setAction(FindPhoneService.ACTION_TOGGLE_ALARM);
        startService(serviceIntent);

        /*Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.message_confirmed));
        startActivityForResult(intent, CONFIRMATION_REQUEST_CODE);*/
    }

    @Override
    public void onTimerSelected(View view) {
        mCanceled = true;
        Toast.makeText(this, getString(R.string.message_canceled), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void animateInDelayedViewAndStartTimer() {
        // We'll translate the views one full screen width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Animate the trigger button off to the left
        ObjectAnimator buttonAnimator = ObjectAnimator.ofFloat(mButton, View.TRANSLATION_X, 0,
                -screenWidth);
        // Animate the delayed confirmation view in from the right
        ObjectAnimator delayedViewAnimator = ObjectAnimator.ofFloat(mDelayedView, View.TRANSLATION_X,
                screenWidth, 0);
        ObjectAnimator textViewAnimator = ObjectAnimator.ofFloat(mtext, View.TRANSLATION_X, screenWidth, 1);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(buttonAnimator, delayedViewAnimator, textViewAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                // Show the delayed confirmation view
                mDelayedView.setVisibility(View.VISIBLE);
                mtext.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Start the timer
                mDelayedView.start();
            }
        });
        set.setDuration(300);
        set.start();
    }
}
