package uic.hcilab.citymeter.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
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
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProvider;
import com.amazonaws.services.cognitoidentityprovider.model.AttributeType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uic.hcilab.citymeter.R;

public class LogInHelper {
    static Context context;
    CognitoUserPool pool;

    private static final String TAG = "AppHelper";
    // App settings

    private static List<String> attributeDisplaySeq;
    private static Map<String, String> signUpFieldsC2O;
    private static Map<String, String> signUpFieldsO2C;

    private static LogInHelper appHelper;
    private static CognitoUserPool userPool;
    private static String user;
    private static CognitoDevice newDevice;

    private static CognitoUserAttributes attributesChanged;
    private static List<AttributeType> attributesToDelete;

    private static List<ItemToDisplay> currDisplayedItems;
    private static  int itemCount;

    private static List<ItemToDisplay> trustedDevices;
    private static int trustedDevicesCount;
    private static List<CognitoDevice> deviceDetails;
    private static CognitoDevice thisDevice;
    private static boolean thisDeviceTrustState;

    private static List<ItemToDisplay> firstTimeLogInDetails;
    private static Map<String, String> firstTimeLogInUserAttributes;
    private static List<String> firstTimeLogInRequiredAttributes;
    private static int firstTimeLogInItemsCount;
    private static Map<String, String> firstTimeLogInUpDatedAttributes;
    private static String firstTimeLoginNewPassword;

    private static List<ItemToDisplay> mfaOptions;
    private static List<String> mfaAllOptionsCode;

    // Change the next three lines of code to run this demo on your user pool

    /**
     * Add your pool id here
     */
    private static String userPoolId = "Your userpool id here";

    /**
     * Add you app id
     */
    private static String clientId = "Your client id here";

    /**
     * App secret associated with your app id - if the App id does not have an associated App secret,
     * set the App secret to null.
     * e.g. clientSecret = null;
     */
    private static String clientSecret = null;

    /**
     * Set Your User Pools region.
     * e.g. if your user pools are in US East (N Virginia) then set cognitoRegion = Regions.US_EAST_1.
     */
    private static final Regions cognitoRegion = Regions.US_EAST_1;

    // User details from the service
    private static CognitoUserSession currSession;
    private static CognitoUserDetails userDetails;

    // User details to display - they are the current values, including any local modification
    private static boolean phoneVerified;
    private static boolean emailVerified;

    private static boolean phoneAvailable;
    private static boolean emailAvailable;

    private static Set<String> currUserAttributes;

