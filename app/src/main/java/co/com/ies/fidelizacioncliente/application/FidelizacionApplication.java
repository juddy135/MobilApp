package co.com.ies.fidelizacioncliente.application;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import co.com.ies.fidelizacioncliente.ActivitySplash;
import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.DateUtils;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * La clase application es una que conserva el estado y valores a través de toda la aplicación
 * en esta guardamos los valores del usuario y realizamos la gestion de los errores que cierran la app
 */
public class FidelizacionApplication extends Application {

    private static FidelizacionApplication singleton;
    private String userName;
    private String userDoc;
    private String userPoints;
    private String cookie;
    private Thread.UncaughtExceptionHandler defaultUEH;

    public static FidelizacionApplication getInstance() {

        return singleton;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/BernardMT.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        // setup handler for uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                // here I do logging of exception to a db
                if (ContextCompat.checkSelfPermission(FidelizacionApplication.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    saveError(ex);
                }
                PendingIntent myActivity = PendingIntent.getActivity(getApplicationContext(),
                        192837, new Intent(getApplicationContext(), ActivitySplash.class),
                        PendingIntent.FLAG_ONE_SHOT);

                AlarmManager alarmManager;
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        15000, myActivity);

                // re-throw critical exception further to the os (important)
                defaultUEH.uncaughtException(thread, ex);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getUserName() {
        MsgUtils.handleInfo("nombre de usuario" + userName);
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDoc() {
        MsgUtils.handleInfo("pidiendo doc de usuario" + userDoc);

        return userDoc;
    }

    public void setUserDoc(String userDoc) {
        this.userDoc = userDoc;
    }

    public String getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(String userPoints) {
        this.userPoints = userPoints;
    }

    public void closeSession() {

        MsgUtils.handleInfo("borrando sesion");
        userDoc = null;
        userName = null;
        userPoints = null;

    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public boolean isUserLogged() {
        return userDoc != null;
    }

    private void saveError(Throwable throwable) {
        FileWriter writer = null;
        try {
            File filePath = new File(Environment.getExternalStorageDirectory(), AppConstants.FileExtension.CRASH_LOG);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            File fileLog = new File(filePath,
                    DateUtils.getDateString(System.currentTimeMillis(), DateUtils.FORMAT_LOG) +
                            AppConstants.FileExtension.FILE_TXT);

            StringWriter errors = new StringWriter();
            throwable.printStackTrace(new PrintWriter(errors));

            fileLog.createNewFile();
            writer = new FileWriter(fileLog);
            writer.append(throwable.toString() + "\n");
            writer.append(throwable.getLocalizedMessage() + "\n");
            writer.append(throwable.getMessage() + "\n");
            writer.append(errors.toString() + "\n");
            writer.flush();

        } catch (IOException e) {
            MsgUtils.handleException(e);
        } catch (Exception e) {
            MsgUtils.handleException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    MsgUtils.handleException(e);
                }
            }
        }
    }

}
