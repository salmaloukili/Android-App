package be.kuleuven.testgeography.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Arrays;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.MCQuestion;
import be.kuleuven.testgeography.model.Quiz;
import be.kuleuven.testgeography.model.User;

public class QuizMCActivity extends AppCompatActivity {
    private Quiz quiz;
    private User user;
    private MCQuestion currentQuestion;
    private TextView txtQuestion; // TextView that displays the question
    private Button btnOption1, btnOption2, btnOption3, btnOption4; // Buttons with choices for answers
    private ImageView imageView;
    private ArrayList<Button> buttonOptions;
    private TextView txtNrQuestion; // TextView that displays how far into the quiz we are
    private TextView txtScore; // TextView that displays the current score
    private TextView txtBase64;
    private final long DURATION = 1500;// 1.5 seconds
    private final int NR_OF_OPTIONS = 4;
    private final String START_URL = "https://studev.groept.be/api/a21pt212/";
    private int nrOfRegions;

    private ArrayList<String> countries;
    private ArrayList<String> capitals;
    private ArrayList<String> flags;
    private ArrayList<String> abbreviations;

    private String URLEND;
    private String[] regionsList;
    private Bitmap bitmap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        countries = new ArrayList<String>();
        capitals = new ArrayList<String>();
        flags = new ArrayList<String>();
        abbreviations = new ArrayList<String>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_image);
        quiz = getIntent().getExtras().getParcelable("Quiz");
        user = getIntent().getExtras().getParcelable("User");

        TextView txt = findViewById(R.id.txtBase64);
        imageView = findViewById(R.id.imViewFPic);

        quiz.setRandomTypes(NR_OF_OPTIONS); // Set up the question types => the question objects are created
        //fillInArrayLists();
        //quiz.setAnswers(countries, capitals, flags, abbreviations);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtBase64 = findViewById(R.id.txtBase64);
        txtBase64.setText("");

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);

        buttonOptions = new ArrayList<>(Arrays.asList(btnOption1, btnOption2, btnOption3, btnOption4));

        txtNrQuestion = findViewById(R.id.txtNrQuestion);
        txtScore = findViewById(R.id.txtScore);

        //setUpNewQuestion();
        waitForData();
        checkContinentsExecuteRequest();
    }


    public void waitForData(){
        txtQuestion.setText("Loading questions...");
        txtScore.setText("");
        txtNrQuestion.setText("");

        btnOption1.setVisibility(View.GONE);
        btnOption2.setVisibility(View.GONE);
        btnOption3.setVisibility(View.GONE);
        btnOption4.setVisibility(View.GONE);

        String b64 = ",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\n";

        byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
        bitmap2 = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(bitmap2);

    }

    public void setUpNewQuestion(){
        btnOption1.setVisibility(View.VISIBLE);
        btnOption2.setVisibility(View.VISIBLE);
        btnOption3.setVisibility(View.VISIBLE);
        btnOption4.setVisibility(View.VISIBLE);
        // get the current question (MCQuestion) and the options (String[]) of the current question
        currentQuestion = (MCQuestion) quiz.getCurrentQuestion();
        String[] options = currentQuestion.getOptionDescriptions();
        // get the current question number. This is incremented with 1 so that the first question number is not 0 but 1 and so on.
        int nr = quiz.getCurrentNr() + 1;
        // Set the textViews of the activity
        txtQuestion.setText(quiz.getCurrentQuestionText());// Set the question text (e.g.: "Sucre is the capital of...?") in activity layout
        txtScore.setText("Current score : " + quiz.getScore()+"/"+quiz.getNrOfQuestions());// Set the current score in activity layout
        txtNrQuestion.setText("Question: " + nr + " of "+ quiz.getNrOfQuestions());// set the current question number in activity layout (e.g.: "Question: 3 of 5")
        Button btnOption;
        for (int i=0; i < buttonOptions.size(); i++){
            btnOption = buttonOptions.get(i);
            btnOption.setText(options[i]);
            btnOption.setBackgroundColor(Color.BLUE);
        }
        imageConvertAndSet(quiz.getFlagURL().get(quiz.getCurrentNr()));

    }

    public void imageConvertAndSet(String b64) {
        b64 = b64.replace("data:image/png;base64", "");
        byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
        bitmap2 = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageView.setImageBitmap(bitmap2);
    }
    /**
     * @param button = the button that calls this method when pressed. Each of the option buttons calls this method when pressed.
     *               If the correct button is pressed, then the button color will be set to Color.GREEN and the score is incremented
     *               At the end of this method, we go to the next question.
     */
    public void btnOption_onClick(View button){
        Button btnOption = (Button) button;
        String ans = currentQuestion.getDescriptionAnswer();
        boolean correct = btnOption.getText().toString() == ans;
        // If correct is true, that means the pressed button is the correct answer.
        if (correct){
            // The pressed button is set to green and the score is incremented by calling the method incrementScore()
            button.setBackgroundColor(Color.GREEN);
            incrementScore();// This is done in a separate method in case we want to do something else when the score needs to be incremented
        } else{
            // The pressed button is set to red since the pressed button is not the correct answer
            button.setBackgroundColor(Color.RED);
            int index = 0;// This integer will be used to loop "through" the Arraylist buttonOptions.
            boolean lookingForAns = true;// this variable will be set to false once we have found the button with the correct answer
            // Starting from index 0, we check if the Button selected from buttonOptions is the correct answer
            while (lookingForAns){
                // If true, the button on this index is the correct answer
                if (buttonOptions.get(index).getText().toString() == ans){
                    // The button that has the correct answer is set to green and the while loop is stopped since we found the correct answer
                    buttonOptions.get(index).setBackgroundColor(Color.GREEN);
                    lookingForAns = false;// stop the while loop
                }
                index++;
            }
        }
        // The Handler object delays the actions that are described in the method run() with DURATION number of milliseconds
        // Here DURATION = 1500, so the actions are delayed with 1.5 seconds
        // What this means is that after a button is pressed, the app displays for 1.5 seconds if the correct button was pushed...
        // ... and if not, what then was the correct button.
        // After that 1.5 seconds, the question number is updated.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // If the question number + 1 equals the total number of questions in the quiz, then the quiz is done
                if (quiz.getCurrentNr() + 1 == quiz.getNrOfQuestions()){
                    endQuiz();// call method endQuiz()
                } else{
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
                    if (nrOfRegions == 1){
                        quiz.setAnswers(countries, capitals, flags, abbreviations);
                        setUpNewQuestion();
                    }

                    //quiz.setAnswers(countries, capitals, flags, abbreviations);
                    //setUpNewQuestion();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QuizMCActivity.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
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
                "Bosnia and Herzegovina", "Botswana", "Cyprus", "Somalia", "South Africa",
                "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland",
                "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania",
                "Thailand", "Togo", "Tonga", "Trinidad And Tobago", "Tunisia", "Turkey",
                "Turkmenistan", "Tuvalu", "Uganda", "Ukraine"));
        capitals = new ArrayList<>(Arrays.asList("Kabul", "Tirana", "Algiers", "Andorra la Vella",
                "Luanda", "St. John's", "Buenos Aires", "Yerevan", "Oranjestad", "Canberra", "Vienna", "Baku",
                "Nassau", "Manama", "Dhaka", "Porto-Novo", "Thimphu", "Sucre",
                "Sarajevo", "Gaborone", "capital of Cyprus", "Mogadishu", "Pretoria",
                "Juba", "Madrid", "Colombo", "Khartoum", "Paramaribo", "Mbabane",
                "Stockholm", "Bern", "Damascus", "Taipei", "Dushanbe", "Dodoma",
                "Bangkok", "Lome", "Nuku'alofa", "Port of Spain", "Tunis", "Ankara",
                "Ashgabat", "Funafuti", "Kampala", "Kiev"));
        abbreviations = new ArrayList<>(Arrays.asList("AFG", "ALB", "DZA", "AND", "AGO",
                "ATG", "ARG", "ARM", "ABW", "AUS", "AUT", "AZE",
                "BHS", "BHR", "BGD", "BEN", "BTN", "BOL",
                "BIH", "BWA", "CYR", "SOM", "ZAF",
                "SSD", "ESP", "LKA", "SDN", "SUR", "SWZ",
                "SWE", "CHE", "SYR", "TWN", "TJK", "TZA",
                "THA", "TGO", "TON", "TTO", "TUN", "TUR",
                "TKM", "TUV", "UGA", "UKR"));
        flags = new ArrayList<>();
    }*/
}