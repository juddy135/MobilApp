package co.com.ies.fidelizacioncliente.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import co.com.ies.fidelizacioncliente.database.DBConstants;

/**
 * Created by obed.gonzalez on 03/03/2017.
 */

public class FileUtils {

    /**
     * Metodo que crea un archivo, para despues ser usado para una evidencia
     * Creado el 18/05/2015 a las 9:05 PM <br>
     *
     * @param rutaFile   ruta en donde se guardara el archivo
     * @param nombreFIle nombre del archivo a crear
     * @return el archivo creado en donde se pondra la evidencia
     * @author <a href="http://www.quipux.com/">Quipux Software.</a></br>
     */
    public static File crearArchivo(String rutaFile, String nombreFIle) {

        File directorio = new File(rutaFile);
        directorio.mkdirs();
        File file = new File(directorio.getAbsolutePath(), nombreFIle);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                MsgUtils.handleException(e);
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                MsgUtils.handleException(e);
            }

        }
        return file;
    }


    /**
     * Metodo para mover archivos de cualqueir tipo de un directorio a otro
     *
     * @param rutaOrigen  ruta de donde se va a tomar el archivo a mover
     * @param rutaDestino ruta para donde se va a mover el archivo destino
     * @Autor Daniel Gutierrez
     */
    public static void moveFile(File rutaOrigen, File rutaDestino) {


        if (!rutaDestino.exists())
            rutaDestino.mkdirs();

        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            File imageComparendo = new File(rutaDestino, rutaOrigen.getName());

            inStream = new FileInputStream(rutaOrigen);
            outStream = new FileOutputStream(imageComparendo);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inStream.read(buffer)) > 0)
                outStream.write(buffer, 0, length);

            inStream.close();
            outStream.close();

            rutaOrigen.delete();

        } catch (IOException e) {
            MsgUtils.handleException(e);
        }
    }

    public static void copyFile(File rutaOrigen, File rutaDestino) {


        if (!rutaDestino.getParentFile().exists())
            rutaDestino.getParentFile().mkdirs();

        InputStream inStream = null;
        OutputStream outStream = null;
        try {


            inStream = new FileInputStream(rutaOrigen);
            outStream = new FileOutputStream(rutaDestino);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inStream.read(buffer)) > 0)
                outStream.write(buffer, 0, length);

            inStream.close();
            outStream.close();

        } catch (IOException e) {
            MsgUtils.handleException(e);
        }
    }

    /**
     * Clase usada para guardar los archivos recibidos desde un servicio web,
     * los cuales usualmente llegan como una cadena de strings
     * @param filePath
     * @param fileName
     * @param stringFile
     */
    public static void createFileFromString(String filePath, String fileName, String stringFile) {

        File file = crearArchivo(filePath, fileName);
        try {
            OutputStream outputStream = new FileOutputStream(file);

            byte[] bytes = Base64.decode(stringFile);

            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            MsgUtils.handleException(e);
        }


    }

    public static void backUpDataBase(Context context) {

        File local = context.getDatabasePath(DBConstants.DB_NAME);
        File external = new File(Environment.getExternalStorageDirectory() + AppConstants.FileExtension.PATH_CONFIG +
                DBConstants.DB_NAME);
        if (local.exists()) {
            copyFile(local, external);
        }

    }


    /**
     * En caso de usar version mayor a 5, validar antes de invocar este metodo si se tienen permisos de escritura
     *
     * @param throwable
     */
    private void saveError(Throwable throwable) {
        FileWriter writer = null;
        try {
            File filePath = new File(Environment.getExternalStorageDirectory(), AppConstants.FileExtension.CRASH_LOG);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            File fileLog = new File(filePath,
                    DateUtils.getDateString(System.currentTimeMillis(), DateUtils.FORMAT_LOG) +
                            AppConstants.FileExtension.FILE_TXT);

            StringWriter errors = new StringWriter();
            throwable.printStackTrace(new PrintWriter(errors));

            fileLog.createNewFile();
            writer = new FileWriter(fileLog);
            writer.append(throwable.toString() + "\n");
            writer.append(throwable.getLocalizedMessage() + "\n");
            writer.append(throwable.getMessage() + "\n");
            writer.append(errors.toString() + "\n");
            writer.flush();

        } catch (IOException e) {
            MsgUtils.handleException(e);
        } catch (Exception e) {
            MsgUtils.handleException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    MsgUtils.handleException(e);
                }
            }
        }
    }
}
