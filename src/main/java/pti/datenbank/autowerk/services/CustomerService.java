package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.CustomerDao;
import pti.datenbank.autowerk.dao.Impl.CustomerDaoImpl;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerService extends BaseService {
    private final CustomerDao dao = new CustomerDaoImpl();

    public CustomerService(AuthService authService) {
        super(authService);
    }

    public Customer findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    public List<Customer> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return dao.findAll();
    }

    public Customer findByUserId(int userId) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByUserId(userId);
    }

    public void create(Customer customer) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(customer);
    }

    public void update(Customer customer) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(customer);
    }

    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }
}
