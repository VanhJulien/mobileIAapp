package com.example.spotiflop;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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


public class UploadTask extends AsyncTask<String, Void, String> {

    private TextView genreTextView;

    public UploadTask(TextView genreTextView) {
        this.genreTextView = genreTextView;
    }

    @Override
    protected String doInBackground(String... params) {

        String filePath = new File(params[0]).getAbsolutePath();
        File file = new File(filePath);
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        System.out.println("file : " + file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audio_file", file.getName(),
                        RequestBody.create(MediaType.parse("audio/wav"), file))
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.17:5000/upload")
                .post(requestBody)
                .build();

        try {
            System.out.println(filePath);
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // File path sent successfull
                String jsonResponse = response.body().string();
                JSONObject json = new JSONObject(jsonResponse);
                String genre = json.getString("genre");
                System.out.println("Le genre est : " + genre);
                return genre;
            } else {
                String genre = "Pas de style trouvé";
                // File path sending failed
                return genre;
            }
        } catch (IOException e) {
            e.printStackTrace();
            String genre = "error";
            return genre;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            genreTextView.setText("Le genre est : " + response);
            // File path sent successfully
        } else {
            genreTextView.setText("Genre non trouvé");
            // File path sending failed
        }
    }
}
