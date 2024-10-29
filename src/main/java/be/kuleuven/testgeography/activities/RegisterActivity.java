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

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.User;

public class RegisterActivity extends AppCompatActivity {
    private String newUsername, newEmail, newPassword, newPassword2;
    private User user;
    private final String ADD_USER_URL = "https://studev.groept.be/api/a21pt212/addUser/";
    //private EditText txtNewUsername, txtNewEmail, txtNewPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btnDone = findViewById(R.id.btnDoneRegister);
    }

    public void btnRegister_onClick(View button){
        EditText txtNewUsername = findViewById(R.id.txtNewUsername);
        EditText txtNewEmail = findViewById(R.id.txtNewEmail);
        EditText txtNewPassword = findViewById(R.id.txtNewPassword);
        EditText txtNewPassword2 = findViewById(R.id.txtNewPassword2);

        newUsername = txtNewUsername.getText().toString();
        newEmail = txtNewEmail.getText().toString();
        newPassword = txtNewPassword.getText().toString();
        newPassword2 = txtNewPassword2.getText().toString();
        RequestQueue requestQueue;
        JsonArrayRequest queueRequest;
        if (!newPassword.contentEquals(newPassword2)){
            Toast.makeText(RegisterActivity.this,"Passwords don't match", Toast.LENGTH_SHORT).show();
        } else{
            user = new User(newUsername, newEmail, newPassword); // create User object
            // Check if the given email has already been used, if so another email must be used to register a new account
            String url = user.getCheckEmailExistsURL();
            requestQueue = Volley.newRequestQueue(this);
            queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(0);// should be null if response is empty

                        int nrOfUsers = o.getInt("nrOfUsers");
                        if (nrOfUsers == 0) {
                            // no user with this email was found so this email is not yet in use, so we can add a new user on this email.
                            Toast.makeText(RegisterActivity.this, "User is added to database", Toast.LENGTH_LONG).show();
                            addUser();
                        } else{
                            // this email is already in use if a password was returned.
                            Toast.makeText(RegisterActivity.this, "The given email address is already in use.\n Please user another email", Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error){
                    Toast.makeText(RegisterActivity.this,"Unable to communicate with the server",Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(queueRequest);
        }
    }

    public void addUser(){
        String url = user.getAddUserURL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest;
        queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                goToMain();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this,"Unable to communicate with the server", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(queueRequest);
    }

    public void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }
}