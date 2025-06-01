package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.AppointmentServiceDao;
import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.models.AppointmentService;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.ServiceType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentServiceDaoImpl implements AppointmentServiceDao {
    private static final String INSERT_SQL =
            "INSERT INTO AppointmentServices (AppointmentID, ServiceTypeID) VALUES (?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE AppointmentServices SET Quantity = ? WHERE AppointmentID = ? AND ServiceTypeID = ?";

    private static final String DELETE_SQL =
            "DELETE FROM AppointmentServices WHERE AppointmentID = ? AND ServiceTypeID = ?";

    private static final String DELETE_BY_APPT_SQL =
            "DELETE FROM AppointmentServices WHERE AppointmentID = ?";

    private static final String SELECT_BY_APPT =
            "SELECT AppointmentID, ServiceTypeID FROM AppointmentServices WHERE AppointmentID = ?";

    private static final String SELECT_BY_SERVICE =
            "SELECT AppointmentID, ServiceTypeID FROM AppointmentServices WHERE ServiceTypeID = ?";

    @Override
    public void insert(AppointmentService entity) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, entity.getAppointment().getAppointmentId());
            ps.setInt(2, entity.getServiceType().getServiceTypeId());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(AppointmentService appointmentService) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(UPDATE_SQL)) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int appointmentId, int serviceTypeId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, appointmentId);
            ps.setInt(2, serviceTypeId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteByAppointmentId(int appointmentId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE_BY_APPT_SQL)) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<AppointmentService> findByAppointmentId(int appointmentId) throws SQLException {
        List<AppointmentService> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_APPT)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<AppointmentService> findByServiceTypeId(int serviceTypeId) throws SQLException {
        List<AppointmentService> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_SERVICE)) {
            ps.setInt(1, serviceTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private AppointmentService mapRow(ResultSet rs) throws SQLException {
        int apptId = rs.getInt("AppointmentID");
        int svcId  = rs.getInt("ServiceTypeID");

        Appointment appt = new AppointmentDaoImpl().findById(apptId);
        ServiceType svc = new ServiceTypeDaoImpl().findById(svcId);

        AppointmentService as = new AppointmentService();
        as.setAppointment(appt);
        as.setServiceType(svc);
        return as;
    }
}
