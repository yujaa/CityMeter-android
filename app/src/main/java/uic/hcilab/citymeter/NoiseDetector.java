package uic.hcilab.citymeter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import com.musicg.api.DetectionApi;
import com.musicg.dsp.FastFourierTransform;
import com.musicg.math.rank.ArrayRankDouble;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;
import com.musicg.wave.extension.Spectrogram;

public class NoiseDetector {

    private AudioRecord mAudioRecord = null;
    private int mBufferSize;
    protected WaveHeader waveHeader;
    protected int fftSampleSize;
    protected int numFrequencyUnit;
    protected double unitFrequency;

    public NoiseDetector() {
        try {
            if (mAudioRecord == null) {
                mBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                mBufferSize= 2048;


                mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
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
    public byte[] measureAmplitude() {
        byte[] buffer = new byte[mBufferSize];//buffer to save the read sound pressure values
        mAudioRecord.read(buffer, 0, mBufferSize);
        return buffer;
    }
    public ExposureObject noiseLevel(double longitude, double latitude) {
        byte[] buffer = measureAmplitude();
        double amplitude = 0;
        double [] amps = new double[512];
        int i = 0;
        for (short window : buffer)//Get the maximum amplitude in each window read (buffer size)
        {
            if ( i < 511) {
                amps[i] = window;
                i++;
            }
            if (Math.abs(window) > amplitude) {
                amplitude = Math.abs(window);
            }
        }
        double dB = 20 * Math.log10(amplitude);
        //Calculating A-weighted dB
        FastFourierTransform fft = new FastFourierTransform();
        double [] magnitudes = fft.getMagnitudes(amps);
        double max_magnitude = magnitudes[0];
        int ctr = 0;
        for (i = 1; i < magnitudes.length; ++i)
        {
            if (magnitudes[i] > max_magnitude)
            {
                max_magnitude = magnitudes[i];
                ctr = i;
            }
        }
        double f = ctr * 44100 / magnitudes.length;
        Log.i("nina", "f intensity " +f);
        double wa1 = 10 * Math.log10((1.562339 * (Math.pow(f, 4))) / ((Math.pow(f, 2) + Math.pow(107.65365, 2)) * (Math.pow(f, 2) + Math.pow(737.86223, 2))));
        double wa2 = 10 * Math.log10((2.242881 * Math.pow(10, 16) * Math.pow(f, 4))/( (Math.pow(( Math.pow(f, 2) + Math.pow(20.598997, 2)), 2) )* (Math.pow((Math.pow(f, 2) + Math.pow(12194.22, 2)), 2))));
        double wa = wa1 + wa2;
        double dBA = wa + dB;
        Log.i("nina", "dba = " + dBA);
        SimpleDateFormat s = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        String msg_timestamp = s.format(new Date());
        ExposureObject result ;
        if (dBA < Double.MAX_VALUE) {
            result = new ExposureObject(msg_timestamp, dB, longitude, latitude);
        }
        else{

            result = new ExposureObject(msg_timestamp, -1.0, longitude, latitude);
        }
        return result;
    }
    public double getFrequency(byte[] audioBytes) {
        try {
            int numSamples = mBufferSize/2;
            if (numSamples > 0 && Integer.bitCount(numSamples) == 1) {
                this.fftSampleSize = numSamples;
                this.numFrequencyUnit = this.fftSampleSize / 2;
                this.unitFrequency = (double) numSamples / 2.0D / (double) this.numFrequencyUnit;
                this.waveHeader = new WaveHeader();
                this.waveHeader.setSampleRate(44100);
                this.waveHeader.setAudioFormat(2);
                Wave wave = new Wave(this.waveHeader, audioBytes);
                Spectrogram spectrogram = wave.getSpectrogram(this.fftSampleSize, 0);
                double[][] spectrogramData = spectrogram.getAbsoluteSpectrogramData();
                double[] spectrum = spectrogramData[0];
                ArrayRankDouble arrayRankDouble = new ArrayRankDouble();
                double robustFrequency = (double) arrayRankDouble.getMaxValueIndex(spectrum) * this.unitFrequency;
                return robustFrequency;

            } else {
                Log.i("nina", "The sample size must be a power of 2");
            }
        } catch (Exception e) {
            Log.i("nina", "error : " + e.toString());

        }
        return 0.0;
    }
}


