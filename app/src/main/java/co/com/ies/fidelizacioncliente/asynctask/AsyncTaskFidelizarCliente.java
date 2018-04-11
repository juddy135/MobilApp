package co.com.ies.fidelizacioncliente.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import co.com.ies.fidelizacioncliente.entity.FidelizarClienteBody;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

/**
 * Permite fidelizar al cliente (se usa con los clientes que usan clave dinamica)
 * se muestra una ventana de dialogo mientras se ejecuta el proceso en background
 *
 * Created by juddy on 27/03/2018.
 */

public class AsyncTaskFidelizarCliente extends AsyncTask<String, Void, String[]> {
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncTaskFidelizarCliente.AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {
        void processFinish(String[] codigoEstado);
    }

    public AsyncTaskFidelizarCliente(Context context, AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, AppConstants.Prefs.SERVICE_PREF);
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_login_waiting));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... params) {
        String docUsr = params[0];
        String[] result = new String[]{AppConstants.WebResult.FAIL, context.getString(R.string.common_fail)};
        String service = AppConstants.WebServs.FIDELIZAR_CLIENTE;
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

                //PARAMETROS en BODY
                FidelizarClienteBody body=new FidelizarClienteBody(docUsr,preferences.getString(AppConstants.Prefs.SERIAL,""),AppConstants.WebCons.MOVIL);

                GsonBuilder gsonBuilder = new GsonBuilder();
                // register type adapters here, specify field naming policy, etc.
                Gson gson = gsonBuilder.create();
                connection = WebUtils.createHttpsConnectionJson(targetUrl,FidelizacionApplication.getInstance().getCookie());
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                wr.write(gson.toJson(body));
                wr.close();

                int status = connection.getResponseCode();
                BufferedReader rd= new BufferedReader(new InputStreamReader(status < 400 ? connection.getInputStream() : connection.getErrorStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
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
    protected void onPostExecute(String[] resultValue) {
        progressDialog.dismiss();
        if (binder != null) {
            binder.processFinish(resultValue);
        }
        super.onPostExecute(resultValue);
    }

}
