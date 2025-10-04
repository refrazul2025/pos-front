package org.palina.venta_ui.dto;

import java.math.BigDecimal;
import java.util.List;

public class VentaDto {

    private Long id;
    private String saleDate;
    private String saleType;
    private BigDecimal total;
    private String customer;
    private Long outletId;
    private String paymentType;
    private boolean saleClosed;
    private List<DetalleVentaDto> saleDetails;
    private List<PagoDto> payments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public boolean isSaleClosed() {
        return saleClosed;
    }

    public void setSaleClosed(boolean saleClosed) {
        this.saleClosed = saleClosed;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<DetalleVentaDto> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<DetalleVentaDto> saleDetails) {
        this.saleDetails = saleDetails;
    }

    public List<PagoDto> getPayments() {
        return payments;
    }

    public void setPayments(List<PagoDto> payments) {
        this.payments = payments;
    }
}
