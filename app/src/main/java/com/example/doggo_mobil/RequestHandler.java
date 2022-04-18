package com.example.doggo_mobil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHandler {
    public static Response get(String url) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        return getResponse(conn);
    }
    public static Response getBearer(String url, String token) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return getResponse(conn);
    }
    public static Response post(String url, String data) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestMethod("POST");
        addRequestBody(conn, data);
        return getResponse(conn);
    }
    public static Response put(String url, String data) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestMethod("PUT");
        addRequestBody(conn, data);
        return getResponse(conn);
    }
    public static Response delete(String url) throws IOException {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestMethod("DELETE");
        return getResponse(conn);
    }

    private static void addRequestBody(HttpURLConnection conn, String data) throws IOException{
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(data);
        writer.flush();
        writer.close();
        os.close();
    }

    private static HttpURLConnection setupConnection(String url) throws IOException{
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        return conn;
    }

    private static Response getResponse(HttpURLConnection conn) throws IOException{
        int responseCode = conn.getResponseCode();
        InputStream is;
        if (responseCode < 400){
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String sor = br.readLine();
        while (sor != null){
            builder.append(sor);
            sor = br.readLine();
        }
        br.close();
        is.close();
        return new Response(responseCode, builder.toString());
    }
}
