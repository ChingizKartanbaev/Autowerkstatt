package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.Impl.VehicleDaoImpl;
import pti.datenbank.autowerk.dao.VehicleDao;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Vehicle;

import java.sql.SQLException;
import java.util.List;

public class VehicleService extends BaseService {
    private final VehicleDao dao = new VehicleDaoImpl();

    public VehicleService(AuthService authService) {
        super(authService);
    }

    public Vehicle findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    public List<Vehicle> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return dao.findAll();
    }

    public List<Vehicle> findByCustomerId(int customerId) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByCustomerId(customerId);
    }

    public void create(Vehicle vehicle) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(vehicle);
    }

    public void update(Vehicle vehicle) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(vehicle);
    }

    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }

    public boolean hasAnyVehicle(int customerId) throws SQLException {
        List<Vehicle> list = dao.findByCustomerId(customerId);
        return (list != null && !list.isEmpty());
    }
}

