package com.javadoh.toptestquiz.utils.bean;

import android.content.Context;

import com.javadoh.toptestquiz.activities.pruebas.bean.PreguntasBean;
import com.javadoh.toptestquiz.activities.pruebas.bean.RespuestaPreDefBean;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by luiseliberal on 05-07-2015.
 */
public class MemoryBean {

    private static List<PreguntasBean> listaEstaticaPreguntasConfig;
    private static List<RespuestaPreDefBean> listaConfigRespuestasPredefinidaComun;
    private static List<String> listaCamposPerfilPredefinidosComun;
    private static String tiempoTotal;
    private static String totalPreguntas;
    private static PreguntasBean preguntasBean;
    private static RespuestaPreDefBean respuestasPreDefBean;
    private static Context context;
    private static int idExamen;
    private static int idExamenPresentado;
    //REPORTE LINEAL DINAMICO HDP
    private static HashMap<String, Integer> mapaDatosPreguntasLineales;
    private static HashMap<String, Integer> mapaDatosRespuestasLineales;
    private static HashMap<String, Integer> mapaDatosTiemposLineales;

    private static PerfilUsuarioNube perfilUsuarioNube;

    public static List<PreguntasBean> getListaEstaticaPreguntasConfig(){

        Collections.shuffle(listaEstaticaPreguntasConfig);
        return listaEstaticaPreguntasConfig;
    }

