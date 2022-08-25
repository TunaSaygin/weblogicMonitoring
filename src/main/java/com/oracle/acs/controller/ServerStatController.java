package com.oracle.acs.controller;

import com.oracle.acs.dao.OracleDAO;
import io.helidon.common.SerializationConfig;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.JsonParser;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import oracle.jdbc.proxy.annotation.Post;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;

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
        System.out.println("method writeResult is called and its result is:\n" + getServerNames());
    }
    @Path("/listDomainServerStats")
    @GET
    public String listDomainServerStats(){
        JsonArray arr = new JsonArray();
        JsonObject finalObject = new JsonObject();
        try {
            ResultSet rst = db.getSession().createStatement().executeQuery("select domainname, username,password,url from monitoring_domains");
            while (rst.next()){
                WebTarget target = ClientBuilder.newClient().target("http://"+rst.getString("url")+"/management/tenant-monitoring/servers");
                Invocation stimulus = target.request().header("Authorization",Authing(rst.getString("username"),rst.getString("password")))
                        .header("accept","application/json").buildGet();
                JsonObject result = new JsonObject(stimulus.invoke().readEntity(String.class));
                //additional information for calling detailedStats getServerNames method
                result.put("url",rst.getString("url"));
                result.put("userName", rst.getString("username"));
                result.put("password",rst.getString("password"));
                arr.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finalObject.put("list", arr);
        return finalObject.toString();
    }
    @Path("/listDomains")
    @GET
    public String getDomains(){
        JsonArray jsonarr = new JsonArray();

        try {
            ResultSet row = db.getSession().createStatement().executeQuery("select * from wls.monitoring_domains");
            while(row.next()){
                JsonObject jsonObject = new JsonObject();
                jsonObject.put("domain_id", row.getInt("domain_id"));
                jsonObject.put("domainName", row.getString("domainName"));
                jsonObject.put("userName", row.getString("userName"));
                jsonObject.put("password", row.getString("password"));
                jsonObject.put("url", row.getString("url"));
                jsonObject.put("created_time",new Date(row.getDate("created_time").getTime()).toInstant());
                jsonarr.add(jsonObject);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonarr.toString();
    }

    @Path("/detailedServerStat")
    @GET
    public String getDetailedStats(){
        JsonArray result = new JsonArray();
        List<String> serverNames = getServerNames();
        String serverName;
        String domainURL;
        String userName;
        String password;
        for(int i=0;i<serverNames.size(); i=i+4){
            serverName = serverNames.get(i);
            domainURL = serverNames.get(i+1);
            userName = serverNames.get(i+2);
            password = serverNames.get(i+3);
            WebTarget target = ClientBuilder.newClient().target("http://"+domainURL+"/management/tenant-monitoring/servers/"+serverName);
            Invocation stimulus = target.request().header("Authorization", Authing(userName, password))
                    .header("accept", "application/json").buildGet();
            JsonObject serverInfo = new JsonObject(stimulus.invoke().readEntity(String.class));
            result.add(serverInfo);
        }
        return result.toString();
    }

    /*public void writeStats(){
        JsonArray rawData = new JsonArray(getDetailedStats());

        db.getSession().createStatement().executeQuery()
    }*/

    @Path("/listTree")
    @GET
    public String getTree(@QueryParam("nodeId") Integer nodeId){
        System.out.println("#####" + nodeId);
        JsonArray jsonarr = new JsonArray();
        JsonObject result = new JsonObject();
        try {
            ResultSet rst = db.getSession().createStatement().executeQuery("select * from wls.menu_nodes where parentNodeId=" + nodeId);
            while(rst.next()){
                JsonObject obj = new JsonObject();
                obj.put("nodeId",rst.getInt("nodeId"))
                        .put("parentNodeId",rst.getInt("parentNodeId"))
                        .put("nodeName",rst.getString("nodeName"))
                        .put("url",rst.getString("url"))
                        .put("leaf",true);
                jsonarr.add(obj);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        result.put("data",jsonarr);
        return result.toString();
    }
    private String Authing(String userName, String password){
        String joint = userName + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(joint.getBytes());
    }

    /**
     * this method provides detailed information needed to get each server information in single array
     * @return this arraylist's size is multiple of 4 since nth index is;
     * -serverName if remainder of 4 is 0.
     * -url if remainder of 4 is 1.
     * -userName if remainder of 4 is 2.
     * -password if remainder of 4 is 3.
     */
    private ArrayList<String> getServerNames(){
        ArrayList<String> result = new ArrayList<>();
        System.out.println("inside writeResults method");
        JsonObject rawData = new JsonObject(listDomainServerStats());
        System.out.println("the raw data is: " + rawData);
        JsonArray arr = rawData.getJsonArray("list");
        Iterator<Object> iter = arr.stream().iterator();
        while (iter.hasNext()){
            //information about domains (url,userName, password)
            JsonObject domainInfo = (JsonObject) iter.next();
            System.out.println(domainInfo.toString());
            Iterator<Object> second= domainInfo.getJsonObject("body").getJsonArray("items").stream().iterator();
            while(second.hasNext()){
                //result.add(((JsonObject)second.next()).getString("name"));
                JsonObject serverInfo = (JsonObject)second.next();
                result.add(serverInfo.getString("name"));
                result.add(domainInfo.getString("url"));
                result.add(domainInfo.getString("userName"));
                result.add(domainInfo.getString("password"));
            }
        }
        return result;
    }
}
