package uic.hcilab.citymeter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.musicg.dsp.FastFourierTransform;

public class NoiseDetector {

    private AudioRecord mAudioRecord = null;
    private int mBufferSize;

    public NoiseDetector() {
        try {
            if (mAudioRecord == null) {
                mBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                mBufferSize = 2048;

                mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
            }
        } catch (Exception e) {
            Log.i("BT", "Noise detect error");
        }
    }

    public boolean isRecording() {
        int state = mAudioRecord.getRecordingState();
        return state == AudioRecord.RECORDSTATE_RECORDING;
    }

    public void start() {
        if (mAudioRecord != null) {
            mAudioRecord.startRecording();
        }
    }

    public void stop() {
        try {
            mAudioRecord.stop();
            mAudioRecord.release();
        } catch (Exception e) {
            Log.i("BT", "recorder close error");
        }
    }

    public byte[] measureAmplitude() {
        byte[] buffer = new byte[mBufferSize];//buffer to save the read sound pressure values
        mAudioRecord.read(buffer, 0, mBufferSize);
        return buffer;
    }

    public ExposureObject noiseLevel(double longitude, double latitude) {
        byte[] buffer = measureAmplitude();
        double amplitude = 0;
        double[] amps = new double[512];
        int i = 0;
        for (short window : buffer)//Get the maximum amplitude in each window read (buffer size)
        {
            if (i < 512) {
                amps[i] = window;
                i++;
            }
            if (Math.abs(window) > amplitude) {
                amplitude = Math.abs(window);
            }
        }
        double dB = 20 * Math.log10(amplitude);
        //Calculating A-weighted dB
        //FFT transform of amplitude give intensity magnitudes
        FastFourierTransform fft = new FastFourierTransform();
        double[] magnitudes = fft.getMagnitudes(amps);
        //To get max frequency, find max magnitude
        double max_magnitude = magnitudes[0];
        int ctr = 0;
        for (i = 1; i < magnitudes.length; ++i) {
            if (magnitudes[i] > max_magnitude) {
                max_magnitude = magnitudes[i];
                ctr = i;
            }
        }
        double f = ctr * 44100 / magnitudes.length;
        double a_weight = getFrequencyWeight(f);
        double dBA = 10 * (Math.log10(Math.pow((amplitude),2))) - a_weight;
        SimpleDateFormat s = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        String msg_timestamp = s.format(new Date());
        ExposureObject result;
        if (dBA < 100 && f != 0) {
            result = new ExposureObject(msg_timestamp, dB, longitude, latitude, -1.0);
        } else {

            result = new ExposureObject(msg_timestamp, -1.0, longitude, latitude, -1.0);
        }
        return result;
    }

    private double getFrequencyWeight(double frequency) {
        if (frequency > 0 && frequency <=6.3 )
            return -85.4;
        else if(frequency > 6.3 && frequency <=8 )
            return -77.8;
        else if(frequency > 8 && frequency <= 10 )
           return -70.4;
        else if(frequency > 10 && frequency <=12.5 )
            return -63.4;
        else if(frequency > 12.5 && frequency <=16 )
            return -56.7;
        else if(frequency > 16 && frequency <=20 )
            return -50.5;
        else if(frequency > 20 && frequency <=25 )
            return -44.7;
        else if(frequency > 25 && frequency <=31.5 )
            return -39.4;
        else if(frequency > 31.5 && frequency <=40 )
            return -34.6;
        else if(frequency > 40 && frequency <=50 )
            return -30.2;
        else if(frequency > 50 && frequency <=63 )
            return -26.2;
        else if(frequency > 63 && frequency <=80 )
            return -22.5;
        else if(frequency > 80 && frequency <=100 )
            return -19.1;
        else if(frequency > 100 && frequency <=125 )
            return -16.1;
        else if(frequency > 125 && frequency <=160 )
            return -13.4;
        else if(frequency > 160 && frequency <=200 )
            return -10.9;
        else if(frequency > 200 && frequency <=250 )
            return -8.6;
        else if(frequency > 250 && frequency <=315 )
            return -6.6;
        else if(frequency > 315 && frequency <=400 )
            return -4.8;
        else if(frequency > 400 && frequency <=500 )
            return -3.2;
        else if(frequency > 500 && frequency <=630 )
            return -1.9;
        else if(frequency > 630 && frequency <=800 )
            return -0.8;
        else if(frequency > 800 && frequency <=1000 )
            return 0.0;
        else if(frequency > 1000 && frequency <=1250 )
            return 0.6;
        else if(frequency > 1250 && frequency <=1600 )
            return 1.0;
        else if(frequency > 1600 && frequency <=2000 )
            return 1.2;
        else if(frequency > 2000 && frequency <=2500 )
            return 1.3;
        else if(frequency > 2500 && frequency <=3150 )
            return 1.2;
        else if(frequency > 3150 && frequency <=4000 )
            return 1.0;
        else if(frequency > 4000 && frequency <=5000 )
            return 0.5;
        else if(frequency > 5000 && frequency <=6300 )
            return -0.1;
        else if(frequency > 6300 && frequency <=8000 )
            return -1.1;
        else if(frequency > 8000 && frequency <=10000 )
            return -2.5;
        else if(frequency > 10000 && frequency <=12500 )
            return -4.3;
        else if(frequency > 12500 && frequency <=16000 )
            return -6.6;
        else if(frequency > 16000 && frequency <=20000 )
            return -9.3;
        else
            return -100.0;
    }
}