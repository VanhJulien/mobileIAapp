package com.example.spotiflop;

import android.os.AsyncTask;
import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        String filePath = new File(params[0]).getAbsolutePath();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody formBody = new FormBody.Builder()
                .add("filePath", filePath)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.180.70:5000/upload")
                .post(formBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // File path sent successfully
                return true;
            } else {
                // File path sending failed
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            // File path sent successfully
        } else {
            // File path sending failed
        }
    }
}
