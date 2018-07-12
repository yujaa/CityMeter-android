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

@DynamoDBTable(tableName = "citymeter-mobilehub-636316455-cognitive_test")

public class CognitiveTestDO {
    private String _uid;
    private String _balance;
    private String _balanceCause;
    private String _blood;
    private String _circleLinesFile;
    private String _copyPictureFile;
    private String _countries12;
    private String _difficultyActivities;
    private String _done;
    private String _drawClockFile;
    private String _groceriesChange;
    private String _majorStroke;
    private String _memoryProblem;
    private String _namePictureHarp;
    private String _namePictureRhino;
    private String _personality;
    private String _quarters;
    private String _sadDepressed;
    private String _todayDate;
    private String _trianglesFile;
    private String _tulip;

    @DynamoDBHashKey(attributeName = "uid")
    @DynamoDBAttribute(attributeName = "uid")
    public String getUid() {
        return _uid;
    }

    public void setUid(final String _uid) {
        this._uid = _uid;
    }
    @DynamoDBAttribute(attributeName = "balance")
    public String getBalance() {
        return _balance;
    }

    public void setBalance(final String _balance) {
        this._balance = _balance;
    }
    @DynamoDBAttribute(attributeName = "balance_cause")
    public String getBalanceCause() {
        return _balanceCause;
    }

    public void setBalanceCause(final String _balanceCause) {
        this._balanceCause = _balanceCause;
    }
    @DynamoDBAttribute(attributeName = "blood")
    public String getBlood() {
        return _blood;
    }

    public void setBlood(final String _blood) {
        this._blood = _blood;
    }
    @DynamoDBAttribute(attributeName = "circle_lines_file")
    public String getCircleLinesFile() {
        return _circleLinesFile;
    }

    public void setCircleLinesFile(final String _circleLinesFile) {
        this._circleLinesFile = _circleLinesFile;
    }
    @DynamoDBAttribute(attributeName = "copy_picture_file")
    public String getCopyPictureFile() {
        return _copyPictureFile;
    }

    public void setCopyPictureFile(final String _copyPictureFile) {
        this._copyPictureFile = _copyPictureFile;
    }
    @DynamoDBAttribute(attributeName = "countries_12")
    public String getCountries12() {
        return _countries12;
    }

    public void setCountries12(final String _countries12) {
        this._countries12 = _countries12;
    }
    @DynamoDBAttribute(attributeName = "difficulty_activities")
    public String getDifficultyActivities() {
        return _difficultyActivities;
    }

    public void setDifficultyActivities(final String _difficultyActivities) {
        this._difficultyActivities = _difficultyActivities;
    }
    @DynamoDBAttribute(attributeName = "done")
    public String getDone() {
        return _done;
    }

    public void setDone(final String _done) {
        this._done = _done;
    }
    @DynamoDBAttribute(attributeName = "draw_clock_file")
    public String getDrawClockFile() {
        return _drawClockFile;
    }

    public void setDrawClockFile(final String _drawClockFile) {
        this._drawClockFile = _drawClockFile;
    }
    @DynamoDBAttribute(attributeName = "groceries_change")
    public String getGroceriesChange() {
        return _groceriesChange;
    }

    public void setGroceriesChange(final String _groceriesChange) {
        this._groceriesChange = _groceriesChange;
    }
    @DynamoDBAttribute(attributeName = "major_stroke")
    public String getMajorStroke() {
        return _majorStroke;
    }

    public void setMajorStroke(final String _majorStroke) {
        this._majorStroke = _majorStroke;
    }
    @DynamoDBAttribute(attributeName = "memory_problem")
    public String getMemoryProblem() {
        return _memoryProblem;
    }

    public void setMemoryProblem(final String _memoryProblem) {
        this._memoryProblem = _memoryProblem;
    }
    @DynamoDBAttribute(attributeName = "name_picture_harp")
    public String getNamePictureHarp() {
        return _namePictureHarp;
    }

    public void setNamePictureHarp(final String _namePictureHarp) {
        this._namePictureHarp = _namePictureHarp;
    }
    @DynamoDBAttribute(attributeName = "name_picture_rhino")
    public String getNamePictureRhino() {
        return _namePictureRhino;
    }

    public void setNamePictureRhino(final String _namePictureRhino) {
        this._namePictureRhino = _namePictureRhino;
    }
    @DynamoDBAttribute(attributeName = "personality")
    public String getPersonality() {
        return _personality;
    }

    public void setPersonality(final String _personality) {
        this._personality = _personality;
    }
    @DynamoDBAttribute(attributeName = "quarters")
    public String getQuarters() {
        return _quarters;
    }

    public void setQuarters(final String _quarters) {
        this._quarters = _quarters;
    }
    @DynamoDBAttribute(attributeName = "sad_depressed")
    public String getSadDepressed() {
        return _sadDepressed;
    }

    public void setSadDepressed(final String _sadDepressed) {
        this._sadDepressed = _sadDepressed;
    }
    @DynamoDBAttribute(attributeName = "today_date")
    public String getTodayDate() {
        return _todayDate;
    }

    public void setTodayDate(final String _todayDate) {
        this._todayDate = _todayDate;
    }
    @DynamoDBAttribute(attributeName = "triangles_file")
    public String getTrianglesFile() {
        return _trianglesFile;
    }

    public void setTrianglesFile(final String _trianglesFile) {
        this._trianglesFile = _trianglesFile;
    }
    @DynamoDBAttribute(attributeName = "tulip")
    public String getTulip() {
        return _tulip;
    }

    public void setTulip(final String _tulip) {
        this._tulip = _tulip;
    }

}
