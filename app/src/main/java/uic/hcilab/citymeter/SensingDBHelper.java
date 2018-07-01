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
    public void createExposureInst_pm(String id, String timestamp, double pm , double lon, double lat ) {
        final UserExposureDO exposureInst = new UserExposureDO();

        Log.i("nina", "1");
        exposureInst.setUserId(id);

        Log.i("nina", "2");
        exposureInst.setTimestamp(timestamp);

        Log.i("nina", "3");
        exposureInst.setPm25(pm);
        exposureInst.setDBA(-1.0);
        exposureInst.setLongitude(lon);
        exposureInst.setLatitude(lat);

        Log.i("nina", "4");
        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(exposureInst);
                Log.i("nina", "wrote to db");
                // Item saved
            }
        }).start();
    }
    public void createExposureInst_dBA(String id, String timestamp, double dbA , double lon, double lat ) {
        final UserExposureDO exposureInst = new UserExposureDO();

        exposureInst.setUserId(id);
        exposureInst.setTimestamp(timestamp);
        exposureInst.setPm25(-1.0);
        exposureInst.setDBA(dbA);
        exposureInst.setLongitude(lon);
        exposureInst.setLatitude(lat);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("nina", "writing");
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
    /*private static final int DATABASE_VERSION = 1;// Database Version
    private static final String DATABASE_NAME = "sensing_db";// Database Name

    private SQLiteDatabase sensingDB;

    public SensingDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.onCreate(sensingDB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.openDb();
        try {
            Cursor cursor = sensingDB.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+TABLE_NAME+"'", null);
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    Log.i("BT","Table Exists");
                    cursor.close();
                }
                else {
                    db.execSQL(SensingDB.CREATE_TABLE);
                    cursor.close();
                    Log.i("BT","Table Created");
                }
            }
        }
        catch (Exception e){
            Log.i("BT", "Can't create table : " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public String readNext(){
        Cursor cursor = sensingDB.query(TABLE_NAME,
                new String[]{ SensingDB.COLUMN_ENTRY, SensingDB.COLUMN_IS_WRITTEN},
                SensingDB.COLUMN_IS_WRITTEN+ "=?",
                new String[]{"0"}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        // prepare note object
        SensingDB result = new SensingDB(
                cursor.getString(cursor.getColumnIndex(SensingDB.COLUMN_ENTRY)),
                cursor.getInt(cursor.getColumnIndex(SensingDB.COLUMN_IS_WRITTEN)));
        // close the db connection
        cursor.close();
        return result.getEntry();
    }

    public void insertDBRow(String entry){
        ContentValues values = new ContentValues();
        values.put(SensingDB.COLUMN_ENTRY, entry);
        values.put(SensingDB.COLUMN_IS_WRITTEN, 0);
        try {
            sensingDB.insert(TABLE_NAME, null, values);
        }
        catch (Exception e){
            Log.i("BT", "Insert to DB failed : " + e.toString());
        }
    }

    public int updateDBRow(String entry) {
        ContentValues values = new ContentValues();
        values.put(SensingDB.COLUMN_IS_WRITTEN, 1);
        // update row
        return sensingDB.update(TABLE_NAME, values, SensingDB.COLUMN_ENTRY + " = ?",
                new String[]{entry});
    }

    public void deleteDBRow(String entry) {
        sensingDB.delete(TABLE_NAME, SensingDB.COLUMN_ENTRY + " = ?",
                new String[]{entry});
    }

    public void openDb() {
        try {
            sensingDB = this.getWritableDatabase();
            sensingDB.enableWriteAheadLogging();//To allow multithread access
        } catch (SQLException e) {
            Log.e(this.getClass().getName(), e.toString());
            throw(new RuntimeException(e));
        }
    }

    public void closeDb() {
        if (sensingDB != null)
            sensingDB.close();
    }

    public void dropTable() {
        sensingDB.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
    }

    public void wipeTable() {
        synchronized (TABLE_NAME) {
            sensingDB.delete(TABLE_NAME, null, null);
        }
    }

    public ArrayList<String> readAll(){
        Cursor cursor = sensingDB.rawQuery("select * from " + TABLE_NAME,null);
        if (cursor != null)
            cursor.moveToFirst();
        ArrayList<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_ENTRY));
                list.add(name);
                cursor.moveToNext();
            }
        }
        // close the db connection
        cursor.close();
        return list;
    }

    @Override
    public synchronized void close() {
        super.close();
        closeDb();
    }
}
*/