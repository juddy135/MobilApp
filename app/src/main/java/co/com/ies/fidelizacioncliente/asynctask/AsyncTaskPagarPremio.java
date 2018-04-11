package co.com.ies.fidelizacioncliente.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

public class AsyncTaskPagarPremio extends AsyncTask<String, Void, String[]> {

    private ProgressDialog progressDialog;
    private Context context;
    private AsyncTaskPagarPremio.AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {
        void processFinish(String[] codigoEstado);
    }

    public AsyncTaskPagarPremio(Context context, AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, AppConstants.Prefs.SERVICE_PREF);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_login_esperando));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... strings) {
        String consecutivo=strings[0];
        String[] result = new String[]{AppConstants.WebResult.FAIL, context.getString(R.string.common_fail)};
        String service = AppConstants.WebServs.PAGAR_PREMIOS;
        StringBuilder response = new StringBuilder();

        HttpsURLConnection connection = null;
        String targetUrl = AppConstants.WebServs.PROTOCOL + preferences.getString(AppConstants.Prefs.URL, "") + service;

        try {

            String cookie = WebUtils.loginService(preferences.getString(AppConstants.Prefs.URL, ""),
                    preferences.getString(AppConstants.Prefs.SERV_USR, ""),
                    preferences.getString(AppConstants.Prefs.SERV_PASS, ""));
            if (cookie != null) {
                FidelizacionApplication.getInstance().setCookie(cookie);
            }

            StringBuilder params = new StringBuilder();
            WebUtils.addParam(params, AppConstants.WebParams.CONSECUTIVO,consecutivo);
            WebUtils.addParam(params, AppConstants.Prefs.NOMBRE_USR, preferences.getString(AppConstants.Prefs.SERV_USR, ""));

            connection = WebUtils.createHttpsConnection(targetUrl + "?" + params.toString(), cookie);

            int statusValidate = connection.getResponseCode();
            BufferedReader rdValidate
                    = new BufferedReader(new InputStreamReader(statusValidate < 400 ? connection.getInputStream() : connection.getErrorStream()));
            String lineV;
            while ((lineV = rdValidate.readLine()) != null) {
                response.append(lineV);
            }

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject jsonStatus = jsonObject.getJSONObject(AppConstants.WebParams.STATUS);


            result[0] = jsonStatus.getString(AppConstants.WebParams.CODE);
            result[1] = jsonStatus.getString(AppConstants.WebParams.MESSAGE);




        } catch (IOException e) {
            MsgUtils.handleException(e);
        } catch (NoSuchAlgorithmException e) {
            MsgUtils.handleException(e);
        } catch (KeyManagementException e) {
            MsgUtils.handleException(e);
        } catch (Exception e) {
            MsgUtils.handleException(e);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }


        return result;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        progressDialog.dismiss();
        if (binder != null) {
            binder.processFinish(strings);
        }
        super.onPostExecute(strings);
    }

}
