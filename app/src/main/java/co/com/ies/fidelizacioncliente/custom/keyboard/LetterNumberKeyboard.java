package co.com.ies.fidelizacioncliente.custom.keyboard;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by obed.gonzalez on 05/10/2017.
 */

public class LetterNumberKeyboard {
    private KeyboardView mKeyboardView;
    private Activity mActivity;
    private OnOkeyClickListener listener;
    private KeyboardView.OnKeyboardActionListener mKeyBoardListener = new KeyboardView.OnKeyboardActionListener() {

        public final static int CODE_DELETE = 1000;
        public final static int CODE_CLOSE = 1001;
        public final static int CODE_OK = 1002;

        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            // NOTE We can say '<Key android:codes="49,50" ... >' in the xml file; all codes come in keyCodes, the first in this list in primaryCode
            // Get the EditText and its Editable
            View focusCurrent = mActivity.getWindow().getCurrentFocus();
            if (focusCurrent != null &&
                    (focusCurrent.getClass() == EditText.class
                            || focusCurrent.getClass() == AppCompatEditText.class)) {
                EditText edittext = (EditText) focusCurrent;
                Editable editable = edittext.getText();
                int start = edittext.getSelectionStart();
                // Apply the key to the edittext
                if (primaryCode == CODE_CLOSE) {
                    hideCustomKeyboard();
                } else if (primaryCode == CODE_DELETE) {
                    if (editable != null && start > 0) editable.delete(start - 1, start);
                } else if (primaryCode == CODE_OK && listener != null) {
                    listener.onOkClick();
                } else { // insert character
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };


    public interface OnOkeyClickListener {
        void onOkClick();
    }

    public LetterNumberKeyboard(Activity host, int viewId, int layoutId, OnOkeyClickListener listener) {
        mActivity = host;
        this.listener = listener;
        mKeyboardView = (KeyboardView) mActivity.findViewById(viewId);
        mKeyboardView.setKeyboard(new Keyboard(mActivity, layoutId));
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(mKeyBoardListener);
        // Hide the standard keyboard initially
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public KeyboardView getmKeyboardView() {
        return mKeyboardView;
    }

    public void setmKeyboardView(KeyboardView mKeyboardView) {
        this.mKeyboardView = mKeyboardView;
    }

    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the CustomKeyboard visible, and hide the system keyboard for view v.
     */
    public void showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Make the CustomKeyboard invisible.
     */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param resid The resource id of the EditText that registers to the custom keyboard.
     */
    public void registerTextView(/*int resid*/@NonNull EditText inputView) {
        // Find the EditText 'resid'
        // EditText inputView = (EditText) mActivity.findViewById(resid);
        // Make the custom keyboard appear
        inputView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showCustomKeyboard(v);
                else hideCustomKeyboard();
            }
        });
        inputView.setOnClickListener(new View.OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'inputView.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'inputView.setCursorVisible(true)' doesn't work )
        inputView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        inputView.setInputType(inputView.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }


}
