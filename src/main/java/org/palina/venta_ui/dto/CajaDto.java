package org.palina.venta_ui.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CajaDto {

    private BigDecimal monto;
    private LocalDate fecha;
    private String tipo;

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
