package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.CurrencyController;
import model.ConversionHistory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HistoryServlet.java
 * --------------------
 * Returns conversion history for the logged-in user.
 *
 * Endpoints:
 *   GET /api/history/user         → history for current user
 *   GET /api/history/filter?from=USD&to=INR → filtered history
 *
 * OOP Concepts: Inheritance, Abstraction
 * Author: Rohit Sharma
 */
@WebServlet("/api/history/*")
public class HistoryServlet extends HttpServlet {

    private ObjectMapper       mapper     = new ObjectMapper();
    private CurrencyController controller = new CurrencyController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Must be logged in
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mapper.writeValue(resp.getOutputStream(),
                Map.of("success", false, "message", "Please login to view history."));
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = req.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        if ("/user".equals(action)) {
            // Get all history for this user
            List<ConversionHistory> list = controller.getUserHistory(userId);
            result.put("success", true);
            result.put("history", toMapList(list));

        } else if ("/filter".equals(action)) {
            // Filtered history — query params: ?from=USD&to=INR
            String from = req.getParameter("from") != null ? req.getParameter("from") : "";
            String to   = req.getParameter("to")   != null ? req.getParameter("to")   : "";

            List<ConversionHistory> list = controller.filterHistory(userId, from, to);
            result.put("success", true);
            result.put("history", toMapList(list));

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

    // -------------------------------------------------------
    // Convert a list of ConversionHistory → List of Maps (for JSON)
    // -------------------------------------------------------
    private List<Map<String, Object>> toMapList(List<ConversionHistory> list) {
        return list.stream().map(h -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id",              h.getId());
            m.put("fromCurrency",    h.getFromCurrency());
            m.put("toCurrency",      h.getToCurrency());
            m.put("amount",          h.getAmount());
            m.put("convertedAmount", h.getConvertedAmount());
            m.put("date",            h.getConversionDate());
            return m;
        }).toList();
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin",  "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
