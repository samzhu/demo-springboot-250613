package com.example.demo.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Book;

/**
 * Book 資料存取層介面
 * 提供標準的 CRUD 操作和複雜查詢功能
 * 
 * 繼承 JpaRepository 提供基本的 CRUD 操作
 * 繼承 JpaSpecificationExecutor 提供動態查詢功能
 * 
 * @author 系統自動生成
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    // 標準的 JpaRepository 已提供以下基本操作：
    // - save(entity): 儲存或更新實體
    // - findById(id): 根據 ID 查詢實體
    // - findAll(): 查詢所有實體
    // - deleteById(id): 根據 ID 刪除實體
    // - count(): 統計實體數量
    // - existsById(id): 檢查實體是否存在

    /**
     * 根據 ISBN 檢查書本是否存在
     * 
     * @param isbn 國際標準書號
     * @return 如果存在返回 true，否則返回 false
     */
    boolean existsByIsbn(String isbn);

    // 可在此處添加自定義查詢方法
    // 例如：
    // List<Book> findByFieldName(String fieldName);
    // Optional<Book> findByUniqueField(String uniqueField);
}