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
import android.widget.Button;
import android.widget.TextView;

import co.com.ies.fidelizacioncliente.R;


public class DialogFragOnlyConfirm extends DialogFragment {

        public static final String MSG = "mensaje";
        public static final String TITLE = "titulo";

        private TextView txtTitle;
        private TextView txtMessage;
        private Button btnAceptar;

        /**
         * Interface necesaria para que la actividad que creo el dialogo sepa que botón se presionó
         */
        public interface ActionsListener {

            void onDialogOnlyConfirm(DialogFragment dialogFragment);
        }

        // Use this instance of the interface to deliver action events
        DialogFragOnlyConfirm.ActionsListener mListener;


        // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
        @Override
        public void onAttach(Activity activity) {

            super.onAttach(activity);
    // Verify that the host activity implements the callback interface
            try {
        // Instantiate the NoticeDialogListener so we can send events to the host
        mListener = (DialogFragOnlyConfirm.ActionsListener) activity;
    } catch (ClassCastException e) {
        // The activity doesn't implement the interface, throw exception
        throw new ClassCastException(activity.toString()
                + " must implement NoticeDialogActionsListener");
    }
}

        public DialogFragOnlyConfirm() {

        }

        @Override
        public void onCancel(DialogInterface dialog) {

            super.onCancel(dialog);
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

        return inflater.inflate(R.layout.dialog_frag_only_confirm, container, false);

    }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        obtenerComponentes(view);
        setListeners();
        String title = null;
        if (bundle != null) {
            title = bundle.getString(TITLE);

            txtTitle.setText(title);
            txtMessage.setText(bundle.getString(MSG));

        }
        super.onViewCreated(view, savedInstanceState);
    }

        @Override
        public void onDestroyView() {
        super.onDestroyView();

    }

        private void obtenerComponentes(View view) {

        btnAceptar = (Button) view.findViewById(R.id.dialog_frag_confirm_btn_accept);
        txtTitle = (TextView) view.findViewById(R.id.dialog_frag_confirm_txt_title);
        txtMessage = (TextView) view.findViewById(R.id.dialog_frag_confirm_txt_msg);
    }

        private void setListeners() {

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onDialogOnlyConfirm(DialogFragOnlyConfirm.this);
            }
        });

    }


}
