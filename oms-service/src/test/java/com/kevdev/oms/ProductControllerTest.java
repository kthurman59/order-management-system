package com.kevdev.oms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevdev.oms.controller.ProductController;
import com.kevdev.oms.entity.Product;
import com.kevdev.oms.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product buildProduct(Long id, String name, String sku, String price) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setSku(sku);
        p.setPrice(new BigDecimal(price));
        return p;
    }

    @Test
    void getAll_returnsListOfProducts() throws Exception {
        Product p1 = buildProduct(1L, "Widget", "W001", "9.99");

        Mockito.when(productRepository.findAll()).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Widget"));
    }

    @Test
    void getById_existingProduct_returnsProduct() throws Exception {
        Product p1 = buildProduct(1L, "Widget", "W001", "9.99");

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(p1));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Widget"));
    }

    @Test
    void getById_missingProduct_returnsNotFound() throws Exception {
        Mockito.when(productRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validProduct_returnsCreated() throws Exception {
        Product toSave = buildProduct(null, "Widget", "W001", "9.99");
        Product saved = buildProduct(10L, "Widget", "W001", "9.99");

        Mockito.when(productRepository.save(any(Product.class))).thenReturn(saved);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(toSave))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Widget"));
    }

    @Test
    void delete_existingProduct_returnsNoContent() throws Exception {
        Mockito.when(productRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_missingProduct_returnsNotFound() throws Exception {
        Mockito.when(productRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}

