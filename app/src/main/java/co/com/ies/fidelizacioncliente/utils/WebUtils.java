package co.com.ies.fidelizacioncliente.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by User on 25/03/2016.
 */
public class WebUtils {

    private static final String UTF_8 = "UTF-8";
    private static final String SIMBOLO_IGUAL = "=";
    private static final int TIME_OUT = 20000;

    /**
     * Verificar si el dispositivo esta conectado a internet
     *
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Deshabilitar la necesidad de que una solicitud https deba validar su identidad por ssl
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static void disableSslVerificaction() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts
                = new TrustManager[]{new javax.net.ssl.X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {

            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {

            }
        }};

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection
                .setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("soporte", "soporte135".toCharArray());
            }
        });

    }

    /**
     * Crear solicitud para una conexion http
     *
     * @param targetURL
     * @param cookies
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static HttpURLConnection createHttpUrlConnection(String targetURL, String cookies) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        URL url = new URL(targetURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Cookie", cookies);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setChunkedStreamingMode(0);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(TIME_OUT);//todo vigilar este param

        return connection;
    }


    public static JSONObject getJsonResponse(HttpURLConnection httpURLConnection) throws IOException, JSONException {

        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        JSONObject jsonResponse = new JSONObject(sb.toString());
        return jsonResponse;
    }

    /**
     * Crear solicitud para una conexion https
     *
     * @param targetURL
     * @param cookies
     * @return
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static HttpsURLConnection createHttpsConnection(String targetURL, String cookies) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        Log.i("CONNECTION",targetURL);
        disableSslVerificaction();
        URL url = new URL(targetURL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        if (cookies != null) {
            connection.setRequestProperty("Cookie", cookies);
        }
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setConnectTimeout(TIME_OUT);//todo vigilar este param
        connection.setUseCaches(false);

        return connection;
    }

    /**
     * Crear solicitud para una conexion https
     *
     * @param targetURL
     * @param cookies
     * @return
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static HttpsURLConnection createHttpsConnectionJson(String targetURL, String cookies) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        Log.i("CONNECTION JSON",targetURL);
        disableSslVerificaction();
        URL url = new URL(targetURL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        if (cookies != null) {
            connection.setRequestProperty("Cookie", cookies);
        }
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setConnectTimeout(TIME_OUT);//todo vigilar este param
        connection.setUseCaches(false);

        return connection;
    }

    public static String loginService(String url, String usr, String pass) throws NoSuchAlgorithmException, IOException, KeyManagementException {

        String targetUrl = AppConstants.WebServs.PROTOCOL + url + AppConstants.WebServs.LOGIN;
        StringBuilder stringBuilder = new StringBuilder();
        addParam(stringBuilder, AppConstants.WebParams.USER, usr);
        addParam(stringBuilder, AppConstants.WebParams.PASS, pass);

        HttpsURLConnection connection = WebUtils.createHttpsConnection(targetUrl + "?" + stringBuilder.toString(), null);

        String cookie = connection.getHeaderField(AppConstants.WebParams.SET_COOKIE);
        if (cookie != null) {
            String[] responseCookie = cookie.split("; ");
            cookie = responseCookie[0];
        }
        return cookie;
    }


    /**
     * Este método permite agregar un parámetro a la petición
     *
     * @param key
     * @param value
     * @throws UnsupportedEncodingException
     */
    public static void addParam(StringBuilder data, String key, String value) {

        try {
            if (data != null) {
                if (data.length() > 0) {
                    data.append("\u0026");
                }
                data.append(URLEncoder.encode(key, UTF_8));
                data.append(SIMBOLO_IGUAL);
                data.append(URLEncoder.encode(value, UTF_8));
            }
        } catch (UnsupportedEncodingException e) {
            MsgUtils.handleException(e);
        }
    }
}
