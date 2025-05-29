package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.dao.PartDao;
import pti.datenbank.autowerk.models.Part;
import pti.datenbank.autowerk.models.User;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PartDaoImpl implements PartDao {
    private static final String SELECT_BASE = "SELECT PartID, CreatedByUserID, Name, Manufacturer, UnitPrice, InStockQty FROM Parts";
    private static final String SELECT_BY_ID = SELECT_BASE + " WHERE PartID=?";
    private static final String INSERT_SQL = "INSERT INTO Parts (CreatedByUserID, Name, Manufacturer, UnitPrice, InStockQty) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Parts SET Name=?, Manufacturer=?, UnitPrice=?, InStockQty=? WHERE PartID=?";
    private static final String DELETE_SQL = "DELETE FROM Parts WHERE PartID=?";
    private static final String SELECT_BY_CREATOR = SELECT_BASE + " WHERE CreatedByUserID = ?";

    @Override public Part findById(Integer id) throws SQLException {
        try(Connection c = DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(SELECT_BY_ID)){
            ps.setInt(1,id); try(ResultSet rs=ps.executeQuery()){ if(rs.next()) return map(rs);} }
        return null;
    }
    @Override public List<Part> findAll() throws SQLException {
        List<Part> list=new ArrayList<>();
        try(Connection c=DBConnection.getConnection(); Statement s=c.createStatement(); ResultSet rs=s.executeQuery(SELECT_BASE)){ while(rs.next()) list.add(map(rs)); }
        return list;
    }
    @Override public void insert(Part p) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,p.getCreatedBy().getUserId()); ps.setString(2,p.getName()); ps.setString(3,p.getManufacturer()); ps.setBigDecimal(4,p.getUnitPrice()); ps.setInt(5,p.getInStockQty()); ps.executeUpdate();
            try(ResultSet keys=ps.getGeneratedKeys()){ if(keys.next()) p.setPartId(keys.getInt(1)); }
        }
    }
    @Override public void update(Part p) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(UPDATE_SQL)){
            ps.setString(1,p.getName()); ps.setString(2,p.getManufacturer()); ps.setBigDecimal(3,p.getUnitPrice()); ps.setInt(4,p.getInStockQty()); ps.setInt(5,p.getPartId()); ps.executeUpdate();
        }
    }
    @Override public void delete(Integer id) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(DELETE_SQL)){ ps.setInt(1,id); ps.executeUpdate(); }
    }
    @Override public List<Part> findByCreator(int userId) throws SQLException {
        List<Part> list=new ArrayList<>();
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(SELECT_BY_CREATOR)){
            ps.setInt(1,userId); try(ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    private Part map(ResultSet rs) throws SQLException {
        Part p = new Part();
        p.setPartId(rs.getInt("PartID"));
        User u = new UserDaoImpl().findById(rs.getInt("CreatedByUserID")); p.setCreatedBy(u);
        p.setName(rs.getString("Name")); p.setManufacturer(rs.getString("Manufacturer")); p.setUnitPrice(rs.getBigDecimal("UnitPrice")); p.setInStockQty(rs.getInt("InStockQty"));
        return p;
    }
}