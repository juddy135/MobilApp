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

import co.com.ies.fidelizacioncliente.ActivityCashless;
import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.entity.ConsultarBilleteroBody;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

/**
 * Permite consultar el billetero del cliente
 *
 * Created by juddy on 03/04/2018.
 */
public class AsyncTaskAskBilletero extends AsyncTask<String, Void, String[]> {
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncTaskAskBilletero.AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {
        void processFinish(String[] codigoEstado);
    }

    public AsyncTaskAskBilletero(Context context, AsyncResponse asyncResponse) {

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
    protected String[] doInBackground(String... params) {
        String docUsr = params[0];
        String claveUsr = params[1];
        String[] result = new String[]{AppConstants.WebResult.FAIL, context.getString(R.string.common_fail),"0"};
        String service = AppConstants.WebServs.CONSULTAR_BILLETERO;
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
            //Parametros Body
            ConsultarBilleteroBody body=new ConsultarBilleteroBody(preferences.getString(AppConstants.Prefs.SERV_USR,""),docUsr,claveUsr);

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

            JSONObject jsonObjectB = new JSONObject(response.toString());
            JSONObject jsonStatus = jsonObjectB.getJSONObject(AppConstants.WebParams.STATUS);
            result[0] = jsonStatus.getString(AppConstants.WebParams.CODE);
            result[1] = jsonStatus.getString(AppConstants.WebParams.MESSAGE);

            if (result[0].equals(AppConstants.WebResult.OK)) {
                result[2]=jsonObjectB.getString(AppConstants.WebParams.VALOR_BILLETERO);
            }

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
