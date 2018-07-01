package uic.hcilab.citymeter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;

public class NoiseDetector {

    private AudioRecord mAudioRecord = null;
    private int mBufferSize;

    public NoiseDetector() {
        try {
            if (mAudioRecord == null) {
                mBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
            }
        }
        catch (Exception e){
            Log.i("BT", "Noise detect error");
        }
    }
    public boolean isRecording(){
        int state =  mAudioRecord.getRecordingState();
        return state == AudioRecord.RECORDSTATE_RECORDING;
    }
    public void start(){
        if (mAudioRecord != null) {
            mAudioRecord.startRecording();
            //Log.i("RECORDER", "Started recording : " + isRecording());
        }
    }
    public void stop() {
        try{
            mAudioRecord.stop();
            mAudioRecord.release();
        }
        catch (Exception e){
            Log.i("BT", "recorder close error");
        }
    }
    public short[] measureAmplitude() {
        short[] buffer = new short[mBufferSize];//buffer to save the read sound pressure values
        mAudioRecord.read(buffer, 0, mBufferSize);
        return buffer;
    }
    public ExposureObject noiseLevel(double longitude, double latitude) {
        short[] buffer = measureAmplitude();
        int amplitude = 0;
        for (short window : buffer)//Get the maximum amplitude in each window read (buffer size)
        {
            if (Math.abs(window) > amplitude) {
                amplitude = Math.abs(window);
            }
        }
        double dB = 20 * Math.log10(amplitude);
        //Calculating A-weighted dB
        //Requires further calculations and transformations to get frequency
        //TODO: Frequency
        /*double f = 8000;//Frequency needs further calculations
        double wa1 = 10 * Math.log10((1.562339 * (Math.pow(f, 4))) / ((Math.pow(f, 2) + Math.pow(107.65365, 2)) * (Math.pow(f, 2) + Math.pow(737.86223, 2))));
        double wa2 = 10 * Math.log10((1.562339 * (Math.pow(10, 16) * Math.pow(f, 4))) / (Math.pow(Math.pow(f, 2) + Math.pow(20.598997, 2), 2) * Math.pow(Math.pow(f, 2) + Math.pow(12194.22, 2), 2)));
        double wa = wa1 + wa2;
        double dBA = wa + dB;*/
        SimpleDateFormat s = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        String msg_timestamp = s.format(new Date());
        ExposureObject result ;
        if (dB < Double.MAX_VALUE) {
            result = new ExposureObject(msg_timestamp, dB, longitude, latitude);
        }
        else{

            result = new ExposureObject(msg_timestamp, -1.0, longitude, latitude);
        }
        return result;
    }

}