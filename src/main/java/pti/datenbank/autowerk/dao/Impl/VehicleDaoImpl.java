package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.dao.VehicleDao;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDaoImpl implements VehicleDao {
    private static final String SELECT_BY_ID = "SELECT VehicleID, CustomerID, LicensePlate, Make, Model, Year FROM Vehicles WHERE VehicleID = ?";
    private static final String SELECT_ALL = "SELECT VehicleID, CustomerID, LicensePlate, Make, Model, Year FROM Vehicles";
    private static final String INSERT_SQL = "INSERT INTO Vehicles (CustomerID, LicensePlate, Make, Model, Year) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Vehicles SET LicensePlate = ?, Make = ?, Model = ?, Year = ? WHERE VehicleID = ?";
    private static final String DELETE_SQL = "DELETE FROM Vehicles WHERE VehicleID = ?";
    private static final String SELECT_BY_CUST = "SELECT VehicleID, CustomerID, LicensePlate, Make, Model, Year FROM Vehicles WHERE CustomerID = ?";

    @Override
    public Vehicle findById(Integer id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }
    @Override
    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(SELECT_ALL)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
    @Override
    public void insert(Vehicle v) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getCustomer().getCustomerId());
            ps.setString(2, v.getLicensePlate());
            ps.setString(3, v.getMake());
            ps.setString(4, v.getModel());
            if (v.getYear() != null) ps.setInt(5, v.getYear()); else ps.setNull(5, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) v.setVehicleId(keys.getInt(1)); }
        }
    }
    @Override
    public void update(Vehicle v) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, v.getLicensePlate());
            ps.setString(2, v.getMake());
            ps.setString(3, v.getModel());
            if (v.getYear()!=null) ps.setInt(4, v.getYear()); else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, v.getVehicleId());
            ps.executeUpdate();
        }
    }
    @Override
    public void delete(Integer id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
    @Override
    public List<Vehicle> findByCustomerId(int customerId) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_CUST)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("VehicleID"));
        Customer cust = new CustomerDaoImpl().findById(rs.getInt("CustomerID"));
        v.setCustomer(cust);
        v.setLicensePlate(rs.getString("LicensePlate"));
        v.setMake(rs.getString("Make"));
        v.setModel(rs.getString("Model"));
        int yr = rs.getInt("Year"); if (!rs.wasNull()) v.setYear(yr);
        return v;
    }
}