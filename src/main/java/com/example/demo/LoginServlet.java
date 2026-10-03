package com.example.demo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session != null && session.getAttribute("nombre") != null) {
           resp.sendRedirect("DashboardServlet");
           return;
        }
        mostrarFormulario(resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombre = req.getParameter("nombre");
        String clave = req.getParameter("clave");
        String edad = req.getParameter("edad");
        String programa = req.getParameter("programa");
        String sexo = req.getParameter("sexo");

        if (nombre == null || clave == null || edad == null || programa == null || sexo == null) {
            mostrarFormulario(resp, "Error, No debe haber campos vacíos.");
            return;
        }

        nombre = nombre.trim();
        clave = clave.trim();
        edad = edad.trim();
        programa = programa.trim();
        sexo = sexo.trim();

        if(!nombre.matches("^[\\p{L}\\p{M}]+([ \\-'][\\p{L}\\p{M}]+){1,3}$")) {
            mostrarFormulario(resp, "Error, Ingrese entre 2 y 4 nombres válidos.");
            return;
        }

        if(!clave.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#$%@&!+=^]).{8,}$")) {
            mostrarFormulario(resp, "Error, La clave debe incluir mínimo 8 caracteres(1 Mayúscula, 1 Minúscula," +
                    "1 Número y 1 Carácter Especial.");
            return;
        }

        if(!(programa.equals("Ingeníeria") || programa.equals("Licenciatura") || programa.equals("Ciencias B")
                || programa.equals("Humanidades"))) {
            mostrarFormulario(resp, "Error, Programa seleccionado no válido.");
            return;
        }

        int edadNumber;
        try {
            edadNumber = Integer.parseInt(edad);
            if(edadNumber < 18 || edadNumber > 100) {
                mostrarFormulario(resp, "Error, La edad debe estar entre 18 y 100.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarFormulario(resp, "Error, Debe ingresar un número válido.");
            return;
        }

        if(!(sexo.equals("true") || sexo.equals("false"))) {
            mostrarFormulario(resp, "Error, Sexo seleccionado no válido.");
            return;
        }

        boolean sexBoolean = Boolean.parseBoolean(sexo);
        String sexoString = sexBoolean ? "Masculino" : "Femenino";

        HttpSession session = req.getSession();
        session.setAttribute("nombre", nombre);
        session.setAttribute("rol", "Usuario");
        session.setMaxInactiveInterval(60 * 15); // Tiempo de Vida de la Sesión: 15 minutos.

        // Creación de Cookies
        Cookie nombreCookie =  new Cookie("nombre_cookie", URLEncoder.encode(nombre, StandardCharsets.UTF_8));
        Cookie edadCookie = new Cookie("edad_cookie", edad);
        Cookie programaCookie = new Cookie("programa_cookie", URLEncoder.encode(programa, StandardCharsets.UTF_8));
        Cookie sexoCookie = new Cookie("sexo_cookie", sexoString);
        Cookie horaCookie = new Cookie("hora_cookie", String.valueOf(System.currentTimeMillis()));

        // Almacenamiento de Cookies
        Cookie[] cookies = {nombreCookie, edadCookie, programaCookie, sexoCookie, horaCookie};
        for(Cookie cookie: cookies) {
            cookie.setMaxAge(60 * 30);
            cookie.setHttpOnly(true);
            resp.addCookie(cookie);
        }

        resp.sendRedirect("DashboardServlet");
    }

    private void mostrarFormulario(HttpServletResponse resp, String error) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.print("<html><head><title>Inicio de Sesión</title></head>");
        out.print("<body style='display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; font-family: Arial, sans-serif; margin: 40px; background-color: #f4f4f9;'>");
        out.print("<div style='min-width: 400px; max-width: 800px; padding: 20px; border: 1px solid #ccc; border-radius: 10px;'>");
        out.print("<h2 style='text-align: center; color: #000;'>Acceso al Sistema</h2>");
        if(error != null) {
            out.print("<p style='color: #D8000C; background-color: #FFBABA; padding: 10px; border-radius: 5px; font-size: 14px;'>"
            + error + "</p>");
        }
        out.print("<form action='LoginServlet' method='POST'>");
        // Nombre
        out.print("<div style='margin-bottom: 15px;'><label style='font-weight: bold; font-size: 14px;'>Nombre</label><br>");
        out.print("<input type='text' name='nombre' required style='width: 100%; border:1px solid #ccc;'></div>");
        // Clave
        out.print("<div style='margin-bottom: 15px;'><label style='font-weight: bold; font-size: 14px;'>Clave</label><br>");
        out.print("<input type='password' name='clave' required style='width: 100%; border:1px solid #ccc;'></div>");
        // Edad
        out.print("<div style='margin-bottom: 15px;'><label style='font-weight: bold; font-size: 14px;'>Edad</label><br>");
        out.print("<input type='number' min=1 max=100 name='edad' required style='width: 100%; border:1px solid #ccc;'></div>");
        // Programa
        out.print("<div style='margin-bottom: 15px;'><label style='font-weight: bold; font-size: 14px;'>Programa</label><br>");
        out.print("<select name='programa' required style='width: 100%; border:1px solid #ccc;'>");
        out.print("<option value='' selected disabled>-- Selecciona Aquí --</option>");
        out.print("<option value='Ingeníeria'>Ingeníeria</option>");
        out.print("<option value='Licenciatura'>Licenciatura</option>");
        out.print("<option value='Ciencias B'>Ciencias Básicas</option>");
        out.print("<option value='Humanidades'>Humanidades</option>");
        out.print("</select></div>");
        // Sexo
        out.print("<label style='font-weight: bold; font-size: 14px;'>Sexo</label><br>");
        out.print("<div style='display: flex; justify-content: space-around; align-items: center; margin-bottom: 15px;'>");
        out.print("<label><input type='radio' name='sexo' required value='true'>Masculino</label>");
        out.print("<label><input type='radio' name='sexo' value='false'>Femenino</label>");
        out.print("</div>");
        // Envío
        out.print("<input type='submit' value='Ingresar' style='padding: 10px; width: 100%; background-color: #007BFF; color: white; border: none; cursor: pointer;'>");
        out.print("</form></div></body></html>");
        out.close();
    }
}
