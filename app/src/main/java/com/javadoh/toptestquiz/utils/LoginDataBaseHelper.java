package com.javadoh.toptestquiz.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.javadoh.toptestquiz.activities.consulta.bean.ReporteAuxBean;
import com.javadoh.toptestquiz.activities.consulta.bean.ReporteExamenActualPregResp;
import com.javadoh.toptestquiz.activities.consulta.bean.ReporteExamenActualTerminado;
import com.javadoh.toptestquiz.activities.consulta.bean.ReportePreguntaLinealAuxBean;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNube;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNubeCreados;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNubeDatos;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNubeSubmitted;
import com.javadoh.toptestquiz.utils.bean.MemoryBean;
import com.javadoh.toptestquiz.utils.bean.OperacionesBdBean;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioCurrentRespuestas;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioEncuestado;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by luiseliberal on 29-06-2015.
 */
public class LoginDataBaseHelper implements OperacionesBdInterface {

    private static final String TAG = LoginDataBaseHelper.class.getName();
    static final String DATABASE_NAME = "dynamictest_ppal.db";
    static final int DATABASE_VERSION = 2;
    public static final int NAME_COLUMN = 1;
    static final String TBL_ENCUESTADO_TAG = "ENCUESTADO";
    static final String TBL_REPORTE_TAG = "REPORTE";

    // SQL Statement to create a new database.
    static final String DATABASE_CREATE_TBL_ENCUESTADO = "create table " + "ENCUESTADO" +
            "( " + "ID" + " integer primary key autoincrement, DNI_ENCUESTADOR text, USERID_ENCUESTADOR integer, " +
            "USERLOGIN_ENCUESTADOR text, DNI_ENCUESTADO text, NOMBRES_ENCUESTADO  text," +
            " APELLIDOS_ENCUESTADO text, DIA_ENCUESTADO text, HORA_ENCUESTADO text, " +
            "LOCACION_ENCUESTADO text, GENERO_ENCUESTADO integer, EDAD_ENCUESTADO integer," +
            " COMUNA_ENCUESTADO text, OCUPACION_ENCUESTADO text); ";


    static final String DATABASE_CREATE_TBL_REPORTE = "create table " + "REPORTE" +
            "( " + "ID" + " integer primary key autoincrement, DNI_ENCUESTADO text, NOMBRES_ENCUESTADO text, " +
            "APELLIDOS_ENCUESTADO text, ID_EXAM integer, ID_EXAMEN_PRESENTADO integer, ID_PREGUNTA integer, " +
            "PREGUNTA text, ID_RESPUESTA integer, RESPUESTA text, TIEMPO_RESPUESTA integer, USERID_ENCUESTADOR integer, " +
            "USERLOGIN_ENCUESTADOR text); ";

    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DBHelper dbHelper;

