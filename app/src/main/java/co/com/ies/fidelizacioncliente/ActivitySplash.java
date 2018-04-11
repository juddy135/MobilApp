package co.com.ies.fidelizacioncliente;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import co.com.ies.fidelizacioncliente.base.ActivityBase;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.FileUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Pantalla inicial, solo aparece unos segundos mostrando el logo de la app
 */
public class ActivitySplash extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        FileUtils.backUpDataBase(this);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                Intent intent = new Intent(ActivitySplash.this, ActivitySetting.class);
                intent.putExtra(AppConstants.SENDING_ACT, ActivitySplash.class.getName());
                startActivity(intent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 2000);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }
}
