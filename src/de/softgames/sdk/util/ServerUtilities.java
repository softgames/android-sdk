/**
 * Helper class used to communicate with the server.
 */
package de.softgames.sdk.util;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import de.softgames.sdk.R;


public final class ServerUtilities {

    public static final int MAX_ATTEMPTS = 6;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final String TAG = "ServerUtilities";
    private static Resources res;

    /**
     * Register this account/device pair within the server.
     * 
     * @return whether the registration succeeded or not.
     */
    public static boolean register(final Context context, final String regId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        res = context.getApplicationContext().getResources();
        String serverUrl = SGSettings.SERVER_URL + "/"
                + context.getPackageName();
        Map<String, String> params = new HashMap<String, String>();

        String deviceId = Installation.id(context);

        params.put("device[device_id]", deviceId);
        params.put("device[registration_id]", regId);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        /**
         * Once GCM returns a registration id, we need to register it in the
         * demo server. As the server might be down, we will retry it a couple
         * times.
         */
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                // Registering on server
                post(serverUrl, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
                // registered successfully
                Toast.makeText(context,
                        res.getString(R.string.sg_registered_on_server),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "registered on server!");
                return true;
            } catch (IOException e) {
                /**
                 * Here we are simplifying and retrying on any error; in a real
                 * application, it should retry only on unrecoverable errors
                 * (like HTTP error code 503).
                 */
                Log.e(TAG, "Failed to register on attempt " + i);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }

        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    public static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        res = context.getApplicationContext().getResources();
        String deviceId = Installation.id(context);
        String serverUrl = SGSettings.SERVER_URL + "/"
                + context.getPackageName() + "/"
                + deviceId;

        try {
            delete(serverUrl, null);
            GCMRegistrar.setRegisteredOnServer(context, false);
            Log.d(TAG, "unregistered from server");
            Toast.makeText(
                    context,
                    res.getQuantityText(R.plurals.sg_server_register_error,
                            ServerUtilities.MAX_ATTEMPTS), Toast.LENGTH_SHORT)
                    .show();
        } catch (IOException e) {
            /**
             * At this point the device is unregistered from GCM, but still
             * registered in the server. We could try to unregister again, but
             * it is not necessary: if the server tries to send a message to the
             * device, it will get a "NotRegistered" error message and should
             * unregister the device.
             */
            Log.e(TAG, "An error occurred unregistering from server");
        }
    }

    /**
     * Issue a POST request to the server.
     * 
     * @param endpoint
     *            POST address.
     * @param params
     * @param params
     *            request parameters.
     * 
     * @throws IOException
     *             propagated from POST.
     */
    private static void delete(String endpoint, Map<String, String> params)
            throws IOException {
        request("DELETE", endpoint, params);
    }

    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        request("POST", endpoint, params);
    }

    private static void request(String method, String endpoint,
            Map<String, String> params) throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();

        byte[] bytes = null;
        if (params != null) {
            Iterator<Entry<String, String>> iterator = params.entrySet()
                    .iterator();
            // constructs the POST body using the parameters
            while (iterator.hasNext()) {
                Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString();
            bytes = body.getBytes();
        }

        Log.d(TAG, "Send '" + method + "' to " + url);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            if (params != null) {
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                // post the request
                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
            }
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
