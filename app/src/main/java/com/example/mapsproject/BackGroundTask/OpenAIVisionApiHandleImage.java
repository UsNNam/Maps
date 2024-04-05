package com.example.mapsproject.BackGroundTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mapsproject.Entity.OpenAIRequest;
import com.example.mapsproject.Entity.OpenAIResponse;
import com.example.mapsproject.R;
import com.example.mapsproject.SearchFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIVisionApiHandleImage extends AsyncTask<String, Void, String> {

    private static final String TAG = "OpenAIApi";
    private SearchFragment searchFragment;
    private Context context;

    private ProgressDialog progressDialog = null;

    public OpenAIVisionApiHandleImage(Context context, SearchFragment fragment) {
        this.searchFragment = fragment;
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String encodeImage = strings[0];
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        // Tạo nội dung cho yêu cầu
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String jsonBody = "{}";
        JSONObject jsonObject = new JSONObject();
        JSONArray messagesArray = new JSONArray();
        JSONObject contentObject = new JSONObject();
        JSONArray contentArray = new JSONArray();
        try {
            jsonObject.put("model", "gpt-4-vision-preview");

            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");

            contentObject.put("type", "text");
            contentObject.put("text", context.getResources().getString(R.string.openai_prompt_image));
            contentArray.put(contentObject);

            contentObject = new JSONObject();
            contentObject.put("type", "image_url");

            JSONObject imageUrlObject = new JSONObject();
            imageUrlObject.put("url", "data:image/jpeg;base64," + encodeImage);
            imageUrlObject.put("detail", "low");

            contentObject.put("image_url", imageUrlObject);
            contentArray.put(contentObject);

            messageObject.put("content", contentArray);
            messagesArray.put(messageObject);

            jsonObject.put("messages", messagesArray);
            jsonObject.put("max_tokens", 50);

            jsonBody = jsonObject.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        String apiOpenAI = context.getResources().getString(R.string.openai_key);


        // Thêm các header vào yêu cầu
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Content-Type", "application/json") // Content-Type
                .addHeader("Authorization", "Bearer " + apiOpenAI) // Authorization
                .post(requestBody)
                .build();

        try {
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e1) {
                Log.e("OpenAIError", e1.getMessage());
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
            } else {
                Log.e(TAG, "ErrorOpenAI: " + response.code());
                Log.e(TAG, "ErrorOpenAI: " + response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        searchFragment.searchLocation.setText(s);
        searchFragment.callApiSearchText();
    }

}
