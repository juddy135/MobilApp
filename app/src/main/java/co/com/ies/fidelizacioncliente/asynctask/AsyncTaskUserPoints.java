package co.com.ies.fidelizacioncliente.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.entity.PuntosBody;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

/**
 * Esta clase no usamos dialog por que es un proceso en bacground,
 * se usa para consultar los puntos de usuario cada cierto tiempo
 */
public class AsyncTaskUserPoints extends AsyncTask<String, Void, String[]> {

    private Context context;
    private AsyncResponse binder = null;

    public interface AsyncResponse {

        void processFinish(String[] codigoEstado);

    }

    public AsyncTaskUserPoints(Context context) {

    }

    public AsyncTaskUserPoints(Context context, AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
    }


    public AsyncTaskUserPoints(AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
    }

    @Override
    protected String[] doInBackground(String... params) {

        String url = params[0];
        String docUsr = params[1];
        String casinoCode = params[2];
        String serial = params[3];
        String result[] = new String[]{AppConstants.WebResult.FAIL, "", "", ""};
        JSONObject jsonResponse = null;
        String service = AppConstants.WebServs.POINTS;
        StringBuilder response = new StringBuilder();

        HttpsURLConnection connection = null;
        String targetUrl = AppConstants.WebServs.PROTOCOL + url + service;

        try {

            PuntosBody body=new PuntosBody(docUsr,serial,AppConstants.WebCons.MOVIL,casinoCode);
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

            result[0] = jsonStatus.getString(AppConstants.WebParams.CODE);
            result[1] = jsonStatus.getString(AppConstants.WebParams.MESSAGE);

            if (result[0].equals(AppConstants.WebResult.OK)) {
                result[2] = jsonObject.getString(AppConstants.WebParams.AVALIABLE_POINTS);
                result[3] = jsonObject.getString(AppConstants.WebParams.REDEEMED_POINTS);
            }

        } catch (IOException e) {
            MsgUtils.handleException(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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

        if (binder != null) {
            binder.processFinish(resultValue);
        }
        super.onPostExecute(resultValue);
    }
}
