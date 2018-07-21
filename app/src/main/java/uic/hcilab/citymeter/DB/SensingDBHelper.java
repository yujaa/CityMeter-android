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
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


public class SensingDBHelper  {
    // Declare a DynamoDBMapper object
    DynamoDBMapper dynamoDBMapper;
    Context ctx;
    List<UserExposureDO> exposures = new List<UserExposureDO>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator<UserExposureDO> iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            return null;
        }

        @Override
        public boolean add(UserExposureDO userExposureDO) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends UserExposureDO> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends UserExposureDO> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public UserExposureDO get(int index) {
            return null;
        }

        @Override
        public UserExposureDO set(int index, UserExposureDO element) {
            return null;
        }

        @Override
        public void add(int index, UserExposureDO element) {

        }

        @Override
        public UserExposureDO remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @NonNull
        @Override
        public ListIterator<UserExposureDO> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<UserExposureDO> listIterator(int index) {
            return null;
        }

        @NonNull
        @Override
        public List<UserExposureDO> subList(int fromIndex, int toIndex) {
            return null;
        }
    };

    public SensingDBHelper (Context context) {
        ctx = context;
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
        connect();
       Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dynamoDBMapper.save(exposureInst);
                    // Item saved
                }catch (Exception e){
                    Log.i("snsDB", "Error writing to dB: " + e.toString());
                }
            }
        });
       try {
           thread.start();
           thread.join();
       }
       catch (Exception e){

       }
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
        connect();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dynamoDBMapper.save(exposureInst);
                    Log.i("snsDB", "wrote to db");
                    // Item saved
                }
                catch (Exception e){
                    Log.i("snsDB", e.toString());
                }
            }
        });
        try {
            thread.start();
            thread.join();
        }
        catch (Exception e){

        }
    }

    public Map<String, Double> getLatestLocation(String id){
        Map<String,Double> location = new HashMap<String, Double>();
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withN("-1.0"));
            UserExposureDO exp = new UserExposureDO();
            exp.setUserId(id);

            final DynamoDBQueryExpression<UserExposureDO> queryExpression = new DynamoDBQueryExpression<UserExposureDO>()
                    .withFilterExpression("longitude <> :val1 and dBA <> :val1 ").withExpressionAttributeValues(eav)
                    .withHashKeyValues(exp);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        exposures = dynamoDBMapper.query(UserExposureDO.class, queryExpression);
                    }
                    catch (Exception e){
                        Log.i("snsDB", "Error: " + e.getLocalizedMessage());
                    }
                }
            });
            thread.start();
            thread.join();
            if (exposures.size() != 0){
                UserExposureDO latest = exposures.get(exposures.size() - 1);
                location.put("longitude", latest.getLongitude());
                location.put("latitude", latest.getLatitude());
            }
        } catch (Exception e) {

            Log.i("snsDB", e.toString()) ;
        }
        return location;
    }
}