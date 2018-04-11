package co.com.ies.fidelizacioncliente.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.custom.list.AdapterSearch;
import co.com.ies.fidelizacioncliente.entity.SelectableString;

import static co.com.ies.fidelizacioncliente.utils.AppConstants.Generic.TIME_TO_CLOSE_DIALOG_SHORT;

/**
 * Created by user on 25/10/2017.
 */

public class DialogFragSearch extends DialogFragment implements AdapterSearch.OnLisItemClickListener {

    public static final String LIST_SEARCH = "list_search";
    private EditText etSearch;
    private RecyclerView rvCategory;
    private Button btnOk;
    private Button btnCancel;
    private TimerTask task;
    private List<SelectableString> listSearch;
    private AdapterSearch adapterSearch;
    private RecyclerView.LayoutManager layoutManager;
    private OnSearchListener mListener;


    public interface OnSearchListener {
        void onSearch(String searchValue, List<String> categoriesSelected);

        void onCancel();
    }

    public DialogFragSearch() {

    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogFragSearch.OnSearchListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogActionsListener");
        }
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

        return inflater.inflate(R.layout.dialog_frag_search, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        obtenerComponentes(view);
        setListeners();
        startTimer();
        if (bundle != null && !bundle.isEmpty()) {
            if (bundle.containsKey(LIST_SEARCH)) {
                listSearch = new ArrayList<>();
                ArrayList<String> list = bundle.getStringArrayList(LIST_SEARCH);
                if (list != null) {
                    for (String value : list) {
                        listSearch.add(new SelectableString(value, false));
                    }
                }
            }

        }

        initialize();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mListener.onCancel();
        super.onCancel(dialog);
    }

    @Override
    public void onListItem(int index) {
        handleListClick(index);
    }

    @Override
    public void onListItemCheck(int index) {
        handleListClick(index);
    }

    private void handleListClick(int index) {
        listSearch.get(index).setChecked(!listSearch.get(index).isChecked());
        adapterSearch.notifyItemChanged(index);
    }

    private void obtenerComponentes(View view) {

        btnOk = (Button) view.findViewById(R.id.dialog_frag_search_btn_ok);
        btnCancel = (Button) view.findViewById(R.id.dialog_frag_search_btn_cancel);
        etSearch = (EditText) view.findViewById(R.id.dialog_frag_search_edt);
        rvCategory = (RecyclerView) view.findViewById(R.id.dialog_frag_search_rv_cat);
    }

    private void setListeners() {

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = etSearch.getText().toString();
                List<String> categoriesChecked = new ArrayList<>();
                for (SelectableString selectableString : listSearch) {
                    if (selectableString.isChecked())
                        categoriesChecked.add(selectableString.getValue().toLowerCase());
                }

                mListener.onSearch(value, categoriesChecked);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCancel();
                dismiss();
            }
        });
    }

    private void initialize() {
        if (listSearch != null && !listSearch.isEmpty()) {

            adapterSearch = new AdapterSearch(getContext(), listSearch, this);
            rvCategory.setAdapter(adapterSearch);
            layoutManager = new GridLayoutManager(getContext(), 2);
            rvCategory.setLayoutManager(layoutManager);

        } else {
            rvCategory.setVisibility(View.GONE);
        }
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