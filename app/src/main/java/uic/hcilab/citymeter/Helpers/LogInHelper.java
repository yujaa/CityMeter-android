package uic.hcilab.citymeter.Helpers;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSettings;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import uic.hcilab.citymeter.R;

public class LogInHelper {
    Context context;
    String userPoolId ;
    String clientId;
    String clientSecret;
    CognitoUserPool pool;
    CognitoUser user;

    public LogInHelper(Context ctx)  {
        context = ctx;
        try {
            userPoolId = getJSON(context).getString("PoolId");
            clientId = getJSON(context).getString("AppClientId");
            clientSecret = getJSON(context).getString("AppClientSecret");
            pool = createUserPool();
        }
        catch (Exception e){

        }
    }

    //    Create a CognitoUserPool
    public CognitoUserPool createUserPool( AmazonCognitoIdentityProvider clientConfiguration) {
        // user pool can also be created with client app configuration:
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, clientConfiguration);
        return userPool;
    }

    public CognitoUserPool createUserPool() {
        CognitoUserPool userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret);
        return userPool;

    }

    //Register a new user
    public void registerUser() {
        // create a handler for registration
        SignUpHandler handler = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, boolean signUpConfirmationState, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {

                // If the sign up was successful, "user" is a CognitoUser object of the user who was signed up.
                // "codeDeliveryDetails" will contain details about where the confirmation codes will be delivered.
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign up failed, code check the exception for cause and perform remedial actions.
            }
        };
    }

    //Get the Cached User
    public CognitoUser getCachedUser() {
        CognitoUser user = pool.getCurrentUser();
        return user;
    }

    // Create a User Object with a UserId
    public CognitoUser createUserObject( String userId) {
        CognitoUser user = pool.getUser(userId);
        return user;
    }

    //Confirm a User
    public void confirmUserSignUp( String code) {
        // create a callback handler for confirm
        GenericHandler handler = new GenericHandler() {

            @Override
            public void onSuccess() {
                // User was successfully confirmed!
            }

            @Override
            public void onFailure(Exception exception) {
                // Confirmation failed, probe exception for details\
            }
        };
        user.confirmSignUp(code, false, handler);//TODO look forced alias creation
    }

    //Request a Confirmation Code
    public void RequestConfirmationCode() {

        // create a callback handler for the confirmation code request
        VerificationHandler handler = new VerificationHandler() {


            @Override
            public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {

            }

            @Override
            public void onFailure(Exception exception) {
                // Confirmation code request failed, probe exception for details
            }
        };

        user.resendConfirmationCode(handler);
    }

    //    Forgot Password:     Get Code     to Set     New Password
    public void getCodeResetPassword( final String newPassword, final String code) {
        ForgotPasswordHandler handler = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                // Forgot password process completed successfully, new password has been successfully set

            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                // A code will be sent, use the "continuation" object to continue with the forgot password process

                // This will indicate where the code was sent
                String codeSentHere = continuation.getParameters().toString();

                // Code to get the code from the user - user dialogs etc.

                // If the program control has to exit this method, take the "continuation" object.
                // "continuation" is the only possible way to continue with the process


                // When the code is available

                // Set the new password
                continuation.setPassword(newPassword);

                // Set the code to verify
                continuation.setVerificationCode(code);

                // Let the forgot password process proceed
                continuation.continueTask();

            }

            /**
             * This is called for all fatal errors encountered during the password reset process
             * Probe {@exception} for cause of this failure.
             * @param exception
             */
            public void onFailure(Exception exception) {
                // Forgot password processing failed, probe the exception for cause
            }
        };

        user.forgotPassword(handler);
    }

    //Authentication Handler: Get Tokens
    public void getTokens( final String userId, final String password, final Map<String, String> validationParameters, final String code) {
        // Implement authentication handler,
        AuthenticationHandler handler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                // Authentication was successful, the "userSession" will have the current valid tokens
                // Time to do awesome stuff
            }

            @Override
            public void getAuthenticationDetails(final AuthenticationContinuation continuation, final String userID) {
                // User authentication details, userId and password are required to continue.
                // Use the "continuation" object to pass the user authentication details

                // After the user authentication details are available, wrap them in an AuthenticationDetails class
                // Along with userId and password, parameters for user pools for Lambda can be passed here
                // The validation parameters "validationParameters" are passed in as a Map<String, String>
                AuthenticationDetails authDetails = new AuthenticationDetails(userId, password, validationParameters);

                // Now allow the authentication to continue
                continuation.setAuthenticationDetails(authDetails);
                continuation.continueTask();
            }

            @Override
            public void getMFACode(final MultiFactorAuthenticationContinuation continuation) {
                // Multi-factor authentication is required to authenticate
                // A code was sent to the user, use the code to continue with the authentication


                // Find where the code was sent to
                String codeSentHere = continuation.getParameters().toString();

                // When the verification code is available, continue to authenticate
                continuation.setMfaCode(code);
                continuation.continueTask();
            }

            @Override
            public void authenticationChallenge(final ChallengeContinuation continuation) {
                // A custom challenge has to be solved to authenticate

                // Set the challenge responses

                // Call continueTask() method to respond to the challenge and continue with authentication.
            }


            @Override
            public void onFailure(final Exception exception) {
                // Authentication failed, probe exception for the cause

            }
        };
        user.getSession(handler);
    }

    //Get User Details
    public void getUserDetails() {
        GetDetailsHandler handler = new GetDetailsHandler() {
            @Override
            public void onSuccess(final CognitoUserDetails list) {
                // Successfully retrieved user details
            }

            @Override
            public void onFailure(final Exception exception) {
                // Failed to retrieve the user details, probe exception for the cause
            }
        };
        user.getDetails(handler);
    }

    //Change or Set User Settings
    public void setUserSetting( String settingName, String settingValue) {
        GenericHandler handler = new GenericHandler() {

            @Override
            public void onSuccess() {
                // Successfully changed settings!
            }

            @Override
            public void onFailure(Exception exception) {
                // Change failed, probe exception for details
            }
        };

        // userSettings is an object of the type CognitoUserSettings,
        CognitoUserSettings userSettings = new CognitoUserSettings();

// Set the user settings
        userSettings.setSettings(settingName, settingValue);

// Now update the new settings to the Amazon Cognito Identity Provider Service
        user.setUserSettings(userSettings, handler);
    }

    // Delete User
    public void deleteUser() {
        GenericHandler handler = new GenericHandler() {

            @Override
            public void onSuccess() {
                // Delete was successful!
            }

            @Override
            public void onFailure(Exception exception) {
                // Delete failed, probe exception for details
            }
        };
        user.deleteUser(handler);
    }

    //Sign Out User
    public void signOutUser() {
// This has cleared all tokens and this user will have to go through the authentication process to get tokens.
        user.signOut();
    }

    //    Get Access and ID Tokens from CognitoUserSession
    public void getTokensFromSession(CognitoUserSession session) {
        // Session is an object of the type CognitoUserSession
        String accessToken = session.getAccessToken().getJWTToken();
        String idToken = session.getIdToken().getJWTToken();
    }

    //    Remember a Device
    public void rememberDevice(CognitoDevice cognitoDevice) {
        GenericHandler handler = new GenericHandler() {

            @Override
            public void onSuccess() {
                // Successful!
            }

            @Override
            public void onFailure(Exception exception) {
                // Failed, probe exception for details
            }
        };
        cognitoDevice.rememberThisDeviceInBackground(handler);
    }

    //    Do Not Remember a Device
    public void forgetDevice(CognitoDevice cognitoDevice) {
        GenericHandler handler = new GenericHandler() {

            @Override
            public void onSuccess() {
                // Successful!
            }

            @Override
            public void onFailure(Exception exception) {
                // Failed, probe exception for details
            }
        };
        cognitoDevice.doNotRememberThisDeviceInBackground(handler);
    }


    public JSONObject getJSON(Context context) {
        String json = null;
        JSONObject jsonObject = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.awsconfiguration);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {

                is.close();
            }


            json = writer.toString();
            jsonObject = new JSONObject(json);
            jsonObject = jsonObject.getJSONObject("CognitoUserPool");
            jsonObject = jsonObject.getJSONObject("Default");
        }
        catch (Exception e ) {
        }
        return jsonObject;

    }

}
