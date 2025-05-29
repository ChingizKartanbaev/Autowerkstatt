package pti.datenbank.autowerk.models;

import java.math.BigDecimal;

public class ServiceType {
    private int serviceTypeId;
    private User createdBy;
    private String name;
    private String description;
    private BigDecimal basePrice;

    public ServiceType() {}

    public ServiceType(int serviceTypeId, User createdBy, String name, String description, BigDecimal basePrice) {
        this.serviceTypeId = serviceTypeId;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
}