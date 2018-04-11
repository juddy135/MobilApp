package co.com.ies.fidelizacioncliente.utils;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase usada para dar formato y obtener fechas
 */
public class DateUtils {

    public static final  String FORMAT_WEBSERV = "yyyy-MM-dd";
    public static final String FORMAT_LOG = "yyyy-MM-dd HH:mm:ss";

    public static String longToString(Long dateLong, String format) {
        Date date = new Date(dateLong);
        SimpleDateFormat df2 = new SimpleDateFormat(format);
        return df2.format(date);
    }

    public static String getDateString (long longDate, String format) {

        return new java.text.SimpleDateFormat(format).format(new java.util.Date(longDate));
    }

    public static long getDateLong (String stringDate, String format) {

        try {
            return new java.text.SimpleDateFormat(format).parse(stringDate).getTime();
        } catch (ParseException p) {
            Log.e(AppConstants.TAG, p.getMessage(), p.fillInStackTrace());
        }
        return -1l;
    }

    /**
     * Retorna como strinf una fecha de entrada segun el patron de entrada<br>
     * Creado el 26/09/2014 a las 16:46:04 <br>
     *
     * @param date
     * @param pattern
     *
     * @return
     *
     * @author <a href="http://www.quipux.com/">Quipux Software.</a></br>
     */
    public static String formatDate (Date date, String pattern) {

        if(date!=null) {
            SimpleDateFormat formateador = new SimpleDateFormat(pattern);
            return formateador.format(date);
        }else {
            return "";
        }

    }

    public static Date getDateFromLong (Context context, long longDate, String formatDate) {

        return new Date(longDate);
    }

    public static Date getDateFromString (String stringDate, String formatDate) {

        Date date = null;
        try {
            DateFormat format = new SimpleDateFormat(formatDate);
            date = format.parse(stringDate);
        } catch (ParseException parse) {
            Log.e(AppConstants.TAG, parse.getMessage(), parse);
        }
        return date;
    }

    public static String obtenerMes (String indexMes) {

        Integer mes = Integer.parseInt(indexMes);
        switch (mes) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";

            default:
                break;
        }
        return "";
    }

    public static String getCurrentTimeStamp(String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);//dd/MM/yyyy
        Date now = new Date(System.currentTimeMillis());
        String strDate = sdfDate.format(now);
        return strDate;
    }
}
