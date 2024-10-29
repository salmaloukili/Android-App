package be.kuleuven.testgeography.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.Quiz;
import be.kuleuven.testgeography.model.User;

public class FinishQuizActivity extends AppCompatActivity {
    private int score;
    private int nrOfQuestions;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_quiz);

        score = getIntent().getExtras().getInt("Score");
        nrOfQuestions = getIntent().getExtras().getInt("nrOfQuestions");
        user = getIntent().getExtras().getParcelable("User");
        user.updateAtEndOfQuiz(score, nrOfQuestions);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, user.getSetUserScoresURL(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // do nothing
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FinishQuizActivity.this,"Unable to communicate with server to update the user statistics. Sorry",
                        Toast.LENGTH_LONG).show();
            }
        });

        TextView txtResult = (TextView) findViewById(R.id.txtResult);
        TextView txtMsg = (TextView) findViewById(R.id.txtMessage);
        ImageView partyPopper = (ImageView) findViewById(R.id.imageStart);
        ImageView applause = (ImageView) findViewById(R.id.imageApplause);
        ImageView rainbow = (ImageView) findViewById(R.id.imageRainbow);

        partyPopper.setVisibility(View.INVISIBLE);
        applause.setVisibility(View.INVISIBLE);
        rainbow.setVisibility(View.INVISIBLE);

        String str = "";

        if (score == nrOfQuestions){
            str = "Congratulations! You nailed all the questions";
            partyPopper.setVisibility(View.VISIBLE);
            rainbow.setVisibility(View.VISIBLE);
        }
        else if (score >= nrOfQuestions*1.0/2){
            str = "That was good! You passed";
            applause.setVisibility(View.VISIBLE);

        } else{
            str = "We all have to start somewhere.\n Next time it will go better ;-)";
        }
        txtResult.setText("You scored " + score + " / " + nrOfQuestions);
        txtMsg.setText(str);
    }

    public void btnStartQuiz_onClick(View button){
        Intent intent = new Intent(this, PopTypeActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }

    public void btnGoToMain_onClick(View button){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }
}