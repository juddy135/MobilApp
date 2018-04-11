package co.com.ies.fidelizacioncliente.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.com.ies.fidelizacioncliente.ActivitySetting;
import co.com.ies.fidelizacioncliente.ActivityStart;
import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.application.FidelizacionApplication;
import co.com.ies.fidelizacioncliente.dialog.DialogFragInput;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;

/**
 * Clase base de la cual heredan el resto de activities, en esta se agregan los metodos genericos usados por todas las clases
 */
public abstract class ActivityBase extends AppCompatActivity implements DialogFragInput.NoticeDialogActionsListener {

    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {

                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    view.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);

                }
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onBackPressed() {

    }

    public void accessToSettings() {

        MsgUtils.showInputDialog(getSupportFragmentManager(), getString(R.string.common_input_pass), null);

    }


    @Override
    public void onDialogInputOkClick(DialogFragment dialogFragment, String textField) {

        if (!textField.isEmpty()) {
            SharedPreferences preferences = SharedPrefUtils.getSharedPreference(this, AppConstants.Prefs.SERVICE_PREF);
            if (preferences.getString(AppConstants.Prefs.PASS, "").equals(textField)) {
                Intent intent = new Intent(this, ActivitySetting.class);
                intent.putExtra(AppConstants.SENDING_ACT, getClass().getName());
                startActivity(intent);
            } else {
                MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.common_wrong_pass));
            }
        }
    }

    @Override
    public void onDialogInputCancelClick(DialogFragment dialogFragment) {

    }

    public void backToValidateService() {
        FidelizacionApplication.getInstance().closeSession();
        startActivity(new Intent(this, ActivityStart.class));
        finish();
    }
}
