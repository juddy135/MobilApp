package co.com.ies.fidelizacioncliente.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

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

        String[] result = new String[]{AppConstants.WebResult.FAIL, context.getString(R.string.common_fail)};
        String process = paramss[0];
        StringBuilder response = new StringBuilder();
        String cookie = FidelizacionApplication.getInstance().getCookie();

        HttpsURLConnection connection = null;

        try {

            if (WebUtils.isOnline(context)) {


                String targetUrlValidate = AppConstants.WebServs.PROTOCOL + preferences.getString(AppConstants.Prefs.URL, "") +
                        AppConstants.WebServs.CALL_SERVICE;

                StringBuilder stringBuilderValidate = new StringBuilder();
                WebUtils.addParam(stringBuilderValidate, AppConstants.WebParams.SERIAL, preferences.getString(AppConstants.Prefs.SERIAL, ""));
                WebUtils.addParam(stringBuilderValidate, AppConstants.WebParams.CASINO_CODE, preferences.getString(AppConstants.Prefs.ID_CASINO, ""));

                connection = WebUtils.createHttpsConnection(targetUrlValidate + "?" + stringBuilderValidate.toString(), cookie);

                int statusValidate = connection.getResponseCode();
                BufferedReader rdValidate
                        = new BufferedReader(new InputStreamReader(statusValidate < 400 ? connection.getInputStream() : connection.getErrorStream()));
                String lineV;
                while ((lineV = rdValidate.readLine()) != null) {
                    response.append(lineV);
                }


                JSONObject jsonObject = new JSONObject(response.toString()).getJSONObject(AppConstants.WebParams.STATUS);

                result[0] = jsonObject.getString(AppConstants.WebParams.CODE);
                result[1] = jsonObject.getString(AppConstants.WebParams.MESSAGE);

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