package co.com.ies.fidelizacioncliente.custom.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.entity.SelectableString;

/**
 * Created by user on 25/10/2017.
 */

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder> {

    private List<SelectableString> listOptions;
    private Context context;
    private OnLisItemClickListener listener;

    public interface OnLisItemClickListener {
        void onListItem(int index);

        void onListItemCheck(int index);
    }


    public AdapterSearch(Context context, List<SelectableString> list, OnLisItemClickListener listener) {
        this.context = context;
        listOptions = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.custom_category_search_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.ckBox.setText(listOptions.get(position).getValue());
        holder.ckBox.setChecked(listOptions.get(position).isChecked());

    }

    @Override
    public int getItemCount() {
        return listOptions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckBox ckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ckBox = (CheckBox) itemView.findViewById(R.id.ctm_category_ck);
            ckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onListItemCheck(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onListItem(getAdapterPosition());
        }
    }
}
