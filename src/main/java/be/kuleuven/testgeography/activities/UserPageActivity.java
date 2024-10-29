package be.kuleuven.testgeography.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import be.kuleuven.testgeography.R;
import be.kuleuven.testgeography.model.User;

public class UserPageActivity extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        user = getIntent().getExtras().getParcelable("User");
        TextView txtName = findViewById(R.id.txtNameProfile);
        TextView txtNrOfQuizzesPlayed = findViewById(R.id.txtNrQuizzesPlayed);
        TextView txtAvgScore = findViewById(R.id.txtAvgScore);
        txtName.setText("Username: " + user.getUsername());
        txtNrOfQuizzesPlayed.setText("You have played "+ user.getTotalNrQuestions() + " questions.");
        txtAvgScore.setText("Your average score = " + user.getAverageScore() + " %");
    }
    @SuppressLint("SetTextI18n")
    public void btnGoToMain_onClick(View button){
        TextView txtName = findViewById(R.id.txtNameProfile);
        //txtInfo.setText(info)
        this.finish();
    }

    // TODO: change password & username doesn't work
    public void btnChangeUsername_onClick(View button){
        Intent intent = new Intent(this, ChangeUsernameActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }
}
