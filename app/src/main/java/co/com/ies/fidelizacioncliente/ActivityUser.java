package co.com.ies.fidelizacioncliente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.asynctask.AsynTaskValidateUser;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskAskBilletero;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskCallService;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskCloseSession;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskEnviarClave;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskEnviarClaveCorreo;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskFidelizarCliente;
import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskUserPoints;
import co.com.ies.fidelizacioncliente.base.ActivityBase;
import co.com.ies.fidelizacioncliente.custom.keyboard.LetterNumberKeyboard;
import co.com.ies.fidelizacioncliente.dialog.DialogFragClave;
import co.com.ies.fidelizacioncliente.dialog.DialogFragConfirm;
import co.com.ies.fidelizacioncliente.entity.EstadoSolicitudEnum;
import co.com.ies.fidelizacioncliente.manager.ManagerStandard;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.PermissionUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.StringUtils;
import co.com.ies.fidelizacioncliente.utils.WebUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;

/**
 * Pantalla destinada a interactuar con el usuario de la máquina
 * Iniciar sesión (fidelizar)
 * Ir al bar
 * Soliciar atención al cliente
 * Mostrar logo del casino en caso de solo ser usado para bar
 */
public class ActivityUser extends ActivityBase implements DialogFragConfirm.NoticeDialogActionsListener, DialogFragClave.ClaveDinamicaDialogActionsListener {

    private final static int ACTION_GET_ITEM = 1;
    private final static int ACTION_CASHLESS = 2;
    private final static int ACTION_CLOSE_SESSION = 10;
    private boolean ALLOW_VIDEO = true; //variable para saber si se puede mostrar el video en determinado momento

    private int currentAction,claveAction;
    public boolean claveCash, claveEnable;

    private LinearLayout lytLogin;
    private RelativeLayout lytUser;
    private RelativeLayout lytCasino;
    private GridLayout gridNumberPad;

    private final static int CONSTANTE_MILISEGUNDOS=1000;
    private final static int CONSTANTE_SEGUNDOS=60;
    private final static int CONSTANTE_REPETICION_VIDEO=5;

    private TextView txtNombre;
    private TextView txtPuntos;
    private TextView txtPuntosRedim;
    private EditText edtDoc;
    private TextView txtCasino;
    private TextView txtNumMaquina;
    private ImageButton btnCloseSession;
    private Button btnBorrar;
    private ImageButton btnKeyBoardShow;
    private ImageView imgCasino;
    private ImageButton btnBar;
    private ImageButton btnCashless;
    //private ImageButton btnCallService;
    private ImageButton btnMenu;
    private ImageView imgCallService;
    private ImageView imgBar;

    private boolean serviceAsked;
    //todo setear de manera correcta el parametro
    private boolean paramEnableKeyboard;
    private boolean paramShowVideo;//parametro para validar si se muestra video
    private SharedPreferences preferences;
    private AsyncTaskUserPoints asyncTaskUserPoints;
    private AsyncTaskEnviarClave asyncTaskEnviarClave;
    private AsyncTaskFidelizarCliente asyncTaskFidelizarCliente;
    private int serviceType, conteoResend;
    private String url;
    private String casinoCode;
    private String serial;
    private String claveCliente;
    private String enableBilletero;
    private String valorBilletero;
    private String idsolicitud="";

    private TimerTask taskPoints;
    private TimerTask taskVideo;

    private AsyncTaskUserPoints.AsyncResponse responseUserPoints;
    private AsyncTaskCloseSession.AsyncResponse responseCloseSession;
    private AsynTaskValidateUser.AsyncResponse responseValidateUser;
    private AsyncTaskCallService.AsyncResponse responseCallService;
    private AsyncTaskEnviarClave.AsyncResponse responseEnviarClave;
    private AsyncTaskEnviarClaveCorreo.AsyncResponse responseEnviarClaveCorreo;
    private AsyncTaskAskBilletero.AsyncResponse responseConsultarBilletero;
    private AsyncTaskFidelizarCliente.AsyncResponse responseFidelizarCliente;
    private LetterNumberKeyboard.OnOkeyClickListener keyboardListener;

