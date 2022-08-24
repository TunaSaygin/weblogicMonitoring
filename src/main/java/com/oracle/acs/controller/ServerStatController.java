package com.oracle.acs.controller;

import com.oracle.acs.dao.OracleDAO;
import io.helidon.common.SerializationConfig;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

@Singleton
@Path("/api/monitorServer")
public class ServerStatController {
    //these variables will be used till cassandra is integrated
    private String httpPort = "127.0.0.1";
    private String httpHost = "17001";
    //the cassandraDao variable
    static OracleDAO db = new OracleDAO();
    /**
     * this method will behave as a scheduler and every 5 min writes the server stats to the cassandra db
     */
    @PostConstruct
    public void init() {
        //SerializationConfig.builder().filterPattern().onNoConfig(SerializationConfig.Action.CONFIGURE);
        System.out.println("app is started");
    }
    @GET
    public String listDomainServerStats(){
        WebTarget target = ClientBuilder.newClient().target("http://"+httpPort+":"+httpHost+"/management/tenant-monitoring/servers");
        Invocation stimulus = target.request().header("Authorization","Basic " + Base64.getEncoder().encodeToString("trial:welcome1".getBytes()))
                .header("accept","application/json").buildGet();
        String result = stimulus.invoke().readEntity(String.class);
        //TODO
        return result;
    }
    @Path("domains")
    @GET
    public String getDomains(){
        String result = "first";
        System.out.println("inside the method");
        try {

            System.out.println("inside try statement");
            Connection con = db.getSession();
            System.out.println("after get session");
            Statement sttmnt = con.createStatement();
            String query = "select DomainName from monitoring_domains";
            System.out.println("--------------------------------------");
            ResultSet rs = sttmnt.executeQuery(query);
            System.out.println("**************************");
            //result = rs.getString("DomainName");
            rs.next();
            result = rs.getString("DomainName");
            System.out.println(result);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
