package be.kuleuven.testgeography.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.User;

public class ChangeUsernameActivity extends AppCompatActivity {
    private User user;
    private String newUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);
        user = getIntent().getExtras().getParcelable("User");

    }

    protected void btnGoToUserProfile_onClick(View button){

    }
}