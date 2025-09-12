package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.Book;
import com.nminh.quanlythuvien.entity.WarehouseLog;
import com.nminh.quanlythuvien.enums.ActionType;
import com.nminh.quanlythuvien.repository.BookRepository;
import com.nminh.quanlythuvien.repository.WarehouseLogRepository;
import com.nminh.quanlythuvien.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WarehouseLogRepository warehouseLogRepository;

    // Lấy danh sách tất cả sách trong kho
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Tăng số lượng sách
    public void increaseQuantity(String bookId, int qty) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + bookId));

        int previousQty = book.getQuantity();
        int newQty = previousQty + qty;

        book.setQuantity(newQty);
        bookRepository.save(book);

        log(book, ActionType.IMPORT, previousQty, newQty, "Nhập thêm sách vào kho");
    }

    // Giảm số lượng sách
    public void decreaseQuantity(String bookId, int qty) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + bookId));

        int previousQty = book.getQuantity();
        if (previousQty < qty) {
            throw new RuntimeException("Không đủ số lượng sách để xuất kho");
        }

        int newQty = previousQty - qty;
        book.setQuantity(newQty);
        bookRepository.save(book);

        log(book, ActionType.EXPORT, previousQty, newQty, "Xuất bớt sách khỏi kho");
    }

    // Cập nhật tên và giá sách
    public void updateBookInfo(String bookId, String newName, Double newPrice) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + bookId));

        int currentQty = book.getQuantity();

        book.setBookName(newName);
        book.setPrice(newPrice);
        bookRepository.save(book);

        log(book, ActionType.UPDATE, currentQty, currentQty, "Cập nhật thông tin sách");
    }

    // Ghi log vào WarehouseLog
    public void log(Book book, ActionType actionType, int preQty, int curQty, String note) {
        WarehouseLog logEntry = WarehouseLog.builder()
                .book(book)
                .actionType(actionType)
                .preQuantity(preQty)
                .currentQuantity(curQty)
                .note(note)
                .build();

        warehouseLogRepository.save(logEntry);
    }
}
