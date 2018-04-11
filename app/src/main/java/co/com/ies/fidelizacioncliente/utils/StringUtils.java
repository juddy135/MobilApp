package co.com.ies.fidelizacioncliente.utils;

/**
 * Clase para trabajar con strings
 */
public class StringUtils {

    /**
     * Validar si un string es nulo o vacío
     *
     * @param string
     * @return
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
