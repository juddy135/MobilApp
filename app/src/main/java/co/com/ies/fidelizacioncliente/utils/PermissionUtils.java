package co.com.ies.fidelizacioncliente.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import co.com.ies.fidelizacioncliente.ActivityUser;

/**
 * Created by user on 7/05/2017.
 */

public class PermissionUtils {


    private PermissionUtils() {

    }

    /**
     * A partir de android 6, es necesario validar si se han concedido los permisos individuales
     *
     * @return
     */
    public static boolean isPermissionsNeeded() {

        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean hasExternalStoragePermission(Context context) {

        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    public static boolean handleVersionExternalStorage(Context context) {
        if (isPermissionsNeeded()) {
            return hasExternalStoragePermission(context);
        } else {
            return true;
        }
    }
}
