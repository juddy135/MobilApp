package co.com.ies.fidelizacioncliente.web;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
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
import co.com.ies.fidelizacioncliente.entity.PremiosBarBody;
import co.com.ies.fidelizacioncliente.manager.ManagerStandard;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.DateUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.StringUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

import static co.com.ies.fidelizacioncliente.utils.AppConstants.Generic.STRING_YES;

/**
 *
 * Created by user on 29/10/2017.
 */

public class CommonServices {

    /**
     * Recibimos la conexión para poder cerrarla dentro del async q lo llame
     *
     * @param connectionBar
     * @param preferences
     * @param context
     * @param cookie
     */
    public static void getCompleteItems(HttpsURLConnection connectionBar, SharedPreferences preferences, Context context, String cookie) throws JSONException, NoSuchAlgorithmException, IOException, KeyManagementException {

        //consumo de servicio para descargar items del bar
        String urlBar = AppConstants.WebServs.PROTOCOL + preferences.getString(AppConstants.Prefs.URL, "") +AppConstants.WebServs.LIST_BAR;

        PremiosBarBody body=new PremiosBarBody(preferences.getString(AppConstants.Prefs.ID_CASINO, ""),AppConstants.Generic.STRING_YES);
        String fecha = preferences.getString(AppConstants.Prefs.DATE, "");
        if(!StringUtils.isNullOrEmpty(fecha)){
            body.setFecha(fecha);
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        connectionBar = WebUtils.createHttpsConnectionJson(urlBar, cookie);
        OutputStreamWriter wr = new OutputStreamWriter(connectionBar.getOutputStream(), "UTF-8");
        wr.write(gson.toJson(body));
        wr.close();

        StringBuilder responseBar = new StringBuilder();
        int statusBar = connectionBar.getResponseCode();
        BufferedReader rdBar
                = new BufferedReader(new InputStreamReader(statusBar < 400 ? connectionBar.getInputStream() : connectionBar.getErrorStream()));
        String lineBar;
        while ((lineBar = rdBar.readLine()) != null) {
            responseBar.append(lineBar);
        }

        JSONObject jsonObjectBar = new JSONObject(responseBar.toString());

        if (jsonObjectBar != null) {
            if (AppConstants.WebResult.OK.equals(jsonObjectBar.getJSONObject(AppConstants.WebParams.STATUS).getString(AppConstants.WebParams.CODE))) {
                JSONArray jsonListaBar = jsonObjectBar.getJSONArray(AppConstants.WebParams.BAR_LIST);
                if (ManagerStandard.getInstance().saveBarItems(context, jsonListaBar)) {

                    SharedPrefUtils.savePreference(context, AppConstants.Prefs.SERVICE_PREF,
                            AppConstants.Prefs.DATE, DateUtils.longToString(System.currentTimeMillis(), DateUtils.FORMAT_LOG));
                }
            }
        }


    }

    public static String[] getParams(SharedPreferences preferences, Context context) throws JSONException, NoSuchAlgorithmException, IOException, KeyManagementException {

        String urlParams = AppConstants.WebServs.PROTOCOL + preferences.getString(AppConstants.Prefs.URL, "") + AppConstants.WebServs.PARAMS;

        StringBuilder responseParams = new StringBuilder();
        String[] result = new String[]{AppConstants.WebResult.FAIL, context.getString(R.string.common_fail)};
        HttpsURLConnection connectionParams = null;
        try {
            connectionParams = WebUtils.createHttpsConnection(urlParams,
                    FidelizacionApplication.getInstance().getCookie());

            int statusParams = connectionParams.getResponseCode();
            BufferedReader rdParams
                    = new BufferedReader(new InputStreamReader(statusParams < 400 ? connectionParams.getInputStream() : connectionParams.getErrorStream()));
            String lineParams;
            while ((lineParams = rdParams.readLine()) != null) {
                responseParams.append(lineParams);
            }

            JSONObject jsonObjectParams = new JSONObject(responseParams.toString());
            JSONObject jsonStatusParams = jsonObjectParams.getJSONObject(AppConstants.WebParams.STATUS);
            result[0] = jsonStatusParams.getString(AppConstants.WebParams.CODE);
            result[1] = jsonStatusParams.getString(AppConstants.WebParams.MESSAGE);

            if (result[0].equals(AppConstants.WebResult.OK)) {

                String configPass = jsonObjectParams.getString(AppConstants.WebParams.CONFIG_PASS);
                String configTimePoints = jsonObjectParams.getString(AppConstants.WebParams.CONFIG_TIME_POINTS);
                String configKeyboard = jsonObjectParams.getString(AppConstants.WebParams.CONFIG_KEYBOARD);

                SharedPreferences.Editor editor = preferences.edit();

                if (!StringUtils.isNullOrEmpty(configPass)) {
                    editor.putString(AppConstants.Prefs.PASS, configPass);
                }

                int valueTimePoints = Integer.valueOf(configTimePoints);
                editor.putInt(AppConstants.Prefs.INTERV_UPDATE_POINTS, valueTimePoints);

                editor.putBoolean(AppConstants.Prefs.SHOW_KEYBOARD,
                        configKeyboard.equalsIgnoreCase(STRING_YES));

                editor.apply();

            }
        } finally {
            if (connectionParams != null) {
                connectionParams.disconnect();
            }
        }
        return result;
    }

}
