package org.example.dao;

import org.example.constants.PaymentStatus;
import org.example.entity.Invoice;
import org.example.utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvoiceDaoImpl implements InvoiceDao {
    private static final Logger logger = Logger.getLogger(InvoiceDaoImpl.class.getName());
    private final Connection connection = DatabaseConnection.getConnection();

    @Override
    public int generateInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (booking_id, user_id, amount, issue_date, payment_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInvoiceParameters(stmt, invoice);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int invoiceId = generatedKeys.getInt(1);
                    invoice.setInvoiceId(invoiceId);
                    return invoiceId;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while generating invoice for booking ID: " + invoice.getBookingId(), e);
        }
        return -1;
    }

    @Override
    public Optional<Invoice> getInvoiceByBookingId(int bookingId) {
        String sql = "SELECT * FROM invoices WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapInvoice(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching invoice by booking ID: " + bookingId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                invoices.add(mapInvoice(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching all invoices", e);
        }
        return invoices;
    }

    @Override
    public void updatePaymentStatus(int invoiceId, PaymentStatus status) {
        String sql = "UPDATE invoices SET payment_status = ? WHERE invoice_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, status.name(), Types.OTHER);
            stmt.setInt(2, invoiceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating payment status for invoice ID: " + invoiceId, e);
        }
    }

    @Override
    public Optional<Invoice> getInvoiceById(int invoiceId) {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapInvoice(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching invoice by ID: " + invoiceId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Invoice> getInvoiceByUserId(int userId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapInvoice(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching invoices for user ID: " + userId, e);
        }
        return invoices;
    }

    // Helper method to map ResultSet to Invoice
    private Invoice mapInvoice(ResultSet rs) throws SQLException {
        return new Invoice(
                rs.getInt("invoice_id"),
                rs.getInt("booking_id"),
                rs.getInt("user_id"),
                rs.getDouble("amount"),
                rs.getTimestamp("issue_date").toLocalDateTime(),
                PaymentStatus.valueOf(rs.getString("payment_status"))
        );
    }

    // Helper method to set parameters for prepared statement
    private void setInvoiceParameters(PreparedStatement stmt, Invoice invoice) throws SQLException {
        stmt.setInt(1, invoice.getBookingId());
        stmt.setInt(2, invoice.getUserId());
        stmt.setDouble(3, invoice.getAmount());
        stmt.setTimestamp(4, Timestamp.valueOf(invoice.getIssueDate()));
        stmt.setObject(5, invoice.getPaymentStatus().name(), Types.OTHER);
    }
}
