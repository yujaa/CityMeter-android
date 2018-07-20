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

@DynamoDBTable(tableName = "citymeter-mobilehub-636316455-users")

public class UsersDO {
    private String _userID;
    private String _dob;
    private String _education;
    private String _ethnicity;
    private String _gender;
    private Double _isCoUser;
    private String _name;

    @DynamoDBHashKey(attributeName = "userID")
    @DynamoDBAttribute(attributeName = "userID")
    public String getUserID() {
        return _userID;
    }

    public void setUserID(final String _userID) {
        this._userID = _userID;
    }
    @DynamoDBAttribute(attributeName = "dob")
    public String getDob() {
        return _dob;
    }

    public void setDob(final String _dob) {
        this._dob = _dob;
    }
    @DynamoDBAttribute(attributeName = "education")
    public String getEducation() {
        return _education;
    }

    public void setEducation(final String _education) {
        this._education = _education;
    }
    @DynamoDBAttribute(attributeName = "ethnicity")
    public String getEthnicity() {
        return _ethnicity;
    }

    public void setEthnicity(final String _ethnicity) {
        this._ethnicity = _ethnicity;
    }
    @DynamoDBAttribute(attributeName = "gender")
    public String getGender() {
        return _gender;
    }

    public void setGender(final String _gender) {
        this._gender = _gender;
    }
    @DynamoDBAttribute(attributeName = "isCoUser")
    public Double getIsCoUser() {
        return _isCoUser;
    }

    public void setIsCoUser(final Double _isCoUser) {
        this._isCoUser = _isCoUser;
    }
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }

}
