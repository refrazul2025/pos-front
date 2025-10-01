package org.palina.venta_ui.dto;

public class ProductoDto {
    private String code;
    private String description;
    private String category1;
    private String category2;
    private String barcode;
    private double purchasePrice;
    private double salePrice;
    private int currentStock;
    private int unitsAdd;
    private int unitsDelete;
    private long outletId;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getUnitsAdd() {
        return unitsAdd;
    }

    public void setUnitsAdd(int unitsAdd) {
        this.unitsAdd = unitsAdd;
    }

    public int getUnitsDelete() {
        return unitsDelete;
    }

    public void setUnitsDelete(int unitsDelete) {
        this.unitsDelete = unitsDelete;
    }

    public long getOutletId() {
        return outletId;
    }

    public void setOutletId(long outletId) {
        this.outletId = outletId;
    }
}
