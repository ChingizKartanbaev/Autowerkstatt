package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.dao.RoleDao;
import pti.datenbank.autowerk.models.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao {
    private static final String SELECT_BY_ID = "SELECT RoleID, RoleName FROM Roles WHERE RoleID = ?";
    private static final String SELECT_ALL   = "SELECT RoleID, RoleName FROM Roles";
    private static final String INSERT_SQL   = "INSERT INTO Roles (RoleName) VALUES(?)";
    private static final String UPDATE_SQL   = "UPDATE Roles SET RoleName = ? WHERE RoleID = ?";
    private static final String DELETE_SQL   = "DELETE FROM Roles WHERE RoleID = ?";
    private static final String SELECT_BY_NAME = "SELECT RoleID, RoleName FROM Roles WHERE RoleName = ?";

    @Override
    public Role findById(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Role(rs.getInt("RoleID"), rs.getString("RoleName"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Role> findAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                roles.add(new Role(rs.getInt("RoleID"), rs.getString("RoleName")));
            }
        }
        return roles;
    }

    @Override
    public void insert(Role role) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.getRoleName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    role.setRoleId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Role role) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, role.getRoleName());
            ps.setInt(2, role.getRoleId());
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
    public Role findByName(String roleName) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_NAME)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Role(rs.getInt("RoleID"), rs.getString("RoleName"));
                }
            }
        }
        return null;
    }
}