package service;

import Client.DatabaseEngine;
import Entity.Itementity;
import Entity.Lineitementity;
import Entity.Salesrecordentity;
import Entity.ShoppingCartLineItem;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext(unitName = "WebService")
    private EntityManager em;
    
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
            Salesrecordentity salesrecord = new Salesrecordentity();
            salesrecord.setAmountdue(finalPrice);
            salesrecord.setAmountpaid(finalPrice);
            
            salesrecord.createTransactionRecord(memberId);
            
            return Response.ok(String.valueOf(salesrecord.getId())).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.toString()).build();
        }
    }
    
    @PUT
    @Path("removeAndUpdateQuantityItemRecord")
    @Produces("application/json")
    public Response removeAndUpdateQuantityItemRecord(
            @QueryParam("salesRecordID") long salesRecordId,
            @QueryParam("itemEntityID") long itemEntityId,
            @QueryParam("quantity") int quantity,
            @QueryParam("countryID") long countryId) {
        try {
            // Initialize the Lineitementity object first
            Itementity item = new Itementity(itemEntityId);
            Lineitementity lineitem = new Lineitementity();
            
            if (item.deductAtDatabase(quantity)) {
                // Move on since we've deducted the stocks
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Unable to deduct from database").build();
            }
            
            // Then retrieve the primary key from the database after adding it
            lineitem.setId(item.addToDatabase(quantity));
            
            // Bind it with the salesrecordentity
            lineitem.addToSalesRecord(salesRecordId);
            
             if (lineitem.getId() > 0) { // I'm just scared, so we'll check it again
                 return Response.status(Response.Status.OK)
                         .entity("Success! ").build();
             } else {
                 return Response.status(Response.Status.CONFLICT)
                         .entity(String.valueOf(lineitem.getId())).build();
             }
        } catch (ClassNotFoundException | SQLException ex) {
             return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.toString()).build();
        }
        
        /**
         * Two Tables to update, 
         * 
         * salesrecordentity_lineitementity
         * `SalesRecordEntity_ID` bigint(20) NOT NULL,
         * `itemsPurchased_ID` bigint(20) NOT NULL,
         * 
         * lineitementity
         * `ID` bigint(20) NOT NULL AUTO_INCREMENT,
         * `PACKTYPE` varchar(255) DEFAULT NULL,
         * `QUANTITY` int(11) DEFAULT NULL,
         * `ITEM_ID` bigint(20) DEFAULT NULL,
         */
//        try {
//            Connection conn = DatabaseEngine.getConnection();
//            
//            String stmt = "INSERT INTO lineitementity (QUANTITY, ITEM_ID)"
//                    + " VALUES "
//                    + "(?, ?)";
//
//            // Auto Incremental Primary Key Retrieval
//            // http://stackoverflow.com/questions/7162989/sqlexception-generated-keys-not-requested-mysql
//            // Statement.RETURN_GENERATED_KEYS resolves the error below:
//            // java.sql.SQLException: Generated keys not requested. You need to specify Statement.RETURN_GENERATED_KEYS to Statement.executeUpdate() or Connection.prepareStatement(). 
//            PreparedStatement ps = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
//            ps.setInt(1, quantity);
//            ps.setLong(2, itemEntityId);
//            
//            //ps.executeQuery();
//            
//            // executeUpdate() Resolves the error below:
//            // java.sql.SQLException: Can not issue data manipulation statements with executeQuery(). 
//            ps.executeUpdate();
//            
//            // Solves the error below?
//            // java.sql.SQLException: Can not issue data manipulation statements with executeQuery(). 
//          
//            ResultSet rs = ps.getGeneratedKeys();
//            rs.next();
//            
//            long lineitementityId = rs.getLong(1);
//            
//            ps.close();
//            
//            // We will now add it to the composite key table
//            
//            String salestmt = "INSERT INTO salesrecordentity_lineitementity "
//                    + "(SalesRecordEntity_ID, itemsPurchased_ID)"
//                    + " VALUES "
//                    + "(?, ?)";
//            
//            PreparedStatement salesps = 
//                    conn.prepareStatement(salestmt, Statement.RETURN_GENERATED_KEYS);
//            salesps.setLong(1, salesRecordId);
//            salesps.setLong(2, lineitementityId);
//            
//            salesps.executeUpdate();
//            
//            // No need to retrieve any data back, let's go back to the
//            // Servlet
//            
//            salesps.close();
//            
//            return Response.ok(String.valueOf(lineitementityId)).build();
//        } catch (Exception ex) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity(ex.toString()).build();
//        }
    }
}
