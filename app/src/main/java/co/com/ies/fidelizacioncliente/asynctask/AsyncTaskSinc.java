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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.entity.GenericItem;
import co.com.ies.fidelizacioncliente.entity.Machine;
import co.com.ies.fidelizacioncliente.manager.ManagerStandard;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.AppConstants.Prefs;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebParams;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebResult;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebServs;
import co.com.ies.fidelizacioncliente.utils.DateUtils;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;
import co.com.ies.fidelizacioncliente.web.CommonServices;

/**
 * Servicio para descargar toda la info necesaria para la configuración del bar,
 * tres servicios son consumidos
 * Casinos
 * Maquinas por casino
 * Parametros de la app (ejemplo: tiempo de actualización de puntos de cliente, clave de acceso a configuración de app,
 * quizás la imagen cambie diariamente con promos o info relevante, mostrado dentro de la imagen)
 */
public class AsyncTaskSinc extends AsyncTask<String, Void, String[]> {


    private ProgressDialog progressDialog;
    private Context context;
    private AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {

        void processFinish(String[] codigoEstado);

    }

    public AsyncTaskSinc(Context context) {

        this.context = context;
        preferences = SharedPrefUtils.getSharedPreference(context, Prefs.SERVICE_PREF);
    }

    public AsyncTaskSinc(Context context, AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, Prefs.SERVICE_PREF);
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_setting_download_info));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... params) {

        String url = params[0];
        String serUsr = params[1];
        String serPass = params[2];
        String[] result = new String[]{WebResult.FAIL, context.getString(R.string.common_fail)};

        StringBuilder responseCasinos = new StringBuilder();
        //StringBuilder responseParams = new StringBuilder();

        String targetUrlCassinos = WebServs.PROTOCOL + url + WebServs.LIST_CASINOS;
        String targetUrlMachines = WebServs.PROTOCOL + url + WebServs.LIST_DEVICES;
        //String urlParams = WebServs.PROTOCOL + url + WebServs.PARAMS;

        HttpsURLConnection connectionCasinos = null;

        try {
            if (WebUtils.isOnline(context)) {
                String cookie = WebUtils.loginService(url,
                        serUsr,
                        serPass);

                if (cookie != null) {

                    String[] responseCookie = cookie.split("; ");
                    FidelizacionApplication.getInstance().setCookie(responseCookie[0]);
                    connectionCasinos = WebUtils.createHttpsConnection(targetUrlCassinos,
                            FidelizacionApplication.getInstance().getCookie());

                    int status = connectionCasinos.getResponseCode();
                    BufferedReader rd
                            = new BufferedReader(new InputStreamReader(status < 400 ? connectionCasinos.getInputStream() : connectionCasinos.getErrorStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        responseCasinos.append(line);
                    }

                    JSONObject jsonObjectCasinos = new JSONObject(responseCasinos.toString());
                    JSONObject jsonStatus = jsonObjectCasinos.getJSONObject(WebParams.STATUS);
                    result[0] = jsonStatus.getString(WebParams.CODE);
                    result[1] = jsonStatus.getString(WebParams.MESSAGE);

                    if (result[0].equals(WebResult.OK)) {

                        JSONArray jsonCasinos = jsonObjectCasinos.getJSONArray(WebParams.CASINOS);
                        ArrayList<GenericItem> listCasinos = new ArrayList<>();
                        ArrayList<Machine> listMaquinas = new ArrayList<>();
                        int length = jsonCasinos.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonCasino = jsonCasinos.getJSONObject(i);

                            listCasinos.add(new GenericItem(jsonCasino.getString(WebParams.CASINO_CODE),
                                    jsonCasino.getString(WebParams.CASINO_NAME)));
                            listMaquinas.addAll(consumeMachinesByCassino(targetUrlMachines, jsonCasino.getString(WebParams.CASINO_CODE), cookie));
                        }

                        ManagerStandard.getInstance().insertCasinos(context, listCasinos);
                        ManagerStandard.getInstance().insertMachines(context, listMaquinas);
                    }
                    SharedPrefUtils.savePreference(context, Prefs.SERVICE_PREF, Prefs.URL,url);
                    result = CommonServices.getParams(preferences,context);

                }
            } else {
                result[0] = WebResult.NO_INTERNET;
                result[1] = context.getString(R.string.common_no_internet);
            }

        } catch (IOException e) {
            MsgUtils.handleException(e);
        } catch (JSONException e) {
            MsgUtils.handleException(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            MsgUtils.handleException(e);
        } finally {
            if (null != connectionCasinos) {
                connectionCasinos.disconnect();
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

    private ArrayList<Machine> consumeMachinesByCassino(String url, String idCassino, String cookie) throws NoSuchAlgorithmException, IOException, KeyManagementException, JSONException {
        StringBuilder response = new StringBuilder();
        ArrayList<Machine> arrayList = new ArrayList<>();
        StringBuilder params = new StringBuilder();
        WebUtils.addParam(params, WebParams.CASINO_CODE, idCassino);
        WebUtils.addParam(params, WebParams.PAGED, AppConstants.WebCons.PAGED);
        WebUtils.addParam(params, WebParams.PAG_SIZE, AppConstants.WebCons.MAX_PAG_SIZE);

        HttpsURLConnection connection = WebUtils.createHttpsConnection(url + "?" + params.toString(),
                cookie);

        int status = connection.getResponseCode();
        BufferedReader rd
                = new BufferedReader(new InputStreamReader(status < 400 ? connection.getInputStream() : connection.getErrorStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }

        JSONObject jsonObject = new JSONObject(response.toString());
        JSONObject jsonStatus = jsonObject.getJSONObject(WebParams.STATUS);
        String result = jsonStatus.getString(WebParams.CODE);

        if (result.equals(WebResult.OK)) {
            JSONArray jsonCasinos = jsonObject.getJSONArray(WebParams.DEVICES);
            int length = jsonCasinos.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonCasino = jsonCasinos.getJSONObject(i);

                arrayList.add(new Machine(jsonCasino.getString(WebParams.NUM_DISP),
                        idCassino,
                        jsonCasino.getString(WebParams.SERIAL),
                        jsonCasino.getString(WebParams.MONTO),
                        jsonCasino.getString(WebParams.STATE_RED),
                        jsonCasino.getString(WebParams.STATE_SERIAL),
                        jsonCasino.getString(WebParams.STATE_SWITCH),
                        jsonCasino.getString(WebParams.STATE_MACHINE)));
            }
        }

        return arrayList;
    }
}
