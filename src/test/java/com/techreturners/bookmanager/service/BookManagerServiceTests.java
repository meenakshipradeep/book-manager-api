package com.techreturners.bookmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techreturners.bookmanager.controller.BookManagerController;
import com.techreturners.bookmanager.model.Book;
import com.techreturners.bookmanager.model.Genre;

import com.techreturners.bookmanager.repository.BookManagerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@DataJpaTest
public class BookManagerServiceTests {

    @Mock
    private BookManagerServiceImpl mockBookManagerServiceImpl;
    @InjectMocks
    private BookManagerController bookManagerController;
    @Autowired
    private MockMvc mockMvcController;
    private ObjectMapper mapper;


    @BeforeEach
    public void setup() {
        mockMvcController = MockMvcBuilders.standaloneSetup(bookManagerController).build();
        mapper = new ObjectMapper();
    }


    @Mock
    private BookManagerRepository mockBookManagerRepository;

    @InjectMocks
    private BookManagerServiceImpl bookManagerServiceImpl;

    @Test
    public void testGetAllBooksReturnsListOfBooks() {

        List<Book> books = new ArrayList<>();
        books.add(new Book(1L, "Book One", "This is the description for Book One", "Person One", Genre.Education));
        books.add(new Book(2L, "Book Two", "This is the description for Book Two", "Person Two", Genre.Education));
        books.add(new Book(3L, "Book Three", "This is the description for Book Three", "Person Three", Genre.Education));

        when(mockBookManagerRepository.findAll()).thenReturn(books);

        List<Book> actualResult = bookManagerServiceImpl.getAllBooks();

        assertThat(actualResult).hasSize(3);
        assertThat(actualResult).isEqualTo(books);
    }

    @Test
    public void testAddABook() {

        var book = new Book(4L, "Book Four", "This is the description for Book Four", "Person Four", Genre.Fantasy);

        when(mockBookManagerRepository.save(book)).thenReturn(book);

        Book actualResult = bookManagerServiceImpl.insertBook(book);

        assertThat(actualResult).isEqualTo(book);
    }

    @Test
    public void testGetBookById() {

        Long bookId = 5L;
        var book = new Book(5L, "Book Five", "This is the description for Book Five", "Person Five", Genre.Fantasy);

        when(mockBookManagerRepository.findById(bookId)).thenReturn(Optional.of(book));

        Book actualResult = bookManagerServiceImpl.getBookById(bookId);

        assertThat(actualResult).isEqualTo(book);
    }

    //User Story 4 - Update Book By Id Solution
    @Test
    public void testUpdateBookById() {

        Long bookId = 5L;
        var book = new Book(5L, "Book Five", "This is the description for Book Five", "Person Five", Genre.Fantasy);

        when(mockBookManagerRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(mockBookManagerRepository.save(book)).thenReturn(book);

        bookManagerServiceImpl.updateBookById(bookId, book);

        verify(mockBookManagerRepository, times(1)).save(book);
    }
    //User Story 5-Delete book by Id solution
    @Test
    public void testDeleteMappingAndGetAllBooksAfterDeletion() throws Exception {
        //Arrange
        List<Book> books = new ArrayList<>();
        books.add(new Book(1L, "Book One", "This is the description for Book One", "Person One", Genre.Education));
        books.add(new Book(2L, "Book Two", "This is the description for Book Two", "Person Two", Genre.Education));
        books.add(new Book(3L, "Book Three", "This is the description for Book Three", "Person Three", Genre.Education));

        List<Book> newBook = new ArrayList<>();
        // books.add(new Book(1L, "Book One", "This is the description for Book One", "Person One", Genre.Education));
        newBook.add(new Book(2L, "Book Two", "This is the description for Book Two", "Person Two", Genre.Education));
        newBook.add(new Book(3L, "Book Three", "This is the description for Book Three", "Person Three", Genre.Education));
        //Act
        when(mockBookManagerServiceImpl.deleteBookById(1L)).thenReturn(newBook);
        //Assert
        // assertThat(newBook).hasSize(2);
        this.mockMvcController.perform(
                        MockMvcRequestBuilders.put("/api/v1/book/1L")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(mockBookManagerServiceImpl, times(1)).deleteBookById(1L);

    }
    @Test
    public void testDeleteBookByBookId() {
        Book book = new Book(1L, "Book Three", "This is the description for Book Three", "Person Three", Genre.Education);
        mockBookManagerRepository.save(book);

        Iterable<Book> books = mockBookManagerRepository.findAll();

        assertThat(mockBookManagerRepository.count()).isEqualTo(1);

        mockBookManagerRepository.deleteById(books.iterator().next().getId());

        assertThat(mockBookManagerRepository.count()).isEqualTo(0);
    }

}
