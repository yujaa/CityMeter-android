package uic.hcilab.citymeter;

public class ExposureObject {
    public String timestamp;
    public Double reading;
    public Double longitude;
    public Double latitude;

    public ExposureObject(String ts, Double vl, Double lng, Double ltd){
        timestamp = ts;
        reading = vl;
        longitude = lng;
        latitude = ltd;

    }
}
