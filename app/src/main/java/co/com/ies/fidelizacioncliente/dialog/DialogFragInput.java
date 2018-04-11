package co.com.ies.fidelizacioncliente.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import co.com.ies.fidelizacioncliente.R;

import static co.com.ies.fidelizacioncliente.utils.AppConstants.Generic.TIME_TO_CLOSE_DIALOG_LONG;

/**
 * Ventana de diálogo para ingresar texto
 */
public class DialogFragInput extends DialogFragment {

    public static final String MSG = "mensaje";
    public static final String TITLE = "titulo";
    private EditText edtInput;
    private TextView txtMessage;
    private TextView txtTitle;
    private Button btnOk;
    private Button btnCancel;
    private TimerTask task;

    /**
     * Interface necesaria para que la actividad que creo el dialogo sepa que botón se presionó y la acción realizada
     */
    public interface NoticeDialogActionsListener {

        void onDialogInputOkClick(DialogFragment dialogFragment, String textField);

        void onDialogInputCancelClick(DialogFragment dialogFragment);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogActionsListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogActionsListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogActionsListener");
        }
    }

    public DialogFragInput() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_frag_input, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        obtenerComponentes(view);
        setListeners();
        startTimer();
        String msg = null;
        String title = null;
        if (bundle != null) {
            txtTitle.setText(bundle.getString(TITLE));
            if (bundle.containsKey(MSG)) {
                txtMessage.setText(bundle.getString(MSG));
            } else {
                txtMessage.setVisibility(View.GONE);
            }

        }
        edtInput.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();

    }

    private void obtenerComponentes(View view) {

        txtTitle = (TextView) view.findViewById(R.id.dialog_frag_input_txt_title);
        txtMessage = (TextView) view.findViewById(R.id.dialog_frag_input_txt_msg);
        edtInput = (EditText) view.findViewById(R.id.dialog_frag_input_edt);
        btnOk = (Button) view.findViewById(R.id.dialog_frag_input_btn_accept);
        btnCancel = (Button) view.findViewById(R.id.dialog_frag_input_btn_cancel);
    }

    private void setListeners() {

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
                mListener.onDialogInputOkClick(DialogFragInput.this, edtInput.getText().toString());
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
                mListener.onDialogInputCancelClick(DialogFragInput.this);
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {

        mListener.onDialogInputCancelClick(this);
        super.onCancel(dialog);
    }


    private void startTimer() {

        task = new TimerTask() {
            @Override
            public void run() {
                dismiss();
                mListener.onDialogInputCancelClick(DialogFragInput.this);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, TIME_TO_CLOSE_DIALOG_LONG);

    }

    private void stopTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

}
