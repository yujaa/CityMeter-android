package uic.hcilab.citymeter;

import android.graphics.Color;

public class DataAnalysis {

    public int getColorOfSound(double sound){
        if (sound < 50) return Color.argb(100,0,228,0);
        else if (sound < 65) return Color.argb(100,255,255,0);
        else if (sound < 80) return Color.argb(100,255,126,0);
        else if (sound < 95) return Color.argb(100,255,0,0);
        else if (sound < 110) return Color.argb(100,143,63,151);
        else return Color.argb(100,126,0,35);
    }

    public int getColorOfPM(double pm25){
        if (pm25 < 50) return Color.argb(100,0,228,0);
        else if (pm25 < 101) return Color.argb(100,255,255,0);
        else if (pm25 < 151) return Color.argb(100,255,126,0);
        else if (pm25 < 201) return Color.argb(100,255,0,0);
        else if (pm25 < 301) return Color.argb(100,143,63,151);
        else return Color.argb(100,126,0,35);
    }

    public String getTextOfSound(double sound){
        if (sound < 50) return "Good";
        else if (sound < 65) return "Moderate";
        else if (sound < 80) return "Unhealthy for Sensitive Groups";
        else if (sound < 95) return "Unhealthy";
        else if (sound < 110) return "Very Unhealthy";
        else return "Hazardous";
    }

    public String getTextOfPM(double pm25){
        if (pm25 < 50) return "Good";
        else if (pm25 < 101) return "Moderate";
        else if (pm25 < 151) return "Unhealthy for Sensitive Groups";
        else if (pm25 < 201) return "Unhealthy";
        else if (pm25 < 301) return "Very Unhealthy";
        else return "Hazardous";
    }
}
