package com.javadoh.toptestquiz.utils;

import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNube;
import com.javadoh.toptestquiz.activities.consulta.bean.premium.ReporteExamenesNubeSubmitted;
import com.javadoh.toptestquiz.utils.bean.OperacionesBdBean;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioEncuestado;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;

import java.util.HashMap;

/**
 * Created by LUIS-EXTERNO on 08-07-2015.
 */
public interface OperacionesBdInterface {

    void sendDataReport(PerfilUsuarioEncuestado usuarioEncuestado, PerfilUsuarioNube encuestador, int idExamen, int correlativoExamenPresentado, int idPregunta, String pregunta, int idRespuesta, String respuesta, int tiempoRespuesta);
    void sendDataEncuestado(PerfilUsuarioEncuestado usuarioEncuestado, PerfilUsuarioNube encuestador);
        HashMap<String,OperacionesBdBean> getTotalReportesPorUsuario(PerfilUsuarioNube encuestador);
    ReporteExamenesNubeSubmitted getCurrentFinishedExamData(PerfilUsuarioNube perfilCreadorExamen,
                                                            ExamenUsuarioNube perfilExamen,
                                                            PerfilUsuarioEncuestado perfilEncuestado,
                                                            int idExamenActual);
}
