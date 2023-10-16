package com.javadoh.toptestquiz.utils.bean;

import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;

import java.io.Serializable;

/**
 * Created by luiseliberal on 26/03/16.
 */
public class DynamicTestResponse implements Serializable{

    private PerfilUsuarioNube perfilUsuarioNube;
    private ExamenUsuarioNube examenesUsuarioNube;

    public PerfilUsuarioNube getPerfilUsuarioNube() {
        return perfilUsuarioNube;
    }

    public void setPerfilUsuarioNube(PerfilUsuarioNube perfilUsuarioNube) {
        this.perfilUsuarioNube = perfilUsuarioNube;
    }

    public ExamenUsuarioNube getExamenesUsuarioNube() {
        return examenesUsuarioNube;
    }

    public void setExamenesUsuarioNube(ExamenUsuarioNube examenesUsuarioNube) {
        this.examenesUsuarioNube = examenesUsuarioNube;
    }
}