    public LoginDataBaseHelper(Context _context) {
        context = _context;
        dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public LoginDataBaseHelper open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertEntry(String userName, String password) {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("USERNAME", userName);
        newValues.put("PASSWORD", password);

        // Insert the row into your table
        db.insert("LOGIN", null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }

    public int deleteEntry(String UserName) {
        //String id=String.valueOf(ID);
        String where = "USERNAME=?";
        int numberOFEntriesDeleted = db.delete("ENCUESTADO", where, new String[]{UserName});
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;
    }

    public String getSingleEntry(String userName) {
        Cursor cursor = db.query("ENCUESTADO", null, " USERNAME=?", new String[]{userName}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
        cursor.close();
        return password;
    }

    public void updateEntry(String userName, String password) {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("USERNAME", userName);
        updatedValues.put("PASSWORD", password);

        String where = "USERNAME = ?";
        db.update("ENCUESTADO", updatedValues, where, new String[]{userName});
    }

    @Override
    public void sendDataReport(PerfilUsuarioEncuestado usuarioEncuestado, PerfilUsuarioNube encuestador, int idExamen, int correlativoExamenPresentado, int idPregunta, String pregunta, int idRespuesta, String respuesta, int tiempoRespuesta) {

        ContentValues newValues = new ContentValues();
        Cursor cursorMaxExamPresented = null;
        int examenPresentado = -1;
        try {
            // Assign values for each row.
            newValues.put("DNI_ENCUESTADO", usuarioEncuestado.getDniEncuestado());
            newValues.put("NOMBRES_ENCUESTADO", usuarioEncuestado.getNombresEncuestado());
            newValues.put("APELLIDOS_ENCUESTADO", usuarioEncuestado.getApellidosEncuestado());
            newValues.put("ID_EXAM", idExamen);
            newValues.put("ID_EXAMEN_PRESENTADO", correlativoExamenPresentado);//EXAMEN LOCAL CORRELATIVO
            newValues.put("ID_PREGUNTA", idPregunta);
            newValues.put("PREGUNTA", pregunta);
            newValues.put("ID_RESPUESTA", idRespuesta);
            newValues.put("RESPUESTA", respuesta);
            newValues.put("TIEMPO_RESPUESTA", tiempoRespuesta);
            newValues.put("USERID_ENCUESTADOR", encuestador.getUser_id());
            newValues.put("USERLOGIN_ENCUESTADOR", encuestador.getUser_login());

            // Insert the row into your table
            db.insert("REPORTE", null, newValues);

            Log.d(TAG, "Se registro correctamente los datos de la pregunta "+idPregunta);

            //TEST DE SET CORRELATIVO EXAMEN PRESENTADO
            MemoryBean.setIdExamenPresentado(correlativoExamenPresentado);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al intentar enviar la respuesta al servidor", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void sendDataEncuestado(PerfilUsuarioEncuestado usuarioEncuestado, PerfilUsuarioNube encuestador) {

        ContentValues newValues = new ContentValues();

        try {
            // Assign values for each row.
            newValues.put("DNI_ENCUESTADOR", encuestador.getUser_rut());
            newValues.put("USERID_ENCUESTADOR", encuestador.getUser_id());
            newValues.put("USERLOGIN_ENCUESTADOR", encuestador.getUser_login());
            newValues.put("DNI_ENCUESTADO", usuarioEncuestado.getDniEncuestado());
            newValues.put("NOMBRES_ENCUESTADO", usuarioEncuestado.getNombresEncuestado());
            newValues.put("APELLIDOS_ENCUESTADO", usuarioEncuestado.getApellidosEncuestado());
            newValues.put("DIA_ENCUESTADO", usuarioEncuestado.getDiaEncuestado());
            newValues.put("HORA_ENCUESTADO", usuarioEncuestado.getHoraEncuestado());
            newValues.put("LOCACION_ENCUESTADO", usuarioEncuestado.getLocacionEncuestado());
            newValues.put("GENERO_ENCUESTADO", usuarioEncuestado.getGeneroEncuestado());
            newValues.put("EDAD_ENCUESTADO", usuarioEncuestado.getEdadEncuestado());
            newValues.put("COMUNA_ENCUESTADO", usuarioEncuestado.getComunaEncuestado());
            newValues.put("OCUPACION_ENCUESTADO", usuarioEncuestado.getOcupacionEncuestado());

            // Insert the row into your table
            db.insert("ENCUESTADO", null, newValues);

            Log.d(TAG, "Se registro correctamente los datos del presentador.");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al intentar enviar la respuesta al servidor", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public HashMap<String, OperacionesBdBean> getTotalReportesPorUsuario(PerfilUsuarioNube encuestador) {

        HashMap<String, OperacionesBdBean> mapaDeReportesUsuario = new LinkedHashMap<>();
        OperacionesBdBean operacionesBdBean;
        Cursor cursor = null;

        try {

            if ("admin".equalsIgnoreCase(encuestador.getUser_login())) {

                cursor = db.rawQuery("SELECT R.ID, R.DNI_ENCUESTADO, R.NOMBRES_ENCUESTADO, R.APELLIDOS_ENCUESTADO, R.ID_EXAM, " +
                        "R.ID_EXAMEN_PRESENTADO, R.ID_PREGUNTA, R.PREGUNTA, R.ID_RESPUESTA, R.RESPUESTA, " +
                        "R.TIEMPO_RESPUESTA, EN.DNI_ENCUESTADOR, EN.USERLOGIN_ENCUESTADOR, EN.DIA_ENCUESTADO, EN.HORA_ENCUESTADO, " +
                        "EN.LOCACION_ENCUESTADO, EN.GENERO_ENCUESTADO, EN.EDAD_ENCUESTADO, EN.COMUNA_ENCUESTADO, " +
                        "EN.OCUPACION_ENCUESTADO, R.USERID_ENCUESTADOR FROM REPORTE R " +
                        "INNER JOIN ENCUESTADO EN " +
                        "ON (R.USERID_ENCUESTADOR = EN.USERID_ENCUESTADOR AND R.DNI_ENCUESTADO = EN.DNI_ENCUESTADO);", null);

                        /*"select ID, ENCUESTADOR, ID_EXAM, ID_PREGUNTA, PREGUNTA, ID_RESPUESTA, RESPUESTA, TIEMPO_RESPUESTA, DIA_ENCUESTADO," +
                        "HORA_ENCUESTADO, LOCACION_ENCUESTADO, GENERO_ENCUESTADO, EDAD_ENCUESTADO," +
                        "CLASE_USUARIO_ENCUESTADO, COMUNA_ENCUESTADO, FRECUENCIA_ENCUESTADO, MOTIVO_ENCUESTADO," +
                        "OCUPACION_ENCUESTADO, OTROS1_ENCUESTADO, OTROS2_ENCUESTADO, OTROS3_ENCUESTADO," +
                        "OTROS4_ENCUESTADO, OTROS5_ENCUESTADO FROM REPORTE;",null);*/

            } else {

                cursor = db.rawQuery("SELECT R.ID, R.DNI_ENCUESTADO, R.NOMBRES_ENCUESTADO, R.APELLIDOS_ENCUESTADO, R.ID_EXAM, " +
                        "R.ID_EXAMEN_PRESENTADO, R.ID_PREGUNTA, R.PREGUNTA, R.ID_RESPUESTA, R.RESPUESTA, " +
                        "R.TIEMPO_RESPUESTA, EN.DNI_ENCUESTADOR, EN.USERLOGIN_ENCUESTADOR, EN.DIA_ENCUESTADO, EN.HORA_ENCUESTADO, " +
                        "EN.LOCACION_ENCUESTADO, EN.GENERO_ENCUESTADO, EN.EDAD_ENCUESTADO, EN.COMUNA_ENCUESTADO, " +
                        "EN.OCUPACION_ENCUESTADO, R.USERID_ENCUESTADOR FROM REPORTE R " +
                        "INNER JOIN ENCUESTADO EN " +
                        "ON (R.USERID_ENCUESTADOR = EN.USERID_ENCUESTADOR AND R.DNI_ENCUESTADO = EN.DNI_ENCUESTADO) " +
                        "WHERE EN.USERLOGIN_ENCUESTADOR = '" + encuestador.getUser_login() + "';", null);

            }

            if (cursor.getCount() < 1) //USUARIO NO EXISTE RETORNA NULL
            {
                cursor.close();
                return null;

            } else {

                cursor.moveToFirst();

                for (int x = 0; x < cursor.getCount(); x++) {

                    int idRow = cursor.getInt(cursor.getColumnIndex("ID"));
                    int idEncuestador = cursor.getInt(cursor.getColumnIndex("USERID_ENCUESTADOR"));
                    String dniEncuestado = cursor.getString(cursor.getColumnIndex("DNI_ENCUESTADO"));
                    operacionesBdBean = new OperacionesBdBean();

                    operacionesBdBean.setIdRow(idRow);
                    operacionesBdBean.setDniEncuestado(dniEncuestado);
                    operacionesBdBean.setNombresEncuestado(cursor.getString(cursor.getColumnIndex("NOMBRES_ENCUESTADO")));
                    operacionesBdBean.setApellidosEncuestado(cursor.getString(cursor.getColumnIndex("APELLIDOS_ENCUESTADO")));
                    operacionesBdBean.setIdExamen(cursor.getInt(cursor.getColumnIndex("ID_EXAM")));
                    operacionesBdBean.setIdPregunta(cursor.getInt(cursor.getColumnIndex("ID_PREGUNTA")));
                    operacionesBdBean.setPregunta(cursor.getString(cursor.getColumnIndex("PREGUNTA")));
                    operacionesBdBean.setIdRespuesta(cursor.getInt(cursor.getColumnIndex("ID_RESPUESTA")));
                    operacionesBdBean.setRespuesta(cursor.getString(cursor.getColumnIndex("RESPUESTA")));
                    operacionesBdBean.setTiempoDeRespuesta(cursor.getInt(cursor.getColumnIndex("TIEMPO_RESPUESTA")));
                    //DATOS PERFIL ENCUESTADO
                    operacionesBdBean.setDniEncuestador(cursor.getString(cursor.getColumnIndex("DNI_ENCUESTADOR")));
                    operacionesBdBean.setUserLoginEncuestador(cursor.getString(cursor.getColumnIndex("USERLOGIN_ENCUESTADOR")));
                    operacionesBdBean.setDia(cursor.getString(cursor.getColumnIndex("DIA_ENCUESTADO")));
                    operacionesBdBean.setHora(cursor.getString(cursor.getColumnIndex("HORA_ENCUESTADO")));
                    operacionesBdBean.setLocacion(cursor.getString(cursor.getColumnIndex("LOCACION_ENCUESTADO")));
                    operacionesBdBean.setGenero(cursor.getInt(cursor.getColumnIndex("GENERO_ENCUESTADO")));
                    operacionesBdBean.setEdad(cursor.getInt(cursor.getColumnIndex("EDAD_ENCUESTADO")));
                    operacionesBdBean.setComuna(cursor.getInt(cursor.getColumnIndex("COMUNA_ENCUESTADO")));
                    operacionesBdBean.setOcupacion(cursor.getInt(cursor.getColumnIndex("OCUPACION_ENCUESTADO")));

                    /*operacionesBdBean.setOtros1(cursor.getInt(cursor.getColumnIndex("OTROS1_ENCUESTADO")));
                    operacionesBdBean.setOtros2(cursor.getInt(cursor.getColumnIndex("OTROS2_ENCUESTADO")));
                    operacionesBdBean.setOtros3(cursor.getInt(cursor.getColumnIndex("OTROS3_ENCUESTADO")));
                    operacionesBdBean.setOtros4(cursor.getInt(cursor.getColumnIndex("OTROS4_ENCUESTADO")));
                    operacionesBdBean.setOtros5(cursor.getInt(cursor.getColumnIndex("OTROS5_ENCUESTADO")));*/

                    //SE AGREGAN LOS DATOS EN EL MAPA
                    mapaDeReportesUsuario.put(idRow + "-" + idEncuestador + "-" + dniEncuestado, operacionesBdBean);

                    cursor.moveToNext();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Lo sentimos, ocurrio un error", Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mapaDeReportesUsuario;
    }

    public String validarDataReporte(String dniEncuestado, String idPregunta) {
        Cursor cursor = db.query("REPORTE", null, " DNI_ENCUESTADO=? AND ID_PREGUNTA=?", new String[]{dniEncuestado, idPregunta}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "";
        }
        cursor.moveToFirst();
        String idRespuesta = cursor.getString(cursor.getColumnIndex("ID_RESPUESTA"));
        cursor.close();
        return idRespuesta;
    }


    public int validarMaxIdPreguntaReporte() {

        int maxIdPregunta = 0;
        Cursor cursor = db.rawQuery("select MAX(ID_PREGUNTA) AS MAX_PREGUNTA FROM REPORTE;", null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return 0;
        }
        cursor.moveToFirst();
        maxIdPregunta = cursor.getInt(cursor.getColumnIndex("MAX_PREGUNTA"));
        cursor.close();

        return maxIdPregunta;
    }


    public int validarUltimoIdExamenPorUsuario(PerfilUsuarioNube encuestador, PerfilUsuarioEncuestado usuarioEncuestado) {

        Cursor cursor = db.query("REPORTE", null, " USERLOGIN_ENCUESTADOR=? AND ID_EXAM = (SELECT MAX(ID_EXAM) FROM REPORTE WHERE DNI_ENCUESTADO =?)", new String[]{encuestador.getUser_login(), usuarioEncuestado.getDniEncuestado()}, null, null, null);
        int idExamen = 0;

        if (cursor.getCount() > 1) //EXISTEN REGISTROS PARA EL USUARIO
        {

            cursor.moveToFirst();

            idExamen = cursor.getInt(cursor.getColumnIndex("ID_EXAM"));

            //AUTO INCREMENTO
            idExamen = idExamen + 1;

            cursor.close();
        }

        return idExamen;
    }

    public int validarUltimoExamenPresentadoUsuario(PerfilUsuarioEncuestado usuarioEncuestado, PerfilUsuarioNube encuestador, int idExamen){

        int examenPresentado = 0;
        Cursor cursorMaxExamPresented = null;

        try {
            cursorMaxExamPresented = db.rawQuery("SELECT MAX(R.ID_EXAMEN_PRESENTADO) AS EXAMEN_PRESENTADO FROM REPORTE R " +
                    "WHERE R.DNI_ENCUESTADO = " + usuarioEncuestado.getDniEncuestado() +
                    " AND R.ID_EXAM = " + idExamen + " AND R.USERID_ENCUESTADOR = " + encuestador.getUser_id(), null);

            if (cursorMaxExamPresented.getCount() < 1) //USUARIO NO EXISTE RETORNA NULL
            {
                cursorMaxExamPresented.close();
            } else {
                cursorMaxExamPresented.moveToFirst();
                examenPresentado = cursorMaxExamPresented.getInt(cursorMaxExamPresented.getColumnIndex("EXAMEN_PRESENTADO"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return examenPresentado+1;
    }


    public boolean validarRegistroExistenteInsert(String usuario, int idExam, int idPregunta) {

        Cursor cursor = db.rawQuery("select * FROM REPORTE WHERE USERLOGIN_ENCUESTADOR = '" + usuario + "' AND ID_EXAM = " + idExam + " AND ID_PREGUNTA = " + idPregunta + ";", null);
        boolean registroExiste = true;

        if (cursor.getCount() > 0) //EXISTEN REGISTROS PARA EL USUARIO
        {
            cursor.close();
        } else {
            registroExiste = false;
            cursor.close();
        }

        return registroExiste;
    }


    //####################### VOY POR AQUI PARA EDITAR #########################//
    public HashMap<String, ReporteAuxBean> getTotalReporteLinealPorEncuesta(ExamenUsuarioNube examenUsuarioNube) {

        HashMap<String, ReporteAuxBean> mapaDeReporteLinealPorEncuesta = new LinkedHashMap<>();
        ReporteAuxBean reporteAuxBean;
        ReportePreguntaLinealAuxBean reportePreguntaLinealAuxBean;
        Cursor cursor = null;
        int totalPreguntasDinamicas = examenUsuarioNube.getExam_conf().get(0).getExamUsuarioNubeCurrentConfList().getQuestions().size();
        StringBuilder cadenaQueryBuild = new StringBuilder();
        int contadorAuxMapaPreguntas = 0;
        int maximaPreguntaRegistradaEnBd = 0;

        try {

            maximaPreguntaRegistradaEnBd = validarMaxIdPreguntaReporte();

            if (totalPreguntasDinamicas < maximaPreguntaRegistradaEnBd) {
                totalPreguntasDinamicas = maximaPreguntaRegistradaEnBd;
            }

            cadenaQueryBuild.append("SELECT R.ID, R.USERLOGIN_ENCUESTADOR, R.DNI_ENCUESTADO, R.ID_EXAM, EN.DIA_ENCUESTADO, " +
                    "EN.HORA_ENCUESTADO, EN.LOCACION_ENCUESTADO, " +
                    "EN.GENERO_ENCUESTADO, EN.EDAD_ENCUESTADO, EN.COMUNA_ENCUESTADO, " +
                    "EN.OCUPACION_ENCUESTADO, R.NOMBRES_ENCUESTADO, R.APELLIDOS_ENCUESTADO, ");

            for (int x = 1; x < totalPreguntasDinamicas + 1; x++) {
                cadenaQueryBuild.append(" SUM(CASE R.ID_PREGUNTA WHEN " + x + " THEN R.ID_PREGUNTA ELSE NULL END) AS P" + x + ", ");
                cadenaQueryBuild.append(" SUM(CASE R.ID_PREGUNTA WHEN " + x + " THEN R.ID_RESPUESTA ELSE NULL END) AS R" + x + ", ");
                if (x + 1 < totalPreguntasDinamicas + 1) {
                    cadenaQueryBuild.append(" SUM(CASE R.ID_PREGUNTA WHEN " + x + " THEN R.TIEMPO_RESPUESTA ELSE NULL END) AS T" + x + ", ");
                } else {
                    cadenaQueryBuild.append(" SUM(CASE R.ID_PREGUNTA WHEN " + x + " THEN R.TIEMPO_RESPUESTA ELSE NULL END) AS T" + x);
                }
            }

            cadenaQueryBuild.append(" FROM REPORTE R INNER JOIN ENCUESTADO EN " +
                    "ON (R.USERID_ENCUESTADOR = EN.USERID_ENCUESTADOR AND R.DNI_ENCUESTADO = EN.DNI_ENCUESTADO) " +
                    "GROUP BY R.ID_EXAM, R.USERLOGIN_ENCUESTADOR ORDER BY R.ID;");

            System.out.println("##### QUERY FINAL ###### -> " + cadenaQueryBuild.toString());

            cursor = db.rawQuery(cadenaQueryBuild.toString(), null);

            if (cursor.getCount() < 1) //USUARIO NO EXISTE RETORNA NULL
            {
                cursor.close();
                return null;

            } else {

                cursor.moveToFirst();

                for (int x = 0; x < cursor.getCount(); x++) {

                    int idRow = cursor.getInt(cursor.getColumnIndex("ID"));
                    String usuarioRep = cursor.getString(cursor.getColumnIndex("USERLOGIN_ENCUESTADOR"));
                    int idExam = cursor.getInt(cursor.getColumnIndex("ID_EXAM"));

                    reporteAuxBean = new ReporteAuxBean();

                    reporteAuxBean.setIdRow(idRow);
                    reporteAuxBean.setEncuestador(usuarioRep);
                    reporteAuxBean.setIdExamen(idExam);
                    //DATOS PERFIL ENCUESTADO
                    reporteAuxBean.setDia(cursor.getString(cursor.getColumnIndex("DIA_ENCUESTADO")));
                    reporteAuxBean.setHora(cursor.getString(cursor.getColumnIndex("HORA_ENCUESTADO")));
                    reporteAuxBean.setLocacion(cursor.getString(cursor.getColumnIndex("LOCACION_ENCUESTADO")));
                    reporteAuxBean.setGenero(cursor.getInt(cursor.getColumnIndex("GENERO_ENCUESTADO")));
                    reporteAuxBean.setEdad(cursor.getInt(cursor.getColumnIndex("EDAD_ENCUESTADO")));
                    reporteAuxBean.setComuna(cursor.getInt(cursor.getColumnIndex("COMUNA_ENCUESTADO")));
                    reporteAuxBean.setOcupacion(cursor.getInt(cursor.getColumnIndex("OCUPACION_ENCUESTADO")));

                    reporteAuxBean.setNombresEncuestado(cursor.getString(cursor.getColumnIndex("NOMBRES_ENCUESTADO")));
                    reporteAuxBean.setApellidosEncuestado(cursor.getString(cursor.getColumnIndex("APELLIDOS_ENCUESTADO")));

                    //SECCION PREGUNTAS LINEAL DINAMICA HDP
                    for (int y = 1; y < totalPreguntasDinamicas + 1; y++) {
                        contadorAuxMapaPreguntas++;
                        reportePreguntaLinealAuxBean = new ReportePreguntaLinealAuxBean();

                        reportePreguntaLinealAuxBean.setIdPreguntaLinea(cursor.getInt(cursor.getColumnIndex("P" + y)));
                        reportePreguntaLinealAuxBean.setIdRespuestaLinea(cursor.getInt(cursor.getColumnIndex("R" + y)));
                        reportePreguntaLinealAuxBean.setIdTiempoRespuestaLinea(cursor.getInt(cursor.getColumnIndex("T" + y)));

                        reporteAuxBean.getPreguntaIdMapaPorLinea().put(contadorAuxMapaPreguntas + "-" + idExam + "-" + usuarioRep, reportePreguntaLinealAuxBean);
                    }
                    //SE AGREGAN LOS DATOS EN EL MAPA
                    mapaDeReporteLinealPorEncuesta.put(idExam + "-" + usuarioRep, reporteAuxBean);

                    cursor.moveToNext();
                }//FIN FOR DEL CURSOR PRINCIPAL
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Lo sentimos, ocurrio un error", Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mapaDeReporteLinealPorEncuesta;
    }

    //METODOS PARA EL MANEJO DE DATOS HACIA EL SERVIDOR
    @Override
    public ReporteExamenesNubeSubmitted getCurrentFinishedExamData(PerfilUsuarioNube perfilCreadorExamen,
                                                                   ExamenUsuarioNube perfilExamen,
                                                                   PerfilUsuarioEncuestado perfilEncuestado,
                                                                   int idExamenActual) {

        //LLENAR ESTE BEAN
        //ReporteExamenActualTerminado reporteExamenActualterminado = null;
        ReporteExamenesNubeSubmitted reporteExamenActualterminado = null;
        Cursor cursor = null;
        float notaFinalExamenActual;
        //ArrayList<ReporteExamenActualPregResp> listaPreguntasRespuestasExamen = null;
        ArrayList<ReporteExamenesNubeDatos> listaPreguntasRespuestasExamen;//LOS BEAN PREMIUM PARA AMBOS PERFILES

        //DATOS DEL CREADOR DEL EXAMEN Y DEL PRESENTADOR DEL EXAMEN
        cursor = db.rawQuery("SELECT DISTINCT R.DNI_ENCUESTADO, R.NOMBRES_ENCUESTADO, R.APELLIDOS_ENCUESTADO, R.ID_EXAM, " +
                "R.ID_EXAMEN_PRESENTADO, EN.DNI_ENCUESTADOR, EN.USERLOGIN_ENCUESTADOR, EN.DIA_ENCUESTADO, EN.HORA_ENCUESTADO, " +
                "EN.LOCACION_ENCUESTADO, EN.GENERO_ENCUESTADO, EN.EDAD_ENCUESTADO, EN.COMUNA_ENCUESTADO, " +
                "EN.OCUPACION_ENCUESTADO, R.USERID_ENCUESTADOR FROM REPORTE R " +
                "INNER JOIN ENCUESTADO EN " +
                "ON (R.USERID_ENCUESTADOR = EN.USERID_ENCUESTADOR AND R.DNI_ENCUESTADO = EN.DNI_ENCUESTADO) " +
                "WHERE R.ID_EXAMEN_PRESENTADO = " +MemoryBean.getIdExamenPresentado()+ " AND R.ID_EXAM = " + idExamenActual +//REEMPLAZAR SEGUNDO POR DATOS QUE VIENEN DEL ARREGLO DE REPORTES ULTIMO SUBMITTED REPORT + 1
                " AND R.DNI_ENCUESTADO = '" + perfilEncuestado.getDniEncuestado() + "' AND R.USERID_ENCUESTADOR = "
                + perfilCreadorExamen.getUser_id() + " AND EN.EDAD_ENCUESTADO = "+ perfilEncuestado.getEdadEncuestado() +" AND EN.OCUPACION_ENCUESTADO = '"+perfilEncuestado.getOcupacionEncuestado()+"';", null);

        if (cursor.getCount() < 1) //USUARIO NO EXISTE RETORNA NULL
        {
            cursor.close();
            Log.d(TAG, "Sin datos al capturar datos de usuarios de la prueba presentada antes de enviarse al servidor.");
            return null;
        } else {

            try {
                cursor.moveToFirst();
                //DATOS DEL USUARIO QUE PRESENTO
                for (int x = 0; x < cursor.getCount(); x++) {
                    reporteExamenActualterminado = new ReporteExamenesNubeSubmitted();
                    reporteExamenActualterminado.setId(0);//SE MANEJA ARRIBA EN EL REST
                    reporteExamenActualterminado.setDni(cursor.getString(cursor.getColumnIndex("DNI_ENCUESTADO")));
                    reporteExamenActualterminado.setNombres(cursor.getString(cursor.getColumnIndex("NOMBRES_ENCUESTADO")));
                    reporteExamenActualterminado.setApellidos(cursor.getString(cursor.getColumnIndex("APELLIDOS_ENCUESTADO")));
                    reporteExamenActualterminado.setId(cursor.getInt(cursor.getColumnIndex("ID_EXAMEN_PRESENTADO")));
                    reporteExamenActualterminado.setDia(cursor.getString(cursor.getColumnIndex("DIA_ENCUESTADO")));
                    reporteExamenActualterminado.setHoraInicio(cursor.getString(cursor.getColumnIndex("HORA_ENCUESTADO")));
                    reporteExamenActualterminado.setSexo(cursor.getString(cursor.getColumnIndex("GENERO_ENCUESTADO")));
                    reporteExamenActualterminado.setDireccion(cursor.getString(cursor.getColumnIndex("LOCACION_ENCUESTADO")));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Error capturando datos de usuarios de la prueba para enviarse al servidor: ", e);
            } finally {
                if(cursor != null)
                cursor.close();
            }
        }

        //TIEMPO TOTAL DE RESPUESTAS
        int tiempoTotaldeRespuestas = 0;

        //DATOS DE PREGUNTAS Y RESPUESTAS
        //
        //esta mala esta
        cursor = db.rawQuery("SELECT R.ID_PREGUNTA, R.PREGUNTA, R.ID_RESPUESTA, R.RESPUESTA, " +
                "R.TIEMPO_RESPUESTA FROM REPORTE R "+
                        //INNER JOIN ENCUESTADO EN " +
                //"ON (R.USERID_ENCUESTADOR = EN.USERID_ENCUESTADOR AND R.DNI_ENCUESTADO = EN.DNI_ENCUESTADO) " +
                "WHERE R.ID_EXAMEN_PRESENTADO = " + MemoryBean.getIdExamenPresentado() + " AND R.ID_EXAM = " + idExamenActual + " AND R.DNI_ENCUESTADO = '" +//REEMPLAZAR SEGUNDO POR DATOS QUE VIENEN DEL ARREGLO DE REPORTES ULTIMO SUBMITTED REPORT + 1
                perfilEncuestado.getDniEncuestado() + "' AND R.USERID_ENCUESTADOR = " + perfilCreadorExamen.getUser_id() + ";", null); //" AND EN.EDAD_ENCUESTADO = '"+ perfilEncuestado.getEdadEncuestado() + "';", null); //" AND EN.OCUPACION_ENCUESTADO = '"+perfilEncuestado.getOcupacionEncuestado()+"';", null);

        if (cursor.getCount() < 1) //USUARIO NO EXISTE RETORNA NULL
        {
            cursor.close();
            Log.d(TAG, "Sin datos encontrados al capturar datos de preguntas y respuestas de la prueba presentada antes de enviarse al servidor.");
            return null;
        } else {

            try {
                cursor.moveToFirst();
                //ReporteExamenActualPregResp reporteExamenActPreguntasResp;
                ReporteExamenesNubeDatos reporteExamenActPreguntasResp;
                listaPreguntasRespuestasExamen = new ArrayList<>();

                if(cursor.getCount() > 0){
                    cursor.moveToPosition(-1);

                    while(cursor.moveToNext()){
                        reporteExamenActPreguntasResp = new ReporteExamenesNubeDatos();
                        reporteExamenActPreguntasResp.setIdPregunta(cursor.getInt(cursor.getColumnIndex("ID_PREGUNTA")));
                        reporteExamenActPreguntasResp.setRespuestaSeleccionada(cursor.getInt(cursor.getColumnIndex("ID_RESPUESTA")));
                        //RESPUESTA SELECCIONADA NO ENCAJA EN ESTE MODELO
                        //reporteExamenActPreguntasResp.setRespuestaSeleccionada();
                        tiempoTotaldeRespuestas += cursor.getInt(cursor.getColumnIndex("TIEMPO_RESPUESTA"));
                        reporteExamenActPreguntasResp.setTiempoRespuesta(cursor.getInt(cursor.getColumnIndex("TIEMPO_RESPUESTA")));
                        //RESPUESTA SELECCIONADA NO ENCAJA EN ESTE MODELO

                        listaPreguntasRespuestasExamen.add(reporteExamenActPreguntasResp);
                    }
                }

                //SETEAMOS LA LISTA DEL BEAN PRINCIPAL CON LA LISTA AUXILIAR DEL BEAN SECUNDARIO
                reporteExamenActualterminado.setDatosPrueba(listaPreguntasRespuestasExamen);

                //AHORA BUSCAMOS LA NOTA FINAL LLAMANDO AL METODO GETCURRENTSCORE PARA SETEAR LA NOTA EN EL BEAN PRINCIPAL
                notaFinalExamenActual = getCurrentExamScore(perfilExamen, reporteExamenActualterminado, idExamenActual);
                //NOTA FINAL PARA MOSTRAR EN VISTA Y VIAJAR AL SERVIDOR
                reporteExamenActualterminado.setNotaFinal(notaFinalExamenActual);

                reporteExamenActualterminado.setTotalTiempoPrueba(tiempoTotaldeRespuestas);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Error al capturar datos de preguntas y respuestas de la prueba presentada antes de enviarse al servidor: ", e);
            }finally {
                if(cursor != null)
                cursor.close();
            }
        }

        return reporteExamenActualterminado;
    }

    /*
    METODO QUE CALCULA EL RESULTADO DE LA PRUEBA Y LO DEVUELVE COMO NOTA FINAL
     */
    private float getCurrentExamScore(ExamenUsuarioNube perfilExamen,
                                      ReporteExamenesNubeSubmitted reporteExamenActualTerminado,
                                     int idExamenActual) {

        float scorePruebaPresentada;
        int totalRespuestasCorrectas = 0;
        int totalPreguntasExamen = perfilExamen.getExam_conf().get(idExamenActual).getExamUsuarioNubeCurrentConfList().getQuestions().size();

        //RECORREMOS LAS PREGUNTAS Y RESPUESTAS REGISTRADAS EN LA NUBE PARA EL EXAMEN
        for(ExamenUsuarioCurrentRespuestas respuestasNube : perfilExamen.getExam_conf().get(idExamenActual).getExamUsuarioNubeCurrentConfList().getAnswers()){

            //POR CADA RESPUESTA REGISTRADA EN EL SERVIDOR BUSCAMOS MATCH DEL ID DE PREGUNTA Y RESPUESTA CORRECTA DE LOS DATOS PRESENTADOS
           for(ReporteExamenesNubeDatos preguntasRespuestasPresentadas :reporteExamenActualTerminado.getDatosPrueba()) {

               //MATCH DE PREGUNTA
               if(respuestasNube.getQuestion_id() == preguntasRespuestasPresentadas.getIdPregunta()){

                   if(respuestasNube.getCorrect_answer() == preguntasRespuestasPresentadas.getRespuestaSeleccionada()){
                       totalRespuestasCorrectas = ++totalRespuestasCorrectas;
                   }
               }
           }
        }
        //CONVERTIMOS LA NOTA FINAL EN BASE AL SCORE DIVIDO ENTRE EL TOTAL DE PREGUNTAS, MULTIPLICADO POR 100
        scorePruebaPresentada = (totalRespuestasCorrectas / (float) totalPreguntasExamen) * 100;

        return scorePruebaPresentada;
    }

}
