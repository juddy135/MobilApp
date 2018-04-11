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
import co.com.ies.fidelizacioncliente.entity.FidelizarClienteBody;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

/**
 *  Clase usada para cerrar sesión
 */
public class AsyncTaskCloseSession extends AsyncTask<Void, Void, String> {


    private ProgressDialog progressDialog;
    private Context context;
    private AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {

        void processFinish(String codigoEstado);

    }

    public AsyncTaskCloseSession(Context context, AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, AppConstants.Prefs.SERVICE_PREF);
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_user_close_session));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {


        String result = AppConstants.WebResult.FAIL;
        StringBuilder response = new StringBuilder();
        HttpsURLConnection connection = null;
        String targetUrl = AppConstants.WebServs.PROTOCOL + preferences.getString(AppConstants.Prefs.URL, "") + AppConstants.WebServs.FIDEL_CLOSE;

        try {
            if (WebUtils.isOnline(context)) {
                /*StringBuilder stringBuilder = new StringBuilder();
                WebUtils.addParam(stringBuilder, AppConstants.WebParams.SERIAL, preferences.getString(AppConstants.Prefs.SERIAL, ""));
                WebUtils.addParam(stringBuilder, AppConstants.WebParams.TYPE, AppConstants.WebCons.MOVIL);
*/
                FidelizarClienteBody body=new FidelizarClienteBody("",preferences.getString(AppConstants.Prefs.SERIAL, ""),AppConstants.WebCons.MOVIL);

                GsonBuilder gsonBuilder = new GsonBuilder();
                // register type adapters here, specify field naming policy, etc.
                Gson gson = gsonBuilder.create();
                connection = WebUtils.createHttpsConnectionJson(targetUrl,FidelizacionApplication.getInstance().getCookie());
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

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonStatus = jsonObject.getJSONObject(AppConstants.WebParams.STATUS);
                result = jsonStatus.getString(AppConstants.WebParams.CODE);

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
    protected void onPostExecute(String resultValue) {

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        if (binder != null) {
            binder.processFinish(resultValue);
        }
        super.onPostExecute(resultValue);
    }
}
