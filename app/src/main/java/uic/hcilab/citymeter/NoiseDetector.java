package uic.hcilab.citymeter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoiseDetector {

    private AudioRecord mAudioRecord = null;
    private int mBufferSize;

    public void noiseDetect() {
        mBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
        mAudioRecord.startRecording();
        Log.i("RECORDER", "Started recording");
        //double val = noiseLevel();
       // Log.i("RECORDER", "dB = " + val);
       // return val;
    }

    public void stop() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
        }
    }

    public String noiseLevel(double longitude, double latitude) {
        short[] buffer = new short[mBufferSize];//buffer to save the read sound pressure values
        mAudioRecord.read(buffer, 0, mBufferSize);
        int amplitude = 0;
        for (short window : buffer)//Get the maximum amplitude in each window read (buffer size)
        {
            if (Math.abs(window) > amplitude)
            {
                amplitude = Math.abs(window);
            }
        }
        double dB = 20 * Math.log10(amplitude);
        //Calculating A-weighted dB
        //Requires further calculations and transformations to get frequency
        //TODO: Frequency
        double f = 8000;//Frequency needs further calculations
        double wa1 = 10* Math.log10((1.562339*(Math.pow(f,4)))/((Math.pow(f,2)+ Math.pow(107.65365,2))*(Math.pow(f,2)+ Math.pow(737.86223,2))));
        double wa2 = 10* Math.log10((1.562339*(Math.pow(10,16)* Math.pow(f,4)))/(Math.pow(Math.pow(f,2)+ Math.pow(20.598997,2),2)* Math.pow(Math.pow(f, 2) + Math.pow(12194.22, 2),2)));
        double wa = wa1 + wa2;
        double dBA = wa + dB;
        SimpleDateFormat s = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        String msg_timestamp = s.format(new Date());
        String result = "[{'latitude': " + latitude + ", 'pm2.5': " + dB + ", 'longitude': " + longitude + ", 'timestamp': '" + msg_timestamp + "'}]";
        return result;
    }

}