package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.Impl.MechanicDaoImpl;
import pti.datenbank.autowerk.dao.MechanicDao;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Mechanic;

import java.sql.SQLException;
import java.util.List;

public class MechanicService extends BaseService {
    private final MechanicDao dao = new MechanicDaoImpl();

    public MechanicService(AuthService authService) {
        super(authService);
    }

    public Mechanic findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    public List<Mechanic> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return dao.findAll();
    }

    public Mechanic findByUserId(int userId) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByUserId(userId);
    }

    public void create(Mechanic mechanic) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(mechanic);
    }

    public void update(Mechanic mechanic) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(mechanic);
    }

    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }
}