package co.com.ies.fidelizacioncliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskAskBilletero;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskCallService;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskCargarBilletero;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskCargarMaquina;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskEnviarClave;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskEnviarClaveCorreo;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskPagarPremio;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskRedimirBilletero;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskVerPremios;
import co.com.ies.fidelizacioncliente.base.ActivityBase;
import co.com.ies.fidelizacioncliente.dialog.DialogFragClave;
import co.com.ies.fidelizacioncliente.dialog.DialogFragConfirm;
import co.com.ies.fidelizacioncliente.dialog.DialogFragOnlyConfirm;
import co.com.ies.fidelizacioncliente.entity.PremioInfo;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;

public class ActivityCashless extends ActivityBase implements DialogFragConfirm.NoticeDialogActionsListener , DialogFragClave.ClaveDinamicaDialogActionsListener, DialogFragOnlyConfirm.ActionsListener{


    private boolean serviceAsked;

    private TextView txtNombre;
    private TextView txtBilletero;
    private ImageButton btnClose;
    private ImageButton btnBack;
    private ImageView imgCallService;

    private AsyncTaskCallService.AsyncResponse responseCallService;
    private AsyncTaskEnviarClave.AsyncResponse responseEnviarClave;
    private AsyncTaskAskBilletero.AsyncResponse responseConsultarBilletero;
    private AsyncTaskCargarBilletero.AsyncResponse responseCargarBilletero;
    private AsyncTaskRedimirBilletero.AsyncResponse responseRedimirBilletero;
    private AsyncTaskCargarMaquina.AsyncResponse responseCargarMaquina;
    private AsyncTaskEnviarClaveCorreo.AsyncResponse responseEnviarClaveCorreo;
    private AsyncTaskVerPremios.AsyncResponse responseVerPremios;
    private AsyncTaskPagarPremio.AsyncResponse responsePagarPremio;

