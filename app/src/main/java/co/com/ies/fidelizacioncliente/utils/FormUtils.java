package co.com.ies.fidelizacioncliente.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import co.com.ies.fidelizacioncliente.R;

/**
 * Clase para interactuar con los componentes de los formularios como spinners y edittext
 */
public class FormUtils {

   public static final String URL_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789:/.";
   public static final String LETTERS_NUMBERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

   /**
    * Asignar filtro para un campo de texto y limitar los caracteres que se pueden usar en el
    * @param allowedCharacters
    * @return
     */
   public static InputFilter getFilter (final String allowedCharacters) {

      return new InputFilter() {
         @Override
         public CharSequence filter (CharSequence source, int start, int end,
                                     Spanned dest, int dstart, int dend) {

            if (end > start) {
               for (int index = start; index < end; index++) {
                  if (!allowedCharacters.contains(String
                          .valueOf(source.charAt(index)))) {
                     return "";
                  }
               }
            }
            return null;
         }
      };
   }

   /**
    * Validar si hay algo en un campo de texto
    * @param editText
    * @param errorMessage
    * @return
     */
   public static int validarEditTextStandard (EditText editText, String errorMessage) {

      if (!editText.getText().toString().trim().isEmpty()) {
         editText.setError(null);
         return 0;
      } else {
         editText.setError(errorMessage != null ? errorMessage : "");
         return 1;
      }
   }

    /**
     * Validar si un item esta seleccionado
     * Para este funciona, se debe usar uno de los adapter spinners creados,
     * dado que estos en su xml usan el textview con id ctm_spn_txt_main, el cual permitirá resaltar el error
     * @param spinner
     * @param errorMessage
     * @return
     */
   public static int validarSpinnerStandard (Spinner spinner, String errorMessage) {

      TextView textView = (TextView) spinner.getSelectedView().findViewById(R.id.ctm_spn_txt_main);
      if (spinner.getSelectedItemPosition() == 0) {
         textView.setError(errorMessage != null ? errorMessage : "");
         return 1;
      } else {
         textView.setError(null);
         return 0;
      }

   }

}
