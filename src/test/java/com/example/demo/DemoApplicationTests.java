package com.example.demo;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.infrastructure.repositories.BookRepository;
import com.example.demo.interfaces.dto.BookDto;
import com.example.demo.interfaces.dto.BookRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("書籍管理系統 API 整合測試")
class DemoApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private BookRepository bookRepository;

	private static final String BASE_URL = "/books";

	@BeforeEach
	void setUp() {
		// 清理測試數據
		bookRepository.deleteAll();
	}

	@Test
	@DisplayName("測試書籍的完整 CRUD 操作流程：新增、查詢、更新和刪除")
	@Tag("integration")
	@Tag("crud")
	void testBookCrudOperations() {
		// 準備測試數據
		BookRequest bookRequest = new BookRequest()
			.title("Spring Boot 實戰")
			.author("張三")
			.isbn("9789863479431")
			.publishYear(2024)
			.price(new BigDecimal("599.00"));

		// 測試新增書本
		ResponseEntity<BookDto> createResponse = restTemplate.postForEntity(
			BASE_URL,
			bookRequest,
			BookDto.class
		);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		assertThat(createResponse.getBody().getTitle()).isEqualTo(bookRequest.getTitle());
		assertThat(createResponse.getBody().getAuthor()).isEqualTo(bookRequest.getAuthor());
		assertThat(createResponse.getBody().getIsbn()).isEqualTo(bookRequest.getIsbn());
		
		Integer bookId = createResponse.getBody().getId();

		// 測試獲取所有書本
		ResponseEntity<List<BookDto>> getAllResponse = restTemplate.exchange(
			BASE_URL,
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<BookDto>>() {}
		);
		assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getAllResponse.getBody()).isNotNull();
		assertThat(getAllResponse.getBody()).hasSize(1);
		assertThat(getAllResponse.getBody().get(0).getTitle()).isEqualTo(bookRequest.getTitle());

		// 測試獲取單本書本
		ResponseEntity<BookDto> getOneResponse = restTemplate.getForEntity(
			BASE_URL + "/" + bookId,
			BookDto.class
		);
		assertThat(getOneResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getOneResponse.getBody()).isNotNull();
		assertThat(getOneResponse.getBody().getTitle()).isEqualTo(bookRequest.getTitle());

		// 測試更新書本
		BookRequest updateRequest = new BookRequest()
			.title("Spring Boot 實戰（第二版）")
			.author("張三")
			.isbn("9789863479431")
			.publishYear(2024)
			.price(new BigDecimal("699.00"));

		ResponseEntity<BookDto> updateResponse = restTemplate.exchange(
			BASE_URL + "/" + bookId,
			HttpMethod.PUT,
			new HttpEntity<>(updateRequest),
			BookDto.class
		);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(updateResponse.getBody()).isNotNull();
		assertThat(updateResponse.getBody().getTitle()).isEqualTo(updateRequest.getTitle());
		assertThat(updateResponse.getBody().getPrice()).isEqualTo(updateRequest.getPrice());

		// 測試刪除書本
		ResponseEntity<Void> deleteResponse = restTemplate.exchange(
			BASE_URL + "/" + bookId,
			HttpMethod.DELETE,
			null,
			Void.class
		);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// 確認書本已被刪除
		ResponseEntity<BookDto> getDeletedResponse = restTemplate.getForEntity(
			BASE_URL + "/" + bookId,
			BookDto.class
		);
		assertThat(getDeletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DisplayName("測試新增重複 ISBN 的書籍時應返回錯誤")
	@Tag("integration")
	@Tag("validation")
	void testCreateBookWithDuplicateIsbn() {
		// 準備第一本書
		BookRequest book1 = new BookRequest()
			.title("第一本書")
			.author("作者一")
			.isbn("9789863479431")
			.publishYear(2024)
			.price(new BigDecimal("599.00"));

		// 新增第一本書
		ResponseEntity<BookDto> createResponse1 = restTemplate.postForEntity(
			BASE_URL,
			book1,
			BookDto.class
		);
		assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// 準備第二本書（使用相同的 ISBN）
		BookRequest book2 = new BookRequest()
			.title("第二本書")
			.author("作者二")
			.isbn("9789863479431")  // 重複的 ISBN
			.publishYear(2024)
			.price(new BigDecimal("699.00"));

		// 嘗試新增第二本書
		ResponseEntity<BookDto> createResponse2 = restTemplate.postForEntity(
			BASE_URL,
			book2,
			BookDto.class
		);
		assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@DisplayName("測試查詢不存在的書籍時應返回 404 錯誤")
	@Tag("integration")
	@Tag("error-handling")
	void testGetNonExistentBook() {
		ResponseEntity<BookDto> response = restTemplate.getForEntity(
			BASE_URL + "/999",
			BookDto.class
		);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
