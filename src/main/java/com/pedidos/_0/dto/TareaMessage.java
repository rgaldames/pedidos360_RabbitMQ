package com.pedidos._0.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class TareaMessage implements Serializable {

    private String id;
    private String tipo; // NOTIFICACION, TICKET, DOCUMENTO
    private String destinatario;
    private String contenido;
    private Map<String, Object> detalles;
    private LocalDateTime fechaCreacion;
    private boolean simularError;

    public TareaMessage() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public TareaMessage(String id, String tipo, String destinatario, String contenido, Map<String, Object> detalles, boolean simularError) {
        this.id = id;
        this.tipo = tipo;
        this.destinatario = destinatario;
        this.contenido = contenido;
        this.detalles = detalles;
        this.fechaCreacion = LocalDateTime.now();
        this.simularError = simularError;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Map<String, Object> getDetalles() {
        return detalles;
    }

    public void setDetalles(Map<String, Object> detalles) {
        this.detalles = detalles;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isSimularError() {
        return simularError;
    }

    public void setSimularError(boolean simularError) {
        this.simularError = simularError;
    }

    @Override
    public String toString() {
        return "TareaMessage{" +
                "id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", destinatario='" + destinatario + '\'' +
                ", contenido='" + contenido + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", simularError=" + simularError +
                '}';
    }
}

