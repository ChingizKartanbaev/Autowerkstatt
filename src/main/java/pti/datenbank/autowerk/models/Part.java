package pti.datenbank.autowerk.models;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Part {
    private int partId;
    private User createdBy;
    private String name;
    private String manufacturer;
    private BigDecimal unitPrice;
    private int inStockQty;

    public Part() {}

    public Part(int partId, User createdBy, String name, String manufacturer, BigDecimal unitPrice, int inStockQty) {
        this.partId = partId;
        this.createdBy = createdBy;
        this.name = name;
        this.manufacturer = manufacturer;
        this.unitPrice = unitPrice;
        this.inStockQty = inStockQty;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getInStockQty() {
        return inStockQty;
    }

    public void setInStockQty(int inStockQty) {
        this.inStockQty = inStockQty;
    }
}