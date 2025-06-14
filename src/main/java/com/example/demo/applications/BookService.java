package com.example.demo.applications;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.infrastructure.repositories.BookRepository;
import com.example.demo.models.Book;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    
    /**
     * 獲取所有書本
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * 根據 ID 獲取書本
     */
    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的書本"));
    }

    /**
     * 新增書本
     */
    @Transactional
    public Book createBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN 已存在");
        }

        book.setCreatedAt(OffsetDateTime.now());
        book.setUpdatedAt(OffsetDateTime.now());
        return bookRepository.save(book);
    }

    /**
     * 更新書本
     */
    @Transactional
    public Book updateBook(Integer id, Book book) {
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的書本"));

        if (!existingBook.getIsbn().equals(book.getIsbn()) && 
            bookRepository.existsByIsbn(book.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN 已存在");
        }

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setPrice(book.getPrice());
        existingBook.setUpdatedAt(OffsetDateTime.now());

        return bookRepository.save(existingBook);
    }

    /**
     * 刪除書本
     */
    @Transactional
    public void deleteBook(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的書本");
        }
        bookRepository.deleteById(id);
    }
}
