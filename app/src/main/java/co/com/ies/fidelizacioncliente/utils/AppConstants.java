package co.com.ies.fidelizacioncliente.utils;

/**
 * Clase contenedora de las constantes usadas en la app
 */
public class AppConstants {

    public final static String TAG = "Fidelizacion";
    public final static String SENDING_ACT = "send_act";

    public final static int RESULT_ACTIVITY_BACK = 10;
    public final static int RESULT_ACTIVITY_CLOSE_OK = 11;

    public final static String RESULT_DIALOG = "resultCode";
    public final static int RESULT_DIALOG_NONE = 0;
    public final static int RESULT_DIALOG_CARGAR = 1;
    public final static int RESULT_DIALOG_DESCARGAR = 2;

    public final static String CONTEO_RESEND = "resend";
    public final static int CONSTANTE_LIMITE_REENVIO_SMS=3;

    /**
     * Nombres de las keys de las preferencias
     */
    public class Prefs {

        public final static String SERVICE_PREF = "serv_pref";//nombre para obtener las preferencias guardadas
        public final static String URL = "url";
        public final static String NUM_DISP = "num_disp";
        public final static String SERIAL = "serial";
        public final static String PASS = "pass";
        public final static String USO = "use";
        public final static String SERV_PASS = "serv_pass";
        public final static String SERV_USR = "serv_usr";
        public final static String ID_CASINO = "id_casino";
        public final static String NOM_CASINO = "nom_casino";
        public final static String INTERV_UPDATE_POINTS = "inter_points";
        public final static String SHOW_KEYBOARD = "show_keyb";
        public final static String SHOW_VIDEO = "show_video";
        public final static String DATE = "date";
        public final static String CODIGO_CASINO = "codigoCasino";
        public final static String NOMBRE_USR = "nombreUsuario";
    }

    /**
     * Los valores aca mostrados son reflejo del array usos_app en el xml strings.xml
     */
    public class ServiceType {

        public final static int FIDELIZACION = 1;
        public final static int BAR = 2;
        public final static int CASHLESS = 3;
        public final static int FIDELBAR = 4;
        public final static int FIDELBARCASH = 5;

    }

    /**
     * Url de los web services usados
     */
    public class WebServs {

        public final static String BASE_URL = "/MobileApp/api/fidelizacion/";
        public final static String FIDELIZACION = BASE_URL + "iniciarFidelizacion";
        public static final String PARAMS = BASE_URL + "configuracionFidelizacion";
        public final static String FIDEL_USR = BASE_URL + "fidelizarMaquina";
        public final static String POINTS = BASE_URL + "visualizarPuntos";
        public final static String FIDEL_CLOSE = BASE_URL + "cerrarFidelizacionMaquina";
        public static final String LOGIN = "/MobileApp/j_spring_security_check";
        public static final String LIST_CASINOS = "/MobileApp/api/casino/listarCasinos";
        public static final String LIST_DEVICES = "/MobileApp/api/dispositivos/listarDispositivos";
        public static final String LIST_BAR = "/MobileApp/api/bar/visualizarPremiosBar";
        public static final String LIST_BARITEMS_USER = "/MobileApp/api/bar/visualizarPremiosBarXPuntos";
        public static final String POINTS_DAY = "/MobileApp/api/bar/visualizarPuntosBarXDia";
        public static final String ORDERS_USER_ESTATE = "/MobileApp/api/bar/listarPeticionesXCliente";
        public static final String ORDER_REDEEM = "/MobileApp/api/bar/realizarPeticion";
        public static final String ORDER_CANCEL = "/MobileApp/api/bar/anularPeticion";
        public static final String ORDER_RECEIVED = "/MobileApp/api/bar/confirmarPeticion";
        public static final String ORDER_BUY = "/MobileApp/api/bar/comprarPremio";
        public static final String CALL_SERVICE = "/MobileApp/api/varios/llamadoMaquina";
        public static final String CASINO_LOGO = "/MobileApp/api/bar/obtenerImagenBar";
        public final static String PROTOCOL = "https://";
        public final static String FIDELIZAR_CLIENTE = BASE_URL + "fidelizarClienteXMaquina";
        public final static String REENVIAR_CLAVE = BASE_URL + "reenviarClaveDinamica";
        public final static String REENVIAR_CLAVE_EMAIL = BASE_URL + "reenviarClaveDinamicaEmail";
        /*CASHLESS*/
        public static final String CARGAR_BILLETERO = "/MobileApp/api/cashless/cargarBilletero";
        public static final String REDIMIR_BILLETERO = "/MobileApp/api/cashless/redimirBilletero";
        public static final String CARGAR_MAQUINA = "/MobileApp/api/cashless/cargarMaquina";
        public static final String CONSULTAR_BILLETERO = "/MobileApp/api/cashless/consultarBilletero";
        public static final String VER_PREMIOS = "/MobileApp/api/premios/listarPremioMaquina";
        public static final String PAGAR_PREMIOS = "/MobileApp/api/premios/pagarPremio";

