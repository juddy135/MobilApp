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
import co.com.ies.fidelizacioncliente.entity.CallServiceBody;
import co.com.ies.fidelizacioncliente.entity.EstadoSolicitudEnum;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

/**
 * Clase usada para consumir el servicio de atención al cliente directa para que alguien se acerque a la máquina
 */
public class AsyncTaskCallService extends AsyncTask<String, Void, String[]> {
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncTaskCallService.AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {

        void onCallServiceFinish(String[] codigoEstado);

    }

    /**
     * @param context
     * @param asyncResponse
     */
    public AsyncTaskCallService(Context context, AsyncTaskCallService.AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, AppConstants.Prefs.SERVICE_PREF);
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_user_processing_call_atendant));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... paramss) {

        String[] result = new String[]{AppConstants.WebResult.FAIL, context.getString(R.string.common_fail),""};
        String documentoCliente = paramss[0];
        String estado = paramss[1];
        String idtransaccion = paramss[2];
        StringBuilder response = new StringBuilder();

        HttpsURLConnection connection = null;

        try {

            if (WebUtils.isOnline(context)) {

                String targetUrl = AppConstants.WebServs.PROTOCOL + preferences.getString(AppConstants.Prefs.URL, "") + AppConstants.WebServs.CALL_SERVICE;

                CallServiceBody body=new CallServiceBody(preferences.getString(AppConstants.Prefs.SERV_USR, ""), preferences.getString(AppConstants.Prefs.ID_CASINO, ""), preferences.getString(AppConstants.Prefs.SERIAL, ""));
                body.setDocumentoCliente(documentoCliente);
                body.setEstado(estado);
                if(!idtransaccion.isEmpty()){
                    body.setIdsolicitud(idtransaccion);
                }

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
                Log.i(":::CALL:::",response.toString());
                JSONObject jsonObjectB = new JSONObject(response.toString());
                JSONObject jsonObject = jsonObjectB.getJSONObject(AppConstants.WebParams.STATUS);

                result[0] = jsonObject.getString(AppConstants.WebParams.CODE);
                result[1] = jsonObject.getString(AppConstants.WebParams.MESSAGE);
                result[2] = jsonObjectB.getString(AppConstants.WebParams.ID_CALLSERVICE);

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

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (binder != null) {
            binder.onCallServiceFinish(resultValue);
        }
        super.onPostExecute(resultValue);
    }
}