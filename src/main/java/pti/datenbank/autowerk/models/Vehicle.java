package pti.datenbank.autowerk.models;

import java.util.HashSet;
import java.util.Set;

public class Vehicle {
    private int vehicleId;
    private Customer customer;
    private String licensePlate;
    private String make;
    private String model;
    private Integer year;

    public Vehicle() {}

    public Vehicle(int vehicleId, Customer customer, String licensePlate, String make, String model, Integer year) {
        this.vehicleId = vehicleId;
        this.customer = customer;
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}