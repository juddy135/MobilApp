package co.com.ies.fidelizacioncliente;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import co.com.ies.fidelizacioncliente.asynctask.AsyncTaskSinc;
import co.com.ies.fidelizacioncliente.base.ActivityBase;
import co.com.ies.fidelizacioncliente.custom.spinner.AdapterSpinnerGeneric;
import co.com.ies.fidelizacioncliente.custom.spinner.AdapterSpinnerSimple;
import co.com.ies.fidelizacioncliente.dialog.DialogFragConfirm;
import co.com.ies.fidelizacioncliente.entity.GenericItem;
import co.com.ies.fidelizacioncliente.manager.ManagerStandard;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.FormUtils;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import co.com.ies.fidelizacioncliente.utils.PermissionUtils;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.XmlUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static co.com.ies.fidelizacioncliente.utils.AppConstants.Generic.TIME_TO_CLOSE_SETTINGS;

/**
 * Pantalla para la configuración de la aplicación
 */
public class ActivitySetting extends ActivityBase implements DialogFragConfirm.NoticeDialogActionsListener {

    private final int PERMISSIONS_REQUEST_WRITE_EXT = 1;
    private final int ACTION_DELETE_ALL = 2;
    private final int ACTION_LOAD_CONFIG = 3;
    private final int ACTION_GOING_START = 4;

    private int currentAction;

    private EditText edtIp = null;
    //private EditText edtPass = null;
    private EditText edtServPass = null;
    private EditText edtServUsr = null;
    //  private EditText edtIntervPoints = null;
    private Spinner spnUso = null;
    private Spinner spnCasino = null;
    private Spinner spnMachine = null;
    private SharedPreferences preferences = null;
    private String erMsgMissingField = null;
    private String erMsgMissingSelec = null;
    private String erMsgBadFormat = null;
    private AdapterSpinnerSimple adapterUso;
    private AdapterSpinnerGeneric adapterCasinos;
    private AdapterSpinnerGeneric adapterMachines;
    private ArrayList<GenericItem> listCasinos;
    private ArrayList<GenericItem> listMachines;
    private ManagerStandard managerStandard;
    private boolean ingresoInicial = true;
    private Timer timer;
    private TimerTask task;
    private AsyncTaskSinc.AsyncResponse responseSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /// getWindow().setBackgroundDrawableResource(R.drawable.background_main);
        obtenerComponentes();
        initialize();
        setFilter();
        setValues();
        setListeners();

        if (!validateConfigPrefs()) {
            if (PermissionUtils.isPermissionsNeeded()) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // No explanation needed, we can request the permission.
                    currentAction = ACTION_LOAD_CONFIG;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXT);

