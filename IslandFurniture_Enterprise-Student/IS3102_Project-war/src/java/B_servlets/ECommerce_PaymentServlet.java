/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import HelperClasses.Member;
import HelperClasses.ShoppingCartLineItem;
import Utils.LuhnAlgorithm;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author nixholas
 */
@WebServlet(name = "ECommerce_PaymentServlet", urlPatterns = {"/ECommerce_PaymentServlet"})
public class ECommerce_PaymentServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        
        try {
            // Debugging Purposes Only
            //out.println(request.getParameter("txtName"));
            
            // Retrieve all of the user payment information first
            String cardName = "";
            long cardNo;
            double finalPrice = 0.0;
            int securityCode;
            int month, year;
            long memberId = 0;
            //String country = "";
            long countryId = 0;
            ArrayList<ShoppingCartLineItem> shoppingCart = null;
            
            if (session.getAttribute("memberID") != null) {
                memberId = (long) session.getAttribute("memberID");
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Your session has expired, please login again.");
            }
            
            // if (session.getAttribute("memberCountry") != null) {
            //    country = (String) session.getAttribute("memberCountry");
            // } else {
            //    response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
            //        + "?errMsg=Your session has expired, please login again.");
            // }
            
            // Debugging Purposes Only
            // out.println((ArrayList<ShoppingCartLineItem>) 
            //        session.getAttribute("shoppingCart"));
            if ((ArrayList<ShoppingCartLineItem>) 
                    session.getAttribute("shoppingCart") != null) {
                shoppingCart = (ArrayList<ShoppingCartLineItem>) 
                        session.getAttribute("shoppingCart");
                
                // Set the countryid immediately
                countryId = shoppingCart.get(0).getCountryID();
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Invalid Cart.");
            }
            
            if (!"".equals(request.getParameter("txtName")) && 
                    request.getParameter("txtName") != null) {
                cardName = request.getParameter("txtName");
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Please enter a valid name.");
            }
            
            if (!"".equals(request.getParameter("txtCardNo")) && 
                    request.getParameter("txtCardNo") != null &&
                    isNumeric(request.getParameter("txtCardNo"))) {
                cardNo = Long.parseLong(request.getParameter("txtCardNo"));
                
                if (LuhnAlgorithm.isValid(cardNo)) {
                    // It's a valid credit number, so we'll pass
                } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Please enter a valid Card Number.");
                }
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Please enter a Card Number.");
            }
            
            if (!"".equals(request.getParameter("txtSecuritycode")) &&
                    isNumeric(request.getParameter("txtSecuritycode"))) {
                securityCode = Integer.parseInt(request.getParameter("txtSecuritycode"));
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Please enter a valid CVV/CVV2.");
            }
            
            if (isNumeric(request.getParameter("month"))) {
                // Debugging Purposes Only
                //out.println(request.getParameter("month"));
                
                month = Integer.parseInt(request.getParameter("month"));
                
                if (month < 0 || month > 12) {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Invalid Month Data.");
                }
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Invalid Month.");
            }
            
            if (!request.getParameter("year").equals("") && 
                    isNumeric(request.getParameter("year"))) {
                // Regex for yyyy
                // https://social.msdn.microsoft.com/Forums/en-US/58770290-0d28-47e6-8dad-fb6517f6fc38/check-if-string-contains-valid-year?forum=csharplanguage
                if (Pattern.compile("^(19|20)[0-9][0-9]") 
                        .matcher(request.getParameter("year")).matches()) {
                    // If the year is proper, set it
                    year = Integer.parseInt(request.getParameter("year"));
                } else {
                    // Else, it's bogus
                    response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                        + "?errMsg=Invalid Year Format (yyyy).");
                }
                
                // Debugging Purposes Only
                //out.println(year);
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Invalid Year.");
            }
            
            // Collate finalPrice
             for (ShoppingCartLineItem item : shoppingCart) {
                finalPrice += (item.getPrice() * item.getQuantity());
             }
            
            // Debugging Purposes Only
            //out.println("Works");
            
            // We'll now parse it to the web API
            Response paymentRowResponse = createPaymentRowAtDB(memberId,
                    finalPrice, countryId);
            
            // Debugging Purposes Only
            //out.println(paymentRowResponse.getStatus());
            
            if (paymentRowResponse.getStatus() == 200) {
                long salesRecordId = Long.parseLong(paymentRowResponse.readEntity(String.class));
                
                // Debugging Purposes Only
                //out.println("Successful Sales Entity Row Creation" + salesRecordId);
                
                // Let's begin linking the shopping cart items to the sales record
                for (ShoppingCartLineItem item : shoppingCart) {
                    // Insert the corresponding line item data to the DB
                    Response itemRowResponse = addItemToPaymentRowAtDB(salesRecordId, item);
                    
                    if (itemRowResponse.getStatus() != 200) {
                        response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                            + "?errMsg=" + itemRowResponse.readEntity(String.class));
                        return;
                    }
                    
                    // Finally, bind the lineitems with the member
                    long lineitementityId = Long.parseLong(itemRowResponse
                    .readEntity(String.class));
                    Response lineItemMemberRes = 
                            bindItemToMemberAtDB(lineitementityId, memberId);
                    
                    if (lineItemMemberRes.getStatus() != 200) {
                        response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                            + "?errMsg=" + lineItemMemberRes.readEntity(String.class));
                        return;
                    }
                }
                
                // Reset the shopping cart
                session.setAttribute("shoppingCart",
                        new ArrayList<ShoppingCartLineItem>());
                session.setAttribute("transactionId", salesRecordId);
                
                // Got to retrieve the shop information for collection
                
                // Now begin propogating back to the shopping cart.
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                            + "?goodMsg=Transaction complete. Have a nice day!");
            } else {
                out.println(paymentRowResponse.readEntity(String.class));
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=" + ex.getMessage());
        }
    }
    
    public boolean isNumeric(String str) {
          return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
    
    public Response createPaymentRowAtDB(long memberId, 
            double finalPrice, long countryId) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client
                .target("http://localhost:8080/IS3102_WebService-Student/webresources/commerce")
                .path("createECommerceTransactionRecord")
                .queryParam("finalPrice", finalPrice)
                .queryParam("countryId", countryId);
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        
        return invocationBuilder.put(Entity.entity(String.valueOf(memberId), MediaType.APPLICATION_JSON));
    }
    
    public Response addItemToPaymentRowAtDB(long salesRecordId, ShoppingCartLineItem item) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client
                .target("http://localhost:8080/IS3102_WebService-Student/webresources/commerce")
                .path("createECommerceLineItemRecord")
                .queryParam("salesRecordID", salesRecordId)
                .queryParam("itemEntityID", item.getId())
                .queryParam("quantity", item.getQuantity())
                .queryParam("countryID", item.getCountryID());
        
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        
        return invocationBuilder.put(Entity.entity(item, MediaType.APPLICATION_JSON));
    }
    
    public Response bindItemToMemberAtDB(long lineitementityId, 
            long memberId) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client
                .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.memberentity")
                .path("addlineitem")
                .queryParam("memberId", memberId);
        
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        
        return invocationBuilder.put(Entity.entity(String.valueOf(lineitementityId), MediaType.APPLICATION_JSON));
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
