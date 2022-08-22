package com.oracle.acs.controller;

import com.oracle.acs.dao.OracleDAO;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    OracleDAO db;
    /**
     * this method will behave as a scheduler and every 5 min writes the server stats to the cassandra db
     */
    @PostConstruct
    public void init() {
        db = new OracleDAO();
        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                //TODO
                //write the results to cassandra
            }
        };
        timer.scheduleAtFixedRate(tt, 0, 300000); //time is in miliseconds
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
        String result = "";
        try {
            ResultSet rs = db.getSession().createStatement().executeQuery("select * from monitoring_domains");
            while(rs.next()){
                result +=rs.getString("DomainName") + "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
