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

    /**
     * Создаёт пользователя с ролью Customer и профиль Customer.
     */
    public void createCustomerUser(User user, Customer profile) throws SQLException {
        checkPermission(Permission.CREATE);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1) Сначала Users
                userService.create(user);
                // 2) Потом Customer, привязываем по сгенерированному userID
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

    /**
     * Создаёт пользователя с ролью Mechanic и профиль Mechanic.
     */
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