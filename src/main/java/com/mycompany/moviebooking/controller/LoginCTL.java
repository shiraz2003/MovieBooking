package com.mycompany.moviebooking.controller;

import com.mycompany.moviebooking.utility.JDBCDataSource;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet(name = "login", urlPatterns = {"/login"})
public class LoginCTL extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve login form parameters
        String identifier = request.getParameter("identifier"); // Username or Email
        String password = request.getParameter("password");     // Password

        try (Connection con = JDBCDataSource.getConnection()) {
            // SQL query to validate user login
            String sql = "SELECT user_id, role FROM users WHERE (username = ? OR email = ?) AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            ps.setString(3, password); // Note: Hash password in production
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Login success: Set session attributes
                HttpSession session = request.getSession();
                session.setAttribute("user_id", rs.getInt("user_id"));
                session.setAttribute("role", rs.getString("role"));

                // Redirect to the home page
                response.sendRedirect("./");
            } else {
                // Login failed: Redirect back to login page with error message
                response.sendRedirect("login?error=true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Internal error: Redirect to error page or show error message
            response.sendRedirect("login?error=internal");
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user login authentication";
    }// </editor-fold>

}
