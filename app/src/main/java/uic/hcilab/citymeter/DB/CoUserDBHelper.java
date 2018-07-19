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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CoUserDBHelper {

    // Declare a DynamoDBMapper object
    private DynamoDBMapper dynamoDBMapper;
    private Context ctx;
    public List<CousersDO> coUsers = new List<CousersDO>() {
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
        public Iterator<CousersDO> iterator() {
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
        public boolean add(CousersDO cousersDO) {
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
        public boolean addAll(@NonNull Collection<? extends CousersDO> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends CousersDO> c) {
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
        public CousersDO get(int index) {
            return null;
        }

        @Override
        public CousersDO set(int index, CousersDO element) {
            return null;
        }

        @Override
        public void add(int index, CousersDO element) {

        }

        @Override
        public CousersDO remove(int index) {
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
        public ListIterator<CousersDO> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<CousersDO> listIterator(int index) {
            return null;
        }

        @NonNull
        @Override
        public List<CousersDO> subList(int fromIndex, int toIndex) {
            return null;
        }
    };
    public Boolean isDone = false;
    public Thread thread;

    public CoUserDBHelper(Context context) {
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
    public void createCoEntry(String uid, String cuid, double isLoc, double isAct, double isCogTest) {
        try {
            final CousersDO entry = new CousersDO();
            entry.setUid(uid);
            entry.setCuid(cuid);
            entry.setCanSeeActivities(isAct);
            entry.setCanSeeLocation(isLoc);
            entry.setCanSeeCogTest(isCogTest);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        dynamoDBMapper.save(entry);
                        isDone = true;
                        // Item saved
                    } catch (Exception e) {
                        Log.i("coDB", "Error writing to dB: " + e.toString());
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

    //retrieve
    public void getCoUser(final String ID, String cID) {
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(ID));
            eav.put(":val2", new AttributeValue().withS(cID));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("uid = :val1 and cuid = :val2").withExpressionAttributeValues(eav);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    coUsers = dynamoDBMapper.scan(CousersDO.class, scanExpression);
                    isDone = true;
                }
            });
            thread.start();
            //while(!isDone){}
            isDone = false;
            thread.join();
        } catch (Exception e) {
        }
    }

    public void getAllCoUsers(String userID) {
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(userID));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("uid = :val1 ").withExpressionAttributeValues(eav);
//        final DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression().withFilterExpression("uid = :val1 ").withExpressionAttributeValues(eav);


//                coUsers = dynamoDBMapper.query(CousersDO.class, scanExpression);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    coUsers = dynamoDBMapper.scan(CousersDO.class, scanExpression);
                    isDone = true;
                }
            });
            thread.start();
            //while(!isDone){}
            isDone = false;
            thread.join();
        } catch (Exception e) {

        }
    }

    public void getAllCareTakers(String userID) {
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(userID));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("cuid = :val1 ").withExpressionAttributeValues(eav);
//        final DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression().withFilterExpression("uid = :val1 ").withExpressionAttributeValues(eav);


//                coUsers = dynamoDBMapper.query(CousersDO.class, scanExpression);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    coUsers = dynamoDBMapper.scan(CousersDO.class, scanExpression);
                    isDone = true;
                }
            });
            thread.start();
            //while(!isDone){}
            isDone = false;
            thread.join();
        } catch (Exception e) {

        }
    }

    public CousersDO updateCoUser(String id, String cid, double location, double activity, double cogTest) {
        final CousersDO user = new CousersDO();
        try {
            user.setUid(id);
            user.setCuid(cid);
            user.setCanSeeLocation(location);
            user.setCanSeeActivities(activity);
            user.setCanSeeCogTest(cogTest);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    dynamoDBMapper.save(user);
                }
            });
            thread.start();
            // while(!isDone){}
            isDone = false;
            thread.join();
        } catch (Exception e) {

        }
        return user;
    }

    public void deletecoUser(final CousersDO coUser) {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    dynamoDBMapper.delete(coUser);
                    isDone = true;
                    // Item deleted
                }
            });

            thread.start();
            //while(!isDone){}
            isDone = false;
            thread.join();
        } catch (Exception e) {

        }

    }
}
