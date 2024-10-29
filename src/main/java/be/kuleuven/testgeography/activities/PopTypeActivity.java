package be.kuleuven.testgeography.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Arrays;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.Quiz;
import be.kuleuven.testgeography.model.User;

public class PopTypeActivity extends Activity{
    private User user;
    private Quiz quiz;
    private CheckBox cbAllTypes;
    private ArrayList<CheckBox> cbTypes;
    private CheckBox cbType2, cbType3, cbType4, cbType5, cbType6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_choice_type);

        user = getIntent().getExtras().getParcelable("User");
        quiz = new Quiz();

        cbAllTypes = findViewById(R.id.checkBoxAll);
        //cbType1 = findViewById(R.id.checkBox1);
        cbType2 = findViewById(R.id.checkBox2);
        cbType3 = findViewById(R.id.checkBox3);
        cbType4 = findViewById(R.id.checkBox4);
        cbType5 = findViewById(R.id.checkBox5);
        cbType6 = findViewById(R.id.checkBox6);
        cbTypes = new ArrayList<>(Arrays.asList( cbType2, cbType3, cbType4, cbType5, cbType6));

        cbAllTypes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(cbAllTypes.isChecked()){
                    for (CheckBox cb : cbTypes){
                        cb.setChecked(true);
                    }
                } else{
                    for (CheckBox cb : cbTypes){
                        cb.setChecked(false);
                    }
                }
            }
        });
    }

    public void btnDone_onClick(View button){
        Quiz newQuiz = new Quiz();// in later versions this will need to be given from the start input
        //String allTypes = cbType1.getText().toString();
        if (!cbType2.isChecked() && !cbType3.isChecked() && !cbType4.isChecked() && !cbType5.isChecked() && !cbType6.isChecked()){
            // Default is that we add all regions
            for (CheckBox cb : cbTypes){
                quiz.addType(cb.getText().toString());
            }
        } else{
            for (CheckBox cb : cbTypes) {
                if (cb.isChecked()) {
                    quiz.addType(cb.getText().toString());
                }
            }
        }
        Intent intent = new Intent(this, PopRegionActivity.class);
        intent.putExtra("User", user);
        intent.putExtra("Quiz",quiz);
        RadioButton MCQuestion = findViewById(R.id.MCQuestion);
        intent.putExtra("MCQuestion", MCQuestion.isChecked());
        startActivity(intent);
    }

    public void btnToMain_onClick(View button){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("User",user);
        startActivity(intent);
    }
}
