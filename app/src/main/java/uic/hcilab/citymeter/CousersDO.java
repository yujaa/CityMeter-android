package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "citymeter-mobilehub-636316455-cousers")

public class CousersDO {
    private String _uid;
    private String _cuid;
    private Double _canSeeActivities;
    private Double _canSeeCogTest;
    private Double _canSeeLocation;

    @DynamoDBHashKey(attributeName = "uid")
    @DynamoDBAttribute(attributeName = "uid")
    public String getUid() {
        return _uid;
    }

    public void setUid(final String _uid) {
        this._uid = _uid;
    }
    @DynamoDBRangeKey(attributeName = "cuid")
    @DynamoDBAttribute(attributeName = "cuid")
    public String getCuid() {
        return _cuid;
    }

    public void setCuid(final String _cuid) {
        this._cuid = _cuid;
    }
    @DynamoDBAttribute(attributeName = "canSeeActivities")
    public Double getCanSeeActivities() {
        return _canSeeActivities;
    }

    public void setCanSeeActivities(final Double _canSeeActivities) {
        this._canSeeActivities = _canSeeActivities;
    }
    @DynamoDBAttribute(attributeName = "canSeeCogTest")
    public Double getCanSeeCogTest() {
        return _canSeeCogTest;
    }

    public void setCanSeeCogTest(final Double _canSeeCogTest) {
        this._canSeeCogTest = _canSeeCogTest;
    }
    @DynamoDBAttribute(attributeName = "canSeeLocation")
    public Double getCanSeeLocation() {
        return _canSeeLocation;
    }

    public void setCanSeeLocation(final Double _canSeeLocation) {
        this._canSeeLocation = _canSeeLocation;
    }

}
