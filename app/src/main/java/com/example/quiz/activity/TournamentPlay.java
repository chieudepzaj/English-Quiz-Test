package com.example.quiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.android.volley.toolbox.ImageLoader;


import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.example.quiz.Constant;
import com.example.quiz.R;
import com.example.quiz.helper.ApiConfig;
import com.example.quiz.helper.AppController;
import com.example.quiz.helper.AudienceProgress;
import com.example.quiz.helper.CircleTimer;
import com.example.quiz.helper.Session;
import com.example.quiz.helper.TouchImageView;
import com.example.quiz.helper.UserSessionManager;
import com.example.quiz.helper.Utils;
import com.example.quiz.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class TournamentPlay extends AppCompatActivity implements OnClickListener {



    public Question question;
    public int questionIndex = 0,
            btnPosition = 0,
    count_question_completed = 0,
            score = 0,
            correctQuestion = 0,
            inCorrectQuestion = 0,
            rightAns;
    public TextView tvScore,coin_count;
    public static CircleTimer progressTimer;
    public static TextView btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5, txtQuestion,tvImgQues, tvAlert, tvIndex;
    public CompleteActivity completeActivity;
    public static Activity activity;
    public CoordinatorLayout playLayout;

    public RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E;
    private Animation animation;
    private final Handler mHandler = new Handler();
    String qid, qtitle, rightanswer, entrypoint, type, from, rightString;
    public Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, RightSwipe_E, Fade_in, slid_up;
    private AudienceProgress progressBarTwo_A, progressBarTwo_B, progressBarTwo_C, progressBarTwo_D, progressBarTwo_E;
    public static Timer timer;
    UserSessionManager session;

    public static ArrayList<String> options;
    public static long leftTime = 0;
    public boolean isDialogOpen = false;
    public static ArrayList<Question> questionList;
    int queattend = 0, correctans = 0, count = 0, queindex = 0, btnposition = 0, wrongans = 0, totalque = 0, point;

    TouchImageView imgQuestion;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ProgressBar progressBar;
    ImageView imgZoom;
    int click = 0;
    int textSize;
    RelativeLayout mainLayout;
    public String fromQue;
    public ScrollView  queScroll;
    NestedScrollView mainScroll;
    public static AdRequest adRequest;
    public RewardedAd rewardedVideoAds;
    public Toolbar toolbar;
    private RewardedVideoAd rewardedVideoAd;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_play);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainLayout = findViewById(R.id.play_layout);
        Locale locale = new Locale(Session.getApplanguage(TournamentPlay.this));
        Session.setApplanguage(TournamentPlay.this, Session.getApplanguage(TournamentPlay.this));
        Locale.setDefault(locale);
        Resources resources = getResources();
        DrawerActivity.config = resources.getConfiguration();
        DrawerActivity.config .setLocale(locale);
        resources.updateConfiguration(DrawerActivity.config , resources.getDisplayMetrics());
        qid = getIntent().getStringExtra("id");
        qtitle = getIntent().getStringExtra("title");
        activity = TournamentPlay.this;
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(qtitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        from = getIntent().getStringExtra("from");
        completeActivity = new CompleteActivity();
        session = new UserSessionManager(activity);
        final int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout, R.id.e_layout};

        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }

        type = from;

        if (from != null) {
            if (from.equals("contest")) {
                entrypoint = getIntent().getStringExtra("entrypoint");
                type = "Contest Quiz";
            } else if (from.equals("spin")) {
                type = "Spin Quiz";
            }
        }


        textSize = Integer.parseInt(Session.getSavedTextSize(activity));
        Session.removeSharedPreferencesData(activity);
        RightSwipe_A = AnimationUtils.loadAnimation(activity, R.anim.anim_right_a);
        RightSwipe_B = AnimationUtils.loadAnimation(activity, R.anim.anim_right_b);
        RightSwipe_C = AnimationUtils.loadAnimation(activity, R.anim.anim_right_c);
        RightSwipe_D = AnimationUtils.loadAnimation(activity, R.anim.anim_right_d);
        RightSwipe_E = AnimationUtils.loadAnimation(activity, R.anim.anim_right_e);
        Fade_in = AnimationUtils.loadAnimation(activity, R.anim.fade_out);

        resetAllValue();
        mainScroll.setOnTouchListener((v, event) -> {
            v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        queScroll.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        progressTimer.setMaxProgress(Constant.CIRCULAR_MAX_PROGRESS);
        progressTimer.setCurrentProgress(Constant.CIRCULAR_MAX_PROGRESS);
        // rewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        RewardsAdsFacebookLoad();
        RewardsAdsGoogleLoads();
    }

    public void RewardsAdsGoogleLoads() {
        adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, Constant.ADMOB_REWARDS_ADS,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        // Log.d(TAG, loadAdError.getMessage());
                        rewardedVideoAds = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardedVideoAds = rewardedAd;

                    }
                });
    }


    public void RewardsAdsFacebookLoad() {
        System.out.println("====== dv " + Constant.FB_REWARDS_ADS);
        rewardedVideoAd = new RewardedVideoAd(this, Constant.FB_REWARDS_ADS);
        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                Log.e(AppController.TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                Log.d(AppController.TAG, "Rewarded video ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                Log.d(AppController.TAG, "Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                Log.d(AppController.TAG, "Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                Log.d(AppController.TAG, "Rewarded video completed!");

                // Call method to give reward


                RewardAdsLoad();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                Log.d(AppController.TAG, "Rewarded video ad closed!");
            }
        };
        rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                        .withAdListener(rewardedVideoAdListener)
                        .build());
    }

    public void RewardAdsLoad() {
        // Instantiate a RewardedVideoAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get
        // a no fill error).
        System.out.println("====== dv " + Constant.FB_REWARDS_ADS);
        rewardedVideoAd = new RewardedVideoAd(this, Constant.FB_REWARDS_ADS);
        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                Log.e(AppController.TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                Log.d(AppController.TAG, "Rewarded video ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                Log.d(AppController.TAG, "Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                Log.d(AppController.TAG, "Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                Log.d(AppController.TAG, "Rewarded video completed!");

                // Call method to give reward

                RewardAdsLoad();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                Log.d(AppController.TAG, "Rewarded video ad closed!");
            }
        };
        rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                        .withAdListener(rewardedVideoAdListener)
                        .build());
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static void ChangeTextSize(int size) {

        if (btnOpt1 != null)
            btnOpt1.setTextSize(size);
        if (btnOpt2 != null)
            btnOpt2.setTextSize(size);
        if (btnOpt3 != null)
            btnOpt3.setTextSize(size);
        if (btnOpt4 != null)
            btnOpt4.setTextSize(size);
        if (btnOpt5 != null)
            btnOpt5.setTextSize(size);
        if (txtQuestion != null)
            txtQuestion.setTextSize(size);

    }

    @SuppressLint("SetTextI18n")
    public void resetAllValue() {
        progressTimer = findViewById(R.id.progressTimer);

        playLayout = findViewById(R.id.innerLayout);
        tvIndex = findViewById(R.id.tvIndex);

        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);
        progressBar = findViewById(R.id.progressBar);
        imgQuestion = findViewById(R.id.imgQuestion);

        tvAlert = findViewById(R.id.tvAlert);

        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);
        btnOpt5 = findViewById(R.id.btnOpt5);

        imgZoom = findViewById(R.id.imgZoom);
        tvAlert.setText(getString(R.string.msg_no_internet));
        txtQuestion = findViewById(R.id.txtQuestion);
        tvImgQues=findViewById(R.id.tvImgQues);
        layout_A = findViewById(R.id.a_layout);
        layout_B = findViewById(R.id.b_layout);
        layout_C = findViewById(R.id.c_layout);
        layout_D = findViewById(R.id.d_layout);
        layout_E = findViewById(R.id.e_layout);
        ChangeTextSize(textSize);
        progressBarTwo_A = findViewById(R.id.progress_A);
        progressBarTwo_B = findViewById(R.id.progress_B);
        progressBarTwo_C = findViewById(R.id.progress_C);
        progressBarTwo_D = findViewById(R.id.progress_D);
        progressBarTwo_E = findViewById(R.id.progress_E);
        progressBarTwo_A.SetAttributes();
        progressBarTwo_B.SetAttributes();
        progressBarTwo_C.SetAttributes();
        progressBarTwo_D.SetAttributes();
        progressBarTwo_E.SetAttributes();

        animation = AnimationUtils.loadAnimation(activity, R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
        count_question_completed = Session.getCountQuestionCompleted(getApplicationContext());
        tvScore = findViewById(R.id.txtScore);
        tvScore.setText(String.valueOf(score));
        coin_count = findViewById(R.id.coin_count);
        coin_count.setText(" ");

        if (Utils.isNetworkAvailable(activity)) {
            getQuestionsFromJson();

        } else {
            tvAlert.setText(getString(R.string.msg_no_internet));
            playLayout.setVisibility(View.GONE);


        }

    }


    private void nextQuizQuestion() {
        queScroll.scrollTo(0, 0);
        queattend = queattend + 1;
        count = 0;
        stopTimer();
        starTimer();

        Constant.LeftTime = 0;
        leftTime = 0;
        setAgain();
        if (questionIndex >= questionList.size()) {
            levelCompleted();
        }
        invalidateOptionsMenu();

        layout_A.setBackgroundResource(R.drawable.card_shadow);
        layout_B.setBackgroundResource(R.drawable.card_shadow);
        layout_C.setBackgroundResource(R.drawable.card_shadow);
        layout_D.setBackgroundResource(R.drawable.card_shadow);
        layout_E.setBackgroundResource(R.drawable.card_shadow);
        layout_A.clearAnimation();
        layout_B.clearAnimation();
        layout_C.clearAnimation();
        layout_D.clearAnimation();
        layout_E.clearAnimation();
        layout_A.setClickable(true);
        layout_B.setClickable(true);
        layout_C.setClickable(true);
        layout_D.setClickable(true);
        layout_E.setClickable(true);
        btnOpt1.startAnimation(RightSwipe_A);
        btnOpt2.startAnimation(RightSwipe_B);
        btnOpt3.startAnimation(RightSwipe_C);
        btnOpt4.startAnimation(RightSwipe_D);
        btnOpt5.startAnimation(RightSwipe_E);


        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            int temp = questionIndex;
            imgQuestion.resetZoom();
            tvIndex.setText(++temp + "/" + questionList.size());

            if (!question.getImage().isEmpty()) {
                txtQuestion.setVisibility(View.GONE);
                imgQuestion.setImageUrl(question.getImage(), imageLoader);
                tvImgQues.startAnimation(Fade_in);
                tvImgQues.setText(question.getQuestion());
                tvImgQues.setVisibility(View.VISIBLE);
                imgQuestion.setVisibility(View.VISIBLE);
                imgZoom.setVisibility(View.VISIBLE);
                imgZoom.setOnClickListener(view -> {
                    click++;
                    if (click == 1)
                        imgQuestion.setZoom(1.25f);
                    else if (click == 2)
                        imgQuestion.setZoom(1.50f);
                    else if (click == 3)
                        imgQuestion.setZoom(1.75f);
                    else if (click == 4) {
                        imgQuestion.setZoom(2.00f);
                        click = 0;
                    }
                });
            } else {
                txtQuestion.startAnimation(Fade_in);
                txtQuestion.setText(question.getQuestion());
                imgZoom.setVisibility(View.GONE);
                imgQuestion.setVisibility(View.GONE);
                tvImgQues.setVisibility(View.GONE);
                txtQuestion.setVisibility(View.VISIBLE);
            }


            options = new ArrayList<String>();
            options.addAll(question.getOptions());

            if (question.getQueType().equals(Constant.TRUE_FALSE)) {
                layout_C.setVisibility(View.GONE);
                layout_D.setVisibility(View.GONE);
                btnOpt1.setGravity(Gravity.CENTER);
                btnOpt2.setGravity(Gravity.CENTER);

            } else {
                Collections.shuffle(options);
                layout_C.setVisibility(View.VISIBLE);
                layout_D.setVisibility(View.VISIBLE);
                btnOpt1.setGravity(Gravity.NO_GRAVITY);
                btnOpt2.setGravity(Gravity.NO_GRAVITY);
            }

            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 4)
                    layout_E.setVisibility(View.GONE);
                else
                    layout_E.setVisibility(View.VISIBLE);

            }
            btnOpt1.setText(Html.fromHtml(options.get(0).trim()));
            btnOpt2.setText(Html.fromHtml(options.get(1).trim()));
            btnOpt3.setText(Html.fromHtml(options.get(2).trim()));
            btnOpt4.setText(Html.fromHtml(options.get(3).trim()));
            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 5)
                    btnOpt5.setText(Html.fromHtml(options.get(4).trim()));
            }

        }
    }

    public void levelCompleted() {
        Utils.TotalQuestion = questionList.size();
        Utils.CoreectQuetion = correctQuestion;
        Utils.WrongQuation = inCorrectQuestion;

        stopTimer();
        Utils.TotalTournment = questionList.size();
        int total = questionList.size();
        int percent = (correctQuestion * 100) / total;

        Utils.level_score = score;
        Utils.TournmentScore = score;
        coin_count.setText("___");

        if (Session.isLogin(getApplicationContext())) {

            UpdateScore(String.valueOf(Utils.TournmentScore), String.valueOf(Utils.TotalTournment));

            AddPoint(String.valueOf(point), type, false);


        }

        if (percent >= Constant.PASSING_PER)
            Session.setLevelComplete(getApplicationContext(), true);
        else
            Session.setLevelComplete(getApplicationContext(), false);


        Intent intent = new Intent(activity, CompleteActivityTournaments.class);
        intent.putExtra("qid", qid);
        intent.putExtra("qtitle", qtitle);
        intent.putExtra("rans", correctQuestion);
        intent.putExtra("wans", inCorrectQuestion);
        intent.putExtra("score", score);
        intent.putExtra("totalque", Utils.TotalTournment);
        startActivity(intent);
        finish();
        blankAllValue();

    }


    public void UpdateScore(final String score, final String queattend) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CONTEST_UPDATE_SCORE, Constant.GET_DATA_KEY);
        params.put(Constant.CONTEST_ID, qid);
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.QUESTION_ATTEND, queattend);
        params.put(Constant.CORRECT_ANSWERS, "" + Utils.CoreectQuetion);
        params.put(Constant.SCORE, "" + score);
        System.out.println("Value of PARAms::=" + params);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {

            }
        }, params);

    }


    private void killActivity() {
        finish();
    }


    public void AddReview(Question question, TextView tvBtnOpt, RelativeLayout layout) {
        layout_A.setClickable(false);
        layout_B.setClickable(false);
        layout_C.setClickable(false);
        layout_D.setClickable(false);
        layout_E.setClickable(false);

        if (tvBtnOpt.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            rightSound();

            layout.setBackgroundResource(R.drawable.right_gradient);
            layout.startAnimation(animation);
            correctQuestion = correctQuestion + 1;
            if (count >= 20) {
                point = point + Constant.score1;
                score = score + Constant.score1;
            } else if (count < 20 && count >= 10) {
                point = point + Constant.score2;
                score = score + Constant.score2;
            } else {
                point = point + Constant.score3;
                score = score + Constant.score3;
            }
            tvScore.setText(String.valueOf(score));
            System.out.println("Values of Score" + score);
        } else {

            playWrongSound();
            layout.setBackgroundResource(R.drawable.wrong_gradient);
            score = score - Constant.PENALTY;
            inCorrectQuestion = inCorrectQuestion + 1;
            System.out.println("Values of INcorrecation" + inCorrectQuestion);
            tvScore.setText(String.valueOf(score));

        }

        question.setSelectedAns(tvBtnOpt.getText().toString());
        if (Constant.QUICK_ANSWER_ENABLE.equals("1"))
            RightAnswerBackgroundSet();
        question.setAttended(true);
        stopTimer();

        questionIndex++;
        mHandler.postDelayed(mUpdateUITimerTask, 1000);

    }

    public void RightAnswerBackgroundSet() {
        if (btnOpt1.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_A.setBackgroundResource(R.drawable.right_gradient);
            layout_A.startAnimation(animation);

        } else if (btnOpt2.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_B.setBackgroundResource(R.drawable.right_gradient);
            layout_B.startAnimation(animation);

        } else if (btnOpt3.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_C.setBackgroundResource(R.drawable.right_gradient);
            layout_C.startAnimation(animation);

        } else if (btnOpt4.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_D.setBackgroundResource(R.drawable.right_gradient);
            layout_D.startAnimation(animation);

        } else if (btnOpt5.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_E.setBackgroundResource(R.drawable.right_gradient);
            layout_E.startAnimation(animation);
        }
    }

    @Override
    public void onClick(View v) {
        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
            layout_C.setClickable(false);
            layout_D.setClickable(false);
            layout_E.setClickable(false);
            if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
                progressBarTwo_A.setVisibility(View.GONE);
                progressBarTwo_B.setVisibility(View.GONE);
                progressBarTwo_C.setVisibility(View.GONE);
                progressBarTwo_D.setVisibility(View.GONE);
                progressBarTwo_E.setVisibility(View.GONE);
            }
            switch (v.getId()) {
                case R.id.a_layout:
                    AddReview(question, btnOpt1, layout_A);
                    break;

                case R.id.b_layout:
                    AddReview(question, btnOpt2, layout_B);

                    break;
                case R.id.c_layout:
                    AddReview(question, btnOpt3, layout_C);

                    break;
                case R.id.d_layout:
                    AddReview(question, btnOpt4, layout_D);

                    break;
                case R.id.e_layout:
                    AddReview(question, btnOpt5, layout_E);

                    break;
            }

        }
    }


    private final Runnable mUpdateUITimerTask = () -> {
        if (getApplicationContext() != null) {

            nextQuizQuestion();

        }
    };


    public void PlayAreaLeaveDialog() {


        if (!tvAlert.getText().equals(getResources().getString(R.string.no_enough_question))) {
            stopTimer();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.exit_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();
            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                // Write your code here to execute after dialog closed
                stopTimer();
                leftTime = 0;
                Constant.LeftTime = 0;
                UpdateScore(String.valueOf(score), String.valueOf(queattend));
                AddPoint(String.valueOf(point), type, false);
                killActivity();

            });

            alertDialog.setNegativeButton(getString(R.string.no), (dialog, which) -> {
                alertDialog1.dismiss();
                Constant.LeftTime = leftTime;
                if (Constant.LeftTime != 0) {

                    timer = new Timer(leftTime, 1000);
                    timer.start();


                }
            });
            // Showing Alert Message
            alertDialog.show();
        } else {
            killActivity();
        }
    }


    //play sound when answer is correct
    public void rightSound() {
        if (Session.getSoundEnableDisable(activity))
            Utils.setrightAnssound(activity);

        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);

    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(activity))
            Utils.setwronAnssound(activity);

        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);

    }

    //set progress again after next question
    private void setAgain() {
        if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
            progressBarTwo_A.setVisibility(View.GONE);
            progressBarTwo_B.setVisibility(View.GONE);
            progressBarTwo_C.setVisibility(View.GONE);
            progressBarTwo_D.setVisibility(View.GONE);
            progressBarTwo_E.setVisibility(View.GONE);
        }
    }


    public void BackButtonMethod() {

        CheckSound();
        PlayAreaLeaveDialog();

    }

    public void CheckSound() {

        if (Session.getSoundEnableDisable(activity))
            Utils.backSoundonclick(activity);

        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);
    }

    public void SettingButtonMethod() {
        CheckSound();
        stopTimer();

        Intent intent = new Intent(activity, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }

    public void getQuestionsFromJson() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_QUESTION_BY_CONTEST, Constant.GET_DATA_KEY);
        params.put(Constant.CONTEST_ID, qid);
        System.out.println("PARAms::=" + params);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    queindex = 0;
                    totalque = 0;
                    session.setData(UserSessionManager.KEY_QUEATTEND, "0");
                    session.setData(UserSessionManager.KEY_CORRECTANS, "0");
                    session.setData(UserSessionManager.KEY_WRONGANS, "0");
                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                        questionList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Question question = new Question();
                            question.setId(Integer.parseInt(object.getString(Constant.ID)));
                            question.setQuestion(object.getString(Constant.QUESTION));
                            question.setImage(object.getString(Constant.IMAGE));
                            question.setQueType(object.getString(Constant.QUE_TYPE));
                            question.addOption(object.getString(Constant.OPTION_A).trim());
                            question.addOption(object.getString(Constant.OPTION_B).trim());
                            question.addOption(object.getString(Constant.OPTION_C).trim());
                            question.addOption(object.getString(Constant.OPTION_D).trim());
                            if (Session.getBoolean(Session.E_MODE, activity)) {
                                if (!object.getString(Constant.OPTION_E).isEmpty() || !object.getString(Constant.OPTION_E).equals(""))
                                    question.addOption(object.getString(Constant.OPTION_E).trim());
                            }
                            question.setSelectedOpt("none");
                            String rightAns = object.getString("answer");
                            question.setAnsOption(rightAns);
                            if (rightAns.equalsIgnoreCase("A")) {
                                question.setTrueAns(object.getString(Constant.OPTION_A).trim());
                            } else if (rightAns.equalsIgnoreCase("B")) {
                                question.setTrueAns(object.getString(Constant.OPTION_B).trim());
                            } else if (rightAns.equalsIgnoreCase("C")) {
                                question.setTrueAns(object.getString(Constant.OPTION_C).trim());
                            } else if (rightAns.equalsIgnoreCase("D")) {
                                question.setTrueAns(object.getString(Constant.OPTION_D).trim());
                            } else if (rightAns.equalsIgnoreCase("E")) {
                                question.setTrueAns(object.getString(Constant.OPTION_E).trim());
                            }

                            question.setNote(object.getString(Constant.NOTE));

                            questionList.add(question);
                        }

                        if (from.equals("contest"))
                            AddPoint("-" + entrypoint, "Contest Entrypoint", true);

                        nextQuizQuestion();
                        playLayout.setVisibility(View.VISIBLE);


                    } else {
                        NotEnoughQuestion();
                    }
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

    public void NotEnoughQuestion() {
        invalidateOptionsMenu();
        tvAlert.setText(getString(R.string.no_enough_question));
        progressBar.setVisibility(View.GONE);
        playLayout.setVisibility(View.GONE);

    }

    public void blankAllValue() {
        questionIndex = 0;
        count_question_completed = 0;
        score = 0;
        correctQuestion = 0;
        inCorrectQuestion = 0;
    }



    public void showRewardedFacebookVideo() {
        if (rewardedVideoAd != null) {
            Activity activityContext = activity;

            // Check if rewardedVideoAd has been loaded successfully
            if (!rewardedVideoAd.isAdLoaded()) {
                return;
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if (rewardedVideoAd.isAdInvalidated()) {
                return;
            }
            rewardedVideoAd.show();
        } else {
            reWardsNotLoad();
        }
    }

    public void showRewardedGoogleVideos() {
        if (rewardedVideoAds != null) {
            Activity activityContext = activity;
            rewardedVideoAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    rewardedVideoAds = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                    // Called when ad fails to show.

                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    RewardsAdsFacebookLoad();
                    // Called when ad is dismissed.
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.

                }
            });
            rewardedVideoAds.show(activityContext, rewardItem -> {

                RewardsAdsFacebookLoad();
            });


        } else {
            reWardsNotLoad();
        }
    }


    public void reWardsNotLoad() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);
        TextView ok = (TextView) dialogView.findViewById(R.id.ok);
        TextView title = (TextView) dialogView.findViewById(R.id.title);
        TextView message = (TextView) dialogView.findViewById(R.id.message);
        message.setText(getString(R.string.rewards_message));
        title.setText(getString(R.string.rewads_adstitle));
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> {
            if (Constant.ADS_TYPE.equals("1")) {
                RewardsAdsGoogleLoads();
            } else {
                RewardsAdsFacebookLoad();
            }
            alertDialog.dismiss();

        });
    }


    public class Timer extends CountDownTimer {
        private Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;
            //   System.out.println("===  timer  " + millisUntilFinished);
            count = (int) (millisUntilFinished / 1000);
            if (progressTimer == null)
                progressTimer = findViewById(R.id.circleTimer);
            else
                progressTimer.setCurrentProgress(count);

            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000)
                progressTimer.SetTimerAttributes(Color.RED, Color.RED);
            else
                progressTimer.SetTimerAttributes(Color.parseColor(Constant.PROGRESS_COLOR), Color.parseColor(Constant.PROGRESS_COLOR));

        }

        @Override
        public void onFinish() {
            if (questionIndex >= questionList.size()) {
                levelCompleted();
            } else {
                playWrongSound();
                score = score - Constant.PENALTY;
                inCorrectQuestion = inCorrectQuestion + 1;
                tvScore.setText(String.valueOf(score));
                mHandler.postDelayed(mUpdateUITimerTask, 100);
                questionIndex++;
            }

        }
    }

    private void AddPoint(final String point, final String type, final Boolean isminus) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.ADD_POINT, Constant.GET_DATA_KEY);
        params.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.POINTS, point);
        params.put(Constant.TYPE, type);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("error").equals("false")) {
                        double point1;
                        if (isminus)
                            point1 = Double.parseDouble(session.getData(UserSessionManager.KEY_POINT)) - Double.parseDouble(point);
                        else
                            point1 = Double.parseDouble(session.getData(UserSessionManager.KEY_POINT)) + Double.parseDouble(point);

                        session.setData(UserSessionManager.KEY_POINT, String.valueOf(point1));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }


    public void starTimer() {
        timer = new Timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        Utils.CheckBgMusic(activity);
        if (Constant.LeftTime != 0) {
            timer = new Timer(leftTime, 1000);
            timer.start();
        }
        coin_count.setText("____");
        super.onResume();
    }

    @Override
    public void onPause() {
        AppController.StopSound();
        Constant.LeftTime = leftTime;
        stopTimer();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        leftTime = 0;
        stopTimer();
        AddPoint(String.valueOf(point), type, false);
        if (Session.getSoundEnableDisable(activity)) {
            int resourceId = R.raw.wrong;
            MediaPlayer mediaplayer = MediaPlayer.create(activity, resourceId);
            mediaplayer.setOnCompletionListener(mp -> {

                mp.reset();
                mp.release();
            });
            int resourceIds = R.raw.right;
            MediaPlayer mediaplayers = MediaPlayer.create(activity, resourceIds);
            mediaplayers.setOnCompletionListener(mp -> {

                mp.reset();
                mp.release();
            });
        }
        blankAllValue();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.report).setVisible(false);
        menu.findItem(R.id.bookmark).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.setting) {
            SettingButtonMethod();
            return true;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {

        PlayAreaLeaveDialog();

    }
}