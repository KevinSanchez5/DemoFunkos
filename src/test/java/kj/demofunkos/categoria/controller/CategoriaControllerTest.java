package kj.demofunkos.categoria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kj.demofunkos.categoria.dto.CategoriaCreateDto;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.categoria.service.CategoriaService;
import kj.demofunkos.funko.exceptions.FunkoNotFoundException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    UUID id = UUID.randomUUID();
    Categoria categoria = new Categoria(id, "Test", LocalDateTime.now(), LocalDateTime.now(), true, new ArrayList<>());

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    CategoriaService categoriaService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    public CategoriaControllerTest(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
        mapper.registerModule(new JavaTimeModule());
    }

    String endpoint = "/categorias";



    @Test
    @Order(1)
    void findAll() throws Exception {
        when(categoriaService.findAll()).thenReturn(List.of(categoria));

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint).accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        List<Categoria> categorias = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Categoria.class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(1, categorias.size()),
                () -> assertEquals(categoria, categorias.get(0)),
                () -> assertFalse(categorias.isEmpty())
        );

    }

    @Test
    @Order(2)
    void findAllEmptyList() throws Exception {
        when(categoriaService.findAll()).thenReturn(List.of());

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint).accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        List<Categoria> categorias = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Categoria.class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(categorias.isEmpty())
        );
        verify(categoriaService, times(1)).findAll();
    }

    @Test
    @Order(3)
    void findById() throws Exception {
        when(categoriaService.findById(id)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/" + id).accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Categoria categoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(this.categoria, categoria),
                () -> assertEquals(this.categoria.getId(), categoria.getId())
        );
        verify(categoriaService, times(1)).findById(id);
    }

    @Test
    @Order(4)
    void findByIdNotFound() throws Exception {
        when(categoriaService.findById(id)).thenThrow(new FunkoNotFoundException(id.toString()));

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/" + id).accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );
        verify(categoriaService, times(1)).findById(id);
    }

    @Test
    @Order(5)
    void save() throws Exception {
        CategoriaCreateDto dto = new CategoriaCreateDto("Test");
        when(categoriaService.save(dto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(endpoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        Categoria categoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus())
        );
        verify(categoriaService, times(1)).save(dto);
    }

    @Test
    @Order(6)
    void saveBadRequest() throws Exception {
        CategoriaCreateDto dto = new CategoriaCreateDto("");
        when(categoriaService.save(dto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                post(endpoint).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );
        verify(categoriaService, times(0)).save(dto);
    }

    @Test
    @Order(7)
    void update() throws Exception {
        CategoriaCreateDto dto = new CategoriaCreateDto("Test");
        when(categoriaService.update(id, dto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(endpoint + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        Categoria categoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
        verify(categoriaService, times(1)).update(id, dto);
    }

    @Test
    @Order(8)
    void updateBadRequest() throws Exception {
        CategoriaCreateDto dto = new CategoriaCreateDto("");
        when(categoriaService.update(id, dto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(endpoint + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );
        verify(categoriaService, times(0)).update(id, dto);
    }

    @Test
    @Order(9)
    void updateNotFound() throws Exception {
        CategoriaCreateDto dto = new CategoriaCreateDto("Test");
        when(categoriaService.update(id, dto)).thenThrow(new FunkoNotFoundException(id.toString()));

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(endpoint + "/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );
        verify(categoriaService, times(1)).update(id, dto);
    }

    @Test
    @Order(10)
    void realDeleteById() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(endpoint + "/realDelete/" + id)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus())
        );
        verify(categoriaService, times(1)).deleteById(id);
    }

    @Test
    @Order(11)
    void realDeleteByIdNotFound() throws Exception {
        doThrow(new FunkoNotFoundException(id.toString())).when(categoriaService).deleteById(id);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(endpoint + "/realDelete/" + id)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
        verify(categoriaService, times(1)).deleteById(id);
    }

    @Test
    @Order(12)
    void deactivateCategoria() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(endpoint + "/" + id + "/desactivar")
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus())
        );
        verify(categoriaService, times(1)).deactivateCategoria(id);
    }

    @Test
    @Order(13)
    void deactivateCategoriaNotFound() throws Exception {
        doThrow(new FunkoNotFoundException(id.toString())).when(categoriaService).deactivateCategoria(id);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(endpoint + "/" + id + "/desactivar")
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
        verify(categoriaService, times(1)).deactivateCategoria(id);
    }

    @Test
    @Order(14)
    void activateCategoria() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(endpoint + "/" + id + "/activar")
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
        verify(categoriaService, times(1)).reactivateCategoria(id);
    }

    @Test
    @Order(15)
    void activateCategoriaNotFound() throws Exception {
        when(categoriaService.reactivateCategoria(id)).thenThrow(new FunkoNotFoundException(id.toString()));

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(endpoint + "/" + id + "/activar")
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
        verify(categoriaService, times(1)).reactivateCategoria(id);
    }

    @Test
    @Order(16)
    void findByNombre() throws Exception {
        when(categoriaService.findByNombre("Test")).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/nombre/Test").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Categoria categoria = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(this.categoria, categoria),
                () -> assertEquals(this.categoria.getId(), categoria.getId())
        );
        verify(categoriaService, times(1)).findByNombre("Test");
    }

    @Test
    @Order(17)
    void findByNombreNotFound() throws Exception {
        when(categoriaService.findByNombre("Test")).thenThrow(new FunkoNotFoundException("Test"));

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/nombre/Test").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );
        verify(categoriaService, times(1)).findByNombre("Test");
    }
}