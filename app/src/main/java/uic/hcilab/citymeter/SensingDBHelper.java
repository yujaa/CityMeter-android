package uic.hcilab.citymeter;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;



public class SensingDBHelper  {
    // Declare a DynamoDBMapper object
    DynamoDBMapper dynamoDBMapper;
    Context ctx;

    SensingDBHelper (Context context) {
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
    public void createExposureInst_pm(String id, String timestamp, double pm , double lon, double lat, double ind ) {
        final UserExposureDO exposureInst = new UserExposureDO();

        exposureInst.setUserId(id);

        exposureInst.setTimestamp(timestamp);

        exposureInst.setPm25(pm);
        exposureInst.setDBA(-1.0);
        exposureInst.setLongitude(lon);
        exposureInst.setLatitude(lat);
        exposureInst.setIndoor(ind);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(exposureInst);
                // Item saved
            }
        }).start();
    }
    public void createExposureInst_dBA(String id, String timestamp, double dbA , double lon, double lat, double ind ) {
        final UserExposureDO exposureInst = new UserExposureDO();

        exposureInst.setUserId(id);
        exposureInst.setTimestamp(timestamp);
        exposureInst.setPm25(-1.0);
        exposureInst.setDBA(dbA);
        exposureInst.setLongitude(lon);
        exposureInst.setLatitude(lat);
        exposureInst.setIndoor(ind);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dynamoDBMapper.save(exposureInst);
                    Log.i("nina", "wrote to db");
                    // Item saved
                }
                catch (Exception e){
                    Log.i("nina", e.toString());
                }
            }
        }).start();
    }
}