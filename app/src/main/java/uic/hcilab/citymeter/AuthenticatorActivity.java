package uic.hcilab.citymeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;

import uic.hcilab.citymeter.Helpers.LogInHelper;

public class AuthenticatorActivity extends AppCompatActivity {
    LogInHelper logInHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        logInHelper = new LogInHelper(this);
        Toolbar loginToolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(loginToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("City Meter");
        }
        Button loginButton = (Button) findViewById(R.id.loginSubmitBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                AuthenticatorActivity.this.finish();
            }
        });

        Button cancelLogin = (Button) findViewById(R.id.cancelLoginBtn);
        cancelLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticatorActivity.this.finish();
            }
        });

        Button signUp = (Button)findViewById(R.id.signupButtonLogin);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                AuthenticatorActivity.this.finish();
            }
        });
    }
}

