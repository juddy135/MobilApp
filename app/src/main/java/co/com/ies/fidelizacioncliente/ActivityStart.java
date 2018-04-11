package co.com.ies.fidelizacioncliente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskValidateService;
import co.com.ies.fidelizacioncliente.base.ActivityBase;
import co.com.ies.fidelizacioncliente.dialog.DialogFragInput;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Pantalla en la cual se valida el servicio, descarga nueva información sobre items
 */
public class ActivityStart extends ActivityBase implements DialogFragInput.NoticeDialogActionsListener {

    public static String PREV_ACTIVITY = "PREV_ACt";
    private SharedPreferences preferences;
    private AsyncTaskValidateService.AsyncResponse responseValidateService;
    private boolean isSentBySettings;
    private TimerTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncTaskValidateService(ActivityStart.this, responseValidateService).execute();

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopValidatingService();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }


    @Override
    public void onDialogInputOkClick(DialogFragment dialogFragment, String textField) {

        if (!textField.isEmpty()) {
            SharedPreferences preferences = SharedPrefUtils.getSharedPreference(this, AppConstants.Prefs.SERVICE_PREF);
            if (preferences.getString(AppConstants.Prefs.PASS, "").equals(textField)) {
                startActivity(new Intent(this, ActivitySetting.class));
            } else {
                startValidatingService();
                MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.common_wrong_pass));
            }
        } else {
            startValidatingService();
        }
    }

    @Override
    public void onDialogInputCancelClick(DialogFragment dialogFragment) {
        startValidatingService();
    }

    private void initialize() {

        preferences = SharedPrefUtils.getSharedPreference(this, AppConstants.Prefs.SERVICE_PREF);

        String classs = ActivitySetting.class.getName();
        Intent intent = getIntent();
        String intento = intent.getStringExtra(AppConstants.SENDING_ACT);
        isSentBySettings = classs.equals(intento);


        responseValidateService = new AsyncTaskValidateService.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {

                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        goToActivity();
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), codigoEstado[1]);
                        startValidatingService();
                        break;

                }
            }
        };

    }

    public void onClickStartService(View view) {
        new AsyncTaskValidateService(this, responseValidateService).execute();
    }

    private void goToActivity() {


        if (validateParams()) {
            startActivity(new Intent(this, ActivityUser.class));
            finish();
        } else {
            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.act_setting_missing_params));
        }
    }


    public void onClickSettings(View view) {
        stopValidatingService();
        accessToSettings();
    }

    private void startValidatingService() {

        task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        validateService();
                    }
                });
            }
        };
        Timer timer = new Timer();
        //si la clase que creo la activity es Settings se empienza casi inmediatamente, en caso contrario se empieza en 30 segundos
        timer.schedule(task, AppConstants.Generic.TIME_TO_VALIDATE_SERVICE);

    }


    private void stopValidatingService() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void validateService() {
        new AsyncTaskValidateService(ActivityStart.this, responseValidateService).execute();
    }

    private boolean validateParams() {
        return preferences.contains(AppConstants.Prefs.PASS)
                && preferences.contains(AppConstants.Prefs.INTERV_UPDATE_POINTS)
                && preferences.contains(AppConstants.Prefs.SHOW_KEYBOARD);
    }
}
