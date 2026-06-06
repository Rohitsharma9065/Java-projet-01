package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.CurrencyController;
import model.ConversionHistory;
import model.Currency;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CurrencyServlet.java
 * ---------------------
 * Handles currency listing and conversion requests.
 *
 * Endpoints:
 *   GET  /api/currency/list         → return all currencies as JSON
 *   POST /api/currency/convert      → perform conversion + save history
 *
 * OOP Concepts: Inheritance (extends HttpServlet), Abstraction
 * Author: Rohit Sharma
 */
@WebServlet("/api/currency/*")
public class CurrencyServlet extends HttpServlet {

    private ObjectMapper       mapper     = new ObjectMapper();
    private CurrencyController controller = new CurrencyController();

    // -------------------------------------------------------
    // GET: list all currencies
    // -------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        if ("/list".equals(action)) {
            // Fetch all currencies from DB
            List<Currency> currencies = controller.getAllCurrencies();

            // Convert each Currency object to a simple Map so Jackson can serialise it
            result.put("success",    true);
            result.put("currencies", currencies.stream().map(c -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id",   c.getId());
                m.put("code", c.getCurrencyCode());
                m.put("name", c.getCurrencyName());
                m.put("rate", c.getExchangeRate());
                return m;
            }).toList());

        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result.put("success", false);
            result.put("message", "Unknown endpoint.");
        }

        mapper.writeValue(resp.getOutputStream(), result);
    }

    // -------------------------------------------------------
    // POST: convert currency
    // -------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setCorsHeaders(resp);
        resp.setContentType("application/json");

        String action = req.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        if ("/convert".equals(action)) {

            @SuppressWarnings("unchecked")
            Map<String, Object> body = mapper.readValue(req.getInputStream(), Map.class);

            String fromCode = (String) body.get("fromCurrency");
            String toCode   = (String) body.get("toCurrency");
            double amount   = Double.parseDouble(body.get("amount").toString());

            // Get user ID from session (0 means guest)
            HttpSession session = req.getSession(false);
            int userId = (session != null && session.getAttribute("userId") != null)
                         ? (int) session.getAttribute("userId") : 0;

            String msg = controller.convertAndSave(userId, fromCode, toCode, amount);
            boolean success = msg.startsWith("SUCCESS");

            if (!success) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            result.put("success", success);
            result.put("message", msg.contains(": ") ? msg.split(": ", 2)[1] : msg);

            // Also return the numeric result so the frontend can display it
            if (success) {
                double converted = controller.convert(fromCode, toCode, amount);
                result.put("convertedAmount", converted);
                result.put("fromCurrency",    fromCode);
                result.put("toCurrency",      toCode);
                result.put("amount",          amount);
            }

        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result.put("success", false);
            result.put("message", "Unknown endpoint.");
        }

        mapper.writeValue(resp.getOutputStream(), result);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin",  "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
