package be.kuleuven.testgeography.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.Quiz;
import be.kuleuven.testgeography.model.User;

public class PopRegionActivity extends AppCompatActivity {
    private Quiz quiz;
    private User user;
    private CheckBox cbAllRegions;
    private CheckBox cbRegion1, cbRegion2, cbRegion3, cbRegion4, cbRegion5;
    private ArrayList<CheckBox> cbRegions;
    private boolean MCQuestion;

    private int nrCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_region);
        quiz = (Quiz) getIntent().getExtras().getParcelable("Quiz");
        user = getIntent().getExtras().getParcelable("User");
        MCQuestion = getIntent().getExtras().getBoolean("MCQuestion");

        cbAllRegions = findViewById(R.id.cbAllRegions);
        cbRegion1 = findViewById(R.id.cbRegion1);
        cbRegion2 = findViewById(R.id.cbRegion2);
        cbRegion3 = findViewById(R.id.cbRegion3);
        cbRegion4 = findViewById(R.id.cbRegion4);
        cbRegion5 = findViewById(R.id.cbRegion5);
        cbRegions = new ArrayList<>(Arrays.asList(cbRegion1, cbRegion2, cbRegion3, cbRegion4, cbRegion5));

        cbAllRegions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cbAllRegions.isChecked()){
                    for (CheckBox cb : cbRegions){
                        cb.setChecked(true);
                    }
                } else{
                    for (CheckBox cb : cbRegions){
                        cb.setChecked(false);
                    }
                }
            }
        });
    }

    public void btnDone_onClick(View button){
        //Quiz newQuiz = new Quiz();// in later versions this will need to be given from the start input

        /** Retrieve the countries names that are selected based on their region
         *
         */
        String allRegions = cbAllRegions.getText().toString();
        if (!cbRegion1.isChecked() && !cbRegion2.isChecked() && !cbRegion3.isChecked() && !cbRegion4.isChecked() && !cbRegion5.isChecked()){
            // default: add all regions if nothing is selected
            for (CheckBox cb : cbRegions){
                quiz.addRegion(cb.getText().toString());
            }
        } else{
            for (CheckBox cb : cbRegions){
                if (cb.isChecked()){
                    quiz.addRegion(cb.getText().toString());
                }
            }
        }
        setNrOfQuestionsInQuiz();

        // Now we get into the real fun part ;-)
        // Set up the questions
        Intent intent;
        if (MCQuestion){
            intent = new Intent(this, QuizMCActivity.class);
        } else {
            intent = new Intent(this, QuizOpenActivity.class);
        }
        intent.putExtra("User", user);
        intent.putExtra("Quiz", quiz);
        startActivity(intent);
    }

    public void setNrOfQuestionsInQuiz(){

        Spinner spNrQuestions = findViewById(R.id.spNrQuestions);
        String selectedNr = (String) spNrQuestions.getSelectedItem();
        int nrQuestions;

            nrQuestions = Integer.parseInt(selectedNr);

        quiz.setNrOfQuestions(nrQuestions);
    }

}