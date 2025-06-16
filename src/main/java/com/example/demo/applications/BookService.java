package com.example.demo.applications;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.config.CacheConfig;
import com.example.demo.infrastructure.repositories.BookRepository;
import com.example.demo.models.Book;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 書本服務類別
 * 提供書本相關的業務邏輯操作
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    
    /**
     * 獲取所有書本
     * @Cacheable - 將結果存入快取，下次相同請求直接從快取返回
     */
    @Cacheable(cacheNames = CacheConfig.BOOKS_CACHE)
    @Observed(name = "books.get_all", contextualName = "service.get_all_books")
    public List<Book> getAllBooks() {
        log.info("從資料庫獲取所有書本");
        return bookRepository.findAll();
    }

    /**
     * 根據 ID 獲取書本
     * @Cacheable - 使用書本 ID 作為快取鍵值
     */
    @Cacheable(cacheNames = CacheConfig.BOOKS_CACHE, key = "#id")
    @Observed(name = "books.get_by_id", contextualName = "service.get_book_by_id")
    public Book getBookById(Integer id) {
        log.info("從資料庫獲取書本 ID: {}", id);
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的書本"));
    }

    /**
     * 新增書本
     * @CacheEvict - 新增書本時清除所有快取
     */
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.BOOKS_CACHE, allEntries = true)
    @Observed(name = "books.update", contextualName = "service.update_book")
    public Book createBook(Book book) {
        log.info("新增書本: {}", book.getTitle());
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN 已存在");
        }

        book.setCreatedAt(OffsetDateTime.now());
        book.setUpdatedAt(OffsetDateTime.now());
        return bookRepository.save(book);
    }

    /**
     * 更新書本
     * @CacheEvict - 更新書本時清除所有快取
     */
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.BOOKS_CACHE, allEntries = true)
    @Observed(name = "books.delete", contextualName = "service.delete_book")
    public Book updateBook(Integer id, Book book) {
        log.info("更新書本 ID: {}", id);
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
     * @CacheEvict - 刪除書本時清除所有快取
     */
    @Async
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.BOOKS_CACHE, allEntries = true)
    public void deleteBook(Integer id) {
        log.info("刪除書本 ID: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的書本");
        }
        bookRepository.deleteById(id);
    }
}
