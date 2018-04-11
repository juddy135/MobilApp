package co.com.ies.fidelizacioncliente.custom.spinner;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import co.com.ies.fidelizacioncliente.R;

/**
 * Adapter sencillo para spinners, solo se reciben array de strings
 */
public class AdapterSpinnerSimple extends ArrayAdapter<String> {

   private String[] arrayStrings;
   private Context context;

   public AdapterSpinnerSimple (Context context, String[] objects) {

      super(context, R.layout.custom_spn_generic_item, objects);
      arrayStrings = objects;
      this.context = context;
   }

   @Override
   public View getView (int position, View convertView, ViewGroup parent) {

      ViewHolder viewHolder;

      if (convertView == null) {
         // inflate the layout
         LayoutInflater inflater = ((Activity) context).getLayoutInflater();
         convertView = inflater.inflate(R.layout.custom_spn_generic_item_no_drop, parent, false);
         // well set up the ViewHolder
         viewHolder = new ViewHolder();
         viewHolder.txtMain = (TextView) convertView.findViewById(R.id.ctm_spn_txt_main);
         // store the holder with the view.
         convertView.setTag(viewHolder);
      } else {
         // we've just avoided calling findViewById() on resource everytime
         // just use the viewHolder
         viewHolder = (ViewHolder) convertView.getTag();
      }
      // object item based on the position
      String text = arrayStrings[position];

      // assign values if the object is not null
      if (text != null) {
         // get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
         viewHolder.txtMain.setText(text);

      }
      return convertView;
   }

   @Override
   public View getDropDownView (int position, View convertView, ViewGroup parent) {

      ViewHolder viewHolder;

      if (convertView == null) {
         // inflate the layout
         LayoutInflater inflater = ((Activity) context).getLayoutInflater();
         convertView = inflater.inflate(R.layout.custom_spn_generic_item, parent, false);
         // well set up the ViewHolder
         viewHolder = new ViewHolder();
         viewHolder.txtMain = (TextView) convertView.findViewById(R.id.ctm_spn_txt_main);
         // store the holder with the view.
         convertView.setTag(viewHolder);
      } else {
         // we've just avoided calling findViewById() on resource everytime
         // just use the viewHolder
         viewHolder = (ViewHolder) convertView.getTag();
      }
      // object item based on the position
      String text = arrayStrings[position];      // assign values if the object is not null
      if (text != null) {
         // get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
         viewHolder.txtMain.setText(text);
      }
      return convertView;
   }

   static class ViewHolder {

      TextView txtMain;
   }
}
