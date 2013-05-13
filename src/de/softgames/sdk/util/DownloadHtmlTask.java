package de.softgames.sdk.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;


public class DownloadHtmlTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = DownloadHtmlTask.class.getSimpleName();
    private static HttpResponse response;

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "Downloading data...");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "Downloaded!");
        super.onPostExecute(result);
    }

    @Override
    protected String doInBackground(String... urls) {
        HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
        HttpGet httpget = new HttpGet(urls[0]); // Set the action you want to do
        Log.d(TAG, "requesting url: " + urls[0]);
        String resString = null;
        try {
            response = httpclient.execute(httpget);
            // Execute it
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent(); // Create an InputStream with
                                                  // the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "utf8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Read line by line
                sb.append(line);
            }

            resString = sb.toString(); // Result is here

            inputStream.close(); // Close the stream

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resString;
    }


}
