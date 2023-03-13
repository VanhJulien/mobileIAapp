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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class UploadTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        String filePath = new File(params[0]).getAbsolutePath();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody formBody = new FormBody.Builder()
                .add("filePath", filePath)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.17:5000/upload")
                .post(formBody)
                .build();
        try {
            System.out.println(filePath);
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

//    OkHttpClient client = new OkHttpClient();
//
//    RequestBody requestBody = new MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("audio_file", file.getName(),
//                    RequestBody.create(MediaType.parse("audio/wav"), file))
//            .build();
//
//    Request request = new Request.Builder()
//            .url("http://your_api_url.com/upload")
//            .post(requestBody)
//            .build();
//
//    Call call = client.newCall(request);
//
//    call.enqueue(new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//            e.printStackTrace();
//        }
//
//        @Override
//        public void onResponse(Call call, Response response) throws IOException {
//            if (response.isSuccessful()) {
//                String responseData = response.body().string();
//                Log.d(TAG, responseData);
//            } else {
//                Log.e(TAG, "Error: " + response.code() + " " + response.message());
//            }
//        }
//    }
//
//    @Override
//    protected Boolean doInBackground(String... strings) {
//        return null;
//    });
}
