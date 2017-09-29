package com.kozak.triangles.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecaptchaVerifier {
    private static final Logger logger = LoggerFactory.getLogger(RecaptchaVerifier.class);

    public static boolean verify(String gRecapResp) throws IOException {
        logger.debug("g-recaptcha-response: {}", gRecapResp);

        HttpsURLConnection conn = createConnection();

        String postParams = String.format("secret=%s&response=%s", Constants.RECAPTCHA_SECRET, gRecapResp);
        sendRequest(conn, postParams);
        String responseStr = getResponse(conn);

        JSONObject rResp = (JSONObject) JSONValue.parse(responseStr);
        logger.debug("Error codes of recaptcha response: {}", rResp.get("error-codes"));

        return new Boolean(rResp.get("success").toString());
    }

    private static HttpsURLConnection createConnection() throws IOException {
        URL url = new URL(Constants.RECAPTHA_URL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        return conn;
    }

    private static void sendRequest(HttpsURLConnection conn, String postParams) throws IOException {
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();
    }

    private static String getResponse(HttpsURLConnection conn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
