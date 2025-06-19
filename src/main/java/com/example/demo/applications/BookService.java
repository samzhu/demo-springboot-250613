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
     * 不使用快取，每次都從資料庫查詢
     */
    @Observed(name = "book.catalog.browse", contextualName = "書本目錄瀏覽", lowCardinalityKeyValues = { "operation",
            "list_all", "source", "database" })
    public List<Book> getAllBooks() {
        log.info("從資料庫獲取所有書本");
        return bookRepository.findAll();
    }

    /**
     * 根據 ID 獲取書本
     * 
     * @Cacheable - 使用書本 ID 作為快取鍵值
     */
    @Cacheable(cacheNames = CacheConfig.BOOKS_CACHE, key = "'book_' + #id")
    @Observed(name = "book.details.view", contextualName = "書本詳情查看", lowCardinalityKeyValues = { "operation",
            "get_by_id", "cache_enabled", "true" })
    public Book getBookById(Integer id) {
        log.info("從資料庫獲取書本 ID: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的書本"));
    }

    /**
     * 新增書本
     * 不需要清除快取，因為不快取 all_books
     */
    @Transactional
    @Observed(name = "book.inventory.add", contextualName = "書本庫存新增", lowCardinalityKeyValues = { "operation", "create",
            "business_impact", "high" })
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
     * 
     * @CacheEvict - 只清除被更新的特定書本快取
     */
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.BOOKS_CACHE, key = "'book_' + #id")
    @Observed(name = "book.inventory.update", contextualName = "書本資訊更新", lowCardinalityKeyValues = { "operation",
            "update", "cache_evict", "single", "business_impact", "medium" })
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
     * 
     * @CacheEvict - 只清除被刪除的特定書本快取
     */
    @Async
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.BOOKS_CACHE, key = "'book_' + #id")
    @Observed(name = "book.inventory.remove", contextualName = "書本庫存移除", lowCardinalityKeyValues = { "operation",
            "delete", "async", "true", "business_impact", "high" })
    public void deleteBook(Integer id) {
        log.info("刪除書本 ID: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的書本");
        }
        bookRepository.deleteById(id);
    }
}
