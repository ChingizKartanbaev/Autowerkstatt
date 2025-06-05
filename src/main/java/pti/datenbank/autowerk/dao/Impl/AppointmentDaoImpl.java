package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.AppointmentDao;
import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDaoImpl implements AppointmentDao {
    private static final String SELECT_BASE =
            "SELECT AppointmentID, CustomerID, MechanicID, VehicleID, ScheduledAt, Status\n" +
                    "  FROM Appointments";

    private static final String SELECT_BY_ID   = SELECT_BASE + " WHERE AppointmentID = ?";
    private static final String INSERT_SQL     =
            "INSERT INTO Appointments (CustomerID, MechanicID, VehicleID, ScheduledAt, Status)\n" +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL     =
            "UPDATE Appointments SET ScheduledAt = ?, Status = ? WHERE AppointmentID = ?";
    private static final String DELETE_SQL     = "DELETE FROM Appointments WHERE AppointmentID = ?";
    private static final String SELECT_BY_CUST = SELECT_BASE + " WHERE CustomerID = ?";
    private static final String SELECT_BY_MECH = SELECT_BASE + " WHERE MechanicID = ?";

    @Override
    public Appointment findById(Integer appointmentId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, appointmentId);
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
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_BASE)) {
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

            // Получаем сгенерированный ключ
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
    public void delete(Integer appointmentId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, appointmentId);
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

    @Override
    public int countActiveByVehicle(int vehicleId) throws SQLException {
        String SQL =
                "SELECT COUNT(*) AS cnt " +
                        "FROM Appointments " +
                        "WHERE VehicleID = ? AND Status IN ('PENDING','CONFIRMED')";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        }
        return 0;
    }

    @Override
    public boolean isMechanicAvailable(int mechanicId, LocalDateTime time) throws SQLException {
        LocalTime t = time.toLocalTime();
        if (t.isBefore(LocalTime.of(9, 0)) || t.isAfter(LocalTime.of(16, 0))) {
            return false;
        }

        String sql = """
        SELECT COUNT(*) 
        FROM Appointments 
        WHERE MechanicID = ? 
          AND ABS(DATEDIFF(minute, ScheduledAt, ?)) < 60
          AND Status NOT IN ('COMPLETED', 'CANCELLED')
    """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mechanicId);
            ps.setTimestamp(2, Timestamp.valueOf(time));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("AppointmentID"));

        Customer cust = new CustomerDaoImpl().findById(rs.getInt("CustomerID"));
        a.setCustomer(cust);

        Mechanic mech = new MechanicDaoImpl().findById(rs.getInt("MechanicID"));
        a.setMechanic(mech);

        Vehicle veh = new VehicleDaoImpl().findById(rs.getInt("VehicleID"));
        a.setVehicle(veh);

        a.setScheduledAt(rs.getTimestamp("ScheduledAt").toLocalDateTime());
        a.setStatus(rs.getString("Status"));

        return a;
    }
}
