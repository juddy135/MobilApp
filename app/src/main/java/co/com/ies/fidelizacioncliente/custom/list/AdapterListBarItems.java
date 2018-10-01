package co.com.ies.fidelizacioncliente.custom.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.entity.BarItem;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.SharedPrefUtils;
import co.com.ies.fidelizacioncliente.utils.StringUtils;

/**
 * Adapter para la lista de items en la pantalla de bar
 */
public class AdapterListBarItems extends RecyclerView.Adapter<AdapterListBarItems.ViewHolderBar> {

    private List<BarItem> listBackUp;
    private List<BarItem> list;
    private Context context;
    private OnBarItemAction listener;
    private boolean isUserLogged;
    private Locale locale;
    private NumberFormat currencyFormatter;
    private NumberFormat numberFormatter;
    private int userPoints = 0;
    private String moneySimbol = null;

    /**
     * Interface utilizada para que la actividad pueda obtener el valor seleecionado con la accion seleccionada
     */
    public interface OnBarItemAction {

        void onBuyItem(BarItem barItem, int position);

        void onRedeemItem(BarItem barItem, int position);

        void onCancelOrder(BarItem barItem, int position);

        void onOrderReceived(BarItem barItem, int position);
    }


    public AdapterListBarItems(Context context, OnBarItemAction listener, List<BarItem> objects, boolean isUserLooged, String points) {
        list = objects;
        listBackUp = new ArrayList<>();
        for (BarItem barItem : objects) {
            listBackUp.add(barItem);
        }
        this.context = context;
        this.listener = listener;
        this.isUserLogged = isUserLooged;
        locale = new Locale("es", "CO");
        currencyFormatter =
                NumberFormat.getCurrencyInstance(locale);
        numberFormatter = NumberFormat.getNumberInstance(locale);

        currencyFormatter.setMaximumFractionDigits(0);
        currencyFormatter.setMinimumFractionDigits(0);

        numberFormatter.setMaximumFractionDigits(0);
        numberFormatter.setMinimumFractionDigits(0);

        if (points != null) {
            userPoints = Integer.valueOf(points);
        }

        moneySimbol = SharedPrefUtils.getSharedPreference(context, AppConstants.Prefs.SERVICE_PREF).
                getString(AppConstants.WebParams.CONFIG_SIMBOLO_DINERO, "C$");

    }


    @Override
    public ViewHolderBar onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.custom_list_item_bar_items;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        AdapterListBarItems.ViewHolderBar viewH =new AdapterListBarItems.ViewHolderBar(view);

