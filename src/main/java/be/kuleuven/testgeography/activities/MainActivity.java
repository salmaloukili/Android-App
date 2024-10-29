package be.kuleuven.testgeography.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import be.kuleuven.testgeography.model.Quiz;
import be.kuleuven.testgeography.model.User;

public class MainActivity extends AppCompatActivity {
    private String str;
    private User user;
    private static final String TRY_URL = "https://studev.groept.be/api/a21pt212/getCountriesAfrica";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = getIntent().getExtras().getParcelable("User");
    }

    @SuppressLint("SetTextI18n")
    public void btnUserPage_onClick(View button){
        Intent intent = new Intent(this, UserPageActivity.class);
        intent.putExtra("User",user);
        startActivity(intent);
    }

    public void btnChooseQuiz_onClick(View button){
        Intent intent = new Intent(this, PopTypeActivity.class);
        Quiz quiz = new Quiz();
        intent.putExtra("Quiz", quiz);
        intent.putExtra("User",user);
        startActivity(intent);
    }

    public void btnLogout_onClick(View button){
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
    }

}
