package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.Impl.PartDaoImpl;
import pti.datenbank.autowerk.dao.PartDao;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Part;

import java.sql.SQLException;
import java.util.List;

public class PartService extends BaseService {
    private final PartDao dao = new PartDaoImpl();

    public PartService(AuthService authService) {
        super(authService);
    }

    public Part findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    public List<Part> findAll() throws SQLException {
        checkPermission(Permission.READ);
        int userId = authService.getCurrentUser().getUserId();
        System.out.println("PartService.findAll() called for user ID: " + userId);

        if (authService.isAdmin() || authService.isMechanic()) {
            return dao.findAll();
        } else {
            return dao.findByCreator(userId);
        }
    }


    public List<Part> findAllForCustomer() throws SQLException {
        return dao.findAll();
    }

    public List<Part> findByCreator(int userId) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByCreator(userId);
    }

    public void create(Part part) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(part);
    }

    public void update(Part part) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(part);
    }

    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }
}