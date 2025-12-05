package com.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.app.model.Cart;
import com.app.model.CartItem;
import com.app.model.Category;
import com.app.model.Product;
import com.app.exceptions.APIException;
import com.app.payloads.ProductDTO;
import com.app.payloads.ProductResponse;
import com.app.repositories.CartRepo;
import com.app.repositories.CategoryRepo;
import com.app.repositories.ProductRepo;

public class ProductServiceImplTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private CartService cartService;

    @Mock
    private FileService fileService;




    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // set @Value field "path"
        ReflectionTestUtils.setField(productService, "path", "images/");

        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");
        category.setProducts(new ArrayList<>());

        product = new Product();
        product.setProductId(1L);
        product.setProductName("Phone");
        product.setDescription("Smartphone");
        product.setPrice(1000.0);
        product.setDiscount(10); // 10%
        product.setQuantity(5);
    }

    // ---------------------------------------------------------
    // 1. addProduct
    // ---------------------------------------------------------

    @Test
    void testAddProduct_Success_NewProduct() {
        category.getProducts().clear();

        when(categoryRepo.findById(category.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setProductId(product.getProductId());
            return p;
        });

        ProductDTO result = productService.addProduct(category.getCategoryId(), product);

        assertNotNull(result);
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals("default.png", product.getImage());

        double expectedSpecialPrice =
                product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        assertEquals(expectedSpecialPrice, product.getSpecialPrice());

        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void testAddProduct_DuplicateProduct_ThrowsAPIException() {
        Product existing = new Product();
        existing.setProductName(product.getProductName());
        existing.setDescription(product.getDescription());

        category.getProducts().add(existing);

        when(categoryRepo.findById(category.getCategoryId())).thenReturn(Optional.of(category));

        assertThrows(APIException.class,
                () -> productService.addProduct(category.getCategoryId(), product));

        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void testAddProduct_CategoryNotFound_ThrowsAPIException() {
        when(categoryRepo.findById(category.getCategoryId())).thenReturn(Optional.empty());

        assertThrows(APIException.class,
                () -> productService.addProduct(category.getCategoryId(), product));

        verify(productRepo, never()).save(any(Product.class));
    }


    @Test
    void addProduct_NoDuplicateButSameNameDifferentDescription_SUCCESS() {
        Category category = new Category();
        category.setCategoryId(1L);

        Product existing = new Product();
        existing.setProductName("Other name");
        existing.setDescription("Other description");

        category.setProducts(List.of(existing));

        Product newProduct = new Product();
        newProduct.setProductName("Other name");
        newProduct.setDescription("Unique desc");
        newProduct.setPrice(100);
        newProduct.setDiscount(10);

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(productRepo.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setProductId(10L);
            return p;
        });

        ProductDTO result = productService.addProduct(1L, newProduct);

        assertEquals("Other name", result.getProductName());
    }

        @Test
        void addProduct_NoDuplicateButSameDescriptionDifferentName_SUCCESS() {
            Category category = new Category();
            category.setCategoryId(1L);

            Product existing = new Product();
            existing.setProductName("Other name");
            existing.setDescription("Other description");

            category.setProducts(List.of(existing));

            Product newProduct = new Product();
            newProduct.setProductName("Unique name");
            newProduct.setDescription("Other description");
            newProduct.setPrice(100);
            newProduct.setDiscount(10);

            when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
            when(productRepo.save(any(Product.class))).thenAnswer(i -> {
                Product p = i.getArgument(0);
                p.setProductId(10L);
                return p;
            });


        ProductDTO result = productService.addProduct(1L, newProduct);

        assertEquals("Unique name", result.getProductName());
    }



    // ---------------------------------------------------------
    // 2. getAllProducts
    // ---------------------------------------------------------

    @Test
    void testGetAllProducts_SortAscending() {
        Product p1 = new Product();
        p1.setProductId(10L);
        p1.setProductName("A");

        Product p2 = new Product();
        p2.setProductId(20L);
        p2.setProductName("B");

        int pageNumber = 0;
        int pageSize = 2;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = new PageImpl<>(Arrays.asList(p1, p2), pageRequest, 2);

        when(productRepo.findAll((Pageable) any())).thenReturn(page);

        ProductResponse response =
                productService.getAllProducts(pageNumber, pageSize, "productName", "asc");

        assertEquals(2, response.getContent().size());
        assertEquals(pageNumber, response.getPageNumber());
        assertEquals(pageSize, response.getPageSize());
        assertEquals(page.getTotalElements(), response.getTotalElements());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertTrue(response.isLastPage());
    }


    @Test
    void testGetAllProducts_SortDescending() {
        Product p1 = new Product();
        p1.setProductId(10L);
        p1.setProductName("A");

        int pageNumber = 0;
        int pageSize = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = new PageImpl<>(Collections.singletonList(p1), pageRequest, 1);

        when(productRepo.findAll((Pageable) any())).thenReturn(page);

        ProductResponse response =
                productService.getAllProducts(pageNumber, pageSize, "productName", "desc");

        assertEquals(1, response.getContent().size());
        assertEquals(pageNumber, response.getPageNumber());
        assertTrue(response.isLastPage());
    }

    // ---------------------------------------------------------
    // 3. searchByCategory
    // ---------------------------------------------------------

    @Test
    void testSearchByCategory_Success_WithProducts() {
        Product p1 = new Product();
        p1.setProductId(10L);
        p1.setProductName("Phone");

        int pageNumber = 0;
        int pageSize = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = new PageImpl<>(Collections.singletonList(p1), pageRequest, 1);

        when(categoryRepo.findById(category.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepo.findAll((Pageable) any())).thenReturn(page);

        ProductResponse response =
                productService.searchByCategory(
                        category.getCategoryId(), pageNumber, pageSize, "productName", "asc");

        assertEquals(1, response.getContent().size());
        assertEquals(pageNumber, response.getPageNumber());
    }

    @Test
    void testSearchByCategory_NoProducts_ThrowsAPIException() {
        int pageNumber = 0;
        int pageSize = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> emptyPage =
                new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        when(categoryRepo.findById(category.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepo.findAll((Pageable) any())).thenReturn(emptyPage);

        APIException ex = assertThrows(
                APIException.class,
                () -> productService.searchByCategory(
                        category.getCategoryId(), pageNumber, pageSize, "productName", "desc")
        );

        assertTrue(ex.getMessage().contains(
                category.getCategoryName() + " category doesn't contain any products"));
    }



    @Test
    void testSearchByCategory_CategoryNotFound_ThrowsAPIException() {
        when(categoryRepo.findById(category.getCategoryId())).thenReturn(Optional.empty());

        assertThrows(
                APIException.class,
                () -> productService.searchByCategory(
                        category.getCategoryId(), 0, 1, "productName", "asc")
        );
    }

    // ---------------------------------------------------------
    // 4. searchProductByKeyword
    // ---------------------------------------------------------

    @Test
    void testSearchProductByKeyword_Success() {
        Product p1 = new Product();
        p1.setProductId(10L);
        p1.setProductName("Phone");

        int pageNumber = 0;
        int pageSize = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = new PageImpl<>(Collections.singletonList(p1), pageRequest, 1);

        String keyword = "phone";

        when(productRepo.findByProductNameLike(eq(keyword), any())).thenReturn(page);

        ProductResponse response =
                productService.searchProductByKeyword(
                        keyword, pageNumber, pageSize, "productName", "asc");

        assertEquals(1, response.getContent().size());
        assertEquals(p1.getProductName(), response.getContent().get(0).getProductName());
    }

    @Test
    void testSearchProductByKeyword_NoProducts_ThrowsAPIException() {
        int pageNumber = 0;
        int pageSize = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> emptyPage =
                new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        String keyword = "phone";

        when(productRepo.findByProductNameLike(eq(keyword), any())).thenReturn(emptyPage);

        APIException ex = assertThrows(
                APIException.class,
                () -> productService.searchProductByKeyword(
                        keyword, pageNumber, pageSize, "productName", "desc")
        );

        assertEquals("Products not found with keyword: " + keyword, ex.getMessage());
    }

    // ---------------------------------------------------------
    // 5. updateProduct
    // ---------------------------------------------------------

    @Test
    void testUpdateProduct_Success_NoCarts() {
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartRepo.findCartsByProductId(product.getProductId()))
                .thenReturn(Collections.emptyList());
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product newData = new Product();
        newData.setProductName("New Phone");
        newData.setDescription("New Desc");
        newData.setPrice(2000.0);
        newData.setDiscount(20);
        newData.setQuantity(10);

        ProductDTO result = productService.updateProduct(product.getProductId(), newData);

        assertNotNull(result);
        assertEquals(newData.getProductName(), result.getProductName());

        double expectedSpecialPrice =
                newData.getPrice() - ((newData.getDiscount() * 0.01) * newData.getPrice());
        assertEquals(expectedSpecialPrice, newData.getSpecialPrice());

        verify(cartService, never()).updateProductInCarts(anyLong(), anyLong());
    }

    @Test
    void testUpdateProduct_Success_WithCarts() {
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart1 = new Cart();
        cart1.setCartId(101L);

        Cart cart2 = new Cart();
        cart2.setCartId(202L);

        CartItem item1 = new CartItem();
        item1.setCart(cart1);
        item1.setProduct(product);
        item1.setQuantity(1);
        item1.setProductPrice(product.getSpecialPrice());

        CartItem item2 = new CartItem();
        item2.setCart(cart2);
        item2.setProduct(product);
        item2.setQuantity(2);
        item2.setProductPrice(product.getSpecialPrice());

        cart1.setCartItems(Collections.singletonList(item1));
        cart2.setCartItems(Collections.singletonList(item2));

        when(cartRepo.findCartsByProductId(product.getProductId()))
                .thenReturn(Arrays.asList(cart1, cart2));

        Product newData = new Product();
        newData.setProductName("Updated Phone");
        newData.setDescription("Updated Desc");
        newData.setPrice(1500.0);
        newData.setDiscount(10);
        newData.setQuantity(7);

        ProductDTO result = productService.updateProduct(product.getProductId(), newData);

        assertNotNull(result);
        assertEquals(newData.getProductName(), result.getProductName());

        verify(cartService, times(1))
                .updateProductInCarts(cart1.getCartId(), product.getProductId());
        verify(cartService, times(1))
                .updateProductInCarts(cart2.getCartId(), product.getProductId());
    }

    @Test
    void testUpdateProduct_ProductNotFound_ThrowsAPIException() {
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.empty());

        Product newData = new Product();
        newData.setProductName("Updated");

        assertThrows(APIException.class,
                () -> productService.updateProduct(product.getProductId(), newData));

        verify(productRepo, never()).save(any(Product.class));
    }

    // ---------------------------------------------------------
    // 6. updateProductImage
    // ---------------------------------------------------------

    @Test
    void testUpdateProductImage_Success() throws IOException {
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));

        MultipartFile image = mock(MultipartFile.class);
        when(fileService.uploadImage(anyString(), eq(image))).thenReturn("newImage.png");
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO result = productService.updateProductImage(product.getProductId(), image);

        assertNotNull(result);
        assertEquals("newImage.png", product.getImage());

        verify(fileService, times(1))
                .uploadImage("images/", image);
        verify(productRepo, times(1)).save(product);
    }

    @Test
    void testUpdateProductImage_ProductNotFound_ThrowsAPIException() throws IOException {
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.empty());

        MultipartFile image = mock(MultipartFile.class);

        assertThrows(APIException.class,
                () -> productService.updateProductImage(product.getProductId(), image));

        verify(fileService, never()).uploadImage(anyString(), any());
    }

    // ---------------------------------------------------------
    // 7. deleteProduct
    // ---------------------------------------------------------

    @Test
    void testDeleteProduct_Success_WithCarts() {
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));

        Cart cart1 = new Cart();
        cart1.setCartId(101L);
        Cart cart2 = new Cart();
        cart2.setCartId(202L);

        when(cartRepo.findCartsByProductId(product.getProductId()))
                .thenReturn(Arrays.asList(cart1, cart2));

        String msg = productService.deleteProduct(product.getProductId());

        assertTrue(msg.contains("Product with productId: " + product.getProductId()));
        verify(cartService, times(1))
                .deleteProductFromCart(cart1.getCartId(), product.getProductId());
        verify(cartService, times(1))
                .deleteProductFromCart(cart2.getCartId(), product.getProductId());
        verify(productRepo, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_Success_NoCarts() {
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartRepo.findCartsByProductId(product.getProductId()))
                .thenReturn(Collections.emptyList());

        String msg = productService.deleteProduct(product.getProductId());

        assertTrue(msg.contains("Product with productId: " + product.getProductId()));
        verify(cartService, never()).deleteProductFromCart(anyLong(), anyLong());
        verify(productRepo, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_ProductNotFound_ThrowsAPIException() {
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.empty());

        assertThrows(APIException.class,
                () -> productService.deleteProduct(product.getProductId()));

        verify(productRepo, never()).delete(any(Product.class));
    }
}
