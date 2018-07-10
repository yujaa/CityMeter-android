package uic.hcilab.citymeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import uic.hcilab.citymeter.Helpers.LogInHelper;

public class SignupActivity extends AppCompatActivity {
    LogInHelper logInHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        logInHelper = new LogInHelper(this);
        Toolbar loginToolbar = (Toolbar) findViewById(R.id.signupToolbar);
        setSupportActionBar(loginToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sign Up");
        }
    }
}
