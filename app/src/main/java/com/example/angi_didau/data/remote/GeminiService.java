package com.example.angi_didau.data.remote;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A lightweight service to call Google Gemini API using REST.
 */
public class GeminiService {

    private static final String TAG = "GeminiService";
    // TODO: Replace with the actual API Key or prompt user to add it.
    private static final String API_KEY = com.example.angi_didau.BuildConfig.GEMINI_API_KEY;
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + API_KEY;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface GeminiCallback {
        void onSuccess(String jsonResult);
        void onError(String errorMessage);
    }

    /**
     * Sends a prompt to Gemini and expects a JSON response.
     */
    public static void generateItinerary(String systemInstructions, String promptText, GeminiCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Build JSON body
                // {
                //   "system_instruction": { "parts": [{ "text": "..." }] },
                //   "contents": [{ "parts": [{ "text": "..." }] }]
                // }
                JSONObject body = new JSONObject();

                // Combine systemInstructions into prompt for gemini-pro compatibility
                String finalPrompt = promptText;
                if (systemInstructions != null && !systemInstructions.isEmpty()) {
                    finalPrompt = "System Instruction: " + systemInstructions + "\n\nUser Request: " + promptText;
                }

                JSONArray contents = new JSONArray();
                JSONObject content = new JSONObject();
                JSONArray parts = new JSONArray();
                JSONObject part = new JSONObject();
                part.put("text", finalPrompt);
                parts.put(part);
                content.put("parts", parts);
                contents.put(content);

                body.put("contents", contents);

                // Write payload
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int statusCode = conn.getResponseCode();
                if (statusCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder responseStr = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        responseStr.append(responseLine.trim());
                    }

                    // Parse the Gemini standard response format
                    JSONObject root = new JSONObject(responseStr.toString());
                    JSONArray candidates = root.getJSONArray("candidates");
                    JSONObject firstCandidate = candidates.getJSONObject(0);
                    JSONObject contentObj = firstCandidate.getJSONObject("content");
                    JSONArray responseParts = contentObj.getJSONArray("parts");
                    String text = responseParts.getJSONObject(0).getString("text");

                    // Clean markdown formatting if returned (e.g. ```json ... ```)
                    if (text.startsWith("```json")) {
                        text = text.substring(7);
                    }
                    if (text.startsWith("```")) {
                        text = text.substring(3);
                    }
                    if (text.endsWith("```")) {
                        text = text.substring(0, text.length() - 3);
                    }

                    String finalText = text.trim();
                    mainHandler.post(() -> callback.onSuccess(finalText));
                } else {
                    // Read error stream
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder errorStr = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorStr.append(responseLine.trim());
                    }
                    Log.e(TAG, "API Error: " + errorStr.toString());
                    mainHandler.post(() -> callback.onError("Lỗi API: " + statusCode));
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception during Gemini API call", e);
                mainHandler.post(() -> callback.onError("Lỗi mạng hoặc xử lý: " + e.getMessage()));
            }
        });
    }
}
