package uic.hcilab.citymeter.DB;


import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class UsersDBHelper {
    // Declare a DynamoDBMapper object
    DynamoDBMapper dynamoDBMapper;
    Context ctx;

    UsersDBHelper (Context context) {
        ctx = context;
        connect();
    }
    public void connect(){
        // AWSMobileClient enables AWS user credentials to access your table
        AWSMobileClient.getInstance().initialize(ctx, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {

            }
        }).execute();


        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();


        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();
    }
    //CREATE
    public void createUser(String id, String name,  double isCo) {
        final UsersDO user = new UsersDO();

        user.setUserID(id);
        user.setName(name);
        user.setIsCoUser(isCo);
        connect();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dynamoDBMapper.save(user);
                    // Item saved
                } catch (Exception e) {
                    Log.i("BT", "Error writing to dB: " + e.toString());
                }
            }
        }).start();
    }

}