    private Locale locale;
    private NumberFormat numberFormatter;
    private LetterNumberKeyboard numberKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        obtenerComponentes();
        setListeners();
        initialize();
        paramShowVideo=true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!StringUtils.isNullOrEmpty(FidelizacionApplication.getInstance().getUserDoc())
                && taskPoints == null) {
            startAskingPoints();
        } //todo borrar para no mostrar video en login; else {
        startShowingVideo();
        //}
    }

    @Override
    protected void onStop() {
        if (serviceType == AppConstants.ServiceType.FIDELIZACION || serviceType == AppConstants.ServiceType.FIDELBAR || serviceType == AppConstants.ServiceType.FIDELBARCASH) {
            if (asyncTaskUserPoints != null) {
                asyncTaskUserPoints.cancel(true);
            }

            if (asyncTaskEnviarClave != null) {
                asyncTaskEnviarClave.cancel(true);
            }

            if (asyncTaskFidelizarCliente != null) {
                asyncTaskFidelizarCliente.cancel(true);
            }

            stopAskingPoints();
        }
        stopShowingVideo();
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        // NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity
        if (numberKeyboard.isCustomKeyboardVisible()) numberKeyboard.hideCustomKeyboard();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString(ActivityBarList.RESULT,"");
            if (result !=null && result.equals(AppConstants.WebResult.SESSION_EXPIRED)) {
                setVisibilityClosedSession();
                MsgUtils.biggetToast(this, getString(R.string.act_user_session_expired));
                ALLOW_VIDEO = true;
                startShowingVideo();
            } else if (result !=null && result.equals(AppConstants.WebResult.OK)) {
                imgBar.setVisibility(View.VISIBLE);
            } else if (result !=null && result.equals(AppConstants.WebResult.FAIL)) {
                MsgUtils.biggetToast(this, getString(R.string.act_user_no_bar));
            }
        }else if(resultCode == AppConstants.RESULT_ACTIVITY_BACK){
            serviceAsked=data.getExtras().getBoolean(AppConstants.Generic.SERVICE_ASKED,true);
            claveCliente=data.getExtras().getString(AppConstants.Generic.CLAVE_BILLETERA,"0");
            if (!serviceAsked) {
                imgCallService.setVisibility(View.INVISIBLE);
            } else {
                imgCallService.setVisibility(View.VISIBLE);
            }
            imgBar.setVisibility(GONE);
            /*if(taskPoints==null){
                startAskingPoints();
            }*/
        }else if(resultCode == AppConstants.RESULT_ACTIVITY_CLOSE_OK){
            currentAction = 0;
            ALLOW_VIDEO = false;
            new AsyncTaskCloseSession(this, responseCloseSession).execute();
        }else if(requestCode!=ACTION_CASHLESS){
            Toast.makeText(this, R.string.act_user_no_select_snack, Toast.LENGTH_LONG).show();
            imgBar.setVisibility(GONE);
        }

        //todo borrar para no mostrar video en login; if(!FidelizacionApplication.getInstance().isUserLogged())
        ALLOW_VIDEO = true;

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void obtenerComponentes() {

        lytLogin = (LinearLayout) findViewById(R.id.act_user_lyt_login);
        lytUser = (RelativeLayout) findViewById(R.id.act_user_lyt_fidel);
        lytCasino = (RelativeLayout) findViewById(R.id.act_user_lyt_casino);
        gridNumberPad = (GridLayout) findViewById(R.id.act_user_grid_number);
        //componente fidelización
        txtNombre = (TextView) findViewById(R.id.act_user_txt_name);
        txtPuntos = (TextView) findViewById(R.id.act_user_txt_points);
        txtPuntosRedim = (TextView) findViewById(R.id.act_user_txt_points_redeemed);
        btnCloseSession = (ImageButton) findViewById(R.id.act_user_btn_close_session);
        txtNumMaquina=(TextView) findViewById(R.id.act_num_maquina);

        //componente casino
        txtCasino = (TextView) findViewById(R.id.act_user_txt_casino);
        imgCasino = (ImageView) findViewById(R.id.act_user_img_casino);

        //componentes asistencia
        btnBar = (ImageButton) findViewById(R.id.act_user_btn_bar);
        btnCashless =(ImageButton) findViewById(R.id.act_user_btn_cashless);
        //btnCallService = (ImageButton) findViewById(R.id.act_user_btn_service);
        btnMenu = (ImageButton) findViewById(R.id.act_user_btn_menu);
        btnKeyBoardShow = (ImageButton) findViewById(R.id.act_user_btn_keyboard);

        imgBar = (ImageView) findViewById(R.id.act_user_img_bar);
        imgCallService = (ImageView) findViewById(R.id.act_user_img_service);

        //componentes login
        edtDoc = (EditText) findViewById(R.id.act_user_txt_doc);
        btnBorrar = (Button) findViewById(R.id.act_user_btn_clear);
        edtDoc.setCustomSelectionActionModeCallback(new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {

            }
        });
    }

    /**
     * inicializamos las respuestas a los asynctasks
     */
    private void setListeners() {

        btnBorrar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtDoc.setText("");
                return true;
            }
        });

        responseUserPoints = new AsyncTaskUserPoints.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {

                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        setTextPoints(codigoEstado[2], codigoEstado[3]);
                        break;
                    case AppConstants.WebResult.FAIL:
                        backToValidateService();
                        break;
                    case AppConstants.WebResult.SESSION_EXPIRED:
                        finishActivity(ACTION_CASHLESS);
                        setVisibilityClosedSession();
                        MsgUtils.biggetToast(ActivityUser.this, getString(R.string.act_user_session_expired));
                        break;
                    default:
                        String noPoints = getString(R.string.act_user_no_points);
                        setTextPoints(noPoints, noPoints);
                        break;

                }
            }
        };

        responseCloseSession = new AsyncTaskCloseSession.AsyncResponse() {
            @Override
            public void processFinish(String codigoEstado) {

                if (codigoEstado.equals(AppConstants.WebResult.FAIL)) {
                    ALLOW_VIDEO = false;
                    backToValidateService();
                } else {
                    ALLOW_VIDEO = true;
                    startShowingVideo();
                    setVisibilityClosedSession();
                }
            }
        };

        responseValidateUser = new AsynTaskValidateUser.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        claveEnable=!codigoEstado[2].equals("0");
                        enableBilletero=codigoEstado[3];
                        if(codigoEstado[2].equals("0") && serviceType!=AppConstants.ServiceType.CASHLESS){//NO HAY CLAVE DINAMICA Y EL USO ES CUALQUIERA MENOS CASHLESS
                            SharedPreferences preferences = SharedPrefUtils.getSharedPreference(ActivityUser.this, AppConstants.Prefs.SERVICE_PREF);
                            MsgUtils.biggetToast(ActivityUser.this,getString(R.string.act_login_welcome,FidelizacionApplication.getInstance().getUserName(),
                                            preferences.getString(AppConstants.Prefs.NOM_CASINO, "")));
                            startAskingPoints();
                            //borrar para no mostrar video en login; ALLOW_VIDEO = false;
                            //borrar para no mostrar video en login; stopShowingVideo();
                            setVisibilityUserLogged();
                            ALLOW_VIDEO = false;//borrar para no mostrar video en login;
                            stopShowingVideo();
                        }else if(enableBilletero==AppConstants.WebResult.FAIL && serviceType==AppConstants.ServiceType.CASHLESS){
                            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),getString(R.string.common_user_no_cashless));
                            //CERRAR SESION
                            closeSesion();
                        }else if (claveEnable){//HAY CLAVE DINAMICA Y EL USO ES DIFERENTE  A CASHLESS
                            //mostrar dialogo para ingresar campo de la clave
                            claveCash=false;
                            dialogClaveDinamica(codigoEstado[2],AppConstants.RESULT_DIALOG_NONE,0);
                        }

                        break;
                    case AppConstants.WebResult.BLOCKED_CLIENT:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),
                                getString(R.string.mesage_client_blocked));
                        ALLOW_VIDEO = true;
                        break;
                    case AppConstants.WebResult.CLIENT_NOT_FOUND:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),
                                getString(R.string.message_client_not_found));
                        //todo borrar para no mostrar video en login; ALLOW_VIDEO = true;
                        break;
                    case AppConstants.WebResult.FAIL:
                        ALLOW_VIDEO = false;
                        backToValidateService();
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),
                                codigoEstado[1]);
                        ALLOW_VIDEO = true;
                        break;

                }

            }
        };

        responseCallService = new AsyncTaskCallService.AsyncResponse() {
            @Override
            public void onCallServiceFinish(String[] codigoEstado) {

                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        if (serviceAsked) {
                            MsgUtils.biggetToast(ActivityUser.this, getString(R.string.act_user_call_attendant_done));
                            imgCallService.setVisibility(View.INVISIBLE);
                            idsolicitud="";
                        } else {
                            MsgUtils.biggetToast(ActivityUser.this, getString(R.string.act_user_call_attendant_waiting));
                            imgCallService.setVisibility(View.VISIBLE);
                            idsolicitud=codigoEstado[2];
                        }
                        serviceAsked = !serviceAsked;
                        //todo borrar para no mostrar video en login; if(!FidelizacionApplication.getInstance().isUserLogged())
                        ALLOW_VIDEO = true;
                        break;
                    case AppConstants.WebResult.FAIL:
                        ALLOW_VIDEO = false;
                        backToValidateService();
                        idsolicitud="";
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),
                                codigoEstado[1]);
                        //todo borrar para no mostrar video en login; if(!FidelizacionApplication.getInstance().isUserLogged())
                        ALLOW_VIDEO = true;
                        idsolicitud="";
                        break;

                }

            }
        };

        keyboardListener = new LetterNumberKeyboard.OnOkeyClickListener() {
            @Override
            public void onOkClick() {
                claveAction=ACTION_CLOSE_SESSION;
                startFidelizacion();
            }
        };

        /*______ENVIAR CLAVE DINAMICA_____*/
        responseEnviarClave= new AsyncTaskEnviarClave.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        dialogClaveDinamica(codigoEstado[2],AppConstants.RESULT_DIALOG_NONE,conteoResend);
                        break;
                    case AppConstants.WebResult.FAIL:
                        ALLOW_VIDEO = false;
                        backToValidateService();
                        Toast.makeText(getApplicationContext(), codigoEstado[1], Toast.LENGTH_LONG).show();
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        ALLOW_VIDEO = true;
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
                        dialogClaveDinamica(codigoEstado[2],AppConstants.RESULT_DIALOG_NONE,conteoResend);
                        break;
                    case AppConstants.WebResult.FAIL:
                        ALLOW_VIDEO = false;
                        backToValidateService();
                        Toast.makeText(getApplicationContext(), codigoEstado[1], Toast.LENGTH_LONG).show();
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        ALLOW_VIDEO = true;
                        break;
                }
            }
        };


        /*______Fideliza el cliente_______*/
        responseFidelizarCliente= new AsyncTaskFidelizarCliente.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        SharedPreferences preferences = SharedPrefUtils.getSharedPreference(ActivityUser.this, AppConstants.Prefs.SERVICE_PREF);
                        MsgUtils.biggetToast(ActivityUser.this,getString(R.string.act_login_welcome,FidelizacionApplication.getInstance().getUserName(), preferences.getString(AppConstants.Prefs.NOM_CASINO, "")));
                        startAskingPoints();
                        //Si el uso es CASHLESS
                        if(serviceType==AppConstants.ServiceType.CASHLESS){
                            consultarBilleteroCliente();
                            //iniciarCashless();
                        }else{
                            setVisibilityUserLogged();
                        }
                        break;
                    case AppConstants.WebResult.FAIL:
                        ALLOW_VIDEO = false;
                        backToValidateService();
                        Toast.makeText(getApplicationContext(), codigoEstado[1], Toast.LENGTH_LONG).show();
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        //todo borrar para no mostrar video en login; if(!FidelizacionApplication.getInstance().isUserLogged())
                        ALLOW_VIDEO = true;
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
                        iniciarCashless();
                        break;
                    case AppConstants.WebResult.CLAVE_VENCIDA:
                        valorBilletero="";
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),
                                getString(R.string.message_clave_vencida));
                        Toast.makeText(getApplicationContext(), codigoEstado[1], Toast.LENGTH_LONG).show();
                        break;
                    default:
                        valorBilletero="";
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),codigoEstado[1]);
                        break;

                }

            }
        };


    }

    /**
     * seteamos las vistas de acuerdo al tipo de uso q tendrá la apk
     */
    private void initialize() {

        preferences = SharedPrefUtils.getSharedPreference(this, AppConstants.Prefs.SERVICE_PREF);

        ALLOW_VIDEO = true;
        paramEnableKeyboard = preferences.getBoolean(AppConstants.Prefs.SHOW_KEYBOARD, false);
        paramShowVideo = preferences.getBoolean(AppConstants.Prefs.SHOW_VIDEO, false);

        locale = new Locale("es", "CO");
        numberFormatter = NumberFormat.getNumberInstance(locale);

        numberFormatter.setMaximumFractionDigits(0);
        numberFormatter.setMinimumFractionDigits(0);

        StringBuilder numMaq=new StringBuilder(getString(R.string.act_num_maquina));
        numMaq.append(": ");
        numMaq.append(preferences.getString(AppConstants.Prefs.NUM_DISP,""));
        txtNumMaquina.setText(numMaq.toString());

        serviceType = preferences.getInt(AppConstants.Prefs.USO, 0);
        if (serviceType == AppConstants.ServiceType.FIDELIZACION) {
            setVisibilityFidelizacion();
        } else if (serviceType == AppConstants.ServiceType.BAR) {
            setVisibilityBar();
        } else if (serviceType == AppConstants.ServiceType.FIDELBAR || serviceType == AppConstants.ServiceType.FIDELBARCASH) {
            setVisibilityFidelBar();
        } else if(serviceType == AppConstants.ServiceType.CASHLESS){
            setVisibilityCashless();
        }
    }

    //______________________________________________________________________________METODOS ON-CLICK

    public void onClickMenu(View view) {
        accessToSettings();
    }

    public void onClickCloseSession(View view) {
        ALLOW_VIDEO = false;
        MsgUtils.showSConfirmDialog(getSupportFragmentManager(), getString(R.string.common_sign_out), getString(R.string.common_exit));
        currentAction = ACTION_CLOSE_SESSION;
    }

    public void onClickCallService(View view) {
        ALLOW_VIDEO = false;
        String estadoS=serviceAsked ?EstadoSolicitudEnum.CANCELADA.toString():EstadoSolicitudEnum.PENDIENTE.toString();
        String docUser=FidelizacionApplication.getInstance().getUserDoc();
        if(docUser== null ){
            docUser="";
        }
        new AsyncTaskCallService(this, responseCallService).execute(docUser,estadoS,idsolicitud);

    }

    public void onClickAskItem(View view) {

        if (ManagerStandard.getInstance().existBarItems(this)) {
            ALLOW_VIDEO = false;
            stopShowingVideo();
            startActivityForResult(new Intent(this, ActivityBarList.class), ACTION_GET_ITEM);
        } else {
            Toast.makeText(this, R.string.act_user_no_bar_items, Toast.LENGTH_LONG).show();
        }
    }

    public void onClickCashless(View view) {
        claveAction=ACTION_CASHLESS;
        if(claveEnable){
            ALLOW_VIDEO = false;
            consultarBilleteroCliente();

            /*claveCash=true;
            enviarClaveDinamica();*/
        }else{
            claveCash=false;
            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert),getString(R.string.common_no_clave));
        }

    }

    public void onClickClear(View view) {

        String doc = edtDoc.getText().toString();

        //manera sencilla, sin formato
        /*if (!doc.isEmpty()) {
            doc = doc.substring(0, doc.length() - 1);
            edtDoc.setText(doc);
        }*/
        //manera con formato
        if (!doc.isEmpty()) {
            if (paramEnableKeyboard) {
                int start = edtDoc.getSelectionStart();
                edtDoc.getText().delete(start - 1, start);
            } else {
                try {
                    long value = numberFormatter.parse(doc).longValue();
                    String docFinal = String.valueOf(value);
                    docFinal = docFinal.substring(0, docFinal.length() - 1);

                    if (docFinal.isEmpty()) {
                        edtDoc.setText(docFinal);
                    } else {
                        long valueFinal = Long.valueOf(docFinal);
                        edtDoc.setText(numberFormatter.format(valueFinal));
                    }
                } catch (ParseException e) {
                    MsgUtils.handleException(e);
                }
            }
        }
    }

    public void onClickTeclado(View view) {

        switch (view.getId()) {
            case R.id.act_user_btn_1:
                setDoc("1");
                break;
            case R.id.act_user_btn_2:
                setDoc("2");
                break;
            case R.id.act_user_btn_3:
                setDoc("3");
                break;
            case R.id.act_user_btn_4:
                setDoc("4");
                break;
            case R.id.act_user_btn_5:
                setDoc("5");
                break;
            case R.id.act_user_btn_6:
                setDoc("6");
                break;
            case R.id.act_user_btn_7:
                setDoc("7");
                break;
            case R.id.act_user_btn_8:
                setDoc("8");
                break;
            case R.id.act_user_btn_9:
                setDoc("9");
                break;
            case R.id.act_user_btn_0:
                setDoc("0");
                break;
            /*case R.id.act_user_btn_ast:
                setDoc("*");
                break;
            case R.id.act_user_btn_num:
                setDoc("#");
                break;*/
            default:
                break;
        }

    }

    public void onClickLoginFidelizacion(View view) {
        claveAction=ACTION_CLOSE_SESSION;
        startFidelizacion();
    }

    public void onClickKeyboard(View view) {
        setVisibilityKeyBoard();

    }

    private void setTextPoints(String puntosDisp, String puntosRedim) {
        txtPuntos.setText(puntosDisp);
        txtPuntosRedim.setText(puntosRedim);
    }

    /**
     * Metodo usado para agregar caracteres desde el teclado númerico
     */
    private void setDoc(String digit) {
        //para realizarlo simple se usa esta forma
        // edtDoc.setText(edtDoc.getText().toString() + digit);
        //para realizarla con formato
        String currentDoc = edtDoc.getText().toString();

        if (StringUtils.isNullOrEmpty(currentDoc)) {
            edtDoc.setText(digit);
        } else {
            try {
                long value = numberFormatter.parse(currentDoc).longValue();
                if (value < AppConstants.Generic.MAX_VALUE_DOC) {
                    Long valueFinal = Long.valueOf(String.valueOf(value) + digit);
                    edtDoc.setText(numberFormatter.format(valueFinal));
                }
            } catch (ParseException e) {
                MsgUtils.handleException(e);
            }
        }
    }

    private void configInitialKeyboard() {
        if (paramEnableKeyboard) {
            gridNumberPad.setVisibility(GONE);
            numberKeyboard = new LetterNumberKeyboard(this, R.id.keyboardview, R.xml.keyboard_lettersnumbers, keyboardListener);
            numberKeyboard.registerTextView(edtDoc);

        } else {
            edtDoc.setEnabled(false);
            btnKeyBoardShow.setVisibility(GONE);
        }
    }

    //______________________________________________________________________________METODOS SET-VISIBILITY

    private void setVisibilityFidelizacion() {
        lytCasino.setVisibility(GONE);
        btnBar.setVisibility(View.INVISIBLE);
        lytUser.setVisibility(GONE);
        btnBar.setVisibility(GONE);
        btnCashless.setVisibility(View.GONE);
        lytLogin.setVisibility(View.VISIBLE);

        configInitialKeyboard();
    }

    private void setVisibilityBar() {
        lytUser.setVisibility(GONE);
        lytLogin.setVisibility(GONE);
        btnCashless.setVisibility(View.GONE);
        File fileLogo = new File(getExternalFilesDir(null), AppConstants.FileExtension.LOGO_CASINO_NAME);
        if (fileLogo.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(fileLogo.getAbsolutePath());
            imgCasino.setImageBitmap(myBitmap);
            imgCasino.setVisibility(View.VISIBLE);
            txtCasino.setVisibility(GONE);
        } else {
            txtCasino.setText(
                    getString(R.string.act_user_only_bar, preferences.getString(AppConstants.Prefs.NOM_CASINO, "")));
            imgCasino.setVisibility(GONE);
        }
        btnMenu.setImageResource(R.drawable.ic_settings_white_36dp);

    }

    private void setVisibilityFidelBar() {
        lytCasino.setVisibility(GONE);
        btnBar.setVisibility(View.VISIBLE);
        btnCashless.setVisibility(View.GONE);
        configInitialKeyboard();
    }

    private void setVisibilityCashless() {

        btnBar.setVisibility(View.GONE);
        imgBar.setVisibility(GONE);

        configInitialKeyboard();
    }

    private void setVisibilityUserLogged() {
        if (serviceType == AppConstants.ServiceType.CASHLESS && enableBilletero==AppConstants.WebResult.OK) {
            iniciarCashless();
        }else if(serviceType != AppConstants.ServiceType.CASHLESS){
            edtDoc.setText("");
            if (serviceType == AppConstants.ServiceType.FIDELBAR) {
                btnBar.setVisibility(View.VISIBLE);
                imgBar.setVisibility(GONE);
            }
            //Solo cuando el usuario se ha logueado se muestra el boton de Cahsless
            if (serviceType == AppConstants.ServiceType.FIDELBARCASH && enableBilletero==AppConstants.WebResult.OK) {
                btnCashless.setVisibility(View.VISIBLE);
            }

            lytLogin.setVisibility(GONE);
            lytUser.setVisibility(View.VISIBLE);

            btnMenu.setVisibility(GONE);
            btnCloseSession.setVisibility(View.VISIBLE);
            txtNumMaquina.setVisibility(GONE);
            txtNombre.setText(FidelizacionApplication.getInstance().getUserName());
        }

    }

    private void setVisibilityClosedSession() {

        FidelizacionApplication.getInstance().closeSession();
        stopAskingPoints();
        txtNombre.setText("");
        txtPuntos.setText("");
        txtPuntosRedim.setText("");

        if (serviceType == AppConstants.ServiceType.FIDELIZACION) {
            btnBar.setVisibility(GONE);
            imgBar.setVisibility(GONE);
        } else if (serviceType == AppConstants.ServiceType.FIDELBAR) {
            imgBar.setVisibility(GONE);
        }else if (serviceType == AppConstants.ServiceType.FIDELBARCASH){
            imgBar.setVisibility(GONE);
            btnCashless.setVisibility(GONE);
        }else if(serviceType == AppConstants.ServiceType.CASHLESS){
            btnCashless.setVisibility(GONE);
        }

        lytLogin.setVisibility(View.VISIBLE);
        lytUser.setVisibility(View.GONE);

        //todo validar si esto es correcto
        setVisibilityInputForm();

        btnMenu.setVisibility(View.VISIBLE);
        btnCloseSession.setVisibility(View.GONE);

        /*todo borrar para no mostrar video en login;
        ALLOW_VIDEO = true;
        startShowingVideo();*/
    }

    private void setVisibilityInputForm() {
        if (paramEnableKeyboard) {
            gridNumberPad.setVisibility(GONE);
        } else {
            edtDoc.setEnabled(false);
            btnKeyBoardShow.setVisibility(GONE);
        }
    }

    private void setVisibilityKeyBoard() {
        if (numberKeyboard.isCustomKeyboardVisible()) {
            numberKeyboard.hideCustomKeyboard();
        } else {
            edtDoc.requestFocus();
            numberKeyboard.showCustomKeyboard(edtDoc);
        }
    }

    //______________________________________________________________________________ASK PUNTOS____________________

    private void startAskingPoints() {
        Log.i(":::PUNTOS:::","pedir puntos");
        url = preferences.getString(AppConstants.Prefs.URL, "");
        casinoCode = preferences.getString(AppConstants.Prefs.ID_CASINO, "");
        serial = preferences.getString(AppConstants.Prefs.SERIAL, "");
        taskPoints = new TimerTask() {
            @Override
            public void run() {
                if (WebUtils.isOnline(ActivityUser.this)) {
                    asyncTaskUserPoints = (AsyncTaskUserPoints) new AsyncTaskUserPoints(responseUserPoints).
                            execute(url, FidelizacionApplication.getInstance().getUserDoc(), casinoCode, serial);
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(taskPoints, 1000,
                preferences.getInt(AppConstants.Prefs.INTERV_UPDATE_POINTS, AppConstants.Generic.DEF_INTERV_UPDATE_POINTS) * 1000);
    }

    private void stopAskingPoints() {
        if (taskPoints != null) {
            taskPoints.cancel();
            taskPoints = null;
        }
    }

    //______________________________________________________________________________VIDEO____________________________

    private void startShowingVideo() {

        stopShowingVideo();
        if (paramShowVideo) {
            taskVideo = new TimerTask() {
                @Override
                public void run() {
                    //todo mostrar video, validaciones dentro del metodo
                    if (ALLOW_VIDEO && PermissionUtils.handleVersionExternalStorage(ActivityUser.this)) {
                        String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                AppConstants.FileExtension.PATH_CONFIG + AppConstants.FileExtension.VIDEO_INTRO;
                        MsgUtils.showVideoDialog(getSupportFragmentManager(), ActivityUser.this, videoPath);

                    }

                }
            };
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(taskVideo, CONSTANTE_REPETICION_VIDEO*CONSTANTE_SEGUNDOS*CONSTANTE_MILISEGUNDOS,CONSTANTE_REPETICION_VIDEO*CONSTANTE_SEGUNDOS*CONSTANTE_MILISEGUNDOS);
        }
    }

    private void stopShowingVideo() {
        if (taskVideo != null) {
            taskVideo.cancel();
            taskVideo = null;
        }
    }

    private void startFidelizacion() {
        if (!edtDoc.getText().toString().isEmpty()) {
            try {
                //todo borrar para no mostrar video en login; ALLOW_VIDEO = false;
                if (paramEnableKeyboard) {
                    setVisibilityKeyBoard();
                    new AsynTaskValidateUser(this, responseValidateUser).execute(edtDoc.getText().toString());
                } else {
                    long doc = numberFormatter.parse(edtDoc.getText().toString()).longValue();
                    new AsynTaskValidateUser(this, responseValidateUser).execute(String.valueOf(doc));
                }
            } catch (ParseException e) {
                MsgUtils.handleException(e);
            }
        } else {
            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.act_login_required));
        }
    }

    public void closeSesion(){
        currentAction = 0;
        ALLOW_VIDEO = false;
        new AsyncTaskCloseSession(this, responseCloseSession).execute();
    }

    //_________________________________________DIALOG INTERFACE___________________________________________
    @Override
    public void onDialogConfirmOkClick(DialogFragment dialogFragment) {
        switch (currentAction) {
            case ACTION_CLOSE_SESSION:
                closeSesion();
                break;
            default:
                break;

        }
    }

    @Override
    public void onDialogConfirmCancelClick(DialogFragment dialogFragment) {
        ALLOW_VIDEO = true;
    }


    /*__________CLAVE DINAMICA___________________*/
    @Override
    public void onDialogConfirmClick(DialogFragment dialogFragment, int resultCode, String clave) {
        conteoResend=0;
//        if(claveCash || claveAction==ACTION_CASHLESS){//ingreso a CASHLESS
//            claveCliente=clave;
//            consultarBilleteroCliente();
//            //iniciarCashless();
//        }else{

        //LOGUEO
            claveCliente=clave;
            long doc;
            try {
                doc = numberFormatter.parse(edtDoc.getText().toString()).longValue();
                if (WebUtils.isOnline(ActivityUser.this)) {
                    asyncTaskFidelizarCliente = (AsyncTaskFidelizarCliente) new AsyncTaskFidelizarCliente(ActivityUser.this, responseFidelizarCliente).execute(String.valueOf(doc));
                }
            } catch (ParseException e) {
                MsgUtils.handleException(e);
            }
 //       }

    }

    @Override
    public void onDialogResendClick(DialogFragment dialogFragment) {
        conteoResend++;
        claveCash=false;
        claveCliente="";
        enviarClaveDinamica();

    }

    @Override
    public void onDialogResendEmailClick(DialogFragment dialogFragment) {
        conteoResend++;
        claveCash=false;
        claveCliente="";
        enviarClaveDinamicaEmail();

    }

    @Override
    public void onDialogCancelClick(DialogFragment dialogFragment) {
        conteoResend=0;
        //Solo se realiza cuando se esta realizando el logueo
        if(claveAction==ACTION_CLOSE_SESSION){
            ALLOW_VIDEO = false;
            backToValidateService();
            claveCliente="";
        }
    }

    /*________________________________MODAL CLAVE DINAMICA___________*/

    public void dialogClaveDinamica(String clave, int action, int numResend){
        DialogFragment dialog = new DialogFragClave();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.WebParams.USER_CLAVE_BD, clave);
        bundle.putInt(AppConstants.RESULT_DIALOG,action);
        bundle.putInt(AppConstants.CONTEO_RESEND,numResend);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(),getResources().getString(R.string.act_login_dialog_clave));
    }

    public void enviarClaveDinamica(){
        long doc;
        try {
            doc = numberFormatter.parse(FidelizacionApplication.getInstance().getUserDoc()).longValue();
            if (WebUtils.isOnline(ActivityUser.this)) {
                new AsyncTaskEnviarClave(ActivityUser.this, responseEnviarClave).execute(String.valueOf(doc));
            }
        } catch (ParseException e) {
            MsgUtils.handleException(e);
        }
    }

    public void enviarClaveDinamicaEmail(){
        long doc;
        try {
            doc = numberFormatter.parse(FidelizacionApplication.getInstance().getUserDoc()).longValue();
            if (WebUtils.isOnline(ActivityUser.this)) {
                new AsyncTaskEnviarClaveCorreo(ActivityUser.this, responseEnviarClaveCorreo).execute(String.valueOf(doc));
            }
        } catch (ParseException e) {
            MsgUtils.handleException(e);
        }
    }

    public void consultarBilleteroCliente(){
        if (WebUtils.isOnline(ActivityUser.this)) {
            new AsyncTaskAskBilletero(ActivityUser.this,responseConsultarBilletero).execute(FidelizacionApplication.getInstance().getUserDoc(), claveCliente);
        }
    }

    public void iniciarCashless(){
        //stopAskingPoints();
        Intent i = new Intent(this, ActivityCashless.class);
        i.putExtra(AppConstants.Generic.SERVICE_ASKED,serviceAsked);
        i.putExtra(AppConstants.Generic.CLAVE_CLIENTE,claveCliente);
        i.putExtra(AppConstants.Generic.VALOR_BILLETERO,valorBilletero);
        startActivityForResult(i, ACTION_CASHLESS);
    }


}
