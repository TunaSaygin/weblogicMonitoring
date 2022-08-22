package com.oracle.acs.dao;
import java.sql.*;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

@Singleton
public class OracleDAO {
    Connection con;
    public OracleDAO() {
        try {
            System.out.println("connection before: -----------------------");
            con = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl", "WLS", "WLSMON" );
            System.out.println("Connected to database");
        } catch (Exception e) {
            System.out.println("not connected***************");
            e.printStackTrace();
        }
    }
    public Connection getSession() throws SQLException {
        return DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl", "WLS", "WLSMON" );
    }

}
