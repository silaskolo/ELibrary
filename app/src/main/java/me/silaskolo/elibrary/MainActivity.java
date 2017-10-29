package me.silaskolo.elibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, DashboardActivity.class));
            return;
        }
    }

    public void onClickOpenLoginActivity(View v) {
        Intent loginActivityIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginActivityIntent);
    }

    public void onClickOpenSignUpActivity(View v) {
        Intent signUpActivityIntent = new Intent(MainActivity.this,SignupActivity.class);
        startActivity(signUpActivityIntent);
    }
}
