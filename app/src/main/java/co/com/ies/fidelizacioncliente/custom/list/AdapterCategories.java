package co.com.ies.fidelizacioncliente.custom.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import co.com.ies.fidelizacioncliente.R;

/**
 * Created by user on 28/10/2017.
 */

public class AdapterCategories extends RecyclerView.Adapter<AdapterCategories.ViewHolderCategory> {


    private List<String> list;
    private Context context;
    private ListenerItemClick listener;

    public interface ListenerItemClick {
        void onListItemClick(int index);
    }

    public AdapterCategories(Context context, List<String> list, ListenerItemClick listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolderCategory onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.custom_category_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ViewHolderCategory(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderCategory holder, int position) {

        if (list.get(position) != null)
            holder.tvCategory.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderCategory extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCategory;

        public ViewHolderCategory(View itemView) {
            super(itemView);
            tvCategory = (TextView) itemView.findViewById(R.id.ctm_category_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onListItemClick(getAdapterPosition());
        }
    }
}
