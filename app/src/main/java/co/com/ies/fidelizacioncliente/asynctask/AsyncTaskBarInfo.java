package co.com.ies.fidelizacioncliente.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.entity.BarInfo;
import co.com.ies.fidelizacioncliente.entity.BarItem;
import co.com.ies.fidelizacioncliente.entity.FidelizarClienteBody;
import co.com.ies.fidelizacioncliente.entity.OrderState;
import co.com.ies.fidelizacioncliente.entity.PremiosBarBody;
import co.com.ies.fidelizacioncliente.entity.PuntosBody;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.AppConstants.Prefs;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebParams;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebResult;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebServs;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;
import co.com.ies.fidelizacioncliente.web.CommonServices;

/**
 * Se consultan 3 servicios diferentes requeridos para la ejecución del bar
 * Lista de items disponibles
 * Si usuario esta logueado, puntos diarios del usuario
 * Peticiones pendientes, en caso de que el usuario este logueado se valida respecto al documento del usuario si no,
 * solo con el serial de la máquina
 */
public class AsyncTaskBarInfo extends AsyncTask<Void, Void, BarInfo> {


    private ProgressDialog progressDialog;
    private Context context;
    private AsyncTaskBarInfo.AsyncResponse binder = null;
    private SharedPreferences preferences;

    public interface AsyncResponse {

        void barInfoFinish(BarInfo barInfo);

    }

