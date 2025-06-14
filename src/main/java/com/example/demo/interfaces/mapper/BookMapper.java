package com.example.demo.interfaces.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.demo.interfaces.dto.BookDto;
import com.example.demo.interfaces.dto.BookRequest;
import com.example.demo.models.Book;

/**
 * 書本資料轉換器
 * 使用 MapStruct 自動生成實作類別
 * 
 * @Mapper 註解說明：
 * - unmappedTargetPolicy = ReportingPolicy.IGNORE：忽略目標物件中未被映射的屬性，避免編譯時產生警告
 * - nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE：當來源物件屬性為 null 時，不更新目標物件的對應屬性
 */
@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BookMapper {

    /**
     * 將實體轉換為 DTO
     */
    BookDto toDto(Book entity);

    /**
     * 將 DTO 轉換為實體
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toEntity(BookRequest dto);

    /**
     * 更新實體
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Book entity, BookRequest dto);
} 