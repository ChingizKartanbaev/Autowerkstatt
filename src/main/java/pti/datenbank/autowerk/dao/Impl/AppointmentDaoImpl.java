package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.AppointmentDao;
import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDaoImpl implements AppointmentDao {
    private static final String SELECT_BASE =
            "SELECT AppointmentID, CustomerID, MechanicID, VehicleID, ScheduledAt, Status FROM Appointments";
    private static final String SELECT_BY_ID = SELECT_BASE + " WHERE AppointmentID = ?";
    private static final String INSERT_SQL =
            "INSERT INTO Appointments (CustomerID, MechanicID, VehicleID, ScheduledAt, Status) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Appointments SET ScheduledAt = ?, Status = ? WHERE AppointmentID = ?";
    private static final String DELETE_SQL = "DELETE FROM Appointments WHERE AppointmentID = ?";
    private static final String SELECT_BY_CUST = SELECT_BASE + " WHERE CustomerID = ?";
    private static final String SELECT_BY_MECH = SELECT_BASE + " WHERE MechanicID = ?";

    @Override
    public Appointment findById(Integer id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Appointment> findAll() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(SELECT_BASE)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public void insert(Appointment a) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getCustomer().getCustomerId());
            ps.setInt(2, a.getMechanic().getMechanicId());
            ps.setInt(3, a.getVehicle().getVehicleId());
            ps.setTimestamp(4, Timestamp.valueOf(a.getScheduledAt()));
            ps.setString(5, a.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    a.setAppointmentId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Appointment a) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(UPDATE_SQL)) {
            ps.setTimestamp(1, Timestamp.valueOf(a.getScheduledAt()));
            ps.setString(2, a.getStatus());
            ps.setInt(3, a.getAppointmentId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Appointment> findByCustomerId(int customerId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_CUST)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Appointment> findByMechanicId(int mechanicId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_MECH)) {
            ps.setInt(1, mechanicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Maps a result set row to an Appointment object.
     */
    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("AppointmentID"));
        // Load related entities via their DAOs
        Customer cust = new CustomerDaoImpl().findById(rs.getInt("CustomerID"));
        Mechanic mech = new MechanicDaoImpl().findById(rs.getInt("MechanicID"));
        Vehicle veh = new VehicleDaoImpl().findById(rs.getInt("VehicleID"));
        a.setCustomer(cust);
        a.setMechanic(mech);
        a.setVehicle(veh);
        a.setScheduledAt(rs.getTimestamp("ScheduledAt").toLocalDateTime());
        a.setStatus(rs.getString("Status"));
        // For simplicity, services and parts lists can be loaded lazily by service layer
        return a;
    }
}