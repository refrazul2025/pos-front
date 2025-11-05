package org.palina.venta_ui.dto;
import javafx.beans.property.*;
import javafx.beans.property.*;

public class Producto {

    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty descripcion = new SimpleStringProperty();
    private final StringProperty categoria1 = new SimpleStringProperty();
    private final StringProperty categoria2 = new SimpleStringProperty();
    private final StringProperty codigoBarras = new SimpleStringProperty();
    private final DoubleProperty precioVenta = new SimpleDoubleProperty();
    private final IntegerProperty stockActual = new SimpleIntegerProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty(1);
    private final DoubleProperty precioCompra = new SimpleDoubleProperty();

    public Producto(String codigo, String descripcion, String categoria1, String categoria2,
                    String codigoBarras, double precioVenta, double precioComrpa, int stockActual) {
        this.codigo.set(codigo);
        this.descripcion.set(descripcion);
        this.categoria1.set(categoria1);
        this.categoria2.set(categoria2);
        this.codigoBarras.set(codigoBarras);
        this.precioVenta.set(precioVenta);
        this.precioCompra.set(precioComrpa);
        this.stockActual.set(stockActual);
    }

    // Constructor copia
    public Producto(Producto otro) {
        this.codigo.set(otro.getCodigo());
        this.descripcion.set(otro.getDescripcion());
        this.categoria1.set(otro.getCategoria1());
        this.categoria2.set(otro.getCategoria2());
        this.codigoBarras.set(otro.getCodigoBarras());
        this.precioVenta.set(otro.getPrecioVenta());
        this.stockActual.set(otro.getStockActual());
        this.cantidad.set(1); // cantidad default
    }

    public Producto(ProductoDto productoDto){
        this.codigo.set(productoDto.getCode());
        this.descripcion.set(productoDto.getDescription());
        this.categoria1.set(productoDto.getCategory1());
        this.categoria2.set(productoDto.getCategory2());
        this.codigoBarras.set(productoDto.getBarcode());
        this.precioVenta.set(productoDto.getSalePrice());
        this.precioCompra.set(productoDto.getSalePrice());
        this.stockActual.set(productoDto.getCurrentStock());
    }

    // Getters y setters normales para los valores
    public String getCodigo() { return codigo.get(); }
    public void setCodigo(String value) { codigo.set(value); }
    public StringProperty codigoProperty() { return codigo; }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String value) { descripcion.set(value); }
    public StringProperty descripcionProperty() { return descripcion; }

    public String getCategoria1() { return categoria1.get(); }
    public void setCategoria1(String value) { categoria1.set(value); }
    public StringProperty categoria1Property() { return categoria1; }

    public String getCategoria2() { return categoria2.get(); }
    public void setCategoria2(String value) { categoria2.set(value); }
    public StringProperty categoria2Property() { return categoria2; }

    public String getCodigoBarras() { return codigoBarras.get(); }
    public void setCodigoBarras(String value) { codigoBarras.set(value); }
    public StringProperty codigoBarrasProperty() { return codigoBarras; }

    public double getPrecioVenta() { return precioVenta.get(); }
    public void setPrecioVenta(double value) { precioVenta.set(value); }
    public DoubleProperty precioVentaProperty() { return precioVenta; }

    public int getStockActual() { return stockActual.get(); }
    public void setStockActual(int value) { stockActual.set(value); }
    public IntegerProperty stockActualProperty() { return stockActual; }

    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int value) { cantidad.set(value); }
    public IntegerProperty cantidadProperty() { return cantidad; }

    public double getPrecioCompra() {
        return precioCompra.get();
    }

    public DoubleProperty precioCompraProperty() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra.set(precioCompra);
    }


}
