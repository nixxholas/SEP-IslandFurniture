package service;

import Entity.ShoppingCartLineItem;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
                    + "AMOUNTPAID, MEMBER_ID)"
                    + " VALUES "
                    + "(?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setDouble(1, finalPrice);
            ps.setDouble(2, finalPrice);
            ps.setLong(3, Long.parseLong(memberId));
            
            ps.executeQuery();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            
            long recordId = rs.getLong(1);
            
            return Response.ok(recordId).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
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
