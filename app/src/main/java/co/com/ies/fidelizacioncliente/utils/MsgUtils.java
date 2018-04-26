package co.com.ies.fidelizacioncliente.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.dialog.DialogFragAlert;
import co.com.ies.fidelizacioncliente.dialog.DialogFragConfirm;
import co.com.ies.fidelizacioncliente.dialog.DialogFragInput;
import co.com.ies.fidelizacioncliente.dialog.DialogFragOnlyConfirm;
import co.com.ies.fidelizacioncliente.dialog.DialogFragSearch;
import co.com.ies.fidelizacioncliente.dialog.DialogFragVideo;

/**
 * Clase usada para crear instancias de las ventanas de dialogos
 */
public class MsgUtils {

    /**
     * Ventana de dialogo para ingresar texto
     *
     * @param supportFragManager
     * @param titulo
     * @param mensaje
     */
    public static void showInputDialog(FragmentManager supportFragManager, @NonNull String titulo, String mensaje) {

        DialogFragment newFragment = new DialogFragInput();
        Bundle args = new Bundle();
        args.putString(DialogFragInput.TITLE, titulo);
        if (mensaje != null) {
            args.putString(DialogFragInput.MSG, mensaje);
        }
        // newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragment);
        newFragment.setArguments(args);
        newFragment.show(supportFragManager, "dialoginput");

    }

    /**
     * Ventana de dialogo para mensaje sencillo con solo boton de aceptar
     *
     * @param supportFragManager
     * @param titulo
     * @param mensaje
     */
    public static void showSimpleMsg(FragmentManager supportFragManager, @NonNull String titulo, String mensaje) {

        DialogFragment newFragment = new DialogFragAlert();
        Bundle args = new Bundle();
        args.putString(DialogFragAlert.TITLE, titulo);
        if (mensaje != null) {
            args.putString(DialogFragAlert.MSG, mensaje);
        }
        newFragment.setArguments(args);
        newFragment.show(supportFragManager, "dialog");

    }

    /**
     * Ventana de dialogo con 2 opciones, cancelar y confirmar
     *
     * @param supportFragManager
     * @param titulo
     * @param mensaje
     */
    public static void showSConfirmDialog(FragmentManager supportFragManager, @NonNull String titulo, String mensaje) {

        DialogFragment newFragment = new DialogFragConfirm();
        Bundle args = new Bundle();
        args.putString(DialogFragConfirm.TITLE, titulo);
        if (mensaje != null) {
            args.putString(DialogFragConfirm.MSG, mensaje);
        }
        newFragment.setArguments(args);
        newFragment.show(supportFragManager, "dialogconfirm");

    }

    /**
     * Ventana de dialogo para reproducir video
     * Se validan permisos para version superiores a android 5 en caso de que el archivo se encuentre externamente
     * Se valida que el archivo exista antes de que sea abierto el dialogo
     * //todo el video debería ser descargado por un servicio como el logo del casino, se recomienda implementación del mismo
     *
     * @param supportFragManager
     */
    public static void showVideoDialog(FragmentManager supportFragManager, Context context, String videoPath) {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            File file = new File(videoPath);
            if (file.exists()) {
                DialogFragment newFragment = new DialogFragVideo();
                Bundle args = new Bundle();
                args.putString(DialogFragVideo.VIDEO_PATH, videoPath);
                newFragment.setArguments(args);
                newFragment.show(supportFragManager, "dialogVideo");
            }
        }

    }

    public static void showSearchDialog(FragmentManager supportFragManager, ArrayList<String> listCategories) {

        DialogFragment newFragment = new DialogFragSearch();
        Bundle args = new Bundle();
        if(listCategories!=null && !listCategories.isEmpty())
        args.putStringArrayList(DialogFragSearch.LIST_SEARCH, listCategories);

        newFragment.setArguments(args);
        newFragment.show(supportFragManager, "dialogconfirm");

    }

    /**
     * Método para manejar la excepción arrojada en un catch,
     * si se desea hacer algo mas con las excepciones se recomienda implementarlo en este método oara que sea trasversal
     *
     * @param e
     */
    public static void handleException(Exception e) {

        Log.e(AppConstants.TAG, e.getMessage(), e);
    }

    /**
     * Metodo para usar el log de android de manera informativa
     *
     * @param message
     */
    public static void handleInfo(String message) {

        Log.i(AppConstants.TAG, message);
    }

    /**
     * Mostrar mensaje temporal sin botones ( toast) en un tamaño mayor al standar
     *
     * @param context
     * @param text
     */
    public static void biggetToast(Context context, String text) {

        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.common_textsize_bigger));
        toast.show();
    }

}
