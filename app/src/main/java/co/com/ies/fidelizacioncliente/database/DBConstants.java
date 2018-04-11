package co.com.ies.fidelizacioncliente.database;

/**
 * Clase donde se almacenan todas las constantes de las tablas en la base de datos local
 */
public class DBConstants {

    public final static String DB_NAME = "DbFidelizacion";
    public final static int DB_VERSION = 2;

    public class Bar {
        public static final String TABLE = "bar_items";
        public static final String ID_ = "id_item";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String AVAILABLES = "availables";
        public static final String POINTS = "points";
        public static final String PATH_THUMBNAIL = "path_thumbnail";
        public static final String CATEGORY = "category";
    }

    public class Cassinos {

        public static final String TABLE = "cassinos";
        public static final String ID_ = "id_cassino";
        public static final String NAME = "name";
    }

    public class Machines {

        public static final String TABLE = "machines";
        public static final String NUM_DISP = "num_disp";
        public static final String ID_CASSINO = Cassinos.ID_;
        public static final String SERIAL = "serial";
        public static final String MONTO = "monto_recaudo";
        public static final String STATE_RED = "state_red";
        public static final String STATE_SERIAL = "state_serial";
        public static final String STATE_SWITCH = "state_switch";
        public static final String STATE_MACHINE = "state_machine";
    }
}