    private SharedPreferences preferences;
    private NumberFormat numberFormatter;
    private String docUSR,claveUSR, valorBilletero="0", valorPremio="0",valorOldBilletero="0.0",valorActBilletero="0.0";
    private int action,actionConfirm, conteoResend;
    private boolean DEVOLVERDINERO=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashless);

        Locale locale = new Locale("es", "CO");
        numberFormatter = NumberFormat.getNumberInstance(locale);

        numberFormatter.setMaximumFractionDigits(2);
        numberFormatter.setMinimumFractionDigits(2);


        componentes();
        establecerInterfaz();
        establecerResponseAsyncTask();


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void componentes(){
        /*  Se establecen los componentes de la interfaz
         * */
        txtNombre=(TextView) findViewById(R.id.act_user_txt_name);
        txtBilletero=(TextView) findViewById(R.id.act_user_txt_billetero);
        btnClose=(ImageButton) findViewById(R.id.act_user_btn_logout);
        btnBack=(ImageButton) findViewById(R.id.act_user_btn_back);
        imgCallService = (ImageView) findViewById(R.id.act_user_img_service);
        preferences = SharedPrefUtils.getSharedPreference(this, AppConstants.Prefs.SERVICE_PREF);

    }

    public void establecerInterfaz(){
        valorBilletero=getIntent().getExtras().getString(AppConstants.Generic.VALOR_BILLETERO,"");
        docUSR=FidelizacionApplication.getInstance().getUserDoc();
        claveUSR=getIntent().getExtras().getString(AppConstants.Generic.CLAVE_CLIENTE, "");
        serviceAsked=getIntent().getExtras().getBoolean(AppConstants.Generic.SERVICE_ASKED,false);
        if (!serviceAsked) {
            imgCallService.setVisibility(View.INVISIBLE);
        } else {
            imgCallService.setVisibility(View.VISIBLE);
        }

        /*  Se inicializan componentes de la interfaz
         * */
        txtNombre.setText(FidelizacionApplication.getInstance().getUserName());
        double bill=Double.valueOf(valorBilletero);
        txtBilletero.setText(numberFormatter.format(bill));

        /* Se habilitan o deshabilitan botones segun el tipo del USO conigurado
        * */
        if(preferences.getInt(AppConstants.Prefs.USO, 0)==AppConstants.ServiceType.CASHLESS){
            btnBack.setVisibility(View.GONE);
            btnClose.setVisibility(View.VISIBLE);
        }else{
            btnBack.setVisibility(View.VISIBLE);
            btnClose.setVisibility(View.GONE);
        }
    }

    public void establecerResponseAsyncTask(){
        /*______________CALL SERVICE_______________*/
        responseCallService = new AsyncTaskCallService.AsyncResponse() {
            @Override
            public void onCallServiceFinish(String[] codigoEstado) {

                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        if (serviceAsked) {
                            MsgUtils.biggetToast(ActivityCashless.this, getString(R.string.act_user_call_attendant_done));
                            imgCallService.setVisibility(View.INVISIBLE);
                        } else {
                            MsgUtils.biggetToast(ActivityCashless.this, getString(R.string.act_user_call_attendant_waiting));
                            imgCallService.setVisibility(View.VISIBLE);

                        }
                        serviceAsked = !serviceAsked;
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        break;

                }

            }
        };

        /*______________ENVIAR CLAVE_______________*/
        responseEnviarClave= new AsyncTaskEnviarClave.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        claveUSR=codigoEstado[2];
                        dialogClaveDinamica(claveUSR,action,conteoResend);
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        break;
                }
            }
        };

        /*______ENVIAR CLAVE DINAMICA EMAIL_____*/
        responseEnviarClaveCorreo= new AsyncTaskEnviarClaveCorreo.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        claveUSR=codigoEstado[2];
                        dialogClaveDinamica(claveUSR,action, conteoResend);
                        break;
                    case AppConstants.WebResult.FAIL:
                        Toast.makeText(getApplicationContext(), codigoEstado[1], Toast.LENGTH_LONG).show();
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        break;

                }


            }
        };

        /*______________CONSULTAR BILLETERO_______________*/
        responseConsultarBilletero= new AsyncTaskAskBilletero.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {

                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        valorBilletero=codigoEstado[2];
                        double bill=Double.valueOf(valorBilletero);
                        txtBilletero.setText(numberFormatter.format(bill));
                        break;
                    case AppConstants.WebResult.CLAVE_VENCIDA:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),getString(R.string.message_clave_vencida));
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        break;

                }

            }
        };
        /*______________CARGAR BILLETERO_______________*/
        responseCargarBilletero= new AsyncTaskCargarBilletero.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        valorBilletero=codigoEstado[2];
                        try {
                            DecimalFormat dF = new DecimalFormat("0.00");
                            Number num = dF.parse(valorBilletero);
                            txtBilletero.setText(numberFormatter.format(num.doubleValue()));
                        } catch (Exception e) {
                            Log.i("CARGAR",e.toString());
                        }


                        break;
                    default:
                        valorBilletero="0.00";
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        break;

                }
            }
        };
        /*______________REDIMIR BILLETERO_______________*/
        responseRedimirBilletero = new AsyncTaskRedimirBilletero.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        //Cargar Maquina
                        valorOldBilletero=valorBilletero;
                        valorBilletero=codigoEstado[2];
                        try {
                            DecimalFormat dF = new DecimalFormat("0.00");
                            Number num = dF.parse(valorBilletero);
                            txtBilletero.setText(numberFormatter.format(num.doubleValue()));
                            Log.i("DESCARGAR",valorBilletero);
                        } catch (Exception e) {
                            Log.i("DESCARGAR",e.toString());
                        }
                        new AsyncTaskCargarMaquina(ActivityCashless.this, responseCargarMaquina).execute(codigoEstado[3]);
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        break;

                }
            }
        };
        /*______________CARGAR MAQUINA_______________*/
        responseCargarMaquina= new AsyncTaskCargarMaquina.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        DEVOLVERDINERO=false;
                        valorOldBilletero="0.00";
                        //double bill=Double.valueOf(valorBilletero);
                        //txtBilletero.setText(numberFormatter.format(bill));
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),getString(R.string.common_load_machine_ok));
                        break;
                    default:
                        DEVOLVERDINERO=true;
                        String mensajeMostrar=codigoEstado[1];
                        if(codigoEstado[0].equals(AppConstants.WebResult.MAQUINA_BLOQUEADA)){
                            mensajeMostrar=getString(R.string.common_error_no_carga_maquina);
                        }
                        showOnlyConfirmDialog(getSupportFragmentManager(), getString(R.string.common_alert),mensajeMostrar);
                        break;

                }
            }
        };

        /*________________VER PREMIO_________________*/
        responseVerPremios=new AsyncTaskVerPremios.AsyncResponse() {
            @Override
            public void processFinish(PremioInfo info) {
                switch (info.getResult()) {
                    case AppConstants.WebResult.OK:
                        //Si se obtuvo el premio y la accion es DESCARGAR
                        if(AppConstants.RESULT_DIALOG_DESCARGAR== action){
                            if(info.getInfo()!=null && info.getInfo().getConsecutivo()!=null && !info.getInfo().getConsecutivo().isEmpty()){
                                valorPremio=info.getInfo().getMontoReal()!=null?info.getInfo().getMontoReal():"0.00";
                                new AsyncTaskPagarPremio(ActivityCashless.this,responsePagarPremio).execute(info.getInfo().getConsecutivo());
                            }
                        }else if(AppConstants.RESULT_DIALOG_CARGAR== actionConfirm){
                            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),getString(R.string.common_error_cargar_maquina));
                        }
                        break;
                    default:
                        if(AppConstants.RESULT_DIALOG_CARGAR==actionConfirm){
                            action=AppConstants.RESULT_DIALOG_CARGAR;
                            enviarClaveDinamica();
                        }else{
                            valorPremio="0.00";
                            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),info.getMessage());
                        }
                        break;
                }
            }
        };

        /*________________PAGAR PREMIO_________________*/
        responsePagarPremio=new AsyncTaskPagarPremio.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        Log.i("PREMIO",valorPremio);
                        //Si se pudo pagar premio, cargar billetero
                        new AsyncTaskCargarBilletero(ActivityCashless.this,responseCargarBilletero).execute(docUSR,claveUSR,valorPremio, preferences.getString(AppConstants.Prefs.SERIAL, ""));
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        break;
                }
            }
        };

    }

    /*______________________________ON CLIC BOTONES  ___________________________________*/
    public void onClickBack(View view) {
        Intent i= new Intent();
        i.putExtra(AppConstants.Generic.SERVICE_ASKED, serviceAsked);
        i.putExtra(AppConstants.Generic.CLAVE_BILLETERA,claveUSR);
        setResult(AppConstants.RESULT_ACTIVITY_BACK,i);
        finish();
    }

    public void onClickCargarMaquina(View view) {
        actionConfirm=AppConstants.RESULT_DIALOG_CARGAR;
        MsgUtils.showSConfirmDialog(getSupportFragmentManager(), getString(R.string.common_load_machine), getString(R.string.common_load_machine_info));
    }

    public void onClickDescargarMaquina(View view) {
        action=AppConstants.RESULT_DIALOG_DESCARGAR;
        enviarClaveDinamica();
    }

    public void onClickCallService(View view) {
        new AsyncTaskCallService(this, responseCallService).execute(serviceAsked ?AppConstants.CallService.STOP_SERVICE :AppConstants.CallService.ASK_SERVICE);
    }

    public void onClickCloseSession(View view) {
        actionConfirm=AppConstants.RESULT_ACTIVITY_CLOSE_OK;
        MsgUtils.showSConfirmDialog(getSupportFragmentManager(), getString(R.string.common_sign_out), getString(R.string.common_exit));
    }

    public void onClickActualizarBilletero(View view) {
        consultarBilleteroCliente();
    }

    /*_______________________________INTERFACE DIALOG_____________________________________*/

    @Override
    public void onDialogConfirmOkClick(DialogFragment dialogFragment) {
        if(actionConfirm==AppConstants.RESULT_ACTIVITY_CLOSE_OK){
            setResult(AppConstants.RESULT_ACTIVITY_CLOSE_OK);
            finish();
        }else if(actionConfirm==AppConstants.RESULT_DIALOG_CARGAR){
            /*if (WebUtils.isOnline(ActivityCashless.this)) {
                new AsyncTaskVerPremios(ActivityCashless.this, responseVerPremios).execute();
            }*/
            action=AppConstants.RESULT_DIALOG_CARGAR;
            enviarClaveDinamica();
        }




    }

    @Override
    public void onDialogConfirmCancelClick(DialogFragment dialogFragment) {

    }

    @Override
    public void onDialogOnlyConfirm(DialogFragment dialogFragment) {
        Log.i("PLATA","DEVOLVER: "+DEVOLVERDINERO);

        if(DEVOLVERDINERO){
            try {
                DecimalFormat dF = new DecimalFormat("0.00");
                Number numAct = dF.parse(valorBilletero);
                Number numOld = dF.parse(valorOldBilletero);
                double num=numOld.doubleValue()-numAct.doubleValue();
                String sum=String.valueOf(num);
                //Si la maquina no se carga, devolver el valor del billetero
                new AsyncTaskCargarBilletero(ActivityCashless.this,responseCargarBilletero).execute(docUSR,claveUSR,sum,"");
                Log.i("DEVOLVER",sum);
            } catch (Exception e) {
                Log.i("DEVOLVER",e.toString());
            }

        }
    }
                //______CLAVE DINAMICA
    @Override
    public void onDialogConfirmClick(DialogFragment dialogFragment, int resultCode, String clave) {
        conteoResend=0;
        action=resultCode;
        if(AppConstants.RESULT_DIALOG_DESCARGAR== resultCode){
            claveUSR=clave;
            if(txtBilletero.getText().toString().isEmpty()){
                valorActBilletero="0.00";
            }else{
                valorActBilletero=txtBilletero.getText().toString();
            }
            if (WebUtils.isOnline(ActivityCashless.this)) {
                new AsyncTaskVerPremios(ActivityCashless.this, responseVerPremios).execute();
            }
        }else if(AppConstants.RESULT_DIALOG_CARGAR== resultCode){
            if (WebUtils.isOnline(ActivityCashless.this)) {

                try {
                    DecimalFormat dF = new DecimalFormat("0.00");
                    Number num = dF.parse(valorBilletero);
                    String entero=String.valueOf(num.intValue());
                    new AsyncTaskRedimirBilletero(ActivityCashless.this, responseRedimirBilletero).execute(docUSR, claveUSR, entero);
                } catch (Exception e) {
                    Log.i("REDIMIR",e.toString());
                }
            }
        }else {
            claveUSR="";
        }
    }

    @Override
    public void onDialogResendClick(DialogFragment dialogFragment) {
        conteoResend++;
        enviarClaveDinamica();
    }

    @Override
    public void onDialogResendEmailClick(DialogFragment dialogFragment) {
        conteoResend++;
        enviarClaveDinamicaEmail();

    }

    @Override
    public void onDialogCancelClick(DialogFragment dialogFragment) {

    }

    /*______________MODAL CLAVE DINAMICA___________*/
    public void enviarClaveDinamica(){
        if (WebUtils.isOnline(ActivityCashless.this)) {
            new AsyncTaskEnviarClave(ActivityCashless.this, responseEnviarClave).execute(docUSR);
        }
    }

    public void enviarClaveDinamicaEmail(){
        if (WebUtils.isOnline(ActivityCashless.this)) {
            new AsyncTaskEnviarClaveCorreo(ActivityCashless.this, responseEnviarClaveCorreo).execute(docUSR);
        }

    }

    public void consultarBilleteroCliente(){
        long doc;
        try {
            doc = numberFormatter.parse(FidelizacionApplication.getInstance().getUserDoc()).longValue();

            if (WebUtils.isOnline(ActivityCashless.this)) {
                new AsyncTaskAskBilletero(ActivityCashless.this,responseConsultarBilletero).execute(String.valueOf(doc), claveUSR);
            }
        } catch (ParseException e) {
            MsgUtils.handleException(e);
        }
    }

    public void dialogClaveDinamica(String clave, int action, int numResend){
        DialogFragment dialog = new DialogFragClave();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.WebParams.USER_CLAVE_BD, clave);
        bundle.putInt(AppConstants.RESULT_DIALOG,action);
        bundle.putInt(AppConstants.CONTEO_RESEND,numResend);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(),getResources().getString(R.string.act_login_dialog_clave));
    }


    /**
     * Ventana de dialogo con solo la opcion de confirmar
     *
     */
    public static void showOnlyConfirmDialog(FragmentManager supportFragManager, @NonNull String titulo, String mensaje) {

        DialogFragment newFragment = new DialogFragOnlyConfirm();
        Bundle args = new Bundle();
        args.putString(DialogFragOnlyConfirm.TITLE, titulo);
        if (mensaje != null) {
            args.putString(DialogFragOnlyConfirm.MSG, mensaje);
        }
        newFragment.setArguments(args);
        newFragment.show(supportFragManager, "dialogonlyconfirm");

    }

}