        public final static String JSON_ID = "JSESSIONID";
    }

    /**
     * Parametros usados al consultar web services
     */
    public class WebParams {

        public final static String SERIAL = "serial"; //identificador de la maquina
        public final static String TYPE = "tipoDispositivo"; //si es cabina o maquina
        public final static String DOC = "numeroDocumento"; //pertence al usuario que se loguea
        public final static String MACHINE = "maquina"; //serial de la maquina
        public final static String RESULT = "status";//resultado de los servicios
        public final static String USER = "j_username";
        public final static String PASS = "j_password";
        public static final String COOKIE = "Cookie";
        public static final String SET_COOKIE = "Set-Cookie";
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String STATUS = "statusDTO";
        public static final String AVALIABLE_POINTS = "cantidadPuntosDisponibles";
        public static final String REDEEMED_POINTS = "cantidadPuntosRedimidos";
        public static final String CASINOS = "casinos";
        public static final String CASINO_NAME = "nombreCasino";
        public static final String CASINO_CODE = "codigoCasino";
        public static final String USER_NAME = "nombreCompleto";
        public static final String USER_CLAVE_BD = "clave";
        public static final String USER_BILLETERO = "enableBilletero";
        public static final String PAGED = "first";
        public static final String PAG_SIZE = "pageSize";
        public static final String DEVICES = "dispositivos";
        public static final String NUM_DISP = "numeroDispositivo";
        public static final String MONTO = "montoRecaudo";
        public static final String STATE_RED = "estadoRed";
        public static final String STATE_SERIAL = "estadoSerial";
        public static final String STATE_SWITCH = "estadoSwitch";
        public static final String STATE_MACHINE = "estadoMaquina";
        public static final String FECHA = "fecha";
        public static final String BAR_LIST = "listaVisualizarPremiosDTO";
        public static final String PREMIOS_LIST = "premios";
        public static final String CASINO_IMAGEN = "imagen";
        public static final String GET_ALL = "cargaTodo";
        public static final String VALOR_BILLETERO = "billeteroCliente";
        public static final String CONSECUTIVO = "consecutivoPremio";
        public static final String VALOR_CARGA = "carga";

        public static final String ITEM_CONSECUTIVO="consecutivo";
        public static final String ITEM_FECHA="fechaGeneracion";
        public static final String ITEM_FORMATO="formato";
        public static final String ITEM_MONTO="monto";
        public static final String ITEM_PROGRESIVO="progresivo";
        public static final String ITEM_SERIAL="serialDispositivo";
        public static final String ITEM_MONTO_RETENCION="montoRetencion";
        public static final String ITEM_MONTO_REAL="montoReal";



        //parametros para la app
        public static final String CONFIG_PASS = "acceso";
        public static final String CONFIG_TIME_POINTS = "tiempo";
        public static final String CONFIG_KEYBOARD = "teclado";
        public static final String CONFIG_VIDEO = "video";
        public static final String CONFIG_TIME_CLOSE_BAR = "tiempo_cerrar_bar";
        public static final String CONFIG_SIMBOLO_DINERO = "simbolo_dinero";

        public static final String BAR_ID = "pk";
        public static final String BAR_NAME = "nombre";
        public static final String BAR_NUM_AVAILABLE = "unidadesDisponibles";
        public static final String BAR_ID_ORDER = "idPeticion";
        public static final String BAR_CATEGORY = "categoriaPremio";
        ;
        public static final String BAR_POINTS = "puntosParaCanjear";
        public static final String BAR_PRICE = "valorParaCanjear";
        public static final String BAR_IMAGE = "imagen";

        public static final String PRICE_NAME = "nombrePremio";
        public static final String ORDERS = "peticiones";
        public static final String ORDER_ID = "idPeticion";
        public static final String ORDER_ID_ITEM = "idPremio";
        public static final String ORDER_STATE = "estadoPeticion";
        public static final String ORDER_NOTE = "nota";
        public static final String ORDER_ABORT = "anular";
        public static final String ORDER_CONFIRM = "confirmar";
        public static final String ORDER_HAS_SESSION = "haySesion";

        public static final String BAR_ITEM_PAYMENT = "medioPago";

    }

