/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import Client.DatabaseEngine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author nixholas
 */
public class Salesrecordentity {
    private long id;
    private double amountdue;
    private double amountpaid;
    private double amountpaidusingpoints;
    private Date CreatedDate;
    private String currency;
    private int loyaltypointsdeducted;
    private String posname;
    private String receiptno;
    private String servedbystaff;
    private long member_id;
    private long store_id;

    public Salesrecordentity() {}
    
    public Salesrecordentity(double amountdue, double amountpaid, double amountpaidusingpoints, Date CreatedDate, String currency, int loyaltypointsdeducted, String posname, String receiptno, String servedbystaff, long member_id, long store_id) {
        this.amountdue = amountdue;
        this.amountpaid = amountpaid;
        this.amountpaidusingpoints = amountpaidusingpoints;
        this.CreatedDate = CreatedDate;
        this.currency = currency;
        this.loyaltypointsdeducted = loyaltypointsdeducted;
        this.posname = posname;
        this.receiptno = receiptno;
        this.servedbystaff = servedbystaff;
        this.member_id = member_id;
        this.store_id = store_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmountdue() {
        return amountdue;
    }

    public void setAmountdue(double amountdue) {
        this.amountdue = amountdue;
    }

    public double getAmountpaid() {
        return amountpaid;
    }

    public void setAmountpaid(double amountpaid) {
        this.amountpaid = amountpaid;
    }

    public double getAmountpaidusingpoints() {
        return amountpaidusingpoints;
    }

    public void setAmountpaidusingpoints(double amountpaidusingpoints) {
        this.amountpaidusingpoints = amountpaidusingpoints;
    }

    public Date getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(Date CreatedDate) {
        this.CreatedDate = CreatedDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getLoyaltypointsdeducted() {
        return loyaltypointsdeducted;
    }

    public void setLoyaltypointsdeducted(int loyaltypointsdeducted) {
        this.loyaltypointsdeducted = loyaltypointsdeducted;
    }

    public String getPosname() {
        return posname;
    }

    public void setPosname(String posname) {
        this.posname = posname;
    }

    public String getReceiptno() {
        return receiptno;
    }

    public void setReceiptno(String receiptno) {
        this.receiptno = receiptno;
    }

    public String getServedbystaff() {
        return servedbystaff;
    }

    public void setServedbystaff(String servedbystaff) {
        this.servedbystaff = servedbystaff;
    }

    public long getMember_id() {
        return member_id;
    }

    public void setMember_id(long member_id) {
        this.member_id = member_id;
    }

    public long getStore_id() {
        return store_id;
    }

    public void setStore_id(long store_id) {
        this.store_id = store_id;
    }
    
    public void createTransactionRecord(String memberId) throws ClassNotFoundException, SQLException {
        Connection conn = DatabaseEngine.getConnection();
                    
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
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // Auto Incremental Primary Key Retrieval
            // http://stackoverflow.com/questions/7162989/sqlexception-generated-keys-not-requested-mysql
            // Statement.RETURN_GENERATED_KEYS resolves the error below:
            // java.sql.SQLException: Generated keys not requested. You need to specify Statement.RETURN_GENERATED_KEYS to Statement.executeUpdate() or Connection.prepareStatement(). 
            PreparedStatement ps = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, this.amountdue);
            ps.setDouble(2, this.amountpaid);
            ps.setDouble(3, 0); // AMOUNTPAIDUSINGPOINTS -- Default 0.
            // http://stackoverflow.com/questions/6777810/a-datetime-equivalent-in-java-sql-is-there-a-java-sql-datetime
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setString(5, "SGD"); // CURRENCY -- Let's assume Default as SGD
            ps.setInt(6, 0); // LOYALTYPOINTSDEDUCTED -- Let's assume Default as 0
            ps.setString(7, null); // POSNAME -- There's no counter in ECommerce.. Set to null
            ps.setString(8, null); // RECEIPTNO -- No physical receipt...
            ps.setString(9, null); // SERVEDBYSTAFF -- No STAFF SERVING ECOMMERCE..
            ps.setLong(10, Long.parseLong(memberId));
            ps.setLong(11, 59); // STORE_ID -- ECommerce -> 10001
            
            //ps.executeQuery();
            
            // executeUpdate() Resolves the error below:
            // java.sql.SQLException: Can not issue data manipulation statements with executeQuery(). 
            ps.executeUpdate();
            
            // Solves the error below?
            // java.sql.SQLException: Can not issue data manipulation statements with executeQuery(). 
          
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            
            this.id = rs.getLong(1);
            
            rs.close();
            ps.close();
    }
}
