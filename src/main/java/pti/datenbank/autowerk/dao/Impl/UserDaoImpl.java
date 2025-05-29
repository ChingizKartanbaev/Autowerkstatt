package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.dao.UserDao;
import pti.datenbank.autowerk.models.Role;
import pti.datenbank.autowerk.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private static final String SELECT_BY_ID    = "SELECT u.UserID, u.Username, u.Password, u.Email, u.CreatedAt, r.RoleID, r.RoleName " +
            "FROM Users u JOIN Roles r ON u.RoleID = r.RoleID WHERE u.UserID = ?";
    private static final String SELECT_ALL      = "SELECT u.UserID, u.Username, u.Password, u.Email, u.CreatedAt, r.RoleID, r.RoleName " +
            "FROM Users u JOIN Roles r ON u.RoleID = r.RoleID";
    private static final String INSERT_SQL      = "INSERT INTO Users (RoleID, Username, Password, Email) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL      = "UPDATE Users SET RoleID = ?, Username = ?, Password = ?, Email = ? WHERE UserID = ?";
    private static final String DELETE_SQL      = "DELETE FROM Users WHERE UserID = ?";
    private static final String SELECT_BY_UNAME = "SELECT u.UserID, u.Username, u.Password, u.Email, u.CreatedAt, r.RoleID, r.RoleName " +
            "FROM Users u JOIN Roles r ON u.RoleID = r.RoleID WHERE u.Username = ?";

    @Override
    public User findById(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role(rs.getInt("RoleID"), rs.getString("RoleName"));
                    LocalDateTime createdAt = rs.getTimestamp("CreatedAt").toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
                    return new User(rs.getInt("UserID"), role,
                            rs.getString("Username"), rs.getString("Password"),
                            rs.getString("Email"), createdAt);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                Role role = new Role(rs.getInt("RoleID"), rs.getString("RoleName"));
                LocalDateTime createdAt = rs.getTimestamp("CreatedAt").toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                users.add(new User(rs.getInt("UserID"), role,
                        rs.getString("Username"), rs.getString("Password"),
                        rs.getString("Email"), createdAt));
            }
        }
        return users;
    }

    @Override
    public void insert(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, user.getRole().getRoleId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setInt(1, user.getRole().getRoleId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getEmail());
            ps.setInt(5, user.getUserId());
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
    public User findByUsername(String username) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_UNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role(rs.getInt("RoleID"), rs.getString("RoleName"));
                    LocalDateTime createdAt = rs.getTimestamp("CreatedAt").toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
                    return new User(rs.getInt("UserID"), role,
                            rs.getString("Username"), rs.getString("Password"),
                            rs.getString("Email"), createdAt);
                }
            }
        }
        return null;
    }
}
