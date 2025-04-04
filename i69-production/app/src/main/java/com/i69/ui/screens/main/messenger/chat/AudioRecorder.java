package com.i69.ui.screens.main.messenger.chat;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Audio Recorder class which uses the MediaRecorder API to record audio.
 */
public class AudioRecorder {
    private MediaRecorder mediaRecorder;

    private String TAG = AudioRecorder.class.getSimpleName();
    private void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }


    void start(String filePath) {
        if (mediaRecorder == null) {
            initMediaRecorder();
        }
        try {
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException illegalStateException) {
            Log.e(TAG, illegalStateException.toString());
        } catch (IOException ioException) {
            Log.e(TAG, ioException.toString());
        } catch (Exception exception) {
            Log.e(TAG, exception.toString());
        }
    }

    void stop() {
        try {
            mediaRecorder.stop();
            destroyMediaRecorder();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void destroyMediaRecorder() {
        mediaRecorder.release();
        mediaRecorder = null;
    }

}
