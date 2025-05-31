package pti.datenbank.autowerk.services.facade;

import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.services.*;

import java.sql.Connection;
import java.sql.SQLException;

public class UserFacade extends BaseService {
    private final UserService userService;
    private final CustomerService customerService;
    private final MechanicService mechanicService;

    public UserFacade(AuthService authService) {
        super(authService);
        this.userService      = new UserService(authService);
        this.customerService  = new CustomerService(authService);
        this.mechanicService  = new MechanicService(authService);
    }

    public void createCustomerUser(User user, Customer profile) throws SQLException {
        checkPermission(Permission.CREATE);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                userService.create(user);
                profile.setUser(user);
                customerService.create(profile);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updateCustomerUser(User user, Customer profile) throws SQLException {
        checkPermission(Permission.UPDATE);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                userService.update(user);
                customerService.update(profile);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void deleteCustomerUser(User user, Customer customer) throws SQLException {
        checkPermission(Permission.DELETE);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                customerService.delete(customer.getCustomerId());
                userService.delete(user.getUserId());
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void createMechanicUser(User user, Mechanic profile) throws SQLException {
        checkPermission(Permission.CREATE);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                userService.create(user);
                profile.setUser(user);
                mechanicService.create(profile);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}