package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.Book;
import com.nminh.quanlythuvien.entity.BookOrderTemp;
import com.nminh.quanlythuvien.model.request.BookOrderTempCreateRequestDTO;
import com.nminh.quanlythuvien.model.response.BookOrderTempCreateResponseDTO;
import com.nminh.quanlythuvien.model.response.BookOrderTempDetailDTO;
import com.nminh.quanlythuvien.repository.BookOrderTempRepository;
import com.nminh.quanlythuvien.repository.BookRepository;
import com.nminh.quanlythuvien.service.BookOrderTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookOrderTempServiceImpl implements BookOrderTempService {
    @Autowired
    private BookOrderTempRepository bookOrderTempRepository;
    @Autowired
    private BookRepository bookRepository;
    @Override
    public BookOrderTempCreateResponseDTO createBookOrderTemp(BookOrderTempCreateRequestDTO bookOrderTempCreateDTO) {
        BookOrderTemp bookOrderTemp = BookOrderTemp.builder()
                .bookId(bookOrderTempCreateDTO.getBookId())
                .userId(bookOrderTempCreateDTO.getUserId())
                .quantity(bookOrderTempCreateDTO.getQuantity())
                .status(true)
                .build();
        bookOrderTempRepository.save(bookOrderTemp);

        Book book = bookRepository.findById(bookOrderTempCreateDTO.getBookId()).get();
        book.setQuantity(book.getQuantity() - bookOrderTempCreateDTO.getQuantity());
        bookRepository.save(book);

        return BookOrderTempCreateResponseDTO.builder()
                .Id(bookOrderTemp.getId())
                .bookId(bookOrderTemp.getBookId())
                .userId(bookOrderTemp.getUserId())
                .quantity(bookOrderTemp.getQuantity())
                .status(true)
                .build();

    }

    @Override
    public List<BookOrderTempDetailDTO> getAllBookOrderTempDetail(String userId) {
        List<BookOrderTemp> bookOrderTempList = bookOrderTempRepository.findByUserId(userId);
        List<BookOrderTempDetailDTO> bookOrderTempDetailDTOList = new ArrayList<>();
        for (BookOrderTemp bookOrderTemp : bookOrderTempList) {
            if(!bookOrderTemp.isStatus()) continue;
            Book book = bookRepository.findById(bookOrderTemp.getBookId()).get();
            BookOrderTempDetailDTO bookOrderTempDetailDTO = new BookOrderTempDetailDTO();

            bookOrderTempDetailDTO.setId(bookOrderTemp.getId());
            bookOrderTempDetailDTO.setBook_id(book.getId());
            bookOrderTempDetailDTO.setBookQuantity(bookOrderTemp.getQuantity());
            bookOrderTempDetailDTO.setBookPrice(book.getPrice());
            bookOrderTempDetailDTO.setBookName(book.getBookName());
            bookOrderTempDetailDTO.setBookUrl(book.getImageUrl());
            bookOrderTempDetailDTO.setBookTotalPrice(book.getPrice()*bookOrderTemp.getQuantity());

            bookOrderTempDetailDTOList.add(bookOrderTempDetailDTO);
        }
        return bookOrderTempDetailDTOList;
    }
}
