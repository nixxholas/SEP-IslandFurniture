/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author nixholas
 */
@WebServlet(name = "ECommerce_RemoveItemFromListServlet", urlPatterns = {"/ECommerce_RemoveItemFromListServlet"})
public class ECommerce_RemoveItemFromListServlet extends HttpServlet {

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
        
        // For Debugging Purposes
        //out.println(request.getParameterValues("delete").length);
        
        try {
            // Retrieve the checkbox data first
            // http://stackoverflow.com/questions/10136062/get-selected-rows-from-a-html-table-using-servlets-and-jsp
            String[] selected = request.getParameterValues("delete");
            
            // For Debugging Purposes.
            // Gotta know what these strings store
            // for (String s : selected) {
            //     out.println(s);
            // }
            
            // We'll first have to retrieve the cart and check it through
            ArrayList<ShoppingCartLineItem> shoppingCart;
                
            if (session.getAttribute("shoppingCart") != null) {
                // Since the shopping cart exists,
                shoppingCart = (ArrayList<ShoppingCartLineItem>) 
                        session.getAttribute("shoppingCart");
                
                // Self Explanatory Checks and deletion of item.
                
                // This code causes ConcurrentModificationException
                // Thorough Explanation on the exception here:
                // http://stackoverflow.com/questions/13807092/error-java-util-concurrentmodificationexception
                // for (ShoppingCartLineItem i : shoppingCart) {
                //    for (String s : selected) {
                //        if (s.equals(i.getSKU())) {
                //            shoppingCart.remove(i);
                //        }
                //    }
                // }
                
                // Instead, we loop and add whatever we want to delete first
                // ArrayList<ShoppingCartLineItem> cartItemsToDelete = 
                //       new ArrayList();
                
                // for (ShoppingCartLineItem i : shoppingCart) {
                //    for (String s : selected) {
                //        if (s.equals(i.getSKU())) {
                //           cartItemsToDelete.add(i);
                //        }
                //    }
                // }
                
                // for (ShoppingCartLineItem i : cartItemsToDelete) {
                //    shoppingCart.remove(i);
                // }
                
                // This logic reduces latency by removing an additional for loop
                // for (int i = 0; i < shoppingCart.size(); i++) {
                //    ShoppingCartLineItem item = shoppingCart.get(i);
                //    
                //    for (String s : selected) {
                //        if (s.equals(item.getSKU())) {
                //            shoppingCart.remove(item);
                //        }
                //    }
                //}
                
                for (String s : selected) {
                    for (int i = 0; i < shoppingCart.size(); i++) {
                        if (shoppingCart.get(i).getSKU().equals(s)) {
                            shoppingCart.remove(i);
                        }   
                    }
                }
                
                // Remember to return the cart
                session.setAttribute("shoppingCart", shoppingCart);
                
                // Redirect the user and return a success message
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?goodMsg=" + selected.length + " items have been removed.");
            } else {
                // Redirect the user and return an error message
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=There is nothing in the cart.");
            }
        } catch (Exception ex) {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=" + ex.toString());
        }
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
