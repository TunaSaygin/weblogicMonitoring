package com.oracle.acs.dao;
import java.sql.*;
import oracle.jdbc.*;
import oracle.jdbc.driver.*;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

@Singleton
public class OracleDAO {
    Connection con;
    @PostConstruct
    public void init() throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        con = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl", "WLS", "WLSMON" );
    }
    public Connection getSession(){
        return this.con;
    }

}
