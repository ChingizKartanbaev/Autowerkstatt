package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.CustomerDao;
import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {
    private static final String SELECT_BY_ID =
            "SELECT CustomerID, UserID, FullName, Phone, Address FROM Customers WHERE CustomerID = ?";
    private static final String SELECT_ALL =
            "SELECT CustomerID, UserID, FullName, Phone, Address FROM Customers";
    private static final String INSERT_SQL =
            "INSERT INTO Customers (UserID, FullName, Phone, Address) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Customers SET FullName = ?, Phone = ?, Address = ? WHERE CustomerID = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Customers WHERE CustomerID = ?";
    private static final String SELECT_BY_USERID =
            "SELECT CustomerID, UserID, FullName, Phone, Address FROM Customers WHERE UserID = ?";

    @Override
    public Customer findById(Integer id) throws SQLException {
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
    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
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
    public void insert(Customer customer) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customer.getUser().getUserId());
            ps.setString(2, customer.getFullName());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    customer.setCustomerId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Customer customer) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getAddress());
            ps.setInt(4, customer.getCustomerId());
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
    public Customer findByUserId(int userId) throws SQLException {
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

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("CustomerID"));
        // Assuming UserDaoImpl can fetch User by ID
        User user = new UserDaoImpl().findById(rs.getInt("UserID"));
        c.setUser(user);
        c.setFullName(rs.getString("FullName"));
        c.setPhone(rs.getString("Phone"));
        c.setAddress(rs.getString("Address"));
        return c;
    }
}
