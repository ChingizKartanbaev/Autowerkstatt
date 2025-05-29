package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.AppointmentPartDao;
import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.AppointmentPart;
import pti.datenbank.autowerk.models.Part;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentPartDaoImpl implements AppointmentPartDao {
    private static final String INSERT_SQL =
            "INSERT INTO AppointmentParts (AppointmentID, PartID, Quantity) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE AppointmentParts SET Quantity = ? WHERE AppointmentID = ? AND PartID = ?";
    private static final String DELETE_SQL =
            "DELETE FROM AppointmentParts WHERE AppointmentID = ? AND PartID = ?";
    private static final String SELECT_BY_APPT =
            "SELECT AppointmentID, PartID, Quantity FROM AppointmentParts WHERE AppointmentID = ?";
    private static final String SELECT_BY_PART =
            "SELECT AppointmentID, PartID, Quantity FROM AppointmentParts WHERE PartID = ?";

    @Override
    public void insert(AppointmentPart entity) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, entity.getAppointment().getAppointmentId());
            ps.setInt(2, entity.getPart().getPartId());
            ps.setInt(3, entity.getQuantity());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateQuantity(int appointmentId, int partId, int quantity) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(UPDATE_SQL)) {
            ps.setInt(1, quantity);
            ps.setInt(2, appointmentId);
            ps.setInt(3, partId);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int appointmentId, int partId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, appointmentId);
            ps.setInt(2, partId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<AppointmentPart> findByAppointmentId(int appointmentId) throws SQLException {
        List<AppointmentPart> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_APPT)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AppointmentPart ap = mapRow(rs);
                    list.add(ap);
                }
            }
        }
        return list;
    }

    @Override
    public List<AppointmentPart> findByPartId(int partId) throws SQLException {
        List<AppointmentPart> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_PART)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AppointmentPart ap = mapRow(rs);
                    list.add(ap);
                }
            }
        }
        return list;
    }

    private AppointmentPart mapRow(ResultSet rs) throws SQLException {
        int apptId = rs.getInt("AppointmentID");
        int partId = rs.getInt("PartID");
        int qty = rs.getInt("Quantity");

        Appointment appt = new AppointmentDaoImpl().findById(apptId);
        Part p = new PartDaoImpl().findById(partId);

        AppointmentPart ap = new AppointmentPart();
        ap.setAppointment(appt);
        ap.setPart(p);
        ap.setQuantity(qty);
        return ap;
    }
}