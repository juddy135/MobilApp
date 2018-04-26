package co.com.ies.fidelizacioncliente.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.utils.AppConstants;

/**
 * Esta dialogo permite que el cliente pueda ingresar la clave dinamica
 *
 * Created by juddy on 27/03/2018.
 */

public class DialogFragClave extends DialogFragment {


    String claveDB;
    int resultRequest, conteoResend=0;
    private EditText etClave;
    private Button btnConfirmar;
    private Button btnCancelar;
    private Button btnReenviar;
    private Button btnReenviarEmail;
    private LinearLayout ll_resend_email;

    public interface ClaveDinamicaDialogActionsListener{
        void onDialogConfirmClick(DialogFragment dialogFragment, int resultCode, String clave);
        void onDialogResendClick(DialogFragment dialogFragment);
        void onDialogCancelClick(DialogFragment dialogFragment);
        void onDialogResendEmailClick(DialogFragment dialogFragment);
    }

    ClaveDinamicaDialogActionsListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ClaveDinamicaDialogActionsListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()+ " must implement NoticeDialogListener");
        }
    }
    /**CONSTRUCTOR*/
    public DialogFragClave() {
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
       return inflater.inflate(R.layout.dialog_frag_clavedinamica, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        etClave=(EditText) view.findViewById(R.id.dialog_frag_clave_edt);
        btnConfirmar=(Button) view.findViewById(R.id.dialog_frag_clave_btn_accept);
        btnCancelar=(Button) view.findViewById(R.id.dialog_frag_clave_btn_cancel);
        btnReenviar=(Button) view.findViewById(R.id.dialog_frag_clave_btn_resend);
        btnReenviarEmail=(Button) view.findViewById(R.id.dialog_frag_clave_btn_resend_email);
        ll_resend_email=(LinearLayout) view.findViewById(R.id.layout_resend_email);

        Bundle bndle = getArguments();
        claveDB=bndle.getString(AppConstants.WebParams.USER_CLAVE_BD,"0");
        resultRequest=bndle.getInt(AppConstants.RESULT_DIALOG, 0);
        conteoResend=bndle.getInt(AppConstants.CONTEO_RESEND, 0);

        //Si se cumple el limite de reenvio de sms
        if(conteoResend+1 >= AppConstants.CONSTANTE_LIMITE_REENVIO_SMS){
            ll_resend_email.setVisibility(View.VISIBLE);
        }else{
            ll_resend_email.setVisibility(View.GONE);
        }

        Log.i("DIALOG"," clave BD"+claveDB);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ACCEPT
                if(claveDB.equals(etClave.getText().toString())){
                    dismiss();
                    mListener.onDialogConfirmClick(DialogFragClave.this, resultRequest, claveDB);
                }else{
                    Toast.makeText(getContext(), getResources().getString(R.string.act_login_error_clave), Toast.LENGTH_SHORT).show();
                    etClave.setText("");
                }
            }
        });

        btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RESEND
                dismiss();
                mListener.onDialogResendClick(DialogFragClave.this);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CANCEL
                dismiss();
                mListener.onDialogCancelClick(DialogFragClave.this);
            }
        });

        btnReenviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RESEND EMAIL
                dismiss();
                mListener.onDialogResendEmailClick(DialogFragClave.this);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
