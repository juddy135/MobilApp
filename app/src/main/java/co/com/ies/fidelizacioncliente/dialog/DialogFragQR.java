package co.com.ies.fidelizacioncliente.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import co.com.ies.fidelizacioncliente.R;

public class DialogFragQR extends DialogFragment {

    public static final String ID_CLIENTE_DIALOG = "idcliente";

    private ImageView viewQR;
    private Button btnAceptar;
    private TextView texto;

    /**
     * Interface necesaria para que la actividad que creo el dialogo sepa que botón se presionó
     */
    public interface ActionsListener {
        void onDialogQrConfirm(DialogFragment dialogFragment);
    }

    // Use this instance of the interface to deliver action events
    DialogFragQR.ActionsListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogFragQR.ActionsListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogActionsListener");
        }
    }

    public DialogFragQR() {

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
    public void onCancel(DialogInterface dialog) {

        super.onCancel(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_frag_qr, container, false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        btnAceptar = view.findViewById(R.id.dialog_frag_simple_btn_ok_qr);
        viewQR = view.findViewById(R.id.dialog_frag_image_qr);
        texto = view.findViewById(R.id.dialog_frag_text_qr);

        String idcliente = null;
        if (bundle != null) {
            idcliente = bundle.getString(ID_CLIENTE_DIALOG);
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(idcliente, BarcodeFormat.QR_CODE, 400, 400);
                viewQR.setImageBitmap(bitmap);
            }catch(Exception e) {

            }

        }

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onDialogQrConfirm(DialogFragQR.this);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

        }
}
