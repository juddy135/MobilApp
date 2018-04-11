package co.com.ies.fidelizacioncliente.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

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
import co.com.ies.fidelizacioncliente.entity.PeticionesBody;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.AppConstants.Prefs;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebParams;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebResult;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebServs;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.StringUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

/**
 * Clase usada para realizar las 4 diferentes peticiones posibles sobre un premio
 * Comprar o redimir producto
 * Cancelar o confirmar producto recibido
 */
public class AsyncTaskAskBarItem extends AsyncTask<String, Void, String[]> {


    private ProgressDialog progressDialog;
    private Context context;
    private AsyncResponse binder = null;
    private SharedPreferences preferences;
    private String[] result;

    public interface AsyncResponse {

        void askItemFinish(String codigoEstado, String process, String idItem);

    }

    public AsyncTaskAskBarItem(Context context) {

        this.context = context;
        preferences = SharedPrefUtils.getSharedPreference(context, Prefs.SERVICE_PREF);
    }

    public AsyncTaskAskBarItem(Context context, AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, Prefs.SERVICE_PREF);
        result = new String[3];
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_user_asking_snack));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... params) {

        String idItem = params[0];
        String process = params[1];
        String note = null;
        result[0] = WebResult.FAIL;
        result[1] = process;
        result[2] = idItem;
        StringBuilder response = new StringBuilder();
        HttpsURLConnection connection = null;
        String baseUrl = WebServs.PROTOCOL + preferences.getString(Prefs.URL, "") + process;
        String numDoc = FidelizacionApplication.getInstance().getUserDoc();
        PeticionesBody body=new PeticionesBody();
        try {
            StringBuilder stringBuilder = new StringBuilder();
            switch (process) {
                case WebServs.ORDER_REDEEM:
                    body.setNumeroDocumento(numDoc);
                    body.setSerial(preferences.getString(Prefs.SERIAL, ""));
                    body.setIdPeticion(idItem);
                    break;
                case WebServs.ORDER_BUY:
                    body.setSerial(preferences.getString(Prefs.SERIAL, ""));
                    body.setIdPeticion(idItem);
                    if (!StringUtils.isNullOrEmpty(numDoc)) {
                        body.setNumeroDocumento(numDoc);
                    }
                    break;
                case WebServs.ORDER_CANCEL:
                    body.setSerial(preferences.getString(Prefs.SERIAL, ""));
                    body.setIdPeticion(idItem);
                    if (!StringUtils.isNullOrEmpty(note)) {
                        body.setNota(note);
                    }
                    body.setHaySesion(!StringUtils.isNullOrEmpty(numDoc) ? AppConstants.Generic.STRING_YES : AppConstants.Generic.STRING_NO);
                    break;
                case WebServs.ORDER_RECEIVED:
                    body.setSerial(preferences.getString(Prefs.SERIAL, ""));
                    body.setIdPeticion(idItem);
                    body.setHaySesion(!StringUtils.isNullOrEmpty(numDoc) ? AppConstants.Generic.STRING_YES : AppConstants.Generic.STRING_NO);

                    break;
            }

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            connection = WebUtils.createHttpsConnectionJson(baseUrl,FidelizacionApplication.getInstance().getCookie());
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            wr.write(gson.toJson(body));
            wr.close();

            int status = connection.getResponseCode();

            BufferedReader rd
                    = new BufferedReader(new InputStreamReader(status < 400 ? connection.getInputStream() : connection.getErrorStream()));

            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            JSONObject jsonObjectBar = new JSONObject(response.toString());
            JSONObject jsonStatus = jsonObjectBar.getJSONObject(AppConstants.WebParams.STATUS);

            result[0] = jsonStatus.getString(WebParams.CODE);


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
            binder.askItemFinish(resultValue[0], resultValue[1], resultValue[2]);
        }
        super.onPostExecute(resultValue);
    }

}