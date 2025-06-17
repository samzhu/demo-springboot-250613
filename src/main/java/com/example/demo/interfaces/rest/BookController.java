package com.example.demo.interfaces.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.applications.BookService;
import com.example.demo.interfaces.api.BooksApi;
import com.example.demo.interfaces.dto.BookDto;
import com.example.demo.interfaces.dto.BookRequest;
import com.example.demo.interfaces.mapper.BookMapper;
import com.example.demo.models.Book;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Tracer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 書本控制器
 * Book - 資料庫實體 (com.example.demo.models.Book)
 * BookDto - API 響應 DTO (com.example.demo.interfaces.dto.BookDto)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController implements BooksApi {

    private final BookService bookService;
    private final BookMapper bookMapper;
    private final Tracer tracer;

    @Override
    public ResponseEntity<List<BookDto>> booksGet() throws Exception {
        log.info("獲取所有書本");
        List<Book> books = bookService.getAllBooks();
        List<BookDto> bookDtos = books.stream()
            .map(bookMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(bookDtos);
    }

    @Override
    public ResponseEntity<Void> booksIdDelete(Integer id) throws Exception {
        log.info("刪除書本，ID: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BookDto> booksIdGet(Integer id) throws Exception {
        log.info("獲取書本，ID: {}", id);
        this.setBookIdInBaggage(id);
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(bookMapper.toDto(book));
    }

    @Override
    public ResponseEntity<BookDto> booksIdPut(Integer id, @Valid BookRequest bookRequest) throws Exception {
        log.info("更新書本，ID: {}, 請求資料: {}", id, bookRequest);
        this.setBookIdInBaggage(id);
        Book bookEntity = bookMapper.toEntity(bookRequest);
        Book updatedBook = bookService.updateBook(id, bookEntity);
        return ResponseEntity.ok(bookMapper.toDto(updatedBook));
    }

    @Override
    public ResponseEntity<BookDto> booksPost(@Valid BookRequest bookRequest) throws Exception {
        log.info("新增書本，請求資料: {}", bookRequest);
        Book bookEntity = bookMapper.toEntity(bookRequest);
        Book createdBook = bookService.createBook(bookEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.toDto(createdBook));
    }


    private void setBookIdInBaggage(Integer bookId) {
        if (bookId == null) {
            return;
        }
        // 根據 application.yml 中設定的名稱 "book-id" 獲取 BaggageField
        Baggage baggage = tracer.getBaggage("book-id");
        if (baggage!= null) {
            // 設定 Baggage 的值
            baggage.makeCurrent(bookId.toString());
            log.info("Baggage 'book-id' 已設定為: {}", baggage.get());
        } else {
            log.warn("Baggage 欄位 'book-id' 未設定或未啟用。");
        }
    }
}
