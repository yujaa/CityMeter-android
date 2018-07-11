package uic.hcilab.citymeter.DB;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CognitiveTestDBHelper {
    // Declare a DynamoDBMapper object
    private DynamoDBMapper dynamoDBMapper;
    private Context ctx;

    public Boolean isDone = false;

    public CognitiveTestDBHelper(Context context) {
        ctx = context;
        connect();
    }

    public void connect() {
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
    public void createCognitiveTestResult( String _uid, String _memoryProblem, String _blood, String _balance, String _balanceCause, String _majorStroke, String _sadDepressed,
                                           String _personality, String _difficultyActivities, String _todayDate, String _namePictureRhino, String _namePictureHarp,
                                           String _tulip , String _quarters, String _groceriesChange, String _copyPictureFile, String _drawClockFile,
                                           String _countries12, String _circleLinesFile, String _trianglesFile, String _done ) {
        try {
            final CognitiveTestDO entry = new CognitiveTestDO();
            entry.setUid(_uid);
            entry.setMemoryProblem(_memoryProblem);
            entry.setBlood(_blood);
            entry.setBalance(_balance);
            entry.setBalanceCause(_balanceCause);
            entry.setMajorStroke(_majorStroke);
            entry.setSadDepressed(_sadDepressed);
            entry.setPersonality(_personality);
            entry.setDifficultyActivities(_difficultyActivities);
            entry.setTodayDate(_todayDate);
            entry.setNamePictureRhino(_namePictureRhino);
            entry.setNamePictureHarp(_namePictureHarp);
            entry.setTulip(_tulip);
            entry.setQuarters(_quarters);
            entry.setGroceriesChange(_groceriesChange);
            entry.setCopyPictureFile(_copyPictureFile);
            entry.setDrawClockFile(_drawClockFile);
            entry.setCountries12(_countries12);
            entry.setCircleLinesFile(_circleLinesFile);
            entry.setTrianglesFile(_trianglesFile);
            entry.setDone(_done);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        dynamoDBMapper.save(entry);
                        isDone = true;
                        // Item saved
                    } catch (Exception e) {
                        Log.i("BT", "Error writing to dB: " + e.toString());
                    }
                }
            });
            thread.start();
            isDone = false;
            thread.join();
        } catch (Exception e)

        {
        }
    }

}
