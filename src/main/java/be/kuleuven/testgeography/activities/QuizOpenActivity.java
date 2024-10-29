package be.kuleuven.testgeography.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.Question;
import be.kuleuven.testgeography.model.Quiz;
import be.kuleuven.testgeography.model.User;

public class QuizOpenActivity extends AppCompatActivity {
    private Quiz quiz;
    private User user;
    private Question currentQuestion;
    private TextView txtQuestion; // TextView that displays the question
    private ImageView imageView;
    private EditText txtAnswer;
    private TextView txtNrQuestion; // TextView that displays how far into the quiz we are
    private TextView txtScore; // TextView that displays the current score
    private Button btnHint;
    private Button nextQ;
    private Button skip;
    private int tries;
    private final long DURATION = 1500;// 1.5 seconds

    private ArrayList<String> countries;
    private ArrayList<String> capitals;
    private ArrayList<String> flags;
    private ArrayList<String> abbreviations;

    private String URLEND;
    private String[] regionsList;
    private Bitmap bitmap2;
    private int nrOfRegions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        countries = new ArrayList<String>();
        capitals = new ArrayList<String>();
        flags = new ArrayList<String>();
        abbreviations = new ArrayList<String>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_open);
        quiz = getIntent().getExtras().getParcelable("Quiz");
        user = getIntent().getExtras().getParcelable("User");

        quiz.setRandomTypes(1);
        //fillInArrayLists();
        //quiz.setAnswers(countries, capitals, flags, abbreviations);

        txtNrQuestion = findViewById(R.id.txtNrQuestion2);
        txtQuestion = findViewById(R.id.txtQuestion2);
        txtScore = findViewById(R.id.txtScore2);
        txtAnswer = findViewById(R.id.txtAnswer);
        btnHint = findViewById(R.id.btnShowHint);
        nextQ = findViewById(R.id.nextQuestion);
        skip = findViewById(R.id.btnSkipQuestion);
        imageView = findViewById(R.id.imageView2);


        waitForData();
        checkContinentsExecuteRequest();
    }


    public void waitForData(){
        txtQuestion.setText("Loading questions...");
        txtScore.setText("");
        txtNrQuestion.setText("");
        txtAnswer.setText("");
        txtAnswer.setHint(null);
        txtAnswer.setTextColor(Color.WHITE);
        btnHint.setText("");

        skip.setVisibility(View.GONE);
        btnHint.setVisibility(View.GONE);
        nextQ.setVisibility(View.GONE);

        String b64 = ",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\n";
        byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
        bitmap2 = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(bitmap2);

    }
    @SuppressLint("ResourceAsColor")
    public void setUpNewQuestion() {
        // get the current question (MCQuestion) and the options (String[]) of the current question
        currentQuestion = quiz.getCurrentQuestion();
        // get the current question number. This is incremented with 1 so that the first question number is not 0 but 1 and so on.
        int nr = quiz.getCurrentNr() + 1;
        tries = 2;
        // Set the textViews of the activity
        txtQuestion.setText(quiz.getCurrentQuestionText()); // Set the question text (e.g.: "Sucre is the capital of...?") in activity layout
        txtScore.setText("Current score : " + quiz.getScore() + "/" + quiz.getNrOfQuestions()); // Set the current score in activity layout
        txtNrQuestion.setText("Question: " + nr + " of " + quiz.getNrOfQuestions()); // set the current question number in activity layout (e.g.: "Question: 3 of 5")
        txtAnswer.setText("");
        txtAnswer.setHint(null);
        txtAnswer.setTextColor(Color.BLACK);

        btnHint.setVisibility(View.VISIBLE);
        nextQ.setVisibility(View.VISIBLE);
        skip.setVisibility(View.VISIBLE);

        btnHint.setText("Show first letter \nof the answer");

        imageConvertAndSet(quiz.getFlagURL().get(quiz.getCurrentNr()));
    }

    public void imageConvertAndSet(String b64) {
        b64 = b64.replace("data:image/png;base64", "");
        byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
        bitmap2 = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(bitmap2);
    }

    public void btnNextQuestion_onClick(View button) {
        String answer = txtAnswer.getText().toString().toLowerCase();
        String correctAnswer = currentQuestion.getDescriptionAnswer().toLowerCase();
        boolean nextQuestion = false;
        boolean correct = answer.contentEquals(correctAnswer);
        if (correct) {
            incrementScore();
            txtAnswer.setTextColor(Color.GREEN);
            txtAnswer.setText("Correct! " + currentQuestion.getDescriptionAnswer());
            nextQuestion = true;
        } else {
            if (tries == 0) {
                nextQuestion = true;
                txtAnswer.setTextColor(Color.RED);
                txtAnswer.setText("Right answer: " + currentQuestion.getDescriptionAnswer());
            } else{
                txtAnswer.setText("");
                if(tries==1){
                    txtAnswer.setHint("Wrong. "+ tries + " try left");
                }else{
                    txtAnswer.setHint("Wrong. "+ tries + " tries left");}
            }
            tries--;
        }
        // The Handler object delays the actions that are described in the method run() with DURATION number of milliseconds
        // Here DURATION = 1500, so the actions are delayed with 1.5 seconds
        // What this means is that after a button is pressed, the app displays for 1.5 seconds if the correct button was pushed...
        // ... and if not, what then was the correct button.
        // After that 1.5 seconds, the question number is updated.
        if (nextQuestion) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // If the question number + 1 equals the total number of questions in the quiz, then the quiz is done
                    if (quiz.getCurrentNr() + 1 == quiz.getNrOfQuestions()) {
                        endQuiz();// call method endQuiz()
                    } else {
                        // Quiz object is updated to go to the next question
                        quiz.nextQuestion();
                        // The activity is updated by setting up the buttons, textview, imageview for the next question
                        setUpNewQuestion();
                    }
                }
            }, DURATION);
        }
    }

    public void btnHint_onClick(View view){
        String correctAns = currentQuestion.getDescriptionAnswer();
        char firstLetter = correctAns.charAt(0);
        btnHint.setText("Answer starts with: " + firstLetter);
    }