        return viewH;
    }

    @Override
    public void onBindViewHolder(ViewHolderBar viewHolderBar, int position) {
        BarItem barItem = list.get(position);
        if (barItem != null) {

            // get the TextView from the ViewHolderBar and then set the text (item name) and tag (item ID) values
            viewHolderBar.txtName.setText(barItem.getName());

            if (barItem.getOrderState() == null) {//Si es un item que se puede comprar/redimir
                viewHolderBar.btnCancel.setVisibility(View.GONE);
                viewHolderBar.btnConfirm.setVisibility(View.GONE);
            }

            //Se muestran item tenga puntos se puede redimir
            if (!StringUtils.isNullOrEmpty(barItem.getPoints())) {
                viewHolderBar.txtPoints.setText(context.getString(R.string.points_param,
                        numberFormated(barItem.getPoints())));
                viewHolderBar.btnRedeem.setVisibility(View.VISIBLE);
            }
            //Mientras el item tenga un precio, se puede comprar
            if (!StringUtils.isNullOrEmpty(barItem.getPrice())) {
                viewHolderBar.txtPrice.setText(context.getString(R.string.price_param, moneySimbol, numberFormated(barItem.getPrice())));
                viewHolderBar.btnBuy.setVisibility(View.VISIBLE);
            }
            if (!StringUtils.isNullOrEmpty(barItem.getAbsoluthPathThumbnail())) {
                File imgFile = new File(barItem.getAbsoluthPathThumbnail());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    viewHolderBar.imgThumbnail.setImageBitmap(myBitmap);
                }
            } else {
                viewHolderBar.imgThumbnail.setVisibility(View.INVISIBLE);
            }
            //Item con puntos pero sin usuario logueado, se oculta panel de puntos
            if (StringUtils.isNullOrEmpty(barItem.getPoints()) ||  !isUserLogged) {
                viewHolderBar.txtPoints.setVisibility(View.INVISIBLE);
            }

            // en caso de q el item tenga un pedido
            if (barItem.getOrderState() != null) {
                viewHolderBar.imgOrderState.setVisibility(View.VISIBLE);
                viewHolderBar.txtOrderState.setVisibility(View.VISIBLE);
                viewHolderBar.btnCancel.setVisibility(View.VISIBLE);
                viewHolderBar.btnConfirm.setVisibility(View.VISIBLE);
                //escondemos los botones para redimir y comprar
                viewHolderBar.btnBuy.setVisibility(View.GONE);
                viewHolderBar.btnRedeem.setVisibility(View.GONE);

                viewHolderBar.imgOrderState.setBackgroundResource(
                        barItem.getOrderState().getState().equals(AppConstants.OrderState.ON_WAY) ?
                                R.drawable.update_orange:
                                R.drawable.bell_ring_yellow);
                viewHolderBar.txtOrderState.setText(barItem.getOrderState().getState().equals(AppConstants.OrderState.ON_WAY) ?
                        R.string.common_on_way:
                        R.string.common_on_hold);

                if (!barItem.getOrderState().isConfirmable()) {
                    setButtonDisable(viewHolderBar.btnConfirm);
                }

                if (!barItem.getOrderState().isNullable()) {
                    setButtonDisable(viewHolderBar.btnCancel);
                }

            } else {
                viewHolderBar.imgOrderState.setVisibility(View.INVISIBLE);
                viewHolderBar.txtOrderState.setVisibility(View.INVISIBLE);
                //Condicional para deshabilitar boton de redimir
                if (StringUtils.isNullOrEmpty(barItem.getPoints()) || /*item puntos vacio*/
                        !isUserLogged || /*usuario no logueado*/
                        (!StringUtils.isNullOrEmpty(barItem.getPoints())/*item puntos*/
                                && isUserLogged /*usuario logueado*/
                                && userPoints < Integer.valueOf(barItem.getPoints())  /*item puntos > puntos usuario*/
                        )
                        ) {
                    setButtonDisable(viewHolderBar.btnRedeem);
                }else{
                    viewHolderBar.btnRedeem.setVisibility(View.VISIBLE);
                }

                if (StringUtils.isNullOrEmpty(barItem.getPrice())) {
                    viewHolderBar.txtPrice.setVisibility(View.INVISIBLE);
                    setButtonDisable(viewHolderBar.btnBuy);
                }
            }
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * desabilitamos el boton y ponemos un color de letra mas claro, para hacerlo ver diferente y sea claro
     * que está desabilitado
     *
     * @param button
     */
    private void setButtonDisable(Button button) {
        button.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setTextColor(context.getColor(R.color.text_disable));
        } else {
            button.setTextColor(context.getResources().getColor(R.color.text_disable));
        }
    }

    /**
     * Método para dar formato a los numeros como valor monetario, en algunos celulares el comportamiento es diferente
     *
     * @param value
     * @return
     */
    private String currencyFormated(String value) {

        return currencyFormatter.format(Long.valueOf(value));
    }

    /**
     * Método para dar formato a los numeros en miles
     *
     * @param value
     * @return
     */
    private String numberFormated(String value) {

        return numberFormatter.format(Long.valueOf(value));
    }

    /**
     * Usado para buscar por categoría y nombre
     *
     * @param keySearch
     * @param filterCategories
     */
    public void filter(String keySearch, List<String> filterCategories) {
        boolean posibleChange = false;
        resetList();

        if (filterCategories != null && !filterCategories.isEmpty()) {
            posibleChange = true;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                BarItem item = (BarItem) iterator.next();
                if (!filterCategories.contains(item.getCategory().toUpperCase()))
                    iterator.remove();
            }
        }

        if (!StringUtils.isNullOrEmpty(keySearch)) {
            posibleChange = true;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                BarItem item = (BarItem) iterator.next();
                if (!item.getName().toUpperCase().contains(keySearch.toUpperCase()))
                    iterator.remove();
            }
        }

        if (posibleChange)
            notifyDataSetChanged();
    }

    /**
     * Usado para buscar por categoría
     *
     * @param keySearch
     */
    public void filter(String keySearch) {
        boolean posibleChange = false;
        resetList();

        if (!StringUtils.isNullOrEmpty(keySearch)) {
            posibleChange = true;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                BarItem item = (BarItem) iterator.next();
                if (!item.getCategory().toUpperCase().equals(keySearch.toUpperCase()))
                    iterator.remove();
            }
        }

        if (posibleChange)
            notifyDataSetChanged();
    }


    public void resetFilter() {
        resetList();
        notifyDataSetChanged();
    }

    private void resetList() {
        Log.i("ADAPTER", "RESET LIST");
        list.clear();
        for (BarItem barItem : listBackUp) {
            list.add(barItem);
        }
    }

    class ViewHolderBar extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtPoints;
        TextView txtPrice;
        TextView txtOrderState;
        ImageView imgThumbnail;
        ImageView imgOrderState;
        Button btnBuy;
        Button btnRedeem;
        Button btnCancel;
        Button btnConfirm;

        public ViewHolderBar(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.ctm_item_bar_txt_name);
            txtPrice = (TextView) itemView.findViewById(R.id.ctm_item_bar_txt_price);
            txtPoints = (TextView) itemView.findViewById(R.id.ctm_item_bar_txt_points);
            txtOrderState = (TextView) itemView.findViewById(R.id.ctm_item_bar_txt_orderstate);
            btnBuy = (Button) itemView.findViewById(R.id.ctm_item_bar_btn_buy);
            btnRedeem = (Button) itemView.findViewById(R.id.ctm_item_bar_btn_redeem);
            btnCancel = (Button) itemView.findViewById(R.id.ctm_item_bar_btn_cancel);
            btnConfirm = (Button) itemView.findViewById(R.id.ctm_item_bar_btn_received);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.ctm_item_bar_img_thumbnail);
            imgOrderState = (ImageView) itemView.findViewById(R.id.ctm_item_bar_img_orderstate);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Log.i("LISTENER","CANCELAR "+list.get(position).getName()+" POS: "+String.valueOf(position));
                    listener.onCancelOrder(list.get(position), position);
                }
            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Log.i("LISTENER","RECEIVED "+list.get(position).getName()+" POS: "+String.valueOf(position));
                    listener.onOrderReceived(list.get(position), position);
                }
            });

            btnRedeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Log.i("LISTENER","REDEEM "+list.get(position).getName()+" POS: "+String.valueOf(position));
                    listener.onRedeemItem(list.get(position), position);
                }
            });

            btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Log.i("LISTENER","BUY "+list.get(position).getName()+" POS: "+String.valueOf(position));
                    listener.onBuyItem(list.get(position), position);
                }
            });
        }
    }

}
