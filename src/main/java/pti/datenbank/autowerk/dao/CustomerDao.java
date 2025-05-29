package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.Customer;

import java.sql.SQLException;

public interface CustomerDao extends GenericDao<Customer, Integer> {
    Customer findByUserId(int userId) throws SQLException;
}
