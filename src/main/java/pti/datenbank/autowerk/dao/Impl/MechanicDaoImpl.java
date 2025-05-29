package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.dao.MechanicDao;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MechanicDaoImpl implements MechanicDao {
    private static final String SELECT_BY_ID =
            "SELECT MechanicID, UserID, FullName, Speciality FROM Mechanics WHERE MechanicID = ?";
    private static final String SELECT_ALL =
            "SELECT MechanicID, UserID, FullName, Speciality FROM Mechanics";
    private static final String INSERT_SQL =
            "INSERT INTO Mechanics (UserID, FullName, Speciality) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Mechanics SET FullName = ?, Speciality = ? WHERE MechanicID = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Mechanics WHERE MechanicID = ?";
    private static final String SELECT_BY_USERID =
            "SELECT MechanicID, UserID, FullName, Speciality FROM Mechanics WHERE UserID = ?";

    @Override
    public Mechanic findById(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
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
    public List<Mechanic> findAll() throws SQLException {
        List<Mechanic> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public void insert(Mechanic mechanic) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mechanic.getUser().getUserId());
            ps.setString(2, mechanic.getFullName());
            ps.setString(3, mechanic.getSpeciality());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    mechanic.setMechanicId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Mechanic mechanic) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, mechanic.getFullName());
            ps.setString(2, mechanic.getSpeciality());
            ps.setInt(3, mechanic.getMechanicId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Mechanic findByUserId(int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Mechanic mapRow(ResultSet rs) throws SQLException {
        Mechanic m = new Mechanic();
        m.setMechanicId(rs.getInt("MechanicID"));
        User user = new UserDaoImpl().findById(rs.getInt("UserID"));
        m.setUser(user);
        m.setFullName(rs.getString("FullName"));
        m.setSpeciality(rs.getString("Speciality"));
        return m;
    }
}
