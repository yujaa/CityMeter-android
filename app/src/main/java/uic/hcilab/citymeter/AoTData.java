package uic.hcilab.citymeter;

import android.content.Context;
import android.os.AsyncTask;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class AoTData extends AsyncTask<String, Void, JSONObject> {
    private Context mContext;
    private Exception exception;
    private ApiCallback mCallback;

    public AoTData(Context context){
        this.mContext = context;
        this.mCallback = (ApiCallback) context;

    }
    @Override
    protected JSONObject doInBackground(String... urls) {

        JSONParser jParser= new JSONParser();
        JSONObject aotData = null;
        try {
            aotData = (JSONObject) jParser.parse("Null");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {

            URL url = new URL("http://34.229.219.45:9000/api/info/nodes");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                try {
                    aotData = (JSONObject) jParser.parse(output);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();

        }
        return aotData;
    }

    protected void onPostExecute(JSONObject result) {
        // TODO: check this.exception
        // TODO: do something with the feed
        mCallback.onApiCallback(result);
    }
}

