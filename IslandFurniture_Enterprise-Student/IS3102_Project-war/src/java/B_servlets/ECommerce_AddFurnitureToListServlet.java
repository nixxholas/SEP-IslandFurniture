/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import HelperClasses.Member;
import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author nixholas
 */
@WebServlet(name = "ECommerce_AddFurnitureToListServlet", urlPatterns = {"/ECommerce_AddFurnitureToListServlet"})
public class ECommerce_AddFurnitureToListServlet extends HttpServlet {

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
        
        String category = (String) session.getAttribute("cat");
        //String country = (String) session.getAttribute("memberCountry");
        
        try {
            long countryId = 0;
            int quantity = 0;
            
            if (session.getAttribute("countryID") != null) {
                countryId = (long) session.getAttribute("countryID");
            } else {
                response.sendRedirect("/B/selectCountry.jsp");
                return;
            }
            
            if (request.getParameter("SKU").equals("")) {
                response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory.jsp"
                    + "?cat=" + URLEncoder.encode(category, "UTF=8")
                    + "&errMsg=Invalid SKU Code.");
            } 
            String sku = (String) request.getParameter("SKU");
            //out.println(id);
            
            quantity = retrieveQuantity(sku);
            
            // Before we add anything to the user's cart, we'll check for the
            // stock first.
            if (quantity > 0) {
                //out.println("Item is in stock");
                ArrayList<ShoppingCartLineItem> shoppingCart;
                
                // We'll have to add the item to the user's cart now.
                //String memberEmail = (String) session.getAttribute("memberEmail");
                
                // Create the item
                ShoppingCartLineItem item = new ShoppingCartLineItem();
                item.setId(request.getParameter("id"));
                item.setSKU(sku);
                item.setPrice(Double.parseDouble(request.getParameter("price")));
                item.setName(request.getParameter("name"));
                item.setImageURL(request.getParameter("imageURL"));
                item.setCountryID(countryId);
                
                // Check the Cart to see if it (both the cart and the item) exists
                if (session.getAttribute("shoppingCart") != null) {
                    // Since the shopping cart exists
                    shoppingCart = (ArrayList<ShoppingCartLineItem>) session.getAttribute("shoppingCart");
                    
                    if (shoppingCart.contains(item)) {
                        // Self explanatory code
                        for (int i = 0; i < shoppingCart.size(); i++) {
                            ShoppingCartLineItem currItem = shoppingCart.get(i);
                            if (currItem.equals(item)) {
                                if ((currItem.getQuantity() + 1) <= quantity) {
                                    currItem.setQuantity(currItem.getQuantity() + 1);
                                } else {
                                    // Encoding URLs the new way
                                    // http://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters
                                    response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory.jsp"
                                        // When we need to include more than one parameter in the URL
                                        // https://coderanch.com/t/289258/java/passing-variables-response-sendRedirect
                                        + "?cat=" + URLEncoder.encode(category)//, "UTF=8")
                                        + "&errMsg=There is insufficient stock for your request.");
                                }
                                break;
                            }
                        }
                    } else {
                        // If things end up here it means that the arraylist already
                        // existed but the code wasn't capable of handling it
                    
                        item.setQuantity(1);
                        shoppingCart.add(item);
                    }
                } else {
                    // Create the shopping cart
                    shoppingCart = new ArrayList();
                    
                    // Simply add the item to the shopping cart since this is
                    // the first item
                    item.setQuantity(1);
                    shoppingCart.add(item);
                }
                
                // Remember to return the cart
                session.setAttribute("shoppingCart", shoppingCart);
                
                // Redirect the user to the cart and display success
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?goodMsg=" + item.getName() + " added successfully to your cart.");
                //+ country);
            } else {
                // Since there isn't any stocks left available, send the user
                // back.
                //out.println("Something happened: " + itemIsAvailable(id,sku));
                
                // Encoding URLs the new way
                // http://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters
                response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory.jsp"
                    // When we need to include more than one parameter in the URL
                    // https://coderanch.com/t/289258/java/passing-variables-response-sendRedirect
                    + "?cat=" + URLEncoder.encode(category)//, "UTF=8")
                    + "&errMsg=There aren't any stocks left.");
            }
        } catch (IOException | NumberFormatException ex) {
            // out.println(ex.toString());
            response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory.jsp"
                    + "?cat=" + URLEncoder.encode(category, "UTF-8")
                    + "?errMsg=" + ex.toString());
        }
    }
    
    /**
     * itemIsAvailable, devised to check for the stock amounts relevant to
     * the ECommerce Store
     * 
     * In this instance, storeID is not required due to the fact that this is
     * only used for the ECommerce Store entity.
     * 
     * @param sku
     * This is the product's UID.
     * @return 
     */
    public int retrieveQuantity(String sku) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client
                .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.storeentity")
                .path("getQuantity")
                .queryParam("storeID", 59)
                .queryParam("SKU", sku);
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        
        String qtyStr = response.readEntity(String.class);
        int qty = Integer.parseInt(qtyStr);
        //System.out.println("status: " + response.getStatus());
        return qty;
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
