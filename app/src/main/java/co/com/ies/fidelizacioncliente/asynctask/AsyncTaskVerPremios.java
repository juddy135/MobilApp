package co.com.ies.fidelizacioncliente.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.entity.PremioBody;
import co.com.ies.fidelizacioncliente.entity.PremioInfo;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

public class AsyncTaskVerPremios extends AsyncTask<String, Void, PremioInfo> {

    private ProgressDialog progressDialog;
    private Context context;
    private AsyncTaskVerPremios.AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {
        void processFinish(PremioInfo info);
    }

    public AsyncTaskVerPremios(Context context, AsyncResponse asyncResponse) {

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
    protected PremioInfo doInBackground(String... strings) {
        PremioInfo result= new PremioInfo();
        result.setResult(AppConstants.WebResult.FAIL);
        String service = AppConstants.WebServs.VER_PREMIOS;
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
            WebUtils.addParam(params, AppConstants.Prefs.CODIGO_CASINO, preferences.getString(AppConstants.Prefs.ID_CASINO, ""));
            WebUtils.addParam(params, AppConstants.Prefs.SERIAL, preferences.getString(AppConstants.Prefs.SERIAL, ""));

            connection = WebUtils.createHttpsConnection(targetUrl + "?" + params.toString(), cookie);

            int statusValidate = connection.getResponseCode();
            BufferedReader rdValidate
                    = new BufferedReader(new InputStreamReader(statusValidate < 400 ? connection.getInputStream() : connection.getErrorStream()));
            String lineV;
            while ((lineV = rdValidate.readLine()) != null) {
                response.append(lineV);
            }

            Log.i("VerPremios",response.toString());
            JSONObject jsonList = new JSONObject(response.toString());
            JSONObject jsonStatus = jsonList.getJSONObject(AppConstants.WebParams.STATUS);
            result.setResult(jsonStatus.getString(AppConstants.WebParams.CODE));
            result.setMessage(jsonStatus.getString(AppConstants.WebParams.MESSAGE));

            if (jsonList != null && result.getResult().equals(AppConstants.WebResult.OK)) {
                savePremio(jsonList.getJSONArray(AppConstants.WebParams.PREMIOS_LIST), result);
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
    protected void onPostExecute(PremioInfo info) {
        progressDialog.dismiss();
        if (binder != null) {
            binder.processFinish(info);
        }
        super.onPostExecute(info);
    }

    private void savePremio(JSONArray jsonArray, PremioInfo result) throws JSONException {
        int size = jsonArray.length();
        if(size>0){
            JSONObject jsonItem = jsonArray.getJSONObject(0);
            PremioBody info=new PremioBody();
            info.setConsecutivo(jsonItem.getString(AppConstants.WebParams.ITEM_CONSECUTIVO));
            info.setFechaGeneracion(jsonItem.getString(AppConstants.WebParams.ITEM_FECHA));
            info.setFormato(jsonItem.getString(AppConstants.WebParams.ITEM_FORMATO));
            info.setMonto(jsonItem.getString(AppConstants.WebParams.ITEM_MONTO));
            info.setProgresivo(jsonItem.getString(AppConstants.WebParams.ITEM_PROGRESIVO));
            info.setSerialDispositivo(jsonItem.getString(AppConstants.WebParams.ITEM_SERIAL));
            info.setMontoReal(jsonItem.getString(AppConstants.WebParams.ITEM_MONTO_REAL));
            info.setMontoRetencion(jsonItem.getString(AppConstants.WebParams.ITEM_MONTO_RETENCION));
            result.setInfo(info);
        }

    }

}
