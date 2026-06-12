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
 * A lightweight, optimized service to call Google Gemini API using REST.
 */
public class GeminiService {

    private static final String TAG = "GeminiService";

    // Lấy API Key từ BuildConfig của bạn
    private static final String API_KEY = com.example.angi_didau.BuildConfig.GEMINI_API_KEY;

    // Trả lại model gemini-flash-latest theo đúng phiên bản đã chạy thành công trước đó
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + API_KEY;
    // Đổi sang CachedThreadPool để hỗ trợ xử lý đa luồng song song, tránh nghẽn khi lặp dữ liệu từ Firebase
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface GeminiCallback {
        void onSuccess(String jsonResult);
        void onError(String errorMessage);
    }

    /**
     * Gửi yêu cầu tới Gemini và nhận về chuỗi JSON lộ trình sạch.
     *
     * @param systemInstructions Chỉ thị vai trò cho AI (Ví dụ: "Bạn là chuyên gia du lịch...")
     * @param promptText         Dữ liệu đã lọc sạch lấy từ Firebase (Ví dụ: "Lộ trình đi Hà Nội 2 ngày...")
     * @param callback           Callback trả kết quả về Main Thread để hiển thị UI
     */
    public static void generateItinerary(String systemInstructions, String promptText, GeminiCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                // Đặt thành 0 (vô hạn) để chờ AI xử lý xong khối dữ liệu lớn
                conn.setConnectTimeout(60000); 
                conn.setReadTimeout(0);
                conn.setDoOutput(true);

                // Khởi tạo Body Request JSON theo cấu trúc chuẩn của Gemini API
                JSONObject body = new JSONObject();

                // 1. Cấu hình System Instruction chuẩn nếu có truyền vào
                if (systemInstructions != null && !systemInstructions.isEmpty()) {
                    JSONObject sysInstructionObj = new JSONObject();
                    JSONArray partsArray = new JSONArray();
                    JSONObject partText = new JSONObject();
                    partText.put("text", systemInstructions);
                    partsArray.put(partText);
                    sysInstructionObj.put("parts", partsArray);
                    body.put("system_instruction", sysInstructionObj);
                }

                // 2. Cấu hình nội dung Prompt (Contents)
                JSONArray contents = new JSONArray();
                JSONObject content = new JSONObject();
                JSONArray parts = new JSONArray();
                JSONObject part = new JSONObject();
                part.put("text", promptText);
                parts.put(part);
                content.put("parts", parts);
                contents.put(content);
                body.put("contents", contents);

                // 3. TỐI ƯU TỐC ĐỘ: Ép Gemini xuất thẳng dữ liệu JSON thô, bỏ qua Markdown (```json ... ```)
                JSONObject generationConfig = new JSONObject();
                generationConfig.put("response_mime_type", "application/json");
                body.put("generation_config", generationConfig);

                // Gửi Payload dữ liệu lên Server
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int statusCode = conn.getResponseCode();
                // Retry on 503 (Service Unavailable) up to 3 attempts
                int attempts = 0;
                while (true) {
                    attempts++;
                    int statusCode = conn.getResponseCode();
                    if (statusCode == 200) {
                        // success handling (existing code)
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8), 8192);
                        StringBuilder responseStr = new StringBuilder();
                        char[] buffer = new char[4096];
                        int bytesRead;
                        while ((bytesRead = br.read(buffer)) != -1) {
                            responseStr.append(buffer, 0, bytesRead);
                        }
                        br.close();

                        JSONObject root = new JSONObject(responseStr.toString());
                        String text = root.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");
                        String finalText = text.trim();
                        Log.d(TAG, "--- GEMINI CLEAN RESPONSE ---");
                        Log.d(TAG, finalText);
                        mainHandler.post(() -> callback.onSuccess(finalText));
                        break; // exit loop
                    } else if (statusCode == 503 && attempts < 3) {
                        // wait and retry
                        Log.w(TAG, "Gemini API 503, retry attempt " + attempts);
                        Thread.sleep(2000);
                        // re-open connection for retry
                        conn.disconnect();
                        conn = (HttpURLConnection) new URL(API_URL).openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setConnectTimeout(60000);
                        conn.setReadTimeout(0);
                        conn.setDoOutput(true);
                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                        continue;
                    } else {
                        // error handling (existing code)
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                        StringBuilder errorStr = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            errorStr.append(line.trim());
                        }
                        br.close();
                        Log.e(TAG, "API Error Status: " + statusCode + " | Details: " + errorStr.toString());
                        mainHandler.post(() -> callback.onError("Lỗi máy chủ API: " + statusCode));
                        break;
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception during Gemini API call", e);
                mainHandler.post(() -> callback.onError("Lỗi kết nối hoặc xử lý: " + e.getMessage()));
            }
        });
    }
}