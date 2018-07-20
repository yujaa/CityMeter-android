package uic.hcilab.citymeter.DB;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "citymeter-mobilehub-636316455-user-exposure")

public class UserExposureDO {
    private String _userId;
    private String _timestamp;
    private Double _dBA;
    private Double _indoor;
    private Double _latitude;
    private Double _longitude;
    private Double _pm25;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "timestamp")
    @DynamoDBAttribute(attributeName = "timestamp")
    public String getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final String _timestamp) {
        this._timestamp = _timestamp;
    }
    @DynamoDBAttribute(attributeName = "dBA")
    public Double getDBA() {
        return _dBA;
    }

    public void setDBA(final Double _dBA) {
        this._dBA = Double.valueOf(_dBA);
    }
    public void setDBA(final String _dBA) {
        this._dBA = Double.valueOf(_dBA);
    }
    @DynamoDBAttribute(attributeName = "indoor")
    public Double getIndoor() {
        return _indoor;
    }

    public void setIndoor(final Double _indoor) {
        this._indoor = _indoor;
    }
    @DynamoDBAttribute(attributeName = "latitude")
    public Double getLatitude() {
        return _latitude;
    }

    public void setLatitude(final Double _latitude) {
        this._latitude = _latitude;
    }
    @DynamoDBAttribute(attributeName = "longitude")
    public Double getLongitude() {
        return _longitude;
    }

    public void setLongitude(final Double _longitude) {
        this._longitude = _longitude;
    }
    @DynamoDBAttribute(attributeName = "pm25")
    public Double getPm25() {
        return _pm25;
    }

    public void setPm25(final Double _pm25) {
        this._pm25 = _pm25;
    }

}
