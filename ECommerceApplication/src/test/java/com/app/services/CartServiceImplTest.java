package com.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.app.model.Cart;
import com.app.model.CartItem;
import com.app.model.Product;
import com.app.exceptions.APIException;
import com.app.payloads.CartDTO;
import com.app.repositories.CartItemRepo;
import com.app.repositories.CartRepo;
import com.app.repositories.ProductRepo;

public class CartServiceImplTest {

    @Mock
    private CartRepo cartRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CartItemRepo cartItemRepo;


    @Spy
    private ModelMapper modelMapper = new ModelMapper();



    @InjectMocks
    private CartServiceImpl cartService;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cart = new Cart();
        cart.setCartId(1L);
        cart.setTotalPrice(0.0);

        product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        product.setSpecialPrice(100.0);
        product.setDiscount(10);
        product.setQuantity(10);

        cartItem = new CartItem();
        cartItem.setCartItemId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(3);
        cartItem.setProductPrice(100.0);

        cart.setCartItems(List.of(cartItem));
    }

    // ---------------------------------------------------------
    // 1. addProductToCart
    // ---------------------------------------------------------

    @Test
    void testAddProductToCart_Success() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.empty());

        CartDTO result = cartService.addProductToCart(cart.getCartId(), product.getProductId(), 2);

        assertEquals(200.0, result.getTotalPrice());
        assertEquals(8, product.getQuantity()); // 10 - 2
        verify(cartItemRepo, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddProductToCart_ProductAlreadyInCart() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.ofNullable(cartItem));

        assertThrows(APIException.class, () -> cartService.addProductToCart(cart.getCartId(), product.getProductId(), 1));
    }

    @Test
    void testAddProductToCart_ProductOutOfStock() {
        product.setQuantity(0);

        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));


        assertThrows(APIException.class, () -> cartService.addProductToCart(cart.getCartId(), product.getProductId(), 1));
    }

    @Test
    void testAddProductToCart_QuantityTooHigh(){

        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.empty());


        assertThrows(APIException.class, () -> cartService.addProductToCart(cart.getCartId(), product.getProductId(), 11));
    }

    @Test
    void testAddProductToCart_CartNotFound() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.addProductToCart(cart.getCartId(), product.getProductId(), 1)
        );

        String msg = ex.getMessage();
        assertTrue(msg.contains("Cart with cartId"));
        assertTrue(msg.contains("was not found!!!"));
    }


    @Test
    void testAddProductToCart_ProductNotFoundById() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.addProductToCart(cart.getCartId(), product.getProductId(), 1)
        );

        String expected = "Product with productId" + product.getProductId() + "was not found!!!";
        assertEquals(expected, ex.getMessage());
    }




    // ---------------------------------------------------------
    // 2. getAllCarts
    // ---------------------------------------------------------

    @Test
    void testGetAllCarts_ReturnsCarts() {
        Cart anotherCart = new Cart();
        anotherCart.setCartId(2L);
        anotherCart.setTotalPrice(300.0);

        when(cartRepo.findAll()).thenReturn(Arrays.asList(cart, anotherCart));

        assertEquals(2, cartService.getAllCarts().size());
    }

    @Test
    void testGetAllCarts_NoCarts() {
        when(cartRepo.findAll()).thenReturn(Collections.emptyList());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.getAllCarts()
        );

        assertEquals("No cart exists!!!", ex.getMessage());
    }


    // ---------------------------------------------------------
    // 3. getCart
    // ---------------------------------------------------------

    @Test
    void testGetCart_Success() {
        when(cartRepo.findCartByEmailAndCartId("test@test.com", cart.getCartId())).thenReturn(cart);

        CartDTO result = cartService.getCart("test@test.com", cart.getCartId());

        assertEquals(cart.getCartId(), result.getCartId());
    }

    @Test
    void testGetCart_NotFound() {
        when(cartRepo.findCartByEmailAndCartId("test@test.com", cart.getCartId())).thenReturn(null);

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.getCart("test@test.com", cart.getCartId())
        );

        String expected = "Cart with cartId" + cart.getCartId() + "was not found!!!";
        assertEquals(expected, ex.getMessage());
    }


    // ---------------------------------------------------------
    // 4. updateProductInCarts
    // ---------------------------------------------------------

    @Test
    void testUpdateProductInCarts_Success() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.ofNullable(cartItem));
        when(cartItemRepo.save(any())).thenReturn(cartItem);

        assertDoesNotThrow(() -> cartService.updateProductInCarts(cart.getCartId(), product.getProductId()));
    }

    @Test
    void testUpdateProductInCarts_ProductNotInCart() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.empty());

        assertThrows(APIException.class, () -> cartService.updateProductInCarts(cart.getCartId(), product.getProductId()));
    }

    @Test
    void testUpdateProductInCarts_CartNotFound() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.updateProductInCarts(cart.getCartId(), product.getProductId())
        );

        String expected = "Cart with cartId" + cart.getCartId()  + "was not found!!!";
        assertEquals(expected, ex.getMessage());
    }


    @Test
    void testUpdateProductInCarts_ProductNotFound() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.updateProductInCarts(cart.getCartId(), product.getProductId())
        );

        String expected = "Product with productId" + product.getProductId() + "was not found!!!";
        assertEquals(expected, ex.getMessage());
    }



    // ---------------------------------------------------------
    // 5. updateProductQuantityInCart
    // ---------------------------------------------------------

    @Test
    void testUpdateProductQuantityInCart_Success() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.ofNullable(cartItem));
        when(cartItemRepo.save(any())).thenReturn(cartItem);

        CartDTO result = cartService.updateProductQuantityInCart(cart.getCartId(), product.getProductId(), 3);

        CartItem cartItemUpdated = cartItemRepo.findCartItemByProductIdAndCartId(result.getCartId(), result.getProducts().get(0).getProductId())
                .orElseThrow(() -> new APIException("Test function: cartItem doesnt exist!!!"));
        assertEquals(3, cartItemUpdated.getQuantity());
    }

    @Test
    void testUpdateProductQuantityInCart_ProductQuantityZero() {
        product.setQuantity(0);

        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));

        assertThrows(APIException.class, () -> cartService.updateProductQuantityInCart(cart.getCartId(), product.getProductId(), 1));
    }




    @Test
    void testUpdateProductQuantityInCart_NotEnoughStock() {
        product.setQuantity(1);

        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));

        assertThrows(APIException.class, () -> cartService.updateProductQuantityInCart(cart.getCartId(), product.getProductId(), 5));
    }


    @Test
    void testUpdateProductQuantityInCart_ProductNotAvailableInCart() {
        product.setQuantity(1);

        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.empty());

        assertThrows(APIException.class, () -> cartService.updateProductQuantityInCart(cart.getCartId(), product.getProductId(), 1));
    }


    @Test
    void testUpdateProductQuantityInCart_CartNotFound() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.updateProductQuantityInCart(cart.getCartId(), product.getProductId(), 1)
        );

        String expected = "Cart with cartId" + cart.getCartId() + "was not found!!!";
        assertEquals(expected, ex.getMessage());
    }


    @Test
    void testUpdateProductQuantityInCart_ProductNotFound() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepo.findById(product.getProductId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.updateProductQuantityInCart(cart.getCartId(), product.getProductId(), 1)
        );

        String expected = "Product with productId" + product.getProductId() + "was not found!!!";
        assertEquals(expected, ex.getMessage());
    }




    // ---------------------------------------------------------
    // 6. deleteProductFromCart
    // ---------------------------------------------------------

    @Test
    void testDeleteProductFromCart_Success() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.ofNullable(cartItem));

        String message = cartService.deleteProductFromCart(cart.getCartId(), product.getProductId());

        assertTrue(message.contains("removed"));
        verify(cartItemRepo, times(1)).deleteCartItemByProductIdAndCartId(product.getProductId(), cart.getCartId());
    }

    @Test
    void testDeleteProductFromCart_ItemNotFound() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(cartItemRepo.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.deleteProductFromCart(cart.getCartId(), product.getProductId())
        );

        String msg = ex.getMessage();
        String expected = "Cart item with cartId" + cart.getCartId()  + "and productId" + product.getProductId() + "was not found!!!";
        assertEquals(expected, msg);
    }


    @Test
    void testDeleteProductFromCart_CartNotFound() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.empty());

        APIException ex = assertThrows(
                APIException.class,
                () -> cartService.deleteProductFromCart(cart.getCartId(), product.getProductId())
        );

        String msg = ex.getMessage();
        String expected = "Cart with cartId" + cart.getCartId() + "was not found!!!";

    }



}
