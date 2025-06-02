package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.CustomerDao;
import pti.datenbank.autowerk.dao.Impl.CustomerDaoImpl;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerService extends BaseService {
    private final CustomerDao dao = new CustomerDaoImpl();
    private final VehicleService vehicleService;
    private final AppointmentService appointmentService;

    public CustomerService(AuthService authService) {
        super(authService);
        this.vehicleService = new VehicleService(authService);
        this.appointmentService = new AppointmentService(authService);
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

    public void delete(int customerId) throws SQLException {
        checkPermission(Permission.DELETE);

        if (vehicleService.hasAnyVehicle(customerId)) {
            throw new SQLException("Unable to delete a client: it has tethered machines.");
        }

        dao.delete(customerId);
    }
}