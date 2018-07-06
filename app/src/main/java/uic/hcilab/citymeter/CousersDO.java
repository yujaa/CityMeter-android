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

}
