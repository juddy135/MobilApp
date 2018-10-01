package co.com.ies.fidelizacioncliente.asynctask;

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
import co.com.ies.fidelizacioncliente.entity.StateCallServiceBody;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

public class AsyncTaskStateCallService extends AsyncTask<String, Void, String[]> {

    private Context context;
    private AsyncTaskStateCallService.AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {
        void onStateCallServiceFinish(String[] codigoEstado);

    }

    public AsyncTaskStateCallService(Context context, AsyncTaskStateCallService.AsyncResponse response) {
        this.context = context;
        this.binder = response;
        preferences = SharedPrefUtils.getSharedPreference(context, AppConstants.Prefs.SERVICE_PREF);

    }

    @Override
    protected String[] doInBackground(String... strings) {
        String[] result = new String[]{AppConstants.WebResult.FAIL, context.getString(R.string.common_fail),""};
        String idtransaccion = strings[0];
        StringBuilder response = new StringBuilder();

        HttpsURLConnection connection = null;

        try {
            if (WebUtils.isOnline(context)) {

                String targetUrl = AppConstants.WebServs.PROTOCOL + preferences.getString(AppConstants.Prefs.URL, "") + AppConstants.WebServs.STATE_CALL_SERVICE;
                StateCallServiceBody body=new StateCallServiceBody(preferences.getString(AppConstants.Prefs.SERV_USR, ""));
                body.setIdsolicitud(idtransaccion);

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
                JSONObject jsonObject = jsonObjectB.getJSONObject(AppConstants.WebParams.STATUS);

                result[0] = jsonObject.getString(AppConstants.WebParams.CODE);
                result[1] = jsonObject.getString(AppConstants.WebParams.MESSAGE);
                result[2] = jsonObjectB.getString(AppConstants.WebParams.STATE_CALLSERVICE);

            }else {
                result[0] = AppConstants.WebResult.NO_INTERNET;
                result[1] = context.getString(R.string.common_no_internet);
            }
        }catch (IOException e) {
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
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String[] strings) {
        if (binder != null) {
            binder.onStateCallServiceFinish(strings);
        }
        super.onPostExecute(strings);
    }
}