    /**
     * Algunos valores de los paramentros enviados son constantes
     */
    public class WebCons {
        public final static String MOVIL = "MOVIL";
        public final static String PAGED = "0";
        public final static String MAX_PAG_SIZE = "1000";
        public final static String CARGAR_CANJEABLE = "CANJEABLE";

    }

    /**
     * Identificadores del resultado de la consulta a un web service
     */
    public class WebResult {

        public final static String FAIL = "-01";
        public final static String NO_INTERNET = "-02";
        public final static String OK = "00";
        public final static String MISSING_PARAM = "03";
        public final static String TERNICAL_ERROR = "09";
        public final static String CASINO_NOT_FOUND = "11";
        public final static String DEVICE_NOT_FOUND = "26";
        public final static String DEVICE_TYPE_NOT_VALID = "36";
        public final static String DEVICE_NOT_ALLOWED = "37";
        public final static String SESSION_EXPIRED = "38";
        public final static String BLOCKED_CLIENT = "40";
        public final static String CLIENT_NOT_FOUND = "41";
        public final static String DEVICE_NOT_AVALIBLE = "42";
        public final static String FIDEL_ERROR = "43";
        public final static String CLOSE_ERROR = "44";
        public final static String CLAVE_VENCIDA = "75";
        public final static String MAQUINA_BLOQUEADA = "78";
    }

    /**
     * Extensiones, nonbres y carpetas de archivos usados en la app
     */
    public class FileExtension {
        public static final String IMAGE_HEAD = "IMG_";
        public static final String VIDEO_HEAD = "VID_";
        public static final String DOC_HEAD = "DOC_";
        public static final String FIRMA_REP = "FM_REP_";
        public static final String FIRMA_FUNC = "FM_FUNC_";
        public static final String IMAGE_EXT = ".jpg";
        public static final String VID_EXT = ".mp4";
        public static final String VISITA_EXT = ".vvi";
        public static final String FILE_TXT = ".txt";

        public static final String FOLDER = "/Fidelizacion/";
        public static final String CRASH_LOG = "/CrashLog/";
        public static final String PATH_CONFIG = "/ConfigFidelizacion/";
        public static final String FILE_CONFIG = "AppConfig.xml";
        public static final String VIDEO_INTRO = "intro_video.mp4";
        public static final String LOGO_CASINO_NAME = "CasinoLogo" + IMAGE_EXT;
    }

    /**
     * Valores genericos usados a través de al aplicación
     */
    public class Generic {
        public final static String ID_DEFAULT = "-999";
        public final static int DEF_INTERV_UPDATE_POINTS = 30;
        public final static int DEF_INTERV_UPDATE_BILLETERO = 60;
        public final static int TIME_TO_CLOSE_BAR = 120000;
        public final static int TIME_TO_CLOSE_SETTINGS = 60000;
        public final static int TIME_TO_VALIDATE_SERVICE = 35000;
        public final static int TIME_TO_CLOSE_DIALOG_SHORT = 15000;
        public final static int TIME_TO_CLOSE_DIALOG_LONG = 30000;
        public static final String STRING_TRUE = "S";
        public static final String STRING_FALSE = "N";
        public static final String STRING_YES = "Si";
        public static final String STRING_NO = "No";
        public static final long MAX_VALUE_DOC = 99999999999l;

        public static final String CONFIG_CONTENT = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<config>\n" +
                "<url></url>\n" +
                "<usuario></usuario>\n" +
                "<contrasena></contrasena>\n" +
                "</config>";


        public static final String PATH_CONFIG = "ConfigFidelizacion";
        public static final String FILE_CONFIG = "AppConfig.xml";

        public static final String PAYMENT_CASH = "Efectivo";
        public static final String PAYMENT_POINTS = "Puntos";

        public static final String SERVICE_ASKED="callservice";
        public static final String CLAVE_CLIENTE="claveCliente";
        public static final String VALOR_BILLETERO="valorBilletero";

    }

    /**
     * Estados posibles de las peticiones realizadas
     */
    public class OrderState {
        public static final String ON_WAY = "EN_CAMINO";
        public static final String ON_QUEUE = "EN_COLA";
    }

    /**
     * identificador para saber que se esta solicitando al servicio de call atendance
     */
    public class CallService {
        public static final String ASK_SERVICE = "ask";//solicitar atencion
        public static final String STOP_SERVICE = "stop";//cancelar solicitud
    }

    /**
     * tag usadas dentro del archivo de configuración
     */
    public class ConfigTags {

        public static final String CONFIG = "config";
        public static final String URL = "url";
        public static final String USER = "usuario";
        public static final String PASS = "contrasena";
    }

}
