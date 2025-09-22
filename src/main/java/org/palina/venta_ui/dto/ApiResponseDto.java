package org.palina.venta_ui.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponseDto <T> {

    @JsonProperty("mensaje")
    private String mensaje;

    @JsonProperty("folio")
    private String folio;

    @JsonProperty("resultado")
    private T resultado;

    @JsonProperty("advertencias")
    private boolean advertencias;

    // Getters & setters

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public T getResultado() {
        return resultado;
    }

    public void setResultado(T resultado) {
        this.resultado = resultado;
    }

    public boolean isAdvertencias() {
        return advertencias;
    }

    public void setAdvertencias(boolean advertencias) {
        this.advertencias = advertencias;
    }
}
