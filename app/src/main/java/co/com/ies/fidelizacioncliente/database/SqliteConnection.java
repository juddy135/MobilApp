package co.com.ies.fidelizacioncliente.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase usada para instanciar conexiones a la bd, controla la cantidad y evitar problemas de conexión
 */
public class SqliteConnection {

    protected Context context;

    protected SQLiteDatabase dbSqlite;

    private final DataBaseHelper dbOpenHelper;

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static SqliteConnection instance;

    private SqliteConnection(Context context) {

        this.dbOpenHelper = new DataBaseHelper(context);
        this.context = context;
        establishDb();
    }


    /**
     * Establece la base de datos
     */
    private void establishDb() {

        if (this.dbSqlite == null) {
            this.dbSqlite = this.dbOpenHelper.getWritableDatabase();
        }
    }

    /**
     * limpia la referencia a la base de datos
     */

    public SQLiteDatabase getDbSqlite() {

        if (dbSqlite == null) {
            establishDb();
        } else if (!dbSqlite.isOpen()) {
            dbSqlite = this.dbOpenHelper.getWritableDatabase();
        }
        return dbSqlite;
    }


    public DataBaseHelper getDBHelper() {

        return dbOpenHelper;
    }

    public static synchronized SqliteConnection getInstance(Context context) {

        if (instance == null) {
            instance = new SqliteConnection(context);
        }
        return instance;
    }

    /**
     * Se valida si existe alguna conexion activa, si lo hay esta es retornada y sumamos el contador de conectados, si no lo hay, la creamos y la retornamos
     *
     * @return
     */
    public synchronized SQLiteDatabase openDatabase() {

        if (mOpenCounter.incrementAndGet() < 1) {
            // Opening new database
            dbSqlite = dbOpenHelper.getWritableDatabase();
        }

        return dbSqlite;
    }

    /**
     * Se valida en este metodo es la unica conexión abierta y si lo es, se cierra la base de daos
     */
    public synchronized void closeDatabase() {

        if (mOpenCounter.decrementAndGet() < 1) {
            // Closing database
            dbSqlite.close();
        }
        /*if(mOpenCounter.get()<0)
            throw new IllegalStateException();*/
    }

}