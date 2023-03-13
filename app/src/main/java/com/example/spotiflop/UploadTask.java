package com.example.spotiflop;

import android.os.AsyncTask;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        String filePath = params[0];
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "recording.3gp",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(filePath)))
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.17:5000/upload")
                .method("POST", body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // File uploaded successfully
                return true;
            } else {
                // File upload failed
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
            // File uploaded successfully
        } else {
            // File upload failed
        }
    }
}