    public static void setListaEstaticaPreguntasConfig(InputStream is){

        if(listaEstaticaPreguntasConfig == null) {

            listaEstaticaPreguntasConfig = new ArrayList<>();
            String text = "";

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();

                parser.setInput(is, null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagname = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (tagname.equalsIgnoreCase("PREGUNTA")) {
                                // create a new instance of employee
                                preguntasBean = new PreguntasBean();
                            }
                            break;

                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;

                        case XmlPullParser.END_TAG:

                            if ("PREGUNTA".equalsIgnoreCase(tagname)) {
                                listaEstaticaPreguntasConfig.add(preguntasBean);
                            } else if ("ID".equalsIgnoreCase(tagname)) {
                                preguntasBean.setIdPregunta(Integer.parseInt(text));
                            } else if ("TEXTO".equalsIgnoreCase(tagname)) {
                                preguntasBean.setTextoPregunta(text);
                            } else if ("IMAGEN".equalsIgnoreCase(tagname)) {
                                preguntasBean.setPathImagenPregunta(text);
                            } else if ("RESPUESTA".equalsIgnoreCase(tagname)) {
                                preguntasBean.setRespuesta(text);
                            }
                            break;

                        default:
                            break;
                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public String getTiempoTotal(){
        return tiempoTotal;
    }

    public void setTiempoTotal(String tiempoTotal){
        MemoryBean.tiempoTotal = tiempoTotal;
    }

    public String getTotalPreguntas(){
        return totalPreguntas;
    }

    public void setTotalPreguntas(String totalPreguntas){

        if("".equalsIgnoreCase(totalPreguntas.trim())){

        }
    }

    public static List<RespuestaPreDefBean> getListaRespuestasPredefinidasComunes(){

        //Collections.shuffle(listaConfigRespuestasPredefinidaComun);
        return listaConfigRespuestasPredefinidaComun;
    }

    public static void setListaRespuestasPredefinidasComunes(InputStream is){

        if(listaConfigRespuestasPredefinidaComun == null) {

            listaConfigRespuestasPredefinidaComun = new ArrayList<>();

            String text = "";

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();

                parser.setInput(is, null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagname = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (tagname.equalsIgnoreCase("RESPUESTA_PREDEFINIDA")) {
                                // create a new instance of employee
                                respuestasPreDefBean = new RespuestaPreDefBean();
                            }
                            break;

                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if ("RESPUESTA_PREDEFINIDA".equalsIgnoreCase(tagname)) {
                                listaConfigRespuestasPredefinidaComun.add(respuestasPreDefBean);
                            } else if ("ID".equalsIgnoreCase(tagname)) {
                                respuestasPreDefBean.setId(Integer.parseInt(text));
                            } else if ("TEXTO".equalsIgnoreCase(tagname)) {
                                respuestasPreDefBean.setRespuesta(text);
                            }
                            break;

                        default:
                            break;
                    }
                    eventType = parser.next();
                }


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//FIN IF LISTA IS NULL

    }

    public static List<String> getListaCamposPerfilPredefinidosComun(){
        return MemoryBean.listaCamposPerfilPredefinidosComun;
    }

    public static void setListaCamposPerfilPredefinidosComun(InputStream is){

        if(listaCamposPerfilPredefinidosComun == null) {

            listaCamposPerfilPredefinidosComun = new ArrayList<>();

            String text = "";

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();

                parser.setInput(is, null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagname = parser.getName();
                    switch (eventType) {

                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;

                        case XmlPullParser.END_TAG:

                            if ("NOMBRE".equalsIgnoreCase(tagname)) {
                                if("SI".equalsIgnoreCase(text)){
                                    listaCamposPerfilPredefinidosComun.add(tagname);
                                }
                            } else if ("EDAD".equalsIgnoreCase(tagname)) {
                                if("SI".equalsIgnoreCase(text)){
                                    listaCamposPerfilPredefinidosComun.add(tagname);
                                }
                            } else if ("SEXO".equalsIgnoreCase(tagname)) {
                                if("SI".equalsIgnoreCase(text)){
                                    listaCamposPerfilPredefinidosComun.add(tagname);
                                }
                            } else if ("PASSWORD".equalsIgnoreCase(tagname)) {
                                if("SI".equalsIgnoreCase(text)){
                                    listaCamposPerfilPredefinidosComun.add(tagname);
                                }
                            }else if ("REPASSWORD".equalsIgnoreCase(tagname)) {
                                if("SI".equalsIgnoreCase(text)){
                                    listaCamposPerfilPredefinidosComun.add(tagname);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                    eventType = parser.next();
                }


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }//FIN LISTA IF NULL
    }//FIN SET LISTA DE PERFILES PREDEFINIDOS

    public static Context getContextBase(){
        return context;
    }

    public static void setContext(Context context){
        MemoryBean.context = context;
    }

    public static int getIdExamen() {
        return idExamen;
    }

    public static void setIdExamen(int idExamen) {
        MemoryBean.idExamen = idExamen;
    }

    public static HashMap<String, Integer> getMapaDatosPreguntasLineales() {
        return mapaDatosPreguntasLineales;
    }

    public static void setMapaDatosPreguntasLineales(HashMap<String, Integer> mapaDatosPreguntasLineales) {
        if(mapaDatosPreguntasLineales == null){mapaDatosPreguntasLineales = new LinkedHashMap<>();}
        MemoryBean.mapaDatosPreguntasLineales = mapaDatosPreguntasLineales;
    }

    public static HashMap<String, Integer> getMapaDatosRespuestasLineales() {
        return mapaDatosRespuestasLineales;
    }

    public static void setMapaDatosRespuestasLineales(HashMap<String, Integer> mapaDatosRespuestasLineales) {
        if(mapaDatosRespuestasLineales == null){mapaDatosRespuestasLineales = new LinkedHashMap<>();}
        MemoryBean.mapaDatosRespuestasLineales = mapaDatosRespuestasLineales;
    }

    public static HashMap<String, Integer> getMapaDatosTiemposLineales() {
        return mapaDatosTiemposLineales;
    }

    public static void setMapaDatosTiemposLineales(HashMap<String, Integer> mapaDatosTiemposLineales) {
        if(mapaDatosTiemposLineales == null){mapaDatosTiemposLineales = new LinkedHashMap<>();}
        MemoryBean.mapaDatosTiemposLineales = mapaDatosTiemposLineales;
    }

    public static PerfilUsuarioNube getPerfilUsuarioNube() {
        return perfilUsuarioNube;
    }

    public static void setPerfilUsuarioNube(PerfilUsuarioNube perfilUsuarioNube) {
        MemoryBean.perfilUsuarioNube = perfilUsuarioNube;
    }

    public static int getIdExamenPresentado() {
        return idExamenPresentado;
    }

    public static void setIdExamenPresentado(int idExamenPresentado) {
        MemoryBean.idExamenPresentado = idExamenPresentado;
    }
}
