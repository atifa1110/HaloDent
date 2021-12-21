package com.dentist.halodent.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.Model.Question;
import com.dentist.halodent.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainQuizActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView number,question;
    private Button btn_next, btn_previous;
    private Button btn_a,btn_b,btn_c,btn_d;
    private LinearProgressIndicator progressBar;
    private ImageButton btn_back;

    private DatabaseReference databaseUsers;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    private int mScore=0;
    private int mQuestionNum=0;
    private int umur=0;
    private Question mQuestionLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // when this activity is about to be launch we need to check if its opened before or not
//        if (Preference.getKeyQuizOpen(getApplicationContext())) {
//            Intent mainActivity = new Intent(getApplicationContext(), ScoreActivity.class);
//            startActivity(mainActivity);
//            finish();
//        }

        Preference.removeQuizData(getApplicationContext());
        umur = Preference.getKeyUserAge(getApplicationContext());
        Log.d("umur",String.valueOf(umur));
        mQuestionLibrary = new Question(umur);

        setContentView(R.layout.activity_main_quiz);

        number = findViewById(R.id.number);
        question = findViewById(R.id.question);
        progressBar = findViewById(R.id.progress);
        btn_back = findViewById(R.id.imageButton);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);

        btn_a = findViewById(R.id.btn1);
        btn_b = findViewById(R.id.btn2);
        btn_c = findViewById(R.id.btn3);
        btn_d = findViewById(R.id.btn4);

        btn_a.setOnClickListener(this);
        btn_b.setOnClickListener(this);
        btn_c.setOnClickListener(this);
        btn_d.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_previous.setOnClickListener(this);

        //show question
        updateAddQuestion();
        //update score
        updateScore(mScore);
    }

    @Override
    public void onClick(View v) {
        int score=0;
        switch (v.getId()){
            case R.id.imageButton:
                onBackPressed();
                break;
            case R.id.btn1:
                score = 1;
                if(mQuestionNum == 4){
                    mQuestionNum++;
                }
                break;
            case R.id.btn2:
                score = 1;
                break;
            case R.id.btn3:
                score = 2;
                break;
            case R.id.btn4:
                score = 3;
                break;
            case R.id.btn_next:
                break;
            case R.id.btn_previous:
                break;
            default:
                break;
        }
        //update current score you have
        updateScore(score);

        //update to the next question
        updateAddQuestion();
    }

    private void updateScore(int score){
        mScore += score;
        Log.d("Your_Score",String.valueOf(mScore));
    }

    private void updateAddQuestion(){
        //check if we are not outside the array bounds
        if(mQuestionNum<mQuestionLibrary.getLength()){
            //set the text for new question and answer
            question.setText(mQuestionLibrary.getQuestion(mQuestionNum));

            if(mQuestionLibrary.getChoice(mQuestionNum,2).equals("null")){
                btn_b.setVisibility(View.GONE);
            }else{
                btn_b.setVisibility(View.VISIBLE);
            }

            if(mQuestionLibrary.getChoice(mQuestionNum,4).equals("null")){
                btn_d.setVisibility(View.GONE);
            }else{
                btn_d.setVisibility(View.VISIBLE);
            }

            btn_a.setText(mQuestionLibrary.getChoice(mQuestionNum,1));
            btn_b.setText(mQuestionLibrary.getChoice(mQuestionNum,2));
            btn_c.setText(mQuestionLibrary.getChoice(mQuestionNum,3));
            btn_d.setText(mQuestionLibrary.getChoice(mQuestionNum,4));
            mQuestionNum++;
            number.setText("Question "+mQuestionNum+"/"+mQuestionLibrary.getLength());
            progressBar.setProgress(mQuestionNum);

        }else{
            //Toast.makeText(MainActivity.this,"It was the last question!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainQuizActivity.this,ScoreActivity.class);
            Log.d("Total_Score",String.valueOf(mScore));
            startActivity(intent);
            //Preference.setKeyQuizOpen(getApplicationContext(),true);
            Preference.setKeyQuizScore(getApplicationContext(),mScore);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}