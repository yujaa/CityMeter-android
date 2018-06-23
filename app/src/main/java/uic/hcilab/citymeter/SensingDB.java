package uic.hcilab.citymeter;

public class SensingDB {
    public static final String TABLE_NAME = "sensing_data";

    public static final String COLUMN_ENTRY = "entry";
    public static final String COLUMN_IS_WRITTEN = "is_written";

    private String entry;
    private int isWritten;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ENTRY + " TEXT PRIMARY KEY,"
                    + COLUMN_IS_WRITTEN + " BIT"
                    + ")";

    public SensingDB() {
    }

    public SensingDB( String dataLine, int isWritten) {
        this.entry = dataLine;
        this.isWritten = isWritten;
    }

    public String getEntry() {
        return entry;
    }

    public int getIsWritten(){
        return isWritten;
    }

    public void setEntry(String dataline) {
        this.entry = dataline;
    }

    public void setIsWritten(int isWritten){
        this.isWritten = isWritten;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

}
