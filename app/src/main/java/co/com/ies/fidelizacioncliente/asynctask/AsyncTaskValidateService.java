package co.com.ies.fidelizacioncliente.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.AppConstants.Prefs;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebParams;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebResult;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebServs;
import co.com.ies.fidelizacioncliente.utils.FileUtils;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;
import co.com.ies.fidelizacioncliente.web.CommonServices;

;

/**
 * Clase usada para validar servicio fidelización disponible,
 * además de consultar otros servicios necesarios para el uso de la aplicación que se requieren actualizar diariamente
 * Logo del casino ( se debe validar si se peude cambiar a la clase de sync)
 * Items del bar, detalles completos del producto como valor, puntos e imagen
 */
public class AsyncTaskValidateService extends AsyncTask<Void, Void, String[]> {


    private ProgressDialog progressDialog;
    private Context context;
    private AsyncResponse binder = null;
    private SharedPreferences preferences;
    private String[] failString;

    public interface AsyncResponse {

        void processFinish(String[] codigoEstado);

    }

    /**
     * @param context
     * @param asyncResponse
     */
    public AsyncTaskValidateService(Context context, AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, Prefs.SERVICE_PREF);
    }

    @Override
    protected void onPreExecute() {

        failString = new String[]{WebResult.FAIL, context.getString(R.string.common_fail)};
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_start_validate_service));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(Void... paramss) {

        String[] result = failString;
        StringBuilder response = new StringBuilder();


        HttpsURLConnection connection = null;
        HttpsURLConnection connectionBar = null;
        HttpsURLConnection connectionLogo = null;

        try {

            if (WebUtils.isOnline(context)) {


                String cookie = WebUtils.loginService(preferences.getString(Prefs.URL, ""),
                        preferences.getString(Prefs.SERV_USR, ""),
                        preferences.getString(Prefs.SERV_PASS, ""));
                if (cookie != null) {

                    FidelizacionApplication.getInstance().setCookie(cookie);
                    String targetUrlValidate = WebServs.PROTOCOL + preferences.getString(Prefs.URL, "") +WebServs.FIDELIZACION;

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    // register type adapters here, specify field naming policy, etc.
                    Gson gson = gsonBuilder.create();
                    String serial=preferences.getString(Prefs.SERIAL, "");
                    connection = WebUtils.createHttpsConnectionJson(targetUrlValidate, cookie);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                    wr.write(gson.toJson(serial));
                    wr.close();

                    int statusValidate = connection.getResponseCode();
                    BufferedReader rdValidate
                            = new BufferedReader(new InputStreamReader(statusValidate < 400 ? connection.getInputStream() : connection.getErrorStream()));
                    String lineV;
                    while ((lineV = rdValidate.readLine()) != null) {
                        response.append(lineV);
                    }


                    JSONObject jsonObject = new JSONObject(response.toString()).getJSONObject(WebParams.STATUS);

                    result[0] = jsonObject.getString(WebParams.CODE);
                    result[1] = jsonObject.getString(WebParams.MESSAGE);

                    //consumo de servicio para descargar items del bar completos
                    CommonServices.getCompleteItems(connectionBar, preferences, context, cookie);


                    //consumo de servicio para descargar logo del casino
                    String urlLogoCasino = WebServs.PROTOCOL + preferences.getString(Prefs.URL, "") +
                            WebServs.CASINO_LOGO;

                    connectionLogo = WebUtils.createHttpsConnection(urlLogoCasino, cookie);

                    StringBuilder responseLogoCasino = new StringBuilder();
                    int statusLogoCasino = connectionLogo.getResponseCode();
                    BufferedReader rdLogoCasino
                            = new BufferedReader(new InputStreamReader(statusLogoCasino < 400 ? connectionLogo.getInputStream() : connectionLogo.getErrorStream()));
                    String lineLogoCasino;
                    while ((lineLogoCasino = rdLogoCasino.readLine()) != null) {
                        responseLogoCasino.append(lineLogoCasino);
                    }

                    JSONObject jsonObjectLogoCasino = new JSONObject(responseLogoCasino.toString());

                    if (jsonObjectLogoCasino != null) {
                        if (WebResult.OK.equals(
                                jsonObjectLogoCasino.getJSONObject(AppConstants.WebParams.STATUS).getString(AppConstants.WebParams.CODE))) {

                            String folderPath = context.getExternalFilesDir(null).getPath();
                            FileUtils.createFileFromString(folderPath,
                                    AppConstants.FileExtension.LOGO_CASINO_NAME,
                                    jsonObjectLogoCasino.getString(WebParams.CASINO_IMAGEN));

                        }
                    }
                    Log.i("Async","VALIDATE");
                    result = CommonServices.getParams(preferences, context);
                }
            } else {
                result[0] = WebResult.NO_INTERNET;
                result[1] = context.getString(R.string.common_no_internet);
            }
        } catch (IOException e) {
            setFailString(result, e);
        } catch (NoSuchAlgorithmException e) {
            setFailString(result, e);
        } catch (KeyManagementException e) {
            setFailString(result, e);
        } catch (JSONException e) {
            setFailString(result, e);
        } catch (Exception e) {
            setFailString(result, e);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }

            if (null != connectionBar) {
                connectionBar.disconnect();
            }

            if (null != connectionLogo) {
                connectionLogo.disconnect();
            }
        }


        return result;
    }

    @Override
    protected void onPostExecute(String[] resultValue) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (binder != null) {
            binder.processFinish(resultValue);
        }
        super.onPostExecute(resultValue);
    }

    private void setFailString(String[] result, Exception e) {
        result = failString;
        MsgUtils.handleException(e);
    }
}
