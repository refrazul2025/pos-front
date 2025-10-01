package org.palina.venta_ui.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoDto {

    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private String paymentType;

    public BigDecimal getAmountPaid(BigDecimal bigDecimal) {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
