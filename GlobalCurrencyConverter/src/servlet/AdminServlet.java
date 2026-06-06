package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.AdminController;
import model.Currency;
import model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminServlet.java
 * ------------------
 * Handles admin panel requests.
 *
 * Endpoints:
 *   GET  /api/admin/currencies      → list all currencies
 *   GET  /api/admin/users           → list all users
 *   GET  /api/admin/reports         → all conversion history
 *   POST /api/admin/addCurrency     → add a new currency
 *   POST /api/admin/updateRate      → update exchange rate
 *   POST /api/admin/deleteCurrency  → delete a currency
 *
 * Security: Only users with role="admin" can call these.
 * OOP Concepts: Inheritance, Encapsulation
 * Author: Rohit Sharma
 */
@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {

    private ObjectMapper    mapper     = new ObjectMapper();
    private AdminController controller = new AdminController();

    // -------------------------------------------------------
    // GET endpoints
    // -------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setCorsHeaders(resp);
        resp.setContentType("application/json");

        // Security check — only admins allowed
        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            mapper.writeValue(resp.getOutputStream(),
                Map.of("success", false, "message", "Admin access required."));
            return;
        }

        String action = req.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        if ("/currencies".equals(action)) {
            List<Currency> list = controller.getAllCurrencies();
            result.put("success",    true);
            result.put("currencies", list.stream().map(c -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id",   c.getId());
                m.put("code", c.getCurrencyCode());
                m.put("name", c.getCurrencyName());
                m.put("rate", c.getExchangeRate());
                return m;
            }).toList());

        } else if ("/users".equals(action)) {
            List<User> users = controller.getAllUsers();
            result.put("success", true);
            result.put("users", users.stream().map(u -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id",        u.getId());
                m.put("name",      u.getName());
                m.put("email",     u.getEmail());
                m.put("role",      u.getRole());
                m.put("lastLogin", u.getLastLogin());
                return m;
            }).toList());

        } else if ("/reports".equals(action)) {
            result.put("success", true);
            result.put("history", controller.getConversionReport().stream().map(h -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id",              h.getId());
                m.put("userName",        h.getUserName());
                m.put("fromCurrency",    h.getFromCurrency());
                m.put("toCurrency",      h.getToCurrency());
                m.put("amount",          h.getAmount());
                m.put("convertedAmount", h.getConvertedAmount());
                m.put("date",            h.getConversionDate());
                return m;
            }).toList());

        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result.put("success", false);
            result.put("message", "Unknown admin endpoint.");
        }

        mapper.writeValue(resp.getOutputStream(), result);
    }

    // -------------------------------------------------------
    // POST endpoints
    // -------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setCorsHeaders(resp);
        resp.setContentType("application/json");

        if (!isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            mapper.writeValue(resp.getOutputStream(),
                Map.of("success", false, "message", "Admin access required."));
            return;
        }

        String action = req.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<String, Object> body = mapper.readValue(req.getInputStream(), Map.class);

        if ("/addCurrency".equals(action)) {
            String code = (String) body.get("code");
            String name = (String) body.get("name");
            double rate = Double.parseDouble(body.get("rate").toString());

            String msg     = controller.addCurrency(code, name, rate);
            boolean success = msg.startsWith("SUCCESS");
            if (!success) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", success);
            result.put("message", msg.contains(": ") ? msg.split(": ",2)[1] : msg);

        } else if ("/updateRate".equals(action)) {
            String code    = (String) body.get("code");
            double newRate = Double.parseDouble(body.get("rate").toString());

            String msg     = controller.updateExchangeRate(code, newRate);
            boolean success = msg.startsWith("SUCCESS");
            if (!success) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", success);
            result.put("message", msg.contains(": ") ? msg.split(": ",2)[1] : msg);

        } else if ("/deleteCurrency".equals(action)) {
            String code = (String) body.get("code");

            String msg     = controller.deleteCurrency(code);
            boolean success = msg.startsWith("SUCCESS");
            if (!success) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", success);
            result.put("message", msg.contains(": ") ? msg.split(": ",2)[1] : msg);

        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result.put("success", false);
            result.put("message", "Unknown admin endpoint.");
        }

        mapper.writeValue(resp.getOutputStream(), result);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // -------------------------------------------------------
    // Helper: check if current session user is admin
    // -------------------------------------------------------
    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String role = (String) session.getAttribute("role");
        return "admin".equalsIgnoreCase(role);
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin",  "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
