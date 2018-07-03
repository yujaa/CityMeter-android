package uic.hcilab.citymeter;

public class ExposureObject {
    public String timestamp;
    public Double reading;
    public Double longitude;
    public Double latitude;
    public Double indoor;

    public ExposureObject(String ts, Double vl, Double lng, Double ltd, Double ind){
        timestamp = ts;
        reading = vl;
        longitude = lng;
        latitude = ltd;
        indoor = ind;
    }
}
