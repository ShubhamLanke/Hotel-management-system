package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.example.constants.PaymentStatus;
import org.example.dao.InvoiceDao;
import org.example.entity.Booking;
import org.example.entity.Invoice;
import org.example.entity.User;
import org.example.exception.ServiceException;

import java.util.List;
import java.util.Optional;

@Log4j2
public class InvoiceServiceImpl implements InvoiceService {
    private final UserService userService;
    private final BookingService bookingService;

    private final InvoiceDao invoiceDao;

    public InvoiceServiceImpl(InvoiceDao invoiceDao, UserService userService, BookingService bookingService) {
        this.invoiceDao = invoiceDao;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @Override
    public int generateInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new ServiceException("Invoice cannot be null.");
        }
        if (invoice.getAmount() <= 0) {
            throw new ServiceException("Invalid invoice amount.");
        }
        return invoiceDao.generateInvoice(invoice);
    }

    @Override
    public Optional<Invoice> getInvoiceByBookingId(int bookingId) {
        if (bookingId <= 0) {
            return Optional.empty();
        }
        Optional<Booking> booking = bookingService.getBookingById(bookingId);
        if (!booking.isPresent()){
            log.info("No booking found for id {}.",bookingId);
            return Optional.empty();
        }
        return invoiceDao.getInvoiceByBooking(booking.get());
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceDao.getAllInvoices();
    }

    @Override
    public boolean updatePaymentStatus(int invoiceId, PaymentStatus status) {
        if (invoiceId <= 0) {
            throw new ServiceException("Invalid invoice ID.");
        }
        if (status == null) {
            throw new ServiceException("Payment status cannot be null.");
        }
        invoiceDao.updatePaymentStatus(invoiceId, status);
        return false;
    }

    @Override
    public Optional<Invoice> getInvoiceById(int invoiceId) {
        if (invoiceId <= 0) {
            throw new ServiceException("Invalid invoice ID.");
        }
        return invoiceDao.getInvoiceById(invoiceId);
    }

    @Override
    public List<Invoice> getInvoiceByUserId(int userID) {
        if (userID <= 0) {
            throw new ServiceException("Invalid user ID.");
        }
        Optional<User> user = userService.getUserById(userID);
        return invoiceDao.getInvoiceByUserId(user.get());
    }
}
