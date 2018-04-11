package co.com.ies.fidelizacioncliente.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.utils.AppConstants;

/**
 * Created by user on 6/05/2017.
 */

public class DialogFragVideo extends DialogFragment implements SurfaceHolder.Callback {

    public static final String VIDEO_PATH = "video_path";
    private VideoView videoView;
    private MediaController mediaController;


    /**
     * Interface necesaria para que la actividad que creo el dialogo sepa que botón se presionó
     */
    public interface NoticeDialogActionsListener {

        void onDialogConfirmOkClick(DialogFragment dialogFragment);

        void onDialogConfirmCancelClick(DialogFragment dialogFragment);
    }

    // Use this instance of the interface to deliver action events
    DialogFragConfirm.NoticeDialogActionsListener mListener;


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogFragConfirm.NoticeDialogActionsListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogActionsListener");
        }
    }

    public DialogFragVideo() {

    }

    @Override
    public void onCancel(DialogInterface dialog) {

        mListener.onDialogConfirmCancelClick(this);
        super.onCancel(dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_frag_video, container, false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        obtenerComponentes(view);

        setListeners();
        if (bundle != null) {
            String videoPath = bundle.getString(VIDEO_PATH);
            videoView.setVideoPath(videoPath);
            videoView.requestFocus();
            videoView.start();

        }


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void obtenerComponentes(View view) {

        videoView = (VideoView) view.findViewById(R.id.dialog_video_view);

    }

    private void setListeners() {

        mediaController = new MediaController(getActivity());
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dismiss();
                return true;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                dismiss();
            }
        });
    }

}