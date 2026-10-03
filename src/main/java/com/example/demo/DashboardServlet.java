package com.example.demo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session == null || session.getAttribute("nombre") == null) {
            resp.sendRedirect("LoginServlet");
            return;
        }

        String usuario = (String) session.getAttribute("nombre");
        String rol = (String) session.getAttribute("rol");

        resp.setContentType("text/html; UTF-8");
        PrintWriter out = resp.getWriter();

        out.print("<html><head><title>Panel de Información</title></head>");
        out.print("<body style='font-family: Arial, sans-serif; margin: 0px; display: flex; justify-content: center; align-items: center; height: 100vh;'>");
        out.print("<div style='min-width: 400px; max-width: 800px; padding: 20px; border: 1px solid #000; border-radius: 10px;'>");
        out.print("<h2 style='color: #00F; display: flex; justify-content: center; align-items: center;'>Bienvenido al Panel de Información</h2>");
        out.print("<div><h3>Cookies del Navegador</h3>");
        out.print("<ul>");
        Cookie[] cookies = req.getCookies();
        if( cookies != null) {
            for(Cookie cookie: cookies) {
                out.print("<li><b>" + cookie.getName() + ":</b> " + URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8) + " </li>");
            }
        }
        out.print("</div>");
        out.print("<form action='LogoutServlet' method='GET'>");
        out.print("<input type='submit' value='Cerrar Sesión' style='padding: 10px; width: 100%; background-color: #007BFF; color: white; cursor: pointer; border: none'>");
        out.print("</form></div></body></html>");
    }
}