                    // PERMISSIONS_REQUEST_WRITE_EXT is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.

                } else {
                    loadConfig();
                }
            } else {
                loadConfig();
            }
        }
    }

    /**
     * metodo para cambiar el tipo de letra
     *
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    switch (currentAction) {
                        case ACTION_LOAD_CONFIG:
                            currentAction = 0;
                            loadConfig();
                            break;
                        case ACTION_GOING_START:
                            currentAction = 0;
                            nextActivity();
                            break;
                        default:
                            break;

                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    switch (currentAction) {
                        case ACTION_LOAD_CONFIG:
                            currentAction = 0;
                            MsgUtils.biggetToast(this, getString(R.string.act_setting_no_permission_ext_storage_config));
                            break;
                        case ACTION_GOING_START:
                            currentAction = 0;
                            MsgUtils.biggetToast(this, getString(R.string.act_setting_no_permission_ext_storage));
                            nextActivity();
                            break;
                        default:
                            break;

                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onDialogConfirmOkClick(DialogFragment dialogFragment) {
        if (ACTION_DELETE_ALL == currentAction) {
            deleteAll();
            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.act_setting_delete_done));
        }
    }

    @Override
    public void onDialogConfirmCancelClick(DialogFragment dialogFragment) {
        currentAction = 0;
    }

    private void obtenerComponentes() {

        edtIp = (EditText) findViewById(R.id.act_setting_edt_ip);
        // edtPass = (EditText) findViewById(R.id.act_setting_edt_pass);
        edtServPass = (EditText) findViewById(R.id.act_setting_edt_serv_pass);
        edtServUsr = (EditText) findViewById(R.id.act_setting_edt_serv_usr);
        //   edtIntervPoints = (EditText) findViewById(R.id.act_setting_edt_interv_points);
        spnUso = (Spinner) findViewById(R.id.act_setting_spn_uso);
        spnCasino = (Spinner) findViewById(R.id.act_setting_spn_casino);
        spnMachine = (Spinner) findViewById(R.id.act_setting_spn_id_machine);
    }

    private void setFilter() {

        edtIp.setFilters(new InputFilter[]{FormUtils.getFilter(FormUtils.URL_CHARACTERS)});
    }

    private void initialize() {

        managerStandard = ManagerStandard.getInstance();

        preferences = SharedPrefUtils.getSharedPreference(this, AppConstants.Prefs.SERVICE_PREF);
        String prevAct = getIntent().getStringExtra(AppConstants.SENDING_ACT);
        String splash = ActivitySplash.class.getName();
        if (splash.equals(prevAct)) {
            if (validatePrefs()) {

                nextActivity();
            }
        } else {
            startTimer();
        }

        adapterUso = new AdapterSpinnerSimple(this, getResources().getStringArray(R.array.usos_app));
        spnUso.setAdapter(adapterUso);

        listCasinos = managerStandard.getAllCassinos(this);
        adapterCasinos = new AdapterSpinnerGeneric(this, listCasinos);
        spnCasino.setAdapter(adapterCasinos);

        listMachines = new ArrayList<>();
        adapterMachines = new AdapterSpinnerGeneric(this, listMachines);
        spnMachine.setAdapter(adapterMachines);

        erMsgBadFormat = getString(R.string.common_wrong_format);
        erMsgMissingField = getString(R.string.common_missing_field);
        erMsgMissingSelec = getString(R.string.common_missing_selection);
    }

    private void setValues() {

        //190.0.24.229:8443,qwe,soporte,soporte135
        edtIp.setText(preferences.getString(AppConstants.Prefs.URL, ""));
        //  edtPass.setText(preferences.getString(AppConstants.Prefs.PASS, "qwe"));
        edtServUsr.setText(preferences.getString(AppConstants.Prefs.SERV_USR, ""));
        edtServPass.setText(preferences.getString(AppConstants.Prefs.SERV_PASS, ""));
        //  edtIntervPoints.setText(
        //        String.valueOf(preferences.getInt(AppConstants.Prefs.INTERV_UPDATE_POINTS, AppConstants.Generic.DEF_INTERV_UPDATE_POINTS)));
        spnUso.setSelection(preferences.getInt(AppConstants.Prefs.USO, 0));
        if (preferences.contains(AppConstants.Prefs.ID_CASINO)) {
            String idCassino = preferences.getString(AppConstants.Prefs.ID_CASINO, "");
            int length = listCasinos.size();
            for (int i = 0; i < length; i++) {
                if (listCasinos.get(i).getId().equals(idCassino)) {
                    spnCasino.setSelection(i);
                    break;
                }
            }
        }


    }

    private void setListeners() {
        spnCasino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                listMachines.clear();
                listMachines.addAll(managerStandard.getGenericMachineByCassino(ActivitySetting.this, listCasinos.get(position).getId()));
                adapterMachines.notifyDataSetChanged();

                if (ingresoInicial) {
                    if (preferences.contains(AppConstants.Prefs.NUM_DISP)) {
                        String numDisp = preferences.getString(AppConstants.Prefs.NUM_DISP, "");
                        int length = listMachines.size();
                        for (int i = 0; i < length; i++) {
                            if (listMachines.get(i).getName().equals(numDisp)) {
                                spnMachine.setSelection(i);
                                break;
                            }
                        }
                    }
                    ingresoInicial = false;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        responseSync = new AsyncTaskSinc.AsyncResponse() {
            @Override
            public void processFinish(String[] codigoEstado) {
                switch (codigoEstado[0]) {
                    case AppConstants.WebResult.OK:
                        preferences = SharedPrefUtils.getSharedPreference(ActivitySetting.this, AppConstants.Prefs.SERVICE_PREF);
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.act_setting_sync_ok));
                        listCasinos.clear();
                        listCasinos.addAll(managerStandard.getAllCassinos(ActivitySetting.this));
                        adapterCasinos.notifyDataSetChanged();
                        break;
                    default:
                        MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), codigoEstado[1]);
                        break;

                }

            }
        };

    }


    private boolean validateForm() {

        int failValitations = 0;
        failValitations += FormUtils.validarEditTextStandard(edtIp, erMsgMissingField);
        //   failValitations += FormUtils.validarEditTextStandard(edtPass, erMsgMissingField);
        failValitations += FormUtils.validarEditTextStandard(edtServPass, erMsgMissingField);
        failValitations += FormUtils.validarEditTextStandard(edtServUsr, erMsgMissingField);
        failValitations += FormUtils.validarSpinnerStandard(spnUso, erMsgMissingSelec);
        failValitations += FormUtils.validarSpinnerStandard(spnCasino, erMsgMissingSelec);
        failValitations += FormUtils.validarSpinnerStandard(spnMachine, erMsgMissingSelec);


        return failValitations == 0 ? true : false;

    }

    /**
     * datos minimos necesarios para consultar casinos y maquinas
     *
     * @return
     */
    private boolean validateFormSinc() {
        int failValitations = 0;
        failValitations += FormUtils.validarEditTextStandard(edtIp, erMsgMissingField);
        failValitations += FormUtils.validarEditTextStandard(edtServPass, erMsgMissingField);
        failValitations += FormUtils.validarEditTextStandard(edtServUsr, erMsgMissingField);
        return failValitations == 0 ? true : false;
    }



    private boolean validatePrefs() {

        return preferences.contains(AppConstants.Prefs.URL)
                && preferences.contains(AppConstants.Prefs.SERIAL)
                && preferences.contains(AppConstants.Prefs.NUM_DISP)
                && preferences.contains(AppConstants.Prefs.PASS)
                && preferences.contains(AppConstants.Prefs.USO)
                && preferences.contains(AppConstants.Prefs.SERV_PASS)
                && preferences.contains(AppConstants.Prefs.SERV_USR)
                && preferences.contains(AppConstants.Prefs.ID_CASINO)
                && preferences.contains(AppConstants.Prefs.INTERV_UPDATE_POINTS);
    }

    private boolean validateConfigPrefs() {

        return preferences.contains(AppConstants.Prefs.URL)
                && preferences.contains(AppConstants.Prefs.SERV_PASS)
                && preferences.contains(AppConstants.Prefs.SERV_USR);
    }

    public void onClicGuardar(View view) {

        if (validateForm()) {
                SharedPreferences.Editor editor = preferences.edit();
                //editor.putString(AppConstants.Prefs.NUM_DISP, edtId.getText().toString());
                //     editor.putString(AppConstants.Prefs.PASS, edtPass.getText().toString());
                editor.putString(AppConstants.Prefs.URL, edtIp.getText().toString());
                editor.putString(AppConstants.Prefs.SERV_USR, edtServUsr.getText().toString());
                editor.putString(AppConstants.Prefs.SERV_PASS, edtServPass.getText().toString());
                editor.putInt(AppConstants.Prefs.USO, spnUso.getSelectedItemPosition());
                editor.putString(AppConstants.Prefs.ID_CASINO, listCasinos.get(spnCasino.getSelectedItemPosition()).getId());
                editor.putString(AppConstants.Prefs.NOM_CASINO, listCasinos.get(spnCasino.getSelectedItemPosition()).getName());
                editor.putString(AppConstants.Prefs.NUM_DISP, listMachines.get(spnMachine.getSelectedItemPosition()).getName());
                editor.putString(AppConstants.Prefs.SERIAL, listMachines.get(spnMachine.getSelectedItemPosition()).getId());
                //   editor.putInt(AppConstants.Prefs.INTERV_UPDATE_POINTS, Integer.valueOf(edtIntervPoints.getText().toString()));
                editor.apply();

                // Here, thisActivity is the current activity
                if (PermissionUtils.isPermissionsNeeded()) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // No explanation needed, we can request the permission.
                        currentAction = ACTION_GOING_START;
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_WRITE_EXT);

                        // PERMISSIONS_REQUEST_WRITE_EXT is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.

                    } else {
                        nextActivity();
                    }
                } else {
                    nextActivity();
                }
        } else {
            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.act_setting_missing_fields));
        }

    }

    public void onClicSync(View view) {
        if (validateFormSinc()) {
            new AsyncTaskSinc(this, responseSync).execute(edtIp.getText().toString(),
                    edtServUsr.getText().toString(), edtServPass.getText().toString());

        } else {
            MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.act_setting_input_ip));

        }
    }

    public void onClicDeleteAll(View view) {
        currentAction = ACTION_DELETE_ALL;
        MsgUtils.showSConfirmDialog(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.act_setting_delete_all));
    }


    private void nextActivity() {
        Intent intent = new Intent(this, ActivityStart.class);
        intent.putExtra(AppConstants.SENDING_ACT, getClass().getName());
        startActivity(intent);
        finish();
    }


    private void startTimer() {

        stopTimer();
        task = new TimerTask() {
            @Override
            public void run() {

                if (validatePrefs()) {
                    nextActivity();
                }

            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, TIME_TO_CLOSE_SETTINGS, TIME_TO_CLOSE_SETTINGS);
    }

    private void stopTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void deleteAll() {

        ManagerStandard.getInstance().deleteAll(this);

        try {
            File folderImages = getExternalFilesDir(null);
            if (folderImages.exists()) {
                File[] files = folderImages.listFiles();
                int sizeImages = files.length;
                for (int i = 0; i < sizeImages; i++) {
                    files[i].delete();
                }
            }
        } catch (Exception e) {
            MsgUtils.handleException(e);
        }

        try {
            if (PermissionUtils.handleVersionExternalStorage(this)) {
                File folderCrash = new File(Environment.getExternalStorageDirectory(), AppConstants.FileExtension.CRASH_LOG);
                if (folderCrash.exists()) {
                    File[] files = folderCrash.listFiles();
                    int sizeImages = files.length;
                    for (int i = 0; i < sizeImages; i++) {
                        files[i].delete();
                    }
                }
            }
        } catch (Exception e) {
            MsgUtils.handleException(e);
        }

        SharedPrefUtils.deleteSharedPreferences(this, AppConstants.Prefs.SERVICE_PREF);
        edtIp.setText("");
        edtServUsr.setText("");
        edtServPass.setText("");

        spnUso.setSelection(0);

        listMachines.clear();
        listMachines.add(new GenericItem(AppConstants.Generic.ID_DEFAULT, getString(R.string.act_setting_select)));
        adapterMachines.notifyDataSetChanged();
        spnMachine.setSelection(0);

        listCasinos.clear();
        listCasinos.add(new GenericItem(AppConstants.Generic.ID_DEFAULT, getString(R.string.act_setting_select)));
        adapterCasinos.notifyDataSetChanged();
        spnCasino.setSelection(0);

        if (PermissionUtils.isPermissionsNeeded()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.
                currentAction = ACTION_LOAD_CONFIG;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXT);

                // PERMISSIONS_REQUEST_WRITE_EXT is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            } else {
                loadConfig();
            }
        } else {
            loadConfig();
        }
    }


    private void loadConfig() {
        try {
            XmlUtils xmlUtils = new XmlUtils();

            File pathConfig = new File(Environment.getExternalStorageDirectory(), AppConstants.FileExtension.PATH_CONFIG);
            if (!pathConfig.exists()) {
                pathConfig.mkdir();
            }

            File fileAppConfig = new File(Environment.getExternalStorageDirectory(), AppConstants.FileExtension.PATH_CONFIG + AppConstants.FileExtension.FILE_CONFIG);
            if (fileAppConfig.exists()) {
                InputStream inputStream = new FileInputStream(fileAppConfig);
                HashMap<String, String> hashMapConfig = xmlUtils.getConfig(inputStream);

                if (hashMapConfig.containsKey(AppConstants.ConfigTags.URL)) {
                    edtIp.setText(hashMapConfig.get(AppConstants.ConfigTags.URL));
                }

                if (hashMapConfig.containsKey(AppConstants.ConfigTags.USER)) {
                    edtServUsr.setText(hashMapConfig.get(AppConstants.ConfigTags.USER));
                }

                if (hashMapConfig.containsKey(AppConstants.ConfigTags.PASS)) {
                    edtServPass.setText(hashMapConfig.get(AppConstants.ConfigTags.PASS));
                }

            } else {
                FileWriter writer = null;
                try {
                    fileAppConfig.createNewFile();
                    writer = new FileWriter(fileAppConfig);
                    writer.append(AppConstants.Generic.CONFIG_CONTENT);
                    writer.flush();
                } catch (IOException e) {
                    MsgUtils.handleException(e);
                } finally {
                    if (writer != null)
                        writer.close();
                }
                MsgUtils.showSimpleMsg(getSupportFragmentManager(), getString(R.string.common_alert), getString(R.string.common_no_config));
            }


        } catch (FileNotFoundException e) {
            MsgUtils.handleException(e);
        } catch (XmlPullParserException e) {
            MsgUtils.handleException(e);
        } catch (IOException e) {
            MsgUtils.handleException(e);
        }

    }
}
