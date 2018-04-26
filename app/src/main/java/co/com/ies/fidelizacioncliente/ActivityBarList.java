package co.com.ies.fidelizacioncliente;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskAskBarItem;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskBarInfo;
import co.com.ies.fidelizacioncliente.base.ActivityBase;
import co.com.ies.fidelizacioncliente.custom.list.AdapterCategories;
import co.com.ies.fidelizacioncliente.custom.list.AdapterListBarItems;
import co.com.ies.fidelizacioncliente.dialog.DialogFragConfirm;
import co.com.ies.fidelizacioncliente.dialog.DialogFragSearch;
import co.com.ies.fidelizacioncliente.entity.BarInfo;
import co.com.ies.fidelizacioncliente.entity.BarItem;
import co.com.ies.fidelizacioncliente.manager.ManagerStandard;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebResult;
import co.com.ies.fidelizacioncliente.utils.AppConstants.WebServs;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.StringUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Pantalla para mostrar lista de items del bar y órdenes relacionadas
 */
public class ActivityBarList extends ActivityBase implements
        DialogFragConfirm.NoticeDialogActionsListener,
        DialogFragSearch.OnSearchListener,
        AsyncTaskBarInfo.AsyncResponse,
        AsyncTaskAskBarItem.AsyncResponse,
        AdapterListBarItems.OnBarItemAction,
        AdapterCategories.ListenerItemClick {

    private final int ACTION_REDEEM_ITEM = 1;
    private final int ACTION_BUY_ITEM = 2;
    private final int ACTION_CANCEL_ITEM = 3;
    private final int ACTION_RECEIVED_ITEM = 4;
    private int currentAction;
    private boolean isRunningTimer = false;
    public static final String RESULT = "result";
    private static BarItem barItemProcess = null;
    private RecyclerView rvBarItems;
    private RecyclerView rvCategories;
    //  private TextView txtEmpty;
    private TextView txtPoints;
    private AdapterListBarItems adapterListBarItems;
    private ArrayList<String> listCategories;
    private AdapterCategories adapterCategories;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManagerCategories;
    private BarInfo barInfo;
    private TimerTask task;
    private Timer timer;
    private boolean isInsideCategory = false;
    private String filterCategory = "";
    String finalPoints ="";
    private Long redeemedPoints=(long) 0;
    private ManagerStandard managerStandard;
    private int timeToClose;

    private AdapterCategories.ListenerItemClick listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_list);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        timeToClose = SharedPrefUtils.getSharedPreference(this, AppConstants.Prefs.SERVICE_PREF).
                getInt(AppConstants.WebParams.CONFIG_TIME_CLOSE_BAR, AppConstants.Generic.TIME_TO_CLOSE_BAR);
        obtenerComponentes();
        startTimer();
        managerStandard = ManagerStandard.getInstance();

        rvCategories.setHasFixedSize(true);
        rvBarItems.setHasFixedSize(true);

        //
        layoutManagerCategories = new LinearLayoutManager(this);
        rvCategories.setLayoutManager(layoutManagerCategories);

        listCategories = managerStandard.getItemCategories(this);
        adapterCategories = new AdapterCategories(this, listCategories, this);
        rvCategories.setAdapter(adapterCategories);

        //
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        rvBarItems.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvBarItems.addItemDecoration(itemDecoration);
        rvCategories.addItemDecoration(itemDecoration);


        new AsyncTaskBarInfo(this, this).execute();
    }

    @Override
    protected void onStop() {
        stopTimer();
        super.onStop();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onDialogConfirmOkClick(DialogFragment dialogFragment) {

        switch (currentAction) {
            case ACTION_REDEEM_ITEM:
                stopTimer();
                new AsyncTaskAskBarItem(this, this).execute(barItemProcess.getId(), WebServs.ORDER_REDEEM);
                currentAction = 0;
                break;
            case ACTION_BUY_ITEM:
                stopTimer();
                new AsyncTaskAskBarItem(this, this).execute(barItemProcess.getId(), WebServs.ORDER_BUY);
                currentAction = 0;
                break;
            case ACTION_CANCEL_ITEM:
                stopTimer();
                new AsyncTaskAskBarItem(this, this).execute(barItemProcess.getOrderState().getId(), WebServs.ORDER_CANCEL);
                currentAction = 0;
                break;
            case ACTION_RECEIVED_ITEM:
                stopTimer();
                new AsyncTaskAskBarItem(this, this).execute(barItemProcess.getOrderState().getId(), WebServs.ORDER_RECEIVED);
                currentAction = 0;
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogConfirmCancelClick(DialogFragment dialogFragment) {
        currentAction = 0;
        startTimer();
    }

    @Override
    public void onSearch(String searchValue, List<String> categoriesSelected) {
        adapterListBarItems.filter(searchValue, categoriesSelected);
    }

    @Override
    public void onCancel() {
        adapterListBarItems.resetFilter();
    }

    @Override
    public void onBuyItem(BarItem barItem, int position) {
        startTimer();
        this.barItemProcess = barItem;
        currentAction = ACTION_BUY_ITEM;
        MsgUtils.showSConfirmDialog(getSupportFragmentManager(),
                getString(R.string.common_alert), getString(R.string.act_bar_list_buy_message, barItem.getName()));
    }

    @Override
    public void onRedeemItem(BarItem barItem, int position) {
        startTimer();
        this.barItemProcess = barItem;
        currentAction = ACTION_REDEEM_ITEM;
        MsgUtils.showSConfirmDialog(getSupportFragmentManager(),
                getString(R.string.common_alert), getString(R.string.act_bar_list_redeem_message, barItem.getName()));
    }

    @Override
    public void onCancelOrder(BarItem barItem, int position) {
        startTimer();
        this.barItemProcess = barItem;
        currentAction = ACTION_CANCEL_ITEM;
        MsgUtils.showSConfirmDialog(getSupportFragmentManager(),
                getString(R.string.common_alert), getString(R.string.act_bar_list_cancel_message, barItem.getName()));
    }

    @Override
    public void onOrderReceived(BarItem barItem, int position) {
        startTimer();
        this.barItemProcess = barItem;
        currentAction = ACTION_RECEIVED_ITEM;
        MsgUtils.showSConfirmDialog(getSupportFragmentManager(),
                getString(R.string.common_alert), getString(R.string.act_bar_list_received_message, barItem.getName()));

    }

    @Override
    public void onListItemClick(int index) {
        rvCategories.setVisibility(View.GONE);
        filterCategory = listCategories.get(index);
        adapterListBarItems.filter(filterCategory);
        adapterListBarItems.notifyDataSetChanged();
        rvBarItems.setVisibility(View.VISIBLE);
        isInsideCategory = true;
    }

    @Override
    public void barInfoFinish(BarInfo barInfo) {
        this.barInfo = barInfo;
        if (barInfo.getResult().equals(WebResult.OK)) {

            barInfo.cleanList();
            if (managerStandard.getBarItemDetails(this, barInfo.getListAvaliableItems())) {

                if (!barInfo.getListAvaliableItems().isEmpty()) {
                    Collections.sort(barInfo.getListAvaliableItems(), new Comparator<BarItem>() {
                        @Override
                        public int compare(BarItem lhs, BarItem rhs) {
                            int compItems = 0;

                            if ((lhs.getOrderState() != null && rhs.getOrderState() == null)) {
                                compItems = -1;
                            } else if ((lhs.getOrderState() == null && rhs.getOrderState() != null)) {
                                compItems = 1;
                            } /*else if ((lhs.getOrderState() == null && rhs.getOrderState() == null) ||
                                    (lhs.getOrderState() != null && rhs.getOrderState() != null)) {
                                compItems = 0;
                            }*/
                            if (compItems == 0) {
                                if (lhs.getOrderState() != null && rhs.getOrderState() != null) {
                                    if (lhs.getOrderState().getState().equals(AppConstants.OrderState.ON_WAY)
                                            && rhs.getOrderState().getState().equals(AppConstants.OrderState.ON_QUEUE)) {
                                        compItems = -1;
                                    } else if (lhs.getOrderState().getState().equals(AppConstants.OrderState.ON_QUEUE)
                                            && rhs.getOrderState().getState().equals(AppConstants.OrderState.ON_WAY)) {
                                        compItems = 1;
                                    }
                                }
                            }
                            return compItems;
                        }
                    });
                }


                listCategories = managerStandard.getItemCategories(this);
                adapterCategories = new AdapterCategories(this, listCategories, this);
                rvCategories.setAdapter(adapterCategories);

                //Verifica que items fueron redimidos para descontar el valor de los puntos
                redeemedPoints =(long) 0;//inicializar puntos redimidos
                if(!StringUtils.isNullOrEmpty(FidelizacionApplication.getInstance().getUserDoc())){
                    for(BarItem bi:barInfo.getListAvaliableItems()){
                        if(!StringUtils.isNullOrEmpty(bi.getPayment())){
                            if(bi.getPayment().equals(AppConstants.Generic.PAYMENT_POINTS)){//redimido
                                redeemedPoints+=Long.parseLong(bi.getPoints());
                            }
                        }
                    }
                    Long total=Long.parseLong(barInfo.getAvaliablePoints())-redeemedPoints;
                    finalPoints=String.valueOf(total);
                }else{
                    finalPoints=barInfo.getAvaliablePoints();
                }


                Log.i("TAG_COMPARE","   Redimidos: "+String.valueOf(redeemedPoints)+" Disponibles: "+barInfo.getAvaliablePoints());
                Log.i("TAG_TOTAL","     Total: "+finalPoints);

                List<BarItem> aux=barInfo.getListAvaliableItems();
                boolean userLog=!StringUtils.isNullOrEmpty(FidelizacionApplication.getInstance().getUserDoc());
                for(BarItem bi:aux){
                    Log.i("LISTA","--"+bi.getName());
                    if (bi.getOrderState() == null) {//Si es un item que se puede comprar/redimir
                        Log.i("LISTA","Se puede comprar/redimir");
                    }else{
                        Log.i("LISTA","Con pedido");
                    }

                    if (StringUtils.isNullOrEmpty(bi.getPoints()) || /*item puntos vacio*/
                            !userLog || /*usuario no logueado*/
                            (!StringUtils.isNullOrEmpty(bi.getPoints())/*item puntos*/ && userLog /*usuario logueado*/
                                    && Integer.valueOf(finalPoints) < Integer.valueOf(bi.getPoints())  /*item puntos > puntos usuario*/
                            )
                            ) {
                        Log.i("LISTA","NO REDIMIR");
                    }else{
                        Log.i("LISTA","REDIMIR");

                    }

                    if (StringUtils.isNullOrEmpty(bi.getPrice())) {
                        Log.i("LISTA","NO COMPRAR");
                    }
                    Log.i("LISTA","--------------------");


                }


                adapterListBarItems = new AdapterListBarItems(this, this, barInfo.getListAvaliableItems(),
                        !StringUtils.isNullOrEmpty(FidelizacionApplication.getInstance().getUserDoc()),finalPoints );
                if (!filterCategory.isEmpty())
                    adapterListBarItems.filter(filterCategory);
                rvBarItems.setAdapter(adapterListBarItems);

            }

            if (barInfo.getAvaliablePoints() != null) {
                txtPoints.setText(getString(R.string.act_bar_points, barInfo.getAvaliablePoints()));
                txtPoints.setVisibility(View.VISIBLE);
            }
            startTimer();

        } else if (barInfo.getResult().equals(WebResult.SESSION_EXPIRED)) {
            closeOnSessionExpired();
        } else if (barInfo.getResult().equals(WebResult.FAIL)) {
            backToValidateService();
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(RESULT, barInfo.getResult());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void askItemFinish(String codigoEstado, String process, String idItem) {

        if (codigoEstado.equals(WebResult.OK)) {
            /*switch (process) {
                case WebServs.ORDER_REDEEM:
                    barItemProcess.setOrderState(new OrderState());
                    break;
                case WebServs.ORDER_BUY:
                    barItemProcess.setOrderState(new OrderState());
                    break;
                case WebServs.ORDER_CANCEL:
                    barItemProcess.setOrderState(null);
                    break;
                case WebServs.ORDER_RECEIVED:
                    barItemProcess.setOrderState(null);
                    break;
            }

            adapterListBarItems.notifyDataSetChanged();*/
            new AsyncTaskBarInfo(this, this).execute();
        } else if (codigoEstado.equals(WebResult.SESSION_EXPIRED)) {
            closeOnSessionExpired();
        } else if (codigoEstado.equals(WebResult.FAIL)) {
            backToValidateService();
        } else {
            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.act_bar_fail_request));
        }
    }

    public void onClickBack(View view) {
        if (isInsideCategory) {
            rvBarItems.setVisibility(View.GONE);
            rvCategories.setVisibility(View.VISIBLE);
            isInsideCategory = false;
            filterCategory = "";
        } else {
            backToUser();
        }
    }

    public void onClickSearch(View view) {
        MsgUtils.showSearchDialog(getSupportFragmentManager(), listCategories);
    }

    private void obtenerComponentes() {
        txtPoints = (TextView) findViewById(R.id.act_bar_txt_points);
        rvBarItems = (RecyclerView) findViewById(R.id.act_bar_list);
        rvCategories = (RecyclerView) findViewById(R.id.act_bar_list_categories);
        // txtEmpty = (TextView) findViewById(R.id.act_bar_txt_empty);
    }

    private void backToUser() {
        if (barInfo.pendingOrders()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(RESULT, WebResult.OK);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }

    private void closeOnSessionExpired() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, WebResult.SESSION_EXPIRED);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void startTimer() {

        stopTimer();
        task = new TimerTask() {
            @Override
            public void run() {
                backToUser();
            }
        };
        timer = new Timer();
        timer.schedule(task, timeToClose);
    }

    private void stopTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

}
