package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 2016. 12. 14..
 */
public class SkillStatsDAO {

    private ConnectionMaker connectionMaker;

    public SkillStatsDAO(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public boolean hasOffer(Domain vo) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement("select count(*) as cnt from offer where id = ?");
            ps.setInt(1, vo.getOfferId());

            rs = ps.executeQuery();
            if(rs.next()){
                if(rs.getInt("cnt") > 0){
                    return true;
                }
            }

        } catch (SQLException sqex){
            sqex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }

        return false;
    }

    public boolean isEqualContent(Domain vo) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement(
                    "select count(*) as cnt from offer_info where offer_regist_date = ? and offer_state = ?");
            ps.setString(1, vo.getOfferRegistDate());
            ps.setString(2, vo.getOfferState());

            rs = ps.executeQuery();
            if(rs.next()){
                if(rs.getInt("cnt") > 0){
                    return true;
                }
            }

        } catch (SQLException sqex){
            sqex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }

        return false;
    }

    public void addOffer(Domain vo) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            if(con == null) {
                con = connectionMaker.makeConnection();
            }

            ps = con.prepareStatement("insert into offer(id, company_id, created) values(?,?,now())");
            ps.setInt(1, vo.getOfferId());
            ps.setInt(2, vo.getCompanyId());

            ps.executeUpdate();

            ps.close();
            con.close();

        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }
    }

    public void addOfferInfo(Domain vo) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement(
                    "insert into offer_info(id, offer_id, offer_subject, offer_regist_date, offer_state, offer_expire_date, created) values(?,?,?,?,?,?,now())");
            ps.setInt(1, vo.getId());
            ps.setInt(2, vo.getOfferId());
            ps.setString(3, vo.getOfferSubject());
            ps.setString(4, vo.getOfferRegistDate());
            ps.setString(5, vo.getOfferState());
            ps.setString(6, vo.getOfferExpireDate());

            ps.executeUpdate();

            ps.close();
            con.close();

        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }
    }


    public void addCompany(Domain vo) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            if(con == null) {
                con = connectionMaker.makeConnection();
            }

            ps = con.prepareStatement("insert into company(id, company_name, created) values(?,?,now())");
            ps.setInt(1, vo.getCompanyId());
            ps.setString(2, vo.getCompanyName());

            ps.executeUpdate();

            ps.close();
            con.close();

        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }

    }

    public void addSkill(Domain vo) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement("insert into offer_skills(id, offer_id, skill_name, created) values(?,?,?,now())");
            ps.setInt(1, vo.getId());
            ps.setInt(2, vo.getOfferId());
            ps.setString(3, vo.getSkillName());

            ps.executeUpdate();

            ps.close();
            con.close();

        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }

    }

    public void addPosition(Domain vo) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement("insert into offer_position(id, offer_id, position, created) values(?,?,?,now())");
            ps.setInt(1, vo.getId());
            ps.setInt(2, vo.getOfferId());
            ps.setString(3, vo.getPosition());

            ps.executeUpdate();

        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }

    }

    public void addExp(Domain vo) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement("insert into offer_exp(id, offer_id, offer_experience, created) values(?,?,?,now())");
            ps.setInt(1, vo.getId());
            ps.setInt(2, vo.getOfferId());
            ps.setString(3, vo.getOfferExp());

            ps.executeUpdate();

        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }

    }

    public List<Domain> get(int id) throws ClassNotFoundException{
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Domain> result = null;

        try{
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement("select * from users where id = ?");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            result = new ArrayList();
            Domain vo = null;
            while(rs.next()){
                vo = new Domain();
                vo.setId(rs.getInt("id"));
                vo.setSkillName(rs.getString("skill_name"));
                vo.setSkillCount(rs.getInt("skill_count"));

                result.add(vo);
            }
        } catch (SQLException sqex){
            sqex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }



        return result;
    }

    public int getMaxId(String tableName) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int maxId = 0;

        try{
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement("select max(ifnull(id,0))+1 AS id from " + tableName);
            rs = ps.executeQuery();

            if(rs.next()){
                maxId = rs.getInt("id");
            }

        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }

        return maxId;
    }

    public void delete(int id) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = connectionMaker.makeConnection();
            ps = con.prepareStatement("delete from rocket_punch where id = ?");
            ps.setInt(1, id);

            ps.executeQuery();
        } catch (SQLException sqex){
            sqex.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }
    }

    public void deleteAll() throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement("delete from offer");
            ps.addBatch();
            ps = con.prepareStatement("delete from company");
            ps.addBatch();
            ps = con.prepareStatement("delete from offer_skills");
            ps.addBatch();
            ps = con.prepareStatement("delete from offer_position");
            ps.addBatch();
            ps = con.prepareStatement("delete from offer_info");
            ps.addBatch();
            ps = con.prepareStatement("delete from offer_exp");
            ps.addBatch();

            ps.executeBatch();
        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }
    }

    public int getCompanyId(String companyName) throws ClassNotFoundException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        int maxId = 0;

        try{
            con = connectionMaker.makeConnection();

            ps = con.prepareStatement("select id from company where company_name = ?");
            ps.setString(1, companyName);

            rs = ps.executeQuery();

            // rs에 여러개가 올 경우 처리해야함
            if(rs.next()){
                maxId = rs.getInt("id");
            }
        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (ps != null) try { ps.close(); } catch(SQLException ex) {}
            if (con != null) try { con.close(); } catch(SQLException ex) {}
        }

        return maxId;
    }
}
