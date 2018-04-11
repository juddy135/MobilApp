package co.com.ies.fidelizacioncliente.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import co.com.ies.fidelizacioncliente.R;

import static co.com.ies.fidelizacioncliente.utils.AppConstants.Generic.TIME_TO_CLOSE_DIALOG_SHORT;

/**
 * Ventana de dialogo sencilla
 */
public class DialogFragAlert extends DialogFragment {

    public static final String MSG = "mensaje";
    public static final String TITLE = "titulo";
    private TextView txtMensaje;
    private TextView txtTitle;
    private Button btnOk;
    private TimerTask task;

    public DialogFragAlert() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_frag_simple, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        obtenerComponentes(view);
        setListeners();
        startTimer();
        if (bundle != null) {
            txtTitle.setText(getString(R.string.common_alert));
            txtMensaje.setText(bundle.getString(MSG));
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();

    }

    private void obtenerComponentes(View view) {

        btnOk = (Button) view.findViewById(R.id.dialog_frag_simple_btn_ok);
        txtTitle = (TextView) view.findViewById(R.id.dialog_frag_simple_txt_title);
        txtMensaje = (TextView) view.findViewById(R.id.dialog_frag_simple_txt_msg);
    }

    private void setListeners() {

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
    }

    private void startTimer() {

        task = new TimerTask() {
            @Override
            public void run() {
                dismiss();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, TIME_TO_CLOSE_DIALOG_SHORT);

    }

    private void stopTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
