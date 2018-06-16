package uic.hcilab.citymeter;

import org.json.simple.JSONObject;

public interface ApiCallback {
    public void onApiCallback(JSONObject jsonData, String url);
}
