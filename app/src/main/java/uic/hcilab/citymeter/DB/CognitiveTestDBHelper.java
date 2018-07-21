package uic.hcilab.citymeter.DB;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static com.amazonaws.services.cognitoidentityprovider.model.AttributeDataType.DateTime;

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
            final com.amazonaws.models.nosql.CognitiveTestDO entry = new com.amazonaws.models.nosql.CognitiveTestDO();
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
            entry.setTimestamp(System.currentTimeMillis()+ "");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        dynamoDBMapper.save(entry);
                        isDone = true;
                        // Item saved
                    } catch (Exception e) {
                        Log.i("cogTDB", "Error writing to dB: " + e.toString());
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

    public List<com.amazonaws.models.nosql.CognitiveTestDO> tests ;
    public void getAllTests(String id){
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(id));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("uid = :val1 ").withExpressionAttributeValues(eav);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tests = dynamoDBMapper.scan(com.amazonaws.models.nosql.CognitiveTestDO.class, scanExpression);
                    }
                    catch (Exception e){
                        Toast.makeText(ctx, "Error reading test results", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (Exception e) {

        }
    }

}
