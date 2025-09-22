package org.palina.venta_ui.dto;

public class ProductoDto {
    private String codigo;
    private String descripcion;
    private String categoria1;
    private String categoria2;
    private String codigoBarras;
    private double precioCompra;
    private double precioVenta;
    private int stockActual;

    // Getters y setters obligatorios para Jackson
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria1() { return categoria1; }
    public void setCategoria1(String categoria1) { this.categoria1 = categoria1; }

    public String getCategoria2() { return categoria2; }
    public void setCategoria2(String categoria2) { this.categoria2 = categoria2; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
}
