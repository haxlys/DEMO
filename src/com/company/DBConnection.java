package com.company;

import org.junit.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {

    public void add(Domain vo) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        try {
            Connection con = null;

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dev_stats?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                    "root", "root");

            PreparedStatement ps = con.prepareStatement(
                    "insert into rocket_punch(id, skill_name, skill_count, regist_date) values(?,?,?,now())");
            ps.setInt(1, vo.getId());
            ps.setString(2, vo.getSkillName());
            ps.setInt(3, vo.getSkillCount());

            ps.executeUpdate();

            ps.close();
            con.close();

        } catch (SQLException sqex) {
            sqex.printStackTrace();
        }

    }

    public List<Domain> get(int id) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dev_stats?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                "root", "root");

        PreparedStatement ps = con.prepareStatement(
                "select * from users where id = ?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        List<Domain> result = new ArrayList();
        Domain vo = null;
        while(rs.next()){
            vo = new Domain();
            vo.setId(rs.getInt("id"));
            vo.setSkillName(rs.getString("skill_name"));
            vo.setSkillCount(rs.getInt("skill_count"));

            result.add(vo);
        }

        rs.close();
        ps.close();
        con.close();

        return result;
    }

    public int getMaxId() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dev_stats?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                "root", "root");

        PreparedStatement ps = con.prepareStatement("select max(ifnull(id,0))+1 AS id from rocket_punch");

        ResultSet rs = ps.executeQuery();
        int maxId = 0;
        if(rs.next()){
            maxId = rs.getInt("id");
        }

        rs.close();
        ps.close();
        con.close();

        return maxId;
    }

    public void delete(int id) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dev_stats?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                "root", "root");

        PreparedStatement ps = con.prepareStatement("delete from rocket_punch where id = ?");
        ps.setInt(1, id);

        ps.close();
        con.close();
    }
}