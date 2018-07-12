package uic.hcilab.citymeter;

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

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

import uic.hcilab.citymeter.DB.UsersDBHelper;
import uic.hcilab.citymeter.Helpers.LogInHelper;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class SignupActivity extends AppCompatActivity {
    LogInHelper logInHelper;
    String username_, password_, name_, dob_, gender_, ethnicity_, education_;
    double isCoUser_;
    UsersDBHelper usersDBHelper;
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
        final EditText username = (EditText)findViewById(R.id.usernameEditSignup);
        final EditText password = (EditText) findViewById(R.id.passwordEditSignup);
        final EditText name = (EditText) findViewById(R.id.nameEditSignup);
        final EditText dob = (EditText)findViewById(R.id.dobEditSignUp);
        final EditText education = (EditText)findViewById(R.id.educationEditSignup);
        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderRadioGroupSignup);
        final RadioGroup ethnicityGroup = (RadioGroup) findViewById(R.id.ethnicityRadioGroupSignUp);
        final EditText otherEthnicity = (EditText) findViewById(R.id.otherEditSignup);
        final CheckBox isCouser = (CheckBox) findViewById(R.id.isCoUserCheckSignup);
        Button cancel = (Button) findViewById(R.id.cancelSignupBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.this.finish();
            }
        });
        Button submit = (Button) findViewById(R.id.signupSubmitBtn);
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
                if (name != null) {
                    if (name.length() > 0) {
                        userAttributes.addAttribute("name", name_);
                    }
                }
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
                userAttributes.addAttribute("gender" , gender_);

                dob_ = dob.getText().toString();
                if (dob_ != null){
                    if (dob_.length() > 0){
                        userAttributes.addAttribute("birthdate", dob_);
                    }
                }

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
                                username_ = "+" + username_;
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
    }

    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
               Toast.makeText(SignupActivity.this, "Successful" , Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Exception exception) {
            //show toast
            Log.i("coco", "Error : " + exception.toString());
            Toast.makeText(SignupActivity.this, "Error: " + exception , Toast.LENGTH_SHORT).show();
        }
    };

}

