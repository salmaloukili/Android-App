package be.kuleuven.testgeography.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Random;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.User;

public class LoginRegisterActivity extends AppCompatActivity {
    EditText email, password;
    User user;
    private final String EMAIL = "lotte.decock@student.kuleuven.be";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        email = findViewById(R.id.editTxtEmail);
        password = findViewById(R.id.editTxtPassword);
    }

    public void btnLogin_onClick(View button){
        // TODO: Check if the username and password are valid and are in our database

        // While we are not connected with the database, we test out the UI with the correct username = Jane Doe
        // And the correct password = password
        // Later on we should remove this
        user = new User("", email.getText().toString(), password.getText().toString(), 0, 0);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = user.getMyPasswordURL();
        JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject o = null;
                try {
                    o = response.getJSONObject(0);
                    String fetched_password = o.getString("password");
                    String entered_password = password.getText().toString();
                    if (fetched_password == "" || fetched_password == null) {
                        // The email is not present in the table user in the database
                        Toast.makeText(LoginRegisterActivity.this, "Email is not registered. Are you already registered?" + email.getText().toString(), Toast.LENGTH_LONG).show();
                    } else if (fetched_password.contentEquals(entered_password)) {
                        // The username is valid and the correct password was entered => Go to MainActivity.
                        user.setUsername(o.getString("username"));
                        user.setTotalScore(o.getInt("totalScore"));
                        user.setTotalNrQuestions(o.getInt("nrQuestions"));
                        goToMain();
                    } else {
                        // If the username is valid, that means the password is wrong
                        Toast.makeText(LoginRegisterActivity.this, "Password incorrect. Please try again", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginRegisterActivity.this, "Email not registered. Are you sure you already registered?" + email.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginRegisterActivity.this,"Unable to communicate with the server",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(queueRequest);
    }

    public void btnRegister_onClick(View button){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void btnStartWithoutLog_onClick(View button){
        user = new User(getRandomName(), EMAIL, "password");
        goToMain();
    }

    public void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("User",user);
        startActivity(intent);
        // finish this activity so we cannot go back to this activity
        finish();
    }

    /**
     * This method is honestly just for my entertainment ~ Lotte
     */
    public String getRandomName(){
        ArrayList<String> randomNames = new ArrayList<>(Arrays.asList("Sweetie", "Dude", "Bob", "Very smart person", "quiz wiz"));
        Random rand = new Random();
        int randomIndex = rand.nextInt(randomNames.size());
        return randomNames.get(randomIndex);
    }

}