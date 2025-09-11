package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.entity.Book;
import com.nminh.quanlythuvien.enums.ActionType;

import java.util.List;

public interface WarehouseService {
    List<Book> getAllBooks();
    void increaseQuantity(String bookId, int qty);
    void decreaseQuantity(String bookId, int qty);
    void updateBookInfo(String bookId, String newName, Double newPrice);
    void log(Book book, ActionType actionType, int preQty, int curQty, String note);

}
