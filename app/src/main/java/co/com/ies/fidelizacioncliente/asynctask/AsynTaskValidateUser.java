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
import co.com.ies.fidelizacioncliente.entity.FidelizarClienteBody;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

/**
 * Clase donde validamos la fidelizacion del usuario para iniciar sesión
 */
public class AsynTaskValidateUser extends AsyncTask<String, Void, String[]> {


    private ProgressDialog progressDialog;
    private Context context;
    private AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {

        void processFinish(String[] codigoEstado);

    }

    public AsynTaskValidateUser(Context context) {

        this.context = context;
        preferences = SharedPrefUtils.getSharedPreference(context, AppConstants.Prefs.SERVICE_PREF);
    }

    public AsynTaskValidateUser(Context context, AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, AppConstants.Prefs.SERVICE_PREF);
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_login_validate_user));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... params) {

        String docUsr = params[0];
        String[] result = new String[]{AppConstants.WebResult.FAIL, context.getString(R.string.common_fail),"0",AppConstants.WebResult.FAIL};
        JSONObject jsonResponse = null;
        String service = AppConstants.WebServs.FIDEL_USR;
        StringBuilder response = new StringBuilder();

        HttpsURLConnection connection = null;
        String targetUrl = AppConstants.WebServs.PROTOCOL + preferences.getString(AppConstants.Prefs.URL, "") + service;

        try {
            if (WebUtils.isOnline(context)) {

                String cookie = WebUtils.loginService(preferences.getString(AppConstants.Prefs.URL, ""),
                        preferences.getString(AppConstants.Prefs.SERV_USR, ""),
                        preferences.getString(AppConstants.Prefs.SERV_PASS, ""));
                if (cookie != null) {
                    FidelizacionApplication.getInstance().setCookie(cookie);
                }

                FidelizarClienteBody body=new FidelizarClienteBody(docUsr,preferences.getString(AppConstants.Prefs.SERIAL, ""),AppConstants.WebCons.MOVIL);

                GsonBuilder gsonBuilder = new GsonBuilder();
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


                if (result[0].equals(AppConstants.WebResult.OK)) {
                    result[2]=jsonObject.getString(AppConstants.WebParams.USER_CLAVE_BD);
                    Log.i("DIALOG"," clave JSON "+result[2]);
                    if(jsonObject.getBoolean(AppConstants.WebParams.USER_BILLETERO)){
                        result[3]=AppConstants.WebResult.OK;
                    }

                    FidelizacionApplication.getInstance().setUserName(jsonObject.getString(AppConstants.WebParams.USER_NAME));
                    FidelizacionApplication.getInstance().setUserDoc(docUsr);
                }

            } else {
                result[0] = AppConstants.WebResult.NO_INTERNET;
                result[1] = context.getString(R.string.common_no_internet);
            }
        } catch (IOException e) {
            MsgUtils.handleException(e);
        } catch (NoSuchAlgorithmException e) {
            MsgUtils.handleException(e);
        } catch (KeyManagementException e) {
            MsgUtils.handleException(e);
        } catch (JSONException e) {
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