    public AsyncTaskBarInfo(Context context, AsyncTaskBarInfo.AsyncResponse asyncResponse) {

        this.context = context;
        this.binder = asyncResponse;
        preferences = SharedPrefUtils.getSharedPreference(context, Prefs.SERVICE_PREF);
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.act_bar_consulting));
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected BarInfo doInBackground(Void... params) {
        BarInfo barInfo = new BarInfo();
        barInfo.setResult(WebResult.FAIL);
        HttpsURLConnection connectionPuntos = null;
        HttpsURLConnection connectionOrders = null;
        HttpsURLConnection connectionBarComplete = null;
        HttpsURLConnection connectionBar = null;
        String numDocumento = FidelizacionApplication.getInstance().getUserDoc();
        String cookie = null;
        try {
            if (WebUtils.isOnline(context)) {

                String newCookie = WebUtils.loginService(preferences.getString(AppConstants.Prefs.URL, ""),
                        preferences.getString(AppConstants.Prefs.SERV_USR, ""),
                        preferences.getString(AppConstants.Prefs.SERV_PASS, ""));
                if (newCookie != null) {
                    FidelizacionApplication.getInstance().setCookie(newCookie);
                    cookie = newCookie;
                } else {
                    cookie = FidelizacionApplication.getInstance().getCookie();
                }
                boolean isUserLogged = numDocumento != null;

                //Validamos si hay algún cambio en los items
                CommonServices.getCompleteItems(connectionBarComplete, preferences, context, cookie);

                String urlBar = WebServs.PROTOCOL + preferences.getString(Prefs.URL, "") + WebServs.LIST_BAR;

                PremiosBarBody body=new PremiosBarBody(preferences.getString(Prefs.ID_CASINO, ""),AppConstants.Generic.STRING_NO);

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                connectionBar = WebUtils.createHttpsConnectionJson(urlBar, cookie);
                OutputStreamWriter wr = new OutputStreamWriter(connectionBar.getOutputStream(), "UTF-8");
                wr.write(gson.toJson(body));
                wr.close();
                //}
                //ambos servicios deben traer lo mismo
                StringBuilder responseBar = new StringBuilder();
                int statusBar = connectionBar.getResponseCode();
                BufferedReader rdBar
                        = new BufferedReader(new InputStreamReader(statusBar < 400 ? connectionBar.getInputStream() : connectionBar.getErrorStream()));
                String lineBar;
                while ((lineBar = rdBar.readLine()) != null) {
                    responseBar.append(lineBar);
                }

                JSONObject jsonListBar = new JSONObject(responseBar.toString());
                JSONObject jsonStatusListBar = jsonListBar.getJSONObject(AppConstants.WebParams.STATUS);
                barInfo.setResult(jsonStatusListBar.getString(AppConstants.WebParams.CODE));

                if (jsonListBar != null &&
                        barInfo.getResult().equals(WebResult.OK)) {
                    saveListInBarInfo(jsonListBar.getJSONArray(WebParams.BAR_LIST), barInfo);
                }

                //consulta a servicio de pedidos
                StringBuilder strBuilderOrders = new StringBuilder();
                String urlPedidos = WebServs.PROTOCOL + preferences.getString(Prefs.URL, "") + WebServs.ORDERS_USER_ESTATE;

                FidelizarClienteBody bodyF=new FidelizarClienteBody();
                bodyF.setSerial( preferences.getString(Prefs.SERIAL, ""));
                if (isUserLogged) {
                    bodyF.setNumeroDocumento(numDocumento);
                }

                connectionOrders = WebUtils.createHttpsConnectionJson(urlPedidos,cookie);
                OutputStreamWriter wrO = new OutputStreamWriter(connectionOrders.getOutputStream(), "UTF-8");
                wrO.write(gson.toJson(bodyF));
                wrO.close();
                int statusOrders = connectionOrders.getResponseCode();

                BufferedReader rdOrders= new BufferedReader(new InputStreamReader(statusOrders < 400 ? connectionOrders.getInputStream() : connectionOrders.getErrorStream()));
                StringBuilder responseOrders = new StringBuilder();
                String lineOrders;
                while ((lineOrders = rdOrders.readLine()) != null) {
                    responseOrders.append(lineOrders);
                }

                JSONObject jsonOrders = new JSONObject(responseOrders.toString());
                JSONObject jsonStatusOrders = jsonOrders.getJSONObject(AppConstants.WebParams.STATUS);

                if (jsonOrders != null &&
                        barInfo.getResult().equals(WebResult.OK)) {

                    if (WebResult.SESSION_EXPIRED.equals(jsonStatusOrders.getString(AppConstants.WebParams.CODE)))
                        barInfo.setResult(jsonStatusOrders.getString(AppConstants.WebParams.CODE));

                    JSONArray jsonOrdersList = jsonOrders.getJSONArray(WebParams.ORDERS);
                    int size = jsonOrdersList.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject jsonOrder = jsonOrdersList.getJSONObject(i);
                        OrderState orderState = new OrderState(jsonOrder.getString(WebParams.ORDER_ID));
                        orderState.setConfirmable(jsonOrder.getBoolean(WebParams.ORDER_CONFIRM));
                        orderState.setNullable(jsonOrder.getBoolean(WebParams.ORDER_ABORT));
                        orderState.setState(jsonOrder.getString(WebParams.ORDER_STATE));
                        orderState.setIdBarItem(jsonOrder.getString(WebParams.ORDER_ID_ITEM));
                        if (!jsonOrder.isNull(WebParams.ORDER_NOTE)) {
                            orderState.setNote(jsonOrder.getString(WebParams.ORDER_NOTE));
                        }
                        //TODO si eun item no tiene valores disponibles deberia venir para poder asignarle una orden en caso que la tenga
                        barInfo.addOrderToItem(orderState);
                        barInfo.addPaymentToItem(jsonOrder.getString(WebParams.ORDER_ID_ITEM),jsonOrder.getString(WebParams.BAR_ITEM_PAYMENT));
                    }
                }

                //consulta a servicio de puntos de usuario, usa los mismo parametros q el de premios
                if (isUserLogged) {
                    StringBuilder strBuilderPuntos = new StringBuilder();
                    WebUtils.addParam(strBuilderPuntos, WebParams.DOC, numDocumento);
                    WebUtils.addParam(strBuilderPuntos, WebParams.CASINO_CODE, preferences.getString(Prefs.ID_CASINO, ""));
                    WebUtils.addParam(strBuilderPuntos, WebParams.SERIAL, preferences.getString(Prefs.SERIAL, ""));

                    PuntosBody bodyP=new PuntosBody(numDocumento, preferences.getString(Prefs.SERIAL, ""),preferences.getString(Prefs.ID_CASINO, ""));
                    connectionPuntos = WebUtils.createHttpsConnectionJson(WebServs.PROTOCOL + preferences.getString(Prefs.URL, "") + WebServs.POINTS_DAY ,cookie);
                    OutputStreamWriter wrP = new OutputStreamWriter(connectionPuntos.getOutputStream(), "UTF-8");
                    wrP.write(gson.toJson(bodyP));
                    wrP.close();

                    int statusPoints = connectionPuntos.getResponseCode();

                    BufferedReader rdPoints
                            = new BufferedReader(new InputStreamReader(statusPoints < 400 ? connectionPuntos.getInputStream() : connectionPuntos.getErrorStream()));
                    StringBuilder responsePoints = new StringBuilder();
                    String linePoints;
                    while ((linePoints = rdPoints.readLine()) != null) {
                        responsePoints.append(linePoints);
                    }

                    JSONObject jsonPoints = new JSONObject(responsePoints.toString());
                    JSONObject jsonStatusPoint = jsonPoints.getJSONObject(AppConstants.WebParams.STATUS);
                    barInfo.setResult(jsonStatusPoint.getString(AppConstants.WebParams.CODE));
                    if (jsonPoints != null &&
                            barInfo.getResult().equals(WebResult.OK)) {
                        barInfo.setAvaliablePoints(jsonPoints.getString(WebParams.AVALIABLE_POINTS));
                    }
                }
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

            if (null != connectionPuntos) {
                connectionPuntos.disconnect();
            }

            if (null != connectionOrders) {
                connectionOrders.disconnect();
            }

            if (null != connectionBar) {
                connectionBar.disconnect();
            }

            if (null != connectionBarComplete) {
                connectionBar.disconnect();
            }
        }

        return barInfo;
    }


    @Override
    protected void onPostExecute(BarInfo barInfo) {

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        if (binder != null) {
            binder.barInfoFinish(barInfo);
        }
        super.onPostExecute(barInfo);
    }

    private void saveListInBarInfo(JSONArray jsonArray, BarInfo barInfo) throws JSONException {
        int size = jsonArray.length();
        List<BarItem> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JSONObject jsonBarItem = jsonArray.getJSONObject(i);
            items.add(new BarItem(jsonBarItem.getString(WebParams.BAR_ID), Integer.valueOf(jsonBarItem.getString(WebParams.BAR_NUM_AVAILABLE))));
        }

        barInfo.setListAvaliableItems(items);
    }

}
