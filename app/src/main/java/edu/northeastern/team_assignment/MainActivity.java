package edu.northeastern.team_assignment;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        int theId = view.getId();
        if (theId == R.id.atYourWebService) {
            Intent intent = new Intent(this, WebService.class);
            startActivity(intent);
        }
        if (theId == R.id.sendSticker) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (theId == R.id.aboutMe) {
            snakeInfo();
        }
    }

    private void snakeInfo() {
        String myInfo = "We are group 55 consisting of Nikhil Sapa, Parker Hentz, Karan Satwani.";
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.parentLayout), myInfo, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
