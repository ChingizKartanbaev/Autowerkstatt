package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.Impl.ServiceTypeDaoImpl;
import pti.datenbank.autowerk.dao.ServiceTypeDao;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.ServiceType;

import java.sql.SQLException;
import java.util.List;

public class ServiceTypeService extends BaseService {
    private final ServiceTypeDao dao = new ServiceTypeDaoImpl();

    public ServiceTypeService(AuthService authService) {
        super(authService);
    }

    public ServiceType findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    public List<ServiceType> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return dao.findAll();
    }

    public List<ServiceType> findByCreator(int userId) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByCreator(userId);
    }

    public void create(ServiceType type) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(type);
    }

    public void update(ServiceType type) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(type);
    }

    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }
}
