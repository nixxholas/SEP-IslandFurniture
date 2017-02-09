package service;

import Entity.ShoppingCartLineItem;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("commerce")
public class ECommerceFacadeREST {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Context
    private UriInfo context;

    public ECommerceFacadeREST() {
    }

    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ECommerce
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
    
    @PUT
    @Path("createECommerceTransactionRecord")
    @Produces("application/json")
    public Response createECommerceTransactionRecord(
            String memberId,
            @QueryParam("finalPrice") double finalPrice,
            @QueryParam("countryID") long countryId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?user=root&password=12345");
            
            // Inserting a row with PreparedStatement
            // http://stackoverflow.com/questions/11804906/insert-row-into-database-with-preparedstatement
            /**
             *   `ID` bigint(20) NOT NULL AUTO_INCREMENT,
             *   ``AMOUNTDUE` double DEFAULT NULL,
             *   ``AMOUNTPAID` double DEFAULT NULL,
             *   ``AMOUNTPAIDUSINGPOINTS` double DEFAULT NULL,
             *   ``CREATEDDATE` datetime DEFAULT NULL,
             *   ``CURRENCY` varchar(255) DEFAULT NULL,
             *   ``LOYALTYPOINTSDEDUCTED` int(11) DEFAULT NULL,
             *   ``POSNAME` varchar(255) DEFAULT NULL,
             *   ``RECEIPTNO` varchar(255) DEFAULT NULL,
             *   ``SERVEDBYSTAFF` varchar(255) DEFAULT NULL,
             *   ``MEMBER_ID` bigint(20) DEFAULT NULL,
             *   ``STORE_ID` bigint(20) DEFAULT NULL,
             */
            String stmt = "INSERT INTO salesrecordentity (AMOUNTDUE, "
                    + "AMOUNTPAID, AMOUNTPAIDUSINGPOINTS, CREATEDDATE, "
                    + "CURRENCY, LOYALTYPOINTSDEDUCTED, POSNAME, "
                    + "RECEIPTNO, SERVEDBYSTAFF, MEMBER_ID, STORE_ID)"
                    + " VALUES "
                    + "(?, ?, ?, ?, ?)";

            // Auto Incremental Primary Key Retrieval
            // http://stackoverflow.com/questions/7162989/sqlexception-generated-keys-not-requested-mysql
            // Statement.RETURN_GENERATED_KEYS resolves the error below:
            // java.sql.SQLException: Generated keys not requested. You need to specify Statement.RETURN_GENERATED_KEYS to Statement.executeUpdate() or Connection.prepareStatement(). 
            PreparedStatement ps = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, finalPrice);
            ps.setDouble(2, finalPrice);
            ps.setDouble(3, 0); // AMOUNTPAIDUSINGPOINTS -- Default 0.
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setString(5, "SGD"); // CURRENCY -- Let's assume Default as SGD
            ps.setInt(6, 0); // LOYALTYPOINTSDEDUCTED -- Let's assume Default as 0
            ps.setString(7, null); // POSNAME -- There's no counter in ECommerce.. Set to null
            ps.setString(8, null); // RECEIPTNO -- No physical receipt...
            ps.setString(9, null); // SERVEDBYSTAFF -- No STAFF SERVING ECOMMERCE..
            ps.setLong(10, Long.parseLong(memberId));
            ps.setLong(11, 10001); // STORE_ID -- ECommerce -> 10001
            
            //ps.executeQuery();
            
            // executeUpdate() Resolves the error below:
            // java.sql.SQLException: Can not issue data manipulation statements with executeQuery(). 
            ps.executeUpdate();
            
            // Solves the error below?
            // java.sql.SQLException: Can not issue data manipulation statements with executeQuery(). 
          
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            
            long recordId = rs.getLong(1);
            
            return Response.ok(String.valueOf(recordId)).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.toString()).build();
        }
    }
    
    @PUT
    @Path("createECommerceLineItemRecord")
    @Produces("application/json")
    public Response createECommerceLineItemRecord(
            @QueryParam("salesRecordID") long salesRecordId,
            @QueryParam("itemEntityID") long itemEntityId,
            @QueryParam("quantity") int quantity,
            @QueryParam("countryID") long countryId) {
        return null;
    }
}
