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
@WebServlet(name = "ECommerce_MinusFurnitureToListServlet", urlPatterns = {"/ECommerce_MinusFurnitureToListServlet"})
public class ECommerce_MinusFurnitureToListServlet extends HttpServlet {

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
        
        // Debugging Only
        //out.println(request.getParameter("SKU"));
        try {
            if (!"".equals(request.getParameter("SKU"))) {
                String sku = request.getParameter("SKU");
                int qty = 0;
                ShoppingCartLineItem item = new ShoppingCartLineItem();
                
                // Iterate through the cart to minus the item from the cart
                ArrayList<ShoppingCartLineItem> shoppingCart;
                
                if (session.getAttribute("shoppingCart") != null) {
                    shoppingCart = (ArrayList<ShoppingCartLineItem>) 
                            session.getAttribute("shoppingCart");
                    
                    for (ShoppingCartLineItem i : shoppingCart) {
                        if (i.getSKU().equals(sku)) {
                            if (i.getQuantity() == 1) {
                                item.setName(i.getName()); // Need this for Message
                                shoppingCart.remove(i);
                                break;
                            }
                            qty = i.getQuantity() - 1;
                            i.setQuantity(qty);
                            item = i;
                            break;
                        }
                    }
                    
                // Remember to return the cart
                session.setAttribute("shoppingCart", shoppingCart);
                
                // Redirect the user to the cart and display success
                if (qty != 0) {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?goodMsg=1 " + item.getName() + " removed successfully from your cart.");
                } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?goodMsg=" + item.getName() + " removed successfully from your cart.");
                }
              }
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Invalid SKU.");
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
