package org.example.dao;

import org.example.constants.PaymentStatus;
import org.example.entity.Invoice;
import org.example.utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDaoImpl implements InvoiceDao {
    private final Connection connection = DatabaseConnection.getConnection();

    @Override
    public int generateInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (booking_id, user_id, amount, issue_date, payment_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, invoice.getBookingId());
            stmt.setInt(2,invoice.getUserId());
            stmt.setDouble(3, invoice.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(invoice.getIssueDate()));
            stmt.setObject(5, invoice.getPaymentStatus().name(), Types.OTHER);
            stmt.executeUpdate();

            // Retrieve generated invoice ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int invoiceId = generatedKeys.getInt(1);
                    invoice.setInvoiceId(invoiceId);
                    return invoiceId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public Invoice getInvoiceByBookingId(int bookingId) {
        String sql = "SELECT * FROM invoices WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Invoice(
                        rs.getInt("invoice_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        PaymentStatus.valueOf(rs.getString("payment_status"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getInt("Invoice_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        PaymentStatus.valueOf(rs.getString("status"))
                );
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Override
    public Invoice getInvoiceById(int invoiceId) {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Invoice(
                        rs.getInt("invoice_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        PaymentStatus.valueOf(rs.getString("payment_status"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Invoice> getInvoiceByUserId(int userId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getInt("invoice_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        PaymentStatus.valueOf(rs.getString("payment_status"))
                );
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

}
