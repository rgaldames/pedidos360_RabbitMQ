package com.pedidos._0.service;

import com.pedidos._0.dto.TareaMessage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class HistorialTareasService {

    private final Deque<Map<String, Object>> tareasProcesadas = new ConcurrentLinkedDeque<>();
    private final Deque<Map<String, Object>> tareasDlq = new ConcurrentLinkedDeque<>();

    public void registrarProcesada(String tipo, TareaMessage mensaje, String detalle) {
        Map<String, Object> registro = new HashMap<>();
        registro.put("id", mensaje.getId());
        registro.put("tipo", tipo);
        registro.put("destinatario", mensaje.getDestinatario());
        registro.put("contenido", mensaje.getContenido());
        registro.put("fecha", new Date().toString());
        registro.put("estado", "PROCESADO_EXITOSAMENTE");
        registro.put("detalle", detalle);
        
        tareasProcesadas.addFirst(registro);
        if (tareasProcesadas.size() > 50) {
            tareasProcesadas.removeLast();
        }
    }

    public void registrarDlq(String colaDlq, TareaMessage mensaje, String motivo) {
        Map<String, Object> registro = new HashMap<>();
        registro.put("id", mensaje.getId());
        registro.put("colaDlq", colaDlq);
        registro.put("tipo", mensaje.getTipo());
        registro.put("destinatario", mensaje.getDestinatario());
        registro.put("contenido", mensaje.getContenido());
        registro.put("fecha", new Date().toString());
        registro.put("estado", "ENVIADO_A_DEAD_LETTER_QUEUE");
        registro.put("motivo", motivo);

        tareasDlq.addFirst(registro);
        if (tareasDlq.size() > 50) {
            tareasDlq.removeLast();
        }
    }

    public List<Map<String, Object>> getTareasProcesadas() {
        return new ArrayList<>(tareasProcesadas);
    }

    public List<Map<String, Object>> getTareasDlq() {
        return new ArrayList<>(tareasDlq);
    }

    public void limpiar() {
        tareasProcesadas.clear();
        tareasDlq.clear();
    }
}

