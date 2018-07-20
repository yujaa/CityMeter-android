package uic.hcilab.citymeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.IdentityChangedListener;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChooseMfaContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;
import com.amazonaws.services.cognitoidentityprovider.model.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import uic.hcilab.citymeter.DB.UsersDBHelper;
import uic.hcilab.citymeter.Helpers.LogInHelper;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class SignupActivity extends AppCompatActivity {
    LogInHelper logInHelper;
    String username_, password_, name_, dob_, gender_, ethnicity_, education_;
    double isCoUser_;
    UsersDBHelper usersDBHelper;

    EditText username ;
    EditText password ;
    EditText name ;
    EditText dob ;
    EditText education ;
    RadioGroup genderGroup;
    RadioGroup ethnicityGroup ;
    EditText otherEthnicity ;
    CheckBox isCouser ;
    Button cancel ;
    Button submit ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
       // logInHelper = new LogInHelper(this);
        usersDBHelper = new UsersDBHelper(SignupActivity.this);
        Toolbar loginToolbar = (Toolbar) findViewById(R.id.signupToolbar);
        setSupportActionBar(loginToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sign Up");
        }
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT); // Make to run your application only in portrait mode
        username = (EditText)findViewById(R.id.usernameEditSignup);
        password = (EditText) findViewById(R.id.passwordEditSignup);
        name = (EditText) findViewById(R.id.nameEditSignup);
        dob = (EditText)findViewById(R.id.dobEditSignUp);
        education = (EditText)findViewById(R.id.educationEditSignup);
        genderGroup = (RadioGroup) findViewById(R.id.genderRadioGroupSignup);
        ethnicityGroup = (RadioGroup) findViewById(R.id.ethnicityRadioGroupSignUp);
        otherEthnicity = (EditText) findViewById(R.id.otherEditSignup);
        isCouser = (CheckBox) findViewById(R.id.isCoUserCheckSignup);
        cancel = (Button) findViewById(R.id.cancelSignupBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.this.finish();
            }
        });
        submit = (Button) findViewById(R.id.signupSubmitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CognitoUserAttributes userAttributes = new CognitoUserAttributes();

                username_ = username.getText().toString();
                if (username_ == null || username_.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "username cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                password_ = password.getText().toString();
                if (password_ == null || password_.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                name_ = name.getText().toString();
                education_ = education.getText().toString();
                if(education_ == null || education_.isEmpty()){
                    Toast.makeText(SignupActivity.this, "education cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                int gender_id = genderGroup.getCheckedRadioButtonId();
                if (gender_id == R.id.femaleRadioSignup){
                    gender_ = "female";
                }
                else{
                    gender_ = "male";
                }

                dob_ = dob.getText().toString();

                int ethnicity_id = ethnicityGroup.getCheckedRadioButtonId();
                switch (ethnicity_id){
                    case R.id.asianRadioSignup:
                        ethnicity_ ="asian";
                        break;
                    case R.id.blackRadioSignup:
                        ethnicity_ = "black";
                        break;
                    case R.id.hispanicRadioSignup:
                        ethnicity_ = "hispanic";
                        break;
                    case R.id.whiteRadioSignup:
                        ethnicity_ = "white";
                        break;
                    case R.id.otherRadioSignup:
                        String val = otherEthnicity.getText().toString();
                        if (val.length() > 0){
                            ethnicity_ = val;
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "Please insert your ethnicity", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    default:
                        Toast.makeText(SignupActivity.this, "Please insert your ethnicity", Toast.LENGTH_SHORT).show();
                        return;
                }
                if(isCouser.isChecked()){
                    isCoUser_ = 1.0;
                }
                else{
                    isCoUser_ = 0.0;
                }


                //TODO: check if phone or email
                if (username_.contains("@") && username_.contains(".")) {
                    if (username_ != null) {
                        if (username_.length() > 0) {
                            userAttributes.addAttribute("email", username_);
                        }
                    }
                }
                else{
                    if (username_ != null) {
                        if (username_.length() > 0) {
                            if(username_.length() == 10 || !username_.contains("+")){
                                username_ = "+1" + username_;
                            }
                            userAttributes.addAttribute("phone_number", username_);
                        }
                    }
                }
                LogInHelper.init(SignupActivity.this);
                LogInHelper.getPool().signUpInBackground(username_, password_, userAttributes, null, signUpHandler);
                usersDBHelper.createUser(username_,name_,dob_, gender_,ethnicity_,education_,isCoUser_);
            }
        });

        LogInHelper.init(getApplicationContext());
        findCurrent();
    }

    private void findCurrent() {
        CognitoUser user = LogInHelper.getPool().getCurrentUser();
        username_ = user.getUserId();
        if(username_ != null) {
            LogInHelper.setUser(username_);
            user.getSessionInBackground(authenticationHandler);
        }
    }


    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            LogInHelper.getPool().getUser(username_).getSessionInBackground(authenticationHandler);
        }


        @Override
        public void onFailure(Exception exception) {
            //show toast
            Log.i("sgnUp", "Error : " + exception.toString());
                Toast.makeText(SignupActivity.this, "Error: " + exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            this.username_ = username;
            LogInHelper.setUser(username);
        }
        if(this.password_ == null) {
            password_ = password.getText().toString();
            if(password == null) {
                Toast.makeText(SignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            if(password.length() < 1) {
                Toast.makeText(SignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username_, password_, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d("sgnUp", " -- Auth Success");
            LogInHelper.setCurrSession(cognitoUserSession);
            LogInHelper.newDevice(device);

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            SignupActivity.this.finish();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username_);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
        }


        @Override
        public void onFailure(Exception e) {
//            label.setText("Sign-in failed");
            Toast.makeText(SignupActivity.this, "Failed : " + e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("sgnUp", "Failure in sign up : " + e.toString());
        }


    };

}

