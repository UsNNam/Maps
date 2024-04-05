package com.example.mapsproject.BackGroundTask;

import android.content.Context;
import android.os.AsyncTask;

import com.example.mapsproject.Entity.OpenAIRequest;
import com.example.mapsproject.Entity.OpenAIResponse;
import com.example.mapsproject.R;
import com.example.mapsproject.SearchFragment;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OpenAIApi extends AsyncTask<String, Void, String> {

    private static final String TAG = "OpenAIApi";
    private SearchFragment searchFragment;
    private Context context;

    public OpenAIApi(Context context, SearchFragment fragment) {
        this.searchFragment = fragment;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String param = strings[0];
        OkHttpClient client = new OkHttpClient();
        // Tạo nội dung cho yêu cầu
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();

        OpenAIRequest.getInstance().addMessage("user", param);

        String jsonBody = gson.toJson(OpenAIRequest.getInstance());
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        String apiOpenAI = context.getResources().getString(R.string.openai_key);


        // Thêm các header vào yêu cầu
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Content-Type", "application/json") // Content-Type
                .addHeader("Authorization", "Bearer "+apiOpenAI) // Authorization
                .post(requestBody)
                .build();

        try {
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (response.isSuccessful()) {
                try {
                    String responseJson = response.body().string();
                    OpenAIResponse openAIResponse = gson.fromJson(responseJson, OpenAIResponse.class);
                    String answer = openAIResponse
                            .getChoices()
                            .get(0)
                            .getMessage()
                            .getContent();

                    OpenAIRequest.getInstance().addMessage("assistant", answer);

                    return answer;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        searchFragment.searchLocation.setText(s);
        searchFragment.callApiSearchText();
    }

}
