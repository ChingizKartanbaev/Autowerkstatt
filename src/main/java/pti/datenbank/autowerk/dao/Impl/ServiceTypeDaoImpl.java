package pti.datenbank.autowerk.dao.Impl;

import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.dao.ServiceTypeDao;
import pti.datenbank.autowerk.models.ServiceType;
import pti.datenbank.autowerk.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTypeDaoImpl implements ServiceTypeDao {
    private static final String SELECT_BASE = "SELECT ServiceTypeID, CreatedByUserID, Name, Description, BasePrice FROM ServiceTypes";
    private static final String SELECT_BY_ID = SELECT_BASE + " WHERE ServiceTypeID = ?";
    private static final String INSERT_SQL = "INSERT INTO ServiceTypes (CreatedByUserID, Name, Description, BasePrice) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE ServiceTypes SET Name=?, Description=?, BasePrice=? WHERE ServiceTypeID=?";
    private static final String DELETE_SQL = "DELETE FROM ServiceTypes WHERE ServiceTypeID=?";
    private static final String SELECT_BY_CREATOR = SELECT_BASE + " WHERE CreatedByUserID = ?";

    @Override
    public ServiceType findById(Integer id) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(SELECT_BY_ID)){
            ps.setInt(1,id);
            try(ResultSet rs=ps.executeQuery()){ if(rs.next()) return map(rs);} }
        return null;
    }
    @Override public List<ServiceType> findAll() throws SQLException {
        List<ServiceType> list=new ArrayList<>();
        try(Connection c=DBConnection.getConnection(); Statement s=c.createStatement(); ResultSet rs=s.executeQuery(SELECT_BASE)){ while(rs.next()) list.add(map(rs)); }
        return list;
    }
    @Override public void insert(ServiceType st) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,st.getCreatedBy().getUserId()); ps.setString(2,st.getName()); ps.setString(3,st.getDescription()); ps.setBigDecimal(4,st.getBasePrice()); ps.executeUpdate();
            try(ResultSet keys=ps.getGeneratedKeys()){ if(keys.next()) st.setServiceTypeId(keys.getInt(1)); }
        }
    }
    @Override public void update(ServiceType st) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(UPDATE_SQL)){
            ps.setString(1,st.getName()); ps.setString(2,st.getDescription()); ps.setBigDecimal(3,st.getBasePrice()); ps.setInt(4,st.getServiceTypeId()); ps.executeUpdate();
        }
    }
    @Override public void delete(Integer id) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(DELETE_SQL)){ ps.setInt(1,id); ps.executeUpdate(); }
    }
    @Override public List<ServiceType> findByCreator(int userId) throws SQLException {
        List<ServiceType> list=new ArrayList<>();
        try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(SELECT_BY_CREATOR)){
            ps.setInt(1,userId); try(ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    private ServiceType map(ResultSet rs) throws SQLException {
        ServiceType st=new ServiceType();
        st.setServiceTypeId(rs.getInt("ServiceTypeID"));
        User u = new UserDaoImpl().findById(rs.getInt("CreatedByUserID")); st.setCreatedBy(u);
        st.setName(rs.getString("Name")); st.setDescription(rs.getString("Description")); st.setBasePrice(rs.getBigDecimal("BasePrice"));
        return st;
    }
}
