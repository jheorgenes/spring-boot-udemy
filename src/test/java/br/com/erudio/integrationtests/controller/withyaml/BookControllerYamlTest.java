package br.com.erudio.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	private static BookVO book;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
		book = new BookVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given()
				.config(RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(user, objectMapper)
					.when()
						.post()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.as(TokenVO.class, objectMapper)
						.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/book/v1") //Setando a rota
				.setPort(TestConfigs.SERVER_PORT) //Definindo a porta
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		var persistedBook = given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(book, objectMapper)
					.when()
						.post()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.as(BookVO.class, objectMapper);
		
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getAuthor());
		
		assertTrue(persistedBook.getId() > 0);
		
		assertEquals("DEV da Depressão", persistedBook.getAuthor());
		assertEquals(300.00, persistedBook.getPrice());
		assertEquals("A vida do dev", persistedBook.getTitle());
	}
	
	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		book.setTitle("Mundo DEV");
		
		var persistedBook = given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(book, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(BookVO.class, objectMapper);
		
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getAuthor());
		
		assertEquals(book.getId(), persistedBook.getId());
		assertEquals("DEV da Depressão", persistedBook.getAuthor());
		assertEquals(300.00, persistedBook.getPrice());
		assertEquals("Mundo DEV", persistedBook.getTitle());
	}
	
	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		var persistedBook = given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("id", book.getId())
					.when()
						.get("{id}")
					.then()
						.statusCode(200)
					.extract()
						.body()
						.as(BookVO.class, objectMapper);
		
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getAuthor());
		
		assertEquals(book.getId(), persistedBook.getId());
		assertEquals("DEV da Depressão", persistedBook.getAuthor());
		assertEquals(300.00, persistedBook.getPrice());
		assertEquals("Mundo DEV", persistedBook.getTitle());
	}
	
	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		
		given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", book.getId())
					.when()
						.delete("{id}")
					.then()
						.statusCode(204);
	}
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(
						EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.when()
						.get()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.as(BookVO[].class, objectMapper);
		
		List<BookVO> books = Arrays.asList(content);
		
		System.out.println(books);
		
		BookVO foundBookOne = books.get(0);
		book = foundBookOne;
		
		assertNotNull(foundBookOne);
		assertNotNull(foundBookOne.getId());
		assertNotNull(foundBookOne.getAuthor());
		assertNotNull(foundBookOne.getLaunchDate());
		assertNotNull(foundBookOne.getPrice());
		assertNotNull(foundBookOne.getAuthor());
		
		assertEquals(book.getId(), foundBookOne.getId());
		assertEquals("Michael C. Feathers", foundBookOne.getAuthor());
		assertEquals(49.0, foundBookOne.getPrice());
		assertEquals("Working effectively with legacy code", foundBookOne.getTitle());
		
		BookVO foundBookThree = books.get(2);
		book = foundBookThree;
		
		assertNotNull(foundBookThree);
		assertNotNull(foundBookThree.getId());
		assertNotNull(foundBookThree.getAuthor());
		assertNotNull(foundBookThree.getLaunchDate());
		assertNotNull(foundBookThree.getPrice());
		assertNotNull(foundBookThree.getAuthor());
		
		assertEquals(book.getId(), foundBookThree.getId());
		assertEquals("Robert C. Martin", foundBookThree.getAuthor());
		assertEquals(77.0, foundBookThree.getPrice());
		assertEquals("Clean Code", foundBookThree.getTitle());
		
		
		BookVO foundBookFive = books.get(4);
		book = foundBookFive;
		
		assertNotNull(foundBookFive);
		assertNotNull(foundBookFive.getId());
		assertNotNull(foundBookFive.getAuthor());
		assertNotNull(foundBookFive.getLaunchDate());
		assertNotNull(foundBookFive.getPrice());
		assertNotNull(foundBookFive.getAuthor());
		
		assertEquals(book.getId(), foundBookFive.getId());
		assertEquals("Steve McConnell", foundBookFive.getAuthor());
		assertEquals(58.0, foundBookFive.getPrice());
		assertEquals("Code complete", foundBookFive.getTitle());
	}

	private void mockBook() {
		book.setAuthor("DEV da Depressão");
		book.setLaunchDate(new Date());
		book.setPrice(300.00);
		book.setTitle("A vida do dev");
	}
}
