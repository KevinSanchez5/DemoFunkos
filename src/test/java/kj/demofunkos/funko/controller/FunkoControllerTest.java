package kj.demofunkos.funko.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.exceptions.FunkoNotFoundException;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.service.FunkoService;

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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FunkoControllerTest {
    Funko funko = new Funko(1L, "test", 1.0, LocalDateTime.now(), LocalDateTime.now(), false, null, null);

    ObjectMapper mapper = new ObjectMapper();
    @MockBean
    FunkoService funkoService;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public FunkoControllerTest(FunkoService funkoService) {
        this.funkoService = funkoService;
        mapper.registerModule(new JavaTimeModule());
    }

    String endpoint ="/funkos";


    @Test
    @Order(1)
    void findAll() throws Exception {
        when(funkoService.findAll()).thenReturn(List.of(funko));


        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint).accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        List<Funko> funkos = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertFalse(funkos.isEmpty()),
                () -> assertEquals(1, funkos.size())
        );

        verify(funkoService, times(1)).findAll();
    }

    @Test
    @Order(2)
    void findAllEmptyList() throws Exception {
        when(funkoService.findAll()).thenReturn(List.of());
        MockHttpServletResponse response = mockMvc.perform(
                (get(endpoint)
                        .accept(MediaType.APPLICATION_JSON)))
                .andReturn().getResponse();

        List<Funko> funkos = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertTrue(funkos.isEmpty())
        );
        verify(funkoService, times(1)).findAll();
    }

    @Test
    @Order(3)
    void findById() throws Exception {
        when(funkoService.findById(1L)).thenReturn(funko);
        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/1").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Funko funko = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertNotNull(funko),
                () -> assertEquals(1L, funko.getId())
        );
        verify(funkoService, times(1)).findById(1L);
    }

    @Test
    @Order(4)
    void findByIdNotFound() throws Exception {
        when(funkoService.findById(1L)).thenThrow(new FunkoNotFoundException(funko.getId()));
        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/1").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );
        verify(funkoService, times(1)).findById(1L);
    }

    @Test
    @Order(5)
    void save() throws Exception{
        FunkoCreateDto dto = new FunkoCreateDto("test", 1.0, null, null, "algo");
        when(funkoService.save(dto)).thenReturn(funko);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(endpoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        Funko savedFunko = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertNotNull(response.getContentAsString()),
                () -> assertEquals(funko.getId(), savedFunko.getId())
        );

        verify(funkoService, times(1)).save(dto);
    }

    @Test
    @Order(6)
    void saveBadRequest() throws Exception {
        FunkoCreateDto dto = new FunkoCreateDto("", 1.0, null, null, null);
        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(endpoint)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, never()).save(dto);
    }

    @Test
    @Order(7)
    void update() throws Exception {
        FunkoUpdateDto dto = new FunkoUpdateDto("updated", 2.0 , null , null, null);
        when(funkoService.update(1L, dto)).thenReturn(funko);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(endpoint + "/1")
                       .accept(MediaType.APPLICATION_JSON)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        Funko updatedFunko = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertNotNull(response.getContentAsString()),
                () -> assertEquals(funko.getId(), updatedFunko.getId())
        );

        verify(funkoService, times(1)).update(1L, dto);
    }

    @Test
    @Order(8)
    void updateNotFound() throws Exception {
        FunkoUpdateDto dto = new FunkoUpdateDto("updated", 2.0, null, null, null);
        when(funkoService.update(1L, dto)).thenThrow(new FunkoNotFoundException(funko.getId()));

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(endpoint + "/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, times(1)).update(1L, dto);
    }

    @Test
    @Order(9)
    void updateBadRequest() throws Exception {
        FunkoUpdateDto dto = new FunkoUpdateDto ("deberiaestarmal", -2.0, null, null, null);
        when(funkoService.update(1L, dto)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser menor o igual a 0"));

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(endpoint + "/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, times(1)).update(1L, dto);
    }
    @Test
    @Order(10)
    void deleteById() throws Exception {
        doNothing().when(funkoService).deleteLogically(1L);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(endpoint + "/1")
                       .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, times(1)).deleteById(1L);
    }

    @Test
    @Order(11)
    void deleteByIdNotFound() throws Exception {
        doThrow(new FunkoNotFoundException(funko.getId())).when(funkoService).deleteById(1L);
        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(endpoint + "/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, times(1)).deleteById(1L);
    }

    @Test
    @Order(12)
    void logicalDeleteById() throws Exception {
        doNothing().when(funkoService).deleteLogically(1L);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(endpoint + "/1/desactivar")
                       .accept(MediaType.APPLICATION_JSON)
                       .param("logicalDelete", "true")
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, times(1)).deleteLogically(1L);
    }

    @Test
    @Order(13)
    void logicalDeleteByIdNotFound() throws Exception {
        doThrow(new FunkoNotFoundException(funko.getId())).when(funkoService).deleteLogically(1L);
        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(endpoint + "/1/desactivar")
                       .accept(MediaType.APPLICATION_JSON)
                       .param("logicalDelete", "true")
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, times(1)).deleteLogically(1L);
    }

    @Test
    @Order(14)
    void reactivateFunko() throws Exception {
        when(funkoService.reactivateFunko(1L)).thenReturn(funko);

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(endpoint + "/1/activar")
                       .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Funko reactivatedFunko = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertNotNull(response.getContentAsString()),
                () -> assertEquals(funko.getId(), reactivatedFunko.getId())
        );

        verify(funkoService, times(1)).reactivateFunko(1L);
    }

    @Test
    @Order(15)
    void reactivateFunkoNotFound() throws Exception {
        when(funkoService.reactivateFunko(1L)).thenThrow(new FunkoNotFoundException(funko.getId()));

        MockHttpServletResponse response = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(endpoint + "/1/activar")
                       .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, times(1)).reactivateFunko(1L);
    }

    @Test
    @Order(16)
    void findByNombre() throws Exception {
        when(funkoService.findByNombreIgnoreCase("test")).thenReturn(funko);

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/nombre/test").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Funko funko = mapper.readValue(response.getContentAsString(), Funko.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertNotNull(funko),
                () -> assertEquals("test", funko.getNombre())
        );

        verify(funkoService, times(1)).findByNombreIgnoreCase("test");
    }

    @Test
    @Order(17)
    void findByNombreNotFound() throws Exception {
        when(funkoService.findByNombreIgnoreCase("test")).thenThrow(new FunkoNotFoundException("test"));

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/nombre/test").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
                () -> assertTrue(response.getContentAsString().isEmpty())
        );

        verify(funkoService, times(1)).findByNombreIgnoreCase("test");
    }

    @Test
    @Order(18)
    void findByPrecioBetween() throws Exception {
        when(funkoService.findByPrecioBetween(1.0, 2.0)).thenReturn(List.of(funko));

        MockHttpServletResponse response = mockMvc.perform(
                get(endpoint + "/precio-entre?minimo=1.0&maximo=2.0").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        List<Funko> funkos = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertFalse(funkos.isEmpty()),
                () -> assertEquals(1, funkos.size())
        );

        verify(funkoService, times(1)).findByPrecioBetween(1.0, 2.0);
    }

}