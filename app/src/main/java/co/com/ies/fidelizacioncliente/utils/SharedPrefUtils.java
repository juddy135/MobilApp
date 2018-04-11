package co.com.ies.fidelizacioncliente.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase para administrar preferencias de la app
 */
public class SharedPrefUtils {

    /**
     * borrar un grupo de preferencias
     *
     * @param context
     * @param keyPreferences
     * @return
     */
    public static boolean deleteSharedPreferences(Context context, String keyPreferences) {

        SharedPreferences prefs = context.getSharedPreferences(keyPreferences, Context.MODE_PRIVATE);
        boolean isEliminadaSharedPreferences = false;
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            if (prefs.getAll().size() == 0) {
                isEliminadaSharedPreferences = true;
            }
        }
        return isEliminadaSharedPreferences;
    }

    /**
     * Borrar una valor especifico de un grupo de preferencias
     *
     * @param context
     * @param keyGroupPreference
     * @param keyPreference
     * @return
     */
    public static boolean deletePreference(Context context, String keyGroupPreference, String keyPreference) {

        SharedPreferences preferences = SharedPrefUtils.getSharedPreference(context, keyGroupPreference);
        if (preferences != null) {
            if (preferences.contains(keyPreference)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(keyPreference);
                editor.commit();
                return true;
            }
        }
        return false;
    }

    /**
     * Obtener las preferencias para consultar valores
     *
     * @param context
     * @param prefGroup
     * @return
     */
    public static SharedPreferences getSharedPreference(Context context, String prefGroup) {

        return context.getSharedPreferences(prefGroup, Context.MODE_PRIVATE);
    }

    /**
     * Guardar un valor dentro de un grupo de preferencias
     *
     * @param context
     * @param prefGroup
     * @param prefName
     * @param value
     */
    public static void savePreference(Context context, String prefGroup, String prefName, String value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(prefGroup, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    /**
     * Obtener un valor string de un grupo de preferencias
     *
     * @param context
     * @param prefGroup
     * @param prefName
     * @param defaulValue
     * @return
     */
    public static String readPreferences(Context context, String prefGroup, String prefName, String defaulValue) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(prefGroup, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefName, defaulValue);
    }


}
