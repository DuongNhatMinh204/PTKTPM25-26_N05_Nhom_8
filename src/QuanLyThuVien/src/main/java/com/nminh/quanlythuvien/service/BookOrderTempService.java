package com.nminh.quanlythuvien.service;

import com.nminh.quanlythuvien.model.request.BookOrderTempCreateRequestDTO;
import com.nminh.quanlythuvien.model.response.BookOrderTempCreateResponseDTO;
import com.nminh.quanlythuvien.model.response.BookOrderTempDetailDTO;

import java.util.List;

public interface BookOrderTempService {
    BookOrderTempCreateResponseDTO createBookOrderTemp(BookOrderTempCreateRequestDTO bookOrderTempCreateDTO);
    List<BookOrderTempDetailDTO> getAllBookOrderTempDetail(String userId);
}
