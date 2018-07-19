package uic.hcilab.citymeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChooseMfaContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import java.util.List;
import java.util.Locale;
import uic.hcilab.citymeter.Helpers.LogInHelper;

public class AuthenticatorActivity extends AppCompatActivity {
    LogInHelper logInHelper;
    // Screen fields
    private EditText inUsername;
    private EditText inPassword;

    //Continuations
    private NewPasswordContinuation newPasswordContinuation;
    private ChooseMfaContinuation mfaOptionsContinuation;

    // User Details
    private String username;
    private String password;

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
        inUsername = (EditText) findViewById(R.id.usernameEditLogin);
        inPassword = (EditText) findViewById(R.id.passwordEditLogin);
        Button loginButton = (Button) findViewById(R.id.loginSubmitBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signInUser();
                }
                catch (Exception e){
                    Toast.makeText(AuthenticatorActivity.this, "error in sign in : " + e.toString(), Toast.LENGTH_LONG).show();
                }
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

        LogInHelper.init(getApplicationContext());
        findCurrent();
    }

    private void signInUser() {
        username = inUsername.getText().toString();
        if(username == null || username.length() < 1) {
            Toast.makeText(AuthenticatorActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        LogInHelper.setUser(username);
        password = inPassword.getText().toString();
        if(password == null || password.length() < 1) {
            Toast.makeText(AuthenticatorActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        LogInHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);

    }

    private void findCurrent() {
        CognitoUser user = LogInHelper.getPool().getCurrentUser();
        username = user.getUserId();
        if(username != null) {
            LogInHelper.setUser(username);
            user.getSessionInBackground(authenticationHandler);
        }
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            if (username.contains("@") && username.contains(".")) {

                this.username = username;
            }
            else{
                this.username ="+1" + username;
            }
            LogInHelper.setUser(this.username);
        }
        if(this.password == null) {
            password = inPassword.getText().toString();
            if(password == null) {
                Toast.makeText(AuthenticatorActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            if(password.length() < 1) {
                Toast.makeText(AuthenticatorActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();

    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d("logIn", " -- Auth Success");
            LogInHelper.setCurrSession(cognitoUserSession);
            LogInHelper.newDevice(device);
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            AuthenticatorActivity.this.finish();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {

        }

        @Override
        public void onFailure(Exception e) {
//            label.setText("Sign-in failed");
            Toast.makeText(AuthenticatorActivity.this, "Failed : " + e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("logIn", " Authentication failed " + e.toString());
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
                // This is the first sign-in attempt for an admin created user
                newPasswordContinuation = (NewPasswordContinuation) continuation;
                LogInHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
                        newPasswordContinuation.getRequiredAttributes());
                Toast.makeText(AuthenticatorActivity.this, "New password required", Toast.LENGTH_SHORT).show();
            } else if ("SELECT_MFA_TYPE".equals(continuation.getChallengeName())) {

                mfaOptionsContinuation = (ChooseMfaContinuation) continuation;
                List<String> mfaOptions = mfaOptionsContinuation.getMfaOptions();
            }
        }
    };




}

