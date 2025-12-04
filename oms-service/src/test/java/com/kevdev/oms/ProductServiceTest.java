package com.kevdev.oms;

import com.kevdev.oms.entity.Product;
import com.kevdev.oms.repository.ProductRepository;
import com.kevdev.oms.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Widget");
        product.setPrice(new BigDecimal("9.99"));
    }

    @Test
    void getAllProducts_returnsAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertThat(result).containsExactly(product);
    }

    @Test
    void getProductById_existingProduct_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(product);
    }

    @Test
    void getProductById_missingProduct_returnsEmptyOptional() {
        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(42L);

        assertThat(result).isEmpty();
    }

    @Test
    void createProduct_persistsAndReturnsProduct() {
        Product toCreate = new Product();
        toCreate.setName("Widget");
        toCreate.setPrice(new BigDecimal("9.99"));

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });

        Product result = productService.createProduct(toCreate);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("Widget");
        assertThat(saved.getPrice()).isEqualByComparingTo("9.99");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Widget");
    }
}