    public static void init(Context ctx) {
        context = ctx;
        try {
            userPoolId = getJSON(context).getString("PoolId");
            clientId = getJSON(context).getString("AppClientId");
            clientSecret = getJSON(context).getString("AppClientSecret");
//            pool = createUserPool();

        setData();

        if (userPool == null) {

            // Create a user pool with default ClientConfiguration
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);

            // This will also work
            /*
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
            cipClient.setRegion(Region.getRegion(cognitoRegion));
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cipClient);
            */


        }

        phoneVerified = false;
        phoneAvailable = false;
        emailVerified = false;
        emailAvailable = false;

        currUserAttributes = new HashSet<String>();
        currDisplayedItems = new ArrayList<ItemToDisplay>();
        trustedDevices = new ArrayList<ItemToDisplay>();
        firstTimeLogInDetails = new ArrayList<ItemToDisplay>();
        firstTimeLogInUpDatedAttributes= new HashMap<String, String>();

        newDevice = null;
        thisDevice = null;
        thisDeviceTrustState = false;

        mfaOptions = new ArrayList<ItemToDisplay>();
        }
        catch (Exception e){

        }
    }

    public static CognitoUserPool getPool() {
        return userPool;
    }

    public static Map<String, String> getSignUpFieldsC2O() {
        return signUpFieldsC2O;
    }

    public static  Map<String, String> getSignUpFieldsO2C() {
        return signUpFieldsO2C;
    }

    public static List<String> getAttributeDisplaySeq() {
        return attributeDisplaySeq;
    }

    public static void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }

    public static  CognitoUserSession getCurrSession() {
        return currSession;
    }

    public static void setUserDetails(CognitoUserDetails details) {
        userDetails = details;
        refreshWithSync();
    }

    public static  CognitoUserDetails getUserDetails() {
        return userDetails;
    }

    public static String getCurrUser() {
        return user;
    }

    public static void setUser(String newUser) {
        user = newUser;
    }

    public static boolean isPhoneVerified() {
        return phoneVerified;
    }

    public static boolean isEmailVerified() {
        return emailVerified;
    }

    public static boolean isPhoneAvailable() {
        return phoneAvailable;
    }

    public static boolean isEmailAvailable() {
        return emailAvailable;
    }

    public static void setPhoneVerified(boolean phoneVerif) {
        phoneVerified = phoneVerif;
    }

    public static void setEmailVerified(boolean emailVerif) {
        emailVerified = emailVerif;
    }

    public static void setPhoneAvailable(boolean phoneAvail) {
        phoneAvailable = phoneAvail;
    }

    public static void setEmailAvailable(boolean emailAvail) {
        emailAvailable = emailAvail;
    }

    public static void clearCurrUserAttributes() {
        currUserAttributes.clear();
    }

    public static void addCurrUserattribute(String attribute) {
        currUserAttributes.add(attribute);
    }

    public static List<String> getNewAvailableOptions() {
        List<String> newOption = new ArrayList<String>();
        for(String attribute : attributeDisplaySeq) {
            if(!(currUserAttributes.contains(attribute))) {
                newOption.add(attribute);
            }
        }
        return  newOption;
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e(TAG, " -- Error: "+exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return  formattedString;
    }

    public  static  int getItemCount() {
        return itemCount;
    }

    public static int getDevicesCount() {
        return trustedDevicesCount;
    }

    public static int getFirstTimeLogInItemsCount() {
        return  firstTimeLogInItemsCount;
    }

    public  static ItemToDisplay getItemForDisplay(int position) {
        return  currDisplayedItems.get(position);
    }

    public static ItemToDisplay getDeviceForDisplay(int position) {
        if (position >= trustedDevices.size()) {
            return new ItemToDisplay(" ", " ", " ", Color.BLACK, Color.DKGRAY, Color.parseColor("#37A51C"), 0, null);
        }
        return trustedDevices.get(position);
    }

    public static ItemToDisplay getUserAttributeForFirstLogInCheck(int position) {
        return firstTimeLogInDetails.get(position);
    }

    public static void setUserAttributeForDisplayFirstLogIn(Map<String, String> currAttributes, List<String> requiredAttributes) {
        firstTimeLogInUserAttributes = currAttributes;
        firstTimeLogInRequiredAttributes = requiredAttributes;
        firstTimeLogInUpDatedAttributes = new HashMap<String, String>();
        refreshDisplayItemsForFirstTimeLogin();
    }

    public static void setUserAttributeForFirstTimeLogin(String attributeName, String attributeValue) {
        if (firstTimeLogInUserAttributes ==  null) {
            firstTimeLogInUserAttributes = new HashMap<String, String>();
        }
        firstTimeLogInUserAttributes.put(attributeName, attributeValue);
        firstTimeLogInUpDatedAttributes.put(attributeName, attributeValue);
        refreshDisplayItemsForFirstTimeLogin();
    }

    public static Map<String, String> getUserAttributesForFirstTimeLogin() {
        return firstTimeLogInUpDatedAttributes;
    }

    public static void setPasswordForFirstTimeLogin(String password) {
        firstTimeLoginNewPassword = password;
    }

    public static String getPasswordForFirstTimeLogin() {
        return firstTimeLoginNewPassword;
    }

    private static void refreshDisplayItemsForFirstTimeLogin() {
        firstTimeLogInItemsCount = 0;
        firstTimeLogInDetails = new ArrayList<ItemToDisplay>();

        for(Map.Entry<String, String> attr: firstTimeLogInUserAttributes.entrySet()) {
            if ("phone_number_verified".equals(attr.getKey()) || "email_verified".equals(attr.getKey())) {
                continue;
            }
            String message = "";
            if ((firstTimeLogInRequiredAttributes != null) && (firstTimeLogInRequiredAttributes.contains(attr.getKey()))) {
                message = "Required";
            }
            ItemToDisplay item = new ItemToDisplay(attr.getKey(), attr.getValue(), message, Color.BLACK, Color.DKGRAY, Color.parseColor("#329AD6"), 0, null);
            firstTimeLogInDetails.add(item);
            firstTimeLogInRequiredAttributes.size();
            firstTimeLogInItemsCount++;
        }

        for (String attr: firstTimeLogInRequiredAttributes) {
            if (!firstTimeLogInUserAttributes.containsKey(attr)) {
                ItemToDisplay item = new ItemToDisplay(attr, "", "Required", Color.BLACK, Color.DKGRAY, Color.parseColor("#329AD6"), 0, null);
                firstTimeLogInDetails.add(item);
                firstTimeLogInItemsCount++;
            }
        }
    }

    public static void newDevice(CognitoDevice device) {
        newDevice = device;
    }

    public static void setDevicesForDisplay(List<CognitoDevice> devicesList) {
        trustedDevicesCount = 0;
        thisDeviceTrustState = false;
        deviceDetails = devicesList;
        trustedDevices = new ArrayList<ItemToDisplay>();
        for(CognitoDevice device: devicesList) {
            if (thisDevice != null && thisDevice.getDeviceKey().equals(device.getDeviceKey())) {
                thisDeviceTrustState = true;
            } else {
                ItemToDisplay item = new ItemToDisplay("", device.getDeviceName(), device.getCreateDate().toString(), Color.BLACK, Color.DKGRAY, Color.parseColor("#329AD6"), 0, null);
                item.setDataDrawable("checked");
                trustedDevices.add(item);
                trustedDevicesCount++;
            }
        }
    }

    public static CognitoDevice getDeviceDetail(int position) {
        if (position <= trustedDevicesCount) {
            return deviceDetails.get(position);
        } else {
            return null;
        }
    }

    public static void setMfaOptionsForDisplay(List<String> options, Map<String, String> parameters) {
        mfaAllOptionsCode = options;
        mfaOptions = new ArrayList<ItemToDisplay>();
        String textToDisplay = "";
        for (String option: options) {
            if ("SMS_MFA".equals(option)) {
                textToDisplay = "Send SMS";
                if (parameters.containsKey("CODE_DELIVERY_DESTINATION")) {
                    textToDisplay = textToDisplay + " to "+ parameters.get("CODE_DELIVERY_DESTINATION");
                }
            } else if ("SOFTWARE_TOKEN_MFA".equals(option)) {
                textToDisplay = "Use TOTP";
                if (parameters.containsKey("FRIENDLY_DEVICE_NAME")) {
                    textToDisplay = textToDisplay + ": " + parameters.get("FRIENDLY_DEVICE_NAME");
                }
            }
            ItemToDisplay item = new ItemToDisplay("", textToDisplay, "", Color.BLACK, Color.DKGRAY, Color.parseColor("#329AD6"), 0, null);
            mfaOptions.add(item);
            textToDisplay = "Unsupported MFA";
        }
    }

    public static List<String> getAllMfaOptions() {
        return mfaAllOptionsCode;
    }

    public static String getMfaOptionCode(int position) {
        return mfaAllOptionsCode.get(position);
    }

    public static ItemToDisplay getMfaOptionForDisplay(int position) {
        if (position >= mfaOptions.size()) {
            return new ItemToDisplay(" ", " ", " ", Color.BLACK, Color.DKGRAY, Color.parseColor("#37A51C"), 0, null);
        }
        return mfaOptions.get(position);
    }

    public static int getMfaOptionsCount() {
        return mfaOptions.size();
    }

    //public static

    public static CognitoDevice getNewDevice() {
        return newDevice;
    }

    public static CognitoDevice getThisDevice() {
        return thisDevice;
    }

    public static void setThisDevice(CognitoDevice device) {
        thisDevice = device;
    }

    public static boolean getThisDeviceTrustState() {
        return thisDeviceTrustState;
    }

    public static void setData() {
        // Set attribute display sequence
        attributeDisplaySeq = new ArrayList<String>();
        attributeDisplaySeq.add("given_name");
        attributeDisplaySeq.add("middle_name");
        attributeDisplaySeq.add("family_name");
        attributeDisplaySeq.add("nickname");
        attributeDisplaySeq.add("phone_number");
        attributeDisplaySeq.add("email");

        signUpFieldsC2O = new HashMap<String, String>();
        signUpFieldsC2O.put("Given name", "given_name");
        signUpFieldsC2O.put("Family name", "family_name");
        signUpFieldsC2O.put("Nick name", "nickname");
        signUpFieldsC2O.put("Phone number", "phone_number");
        signUpFieldsC2O.put("Phone number verified", "phone_number_verified");
        signUpFieldsC2O.put("Email verified", "email_verified");
        signUpFieldsC2O.put("Email","email");
        signUpFieldsC2O.put("Middle name","middle_name");

        signUpFieldsO2C = new HashMap<String, String>();
        signUpFieldsO2C.put("given_name", "Given name");
        signUpFieldsO2C.put("family_name", "Family name");
        signUpFieldsO2C.put("nickname", "Nick name");
        signUpFieldsO2C.put("phone_number", "Phone number");
        signUpFieldsO2C.put("phone_number_verified", "Phone number verified");
        signUpFieldsO2C.put("email_verified", "Email verified");
        signUpFieldsO2C.put("email", "Email");
        signUpFieldsO2C.put("middle_name", "Middle name");

    }

    public static void refreshWithSync() {
        // This will refresh the current items to display list with the attributes fetched from service
        List<String> tempKeys = new ArrayList<>();
        List<String> tempValues = new ArrayList<>();

        emailVerified = false;
        phoneVerified = false;

        emailAvailable = false;
        phoneAvailable = false;

        currDisplayedItems = new ArrayList<ItemToDisplay>();
        currUserAttributes.clear();
        itemCount = 0;

        for(Map.Entry<String, String> attr: userDetails.getAttributes().getAttributes().entrySet()) {

            tempKeys.add(attr.getKey());
            tempValues.add(attr.getValue());

            if(attr.getKey().contains("email_verified")) {
                emailVerified = attr.getValue().contains("true");
            }
            else if(attr.getKey().contains("phone_number_verified")) {
                phoneVerified = attr.getValue().contains("true");
            }

            if(attr.getKey().equals("email")) {
                emailAvailable = true;
            }
            else if(attr.getKey().equals("phone_number")) {
                phoneAvailable = true;
            }
        }

        // Arrange the input attributes per the display sequence
        Set<String> keySet = new HashSet<>(tempKeys);
        for(String det: attributeDisplaySeq) {
            if(keySet.contains(det)) {
                // Adding items to display list in the required sequence

                ItemToDisplay item = new ItemToDisplay(signUpFieldsO2C.get(det), tempValues.get(tempKeys.indexOf(det)), "",
                        Color.BLACK, Color.DKGRAY, Color.parseColor("#37A51C"),
                        0, null);

                if(det.contains("email")) {
                    if(emailVerified) {
                        item.setDataDrawable("checked");
                        item.setMessageText("Email verified");
                    }
                    else {
                        item.setDataDrawable("not_checked");
                        item.setMessageText("Email not verified");
                        item.setMessageColor(Color.parseColor("#E94700"));
                    }
                }

                if(det.contains("phone_number")) {
                    if(phoneVerified) {
                        item.setDataDrawable("checked");
                        item.setMessageText("Phone number verified");
                    }
                    else {
                        item.setDataDrawable("not_checked");
                        item.setMessageText("Phone number not verified");
                        item.setMessageColor(Color.parseColor("#E94700"));
                    }
                }

                currDisplayedItems.add(item);
                currUserAttributes.add(det);
                itemCount++;
            }
        }
    }

    public static void modifyAttribute(String attributeName, String newValue) {
        //

    }

    public static void deleteAttribute(String attributeName) {

    }

    public LogInHelper(Context ctx)  {
        context = ctx;
        try {
            userPoolId = getJSON(context).getString("PoolId");
            clientId = getJSON(context).getString("AppClientId");
            clientSecret = getJSON(context).getString("AppClientSecret");
//            pool = createUserPool();
        }
        catch (Exception e){

        }
    }
/*
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
        //user.confirmSignUp(code, false, handler);//TODO look forced alias creation
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

            //
            //  This is called for all fatal errors encountered during the password reset process
            //  Probe {@exception} for cause of this failure.
             //  @param exception

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
    public void mygetUserDetails() {
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
*/

    public static JSONObject getJSON(Context context) {
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
