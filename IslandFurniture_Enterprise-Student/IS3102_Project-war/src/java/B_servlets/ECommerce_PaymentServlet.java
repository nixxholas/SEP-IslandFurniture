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
import java.util.regex.Pattern;
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
            String name = "";
            String cardNo = "";
            //String countryId = (String) session.getAttribute("URLprefix");
            int securityCode;
            int month, year;
            double finalPrice = 0.00;
            ArrayList<ShoppingCartLineItem> shoppingCart = null;
            
            if (!"".equals(request.getParameter("txtName")) && 
                    request.getParameter("txtName") != null) {
                name = request.getParameter("txtName");
            } else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=Please enter a valid name.");
            }
            
            if (!"".equals(request.getParameter("txtName")) && 
                    request.getParameter("txtName") != null) {
                cardNo = request.getParameter("txtCardNo");
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
//            for (ShoppingCartLineItem item : shoppingCart) {
//                finalPrice += (item.getPrice() * item.getQuantity());
//            }
            
            // Debugging Purposes Only
            out.println("Works");
            // session.getAttribute("URLprefix")
            
            // 
            
        } catch (Exception ex) {
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp"
                    + "?errMsg=" + ex.toString());
        }
    }
    
    public static boolean isNumeric(String str)
        {
          return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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