// TODO: button skip question doesn't work
    public void btnSkipQuestion_onClick(View view){
        txtAnswer.setTextColor(Color.RED);
        txtAnswer.setText("Right answer: " + currentQuestion.getDescriptionAnswer());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // If the question number + 1 equals the total number of questions in the quiz, then the quiz is done
                if (quiz.getCurrentNr() + 1 == quiz.getNrOfQuestions()) {
                    endQuiz();// call method endQuiz()
                } else {
                    // Quiz object is updated to go to the next question
                    quiz.nextQuestion();
                    // The activity is updated by setting up the buttons, textview, imageview for the next question
                    setUpNewQuestion();
                }
            }
        }, DURATION);
    }

    public void incrementScore(){
        quiz.incrementScore();
        txtScore.setText("Current score : " + quiz.getScore()+"/"+quiz.getNrOfQuestions());
    }

    public void endQuiz(){
        Intent intent = new Intent(this, FinishQuizActivity.class);
        int score = quiz.getScore();
        int nrOfQuestion = quiz.getNrOfQuestions();
        user.updateAtEndOfQuiz(score, nrOfQuestion);
        user.updateAverageScore();
        intent.putExtra("User", user);
        intent.putExtra("Score", score);
        intent.putExtra("nrOfQuestions",nrOfQuestion);
        startActivity(intent);

        // Finish this activity so that the user cannot go back to this activity with the return button
        finish();
    }

    public void checkContinentsExecuteRequest() {
        regionsList = quiz.getQuizRegionsArray();
        nrOfRegions = quiz.getQuizRegionsArray().length;

        if (checkIfRegionIsPresent("Africa") && checkIfRegionIsPresent("Oceania") && checkIfRegionIsPresent("Asia") && checkIfRegionIsPresent("Europe") && checkIfRegionIsPresent("Americas")) {
            URLEND = "getAllCountries";
            executeVolleyRequest();
        } else {
            if (checkIfRegionIsPresent("Africa")) {
                URLEND = "getCountriesAfrica";
                executeVolleyRequest();
            }
            if (checkIfRegionIsPresent("Asia")) {
                URLEND = "getCountriesAsia";
                executeVolleyRequest();
            }
            if (checkIfRegionIsPresent("Europe")) {
                URLEND = "getCountriesEurope";
                executeVolleyRequest();
            }
            if (checkIfRegionIsPresent("Americas")) {
                URLEND = "getCountriesAmericas";
                executeVolleyRequest();
            }
            if (checkIfRegionIsPresent("Oceania")) {
                URLEND = "getCountriesOceania";
                executeVolleyRequest();
            }
        }
    }
    public void executeVolleyRequest () {
        String TRY_URL = "https://studev.groept.be/api/a21pt212/" + URLEND;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, TRY_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject o = null;
                try {
                    for (int i = 0; i < response.length(); i++) {
                        o = response.getJSONObject(i);// get a selected record
                        countries.add(o.getString("UPPER(name)"));
                        capitals.add(o.getString("capital"));
                        flags.add(o.getString("flagImage"));
                        abbreviations.add(o.getString("iso3"));
                    }
                    nrOfRegions--;
                    if (nrOfRegions <= 1){
                        quiz.setAnswers(countries, capitals, flags, abbreviations);
                        setUpNewQuestion();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QuizOpenActivity.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(queueRequest);
    }

    public boolean checkIfRegionIsPresent (String continent){
        for (int i = 0; i < regionsList.length; i++) {
            if (regionsList[i].equals(continent)) {
                return true;
            }
        }
        return false;
    }
    /*public void fillInArrayLists(){
        countries = new ArrayList<>(Arrays.asList("Afghanistan","Albania","Algeria","Andorra","Angola",
                "Antigua And Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
                "The Bahamas", "Bahrain", "Bangladesh", "Benin", "Bhutan", "Bolivia",
                "Bosnia and Herzegovina", "Botswana", "Somalia", "South Africa",
                "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland",
                "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania",
                "Thailand", "Togo", "Tonga", "Trinidad And Tobago", "Tunisia", "Turkey",
                "Turkmenistan", "Tuvalu", "Uganda", "Ukraine"));
        capitals = new ArrayList<>(Arrays.asList("Kabul", "Tirana", "Algiers", "Andorra la Vella",
                "Luanda", "St. John's", "Buenos Aires", "Yerevan", "Oranjestad", "Canberra", "Vienna", "Baku",
                "Nassau", "Manama", "Dhaka", "Porto-Novo", "Thimphu", "Sucre",
                "Sarajevo", "Gaborone", "Mogadishu", "Pretoria",
                "Juba", "Madrid", "Colombo", "Khartoum", "Paramaribo", "Mbabane",
                "Stockholm", "Bern", "Damascus", "Taipei", "Dushanbe", "Dodoma",
                "Bangkok", "Lome", "Nuku'alofa", "Port of Spain", "Tunis", "Ankara",
                "Ashgabat", "Funafuti", "Kampala", "Kiev"));
        abbreviations = new ArrayList<>(Arrays.asList("AFG", "ALB", "DZA", "AND", "AGO",
                "ATG", "ARG", "ARM", "ABW", "AUS", "AUT", "AZE",
                "BHS", "BHR", "BGD", "BEN", "BTN", "BOL",
                "BIH", "BWA", "SOM", "ZAF",
                "SSD", "ESP", "LKA", "SDN", "SUR", "SWZ",
                "SWE", "CHE", "SYR", "TWN", "TJK", "TZA",
                "THA", "TGO", "TON", "TTO", "TUN", "TUR",
                "TKM", "TUV", "UGA", "UKR"));
        flags = new ArrayList<>();
    }*/
}