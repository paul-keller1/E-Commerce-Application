package com.app.service;

import com.app.benchmark.AbstractBenchmark;
import com.app.config.UserInfoConfig;
import com.app.dto.CartDTO;
import com.app.model.Cart;
import com.app.model.CartItem;
import com.app.model.Product;
import com.app.model.User;
import com.app.repository.CartItemRepo;
import com.app.repository.CartRepo;
import com.app.repository.ProductRepo;
import com.app.repository.UserRepo;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CartServiceImplBenchmark extends AbstractBenchmark {

    private static CartService cartService;
    private static CartRepo cartRepo;
    private static ProductRepo productRepo;
    private static CartItemRepo cartItemRepo;
    private static UserRepo userRepo;
    private static EntityManager em;

    @Autowired
    void setBeans(CartService svc, CartRepo cRepo, ProductRepo pRepo, CartItemRepo ciRepo, UserRepo uRepo, EntityManager entityManager) {
        CartServiceImplBenchmark.cartService = svc;
        CartServiceImplBenchmark.cartRepo = cRepo;
        CartServiceImplBenchmark.productRepo = pRepo;
        CartServiceImplBenchmark.cartItemRepo = ciRepo;
        CartServiceImplBenchmark.userRepo = uRepo;
        CartServiceImplBenchmark.em = entityManager;
    }

    @Param({"10"})
    public int initialProductCount;

    private Long userId;
    private String userEmail;
    private Long cartId;

    private Long invProductIdFresh;
    private Long invProductIdInCart;

    @Setup(Level.Trial)
    public void trialSetup() {
        if (em != null) em.clear();

        cartItemRepo.deleteAllInBatch();
        cartRepo.deleteAllInBatch();
        productRepo.deleteAllInBatch();
        userRepo.deleteAllInBatch();

        if (em != null) em.clear();

        userEmail = "bench-user+" + UUID.randomUUID() + "@example.com";
        User u = createAndPersistBenchmarkUser(userEmail);
        userId = extractUserId(u);

        setSecurityPrincipalUserId(userId);

        Cart cart = new Cart();
        cart.setUser(u);
        cart.setTotalPrice(0.0);
        cart = cartRepo.save(cart);
        cartId = cart.getCartId();

        for (int i = 0; i < initialProductCount; i++) {
            productRepo.save(makeProduct("Seed-" + i, 100, 0.0, 9.99));
        }

        if (em != null) em.clear();
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
        setSecurityPrincipalUserId(userId);

        Product fresh = productRepo.save(makeProduct("Fresh-" + UUID.randomUUID(), 50, 0.0, 12.34));
        invProductIdFresh = fresh.getProductId();

        Product inCart = productRepo.save(makeProduct("InCart-" + UUID.randomUUID(), 50, 0.0, 7.89));
        invProductIdInCart = inCart.getProductId();

        Cart cart = cartRepo.findById(cartId).orElseThrow();

        CartItem ci = new CartItem();
        ci.setCart(cart);
        ci.setProduct(inCart);
        ci.setQuantity(2);
        ci.setDiscount(inCart.getDiscount());
        ci.setProductPrice(inCart.getSpecialPrice());
        cartItemRepo.save(ci);

        cart.setTotalPrice(cart.getTotalPrice() + (ci.getProductPrice() * ci.getQuantity()));
        cartRepo.save(cart);

        if (em != null) em.clear();
    }

    @TearDown(Level.Invocation)
    public void invocationTearDown() {
        if (em != null) em.clear();

        cartItemRepo.deleteAllInBatch();

        Cart cart = cartRepo.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setTotalPrice(0.0);
            cartRepo.save(cart);
        }

        if (invProductIdFresh != null) {
            safeDeleteProduct(invProductIdFresh);
        }
        if (invProductIdInCart != null) {
            safeDeleteProduct(invProductIdInCart);
        }

        if (em != null) em.clear();
        SecurityContextHolder.clearContext();
    }

    @Benchmark
    public void addProductToCart_newProduct(Blackhole bh) {
        CartDTO res = cartService.addProductToCart(invProductIdFresh, 3);
        bh.consume(res);
    }

    @Benchmark
    public void addProductToCart_alreadyExists_throws(Blackhole bh) {
        try {
            bh.consume(cartService.addProductToCart(invProductIdInCart, 1));
        } catch (RuntimeException ex) {
            bh.consume(ex.getClass());
        }
    }

    @Benchmark
    public void updateProductQuantityInCart_existing(Blackhole bh) {
        CartDTO res = cartService.updateProductQuantityInCart(cartId, invProductIdInCart, 5);
        bh.consume(res);
    }

    @Benchmark
    public void deleteProductFromCart_existing(Blackhole bh) {
        String res = cartService.deleteProductFromCart(cartId, invProductIdInCart);
        bh.consume(res);
    }

    @Benchmark
    public void updateProductInCarts_existing(Blackhole bh) {
        cartService.updateProductInCarts(cartId, invProductIdInCart);
        bh.consume(1);
    }

    @Benchmark
    public void getAllCarts(Blackhole bh) {
        bh.consume(cartService.getAllCarts());
    }

    @Benchmark
    public void getCart_byEmailAndId(Blackhole bh) {
        bh.consume(cartService.getCart(userEmail, cartId));
    }

    private static Product makeProduct(String name, int quantity, double discount, double specialPrice) {
        Product p = new Product();
        p.setName(name);
        p.setDescription("Bench description " + name);
        p.setQuantity(quantity);
        p.setDiscount(discount);
        p.setSpecialPrice(specialPrice);
        return p;
    }

    private static void safeDeleteProduct(Long productId) {
        try {
            productRepo.deleteById(productId);
        } catch (Exception ignored) {
        }
    }

    private static void setSecurityPrincipalUserId(Long userId) {
        Object principal = instantiateUserInfoConfigWithUserId(userId);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private static Object instantiateUserInfoConfigWithUserId(Long userId) {
        try {
            UserInfoConfig cfg = new UserInfoConfig();

            try {
                cfg.getClass().getMethod("setUserId", Long.class).invoke(cfg, userId);
                return cfg;
            } catch (NoSuchMethodException ignored) {
            }

            if (setFieldIfExists(cfg, "userId", userId)) return cfg;
            if (setFieldIfExists(cfg, "id", userId)) return cfg;

            return cfg;
        } catch (Exception e) {
            try {
                Constructor<?>[] ctors = UserInfoConfig.class.getDeclaredConstructors();
                for (Constructor<?> c : ctors) {
                    Class<?>[] params = c.getParameterTypes();
                    if (params.length == 1 && (params[0] == Long.class || params[0] == long.class)) {
                        c.setAccessible(true);
                        return c.newInstance(userId);
                    }
                }
            } catch (Exception ignored) {
            }
            return new Object();
        }
    }

    private static User createAndPersistBenchmarkUser(String email) {
        User u = new User();
        setFieldIfExists(u, "email", email);
        setFieldIfExists(u, "username", "bench_" + UUID.randomUUID());
        setFieldIfExists(u, "firstName", "Bench");
        setFieldIfExists(u, "lastName", "User");
        setFieldIfExists(u, "password", "bench-password");
        return userRepo.save(u);
    }

    private static Long extractUserId(User u) {
        try {
            return (Long) u.getClass().getMethod("getUserId").invoke(u);
        } catch (Exception ignored) {
        }

        try {
            return (Long) u.getClass().getMethod("getId").invoke(u);
        } catch (Exception ignored) {
        }

        Object val = getFieldIfExists(u, "userId");
        if (val instanceof Long l) return l;
        val = getFieldIfExists(u, "id");
        if (val instanceof Long l) return l;

        throw new IllegalStateException("Could not extract user id from User entity (expected getUserId()/getId() or field userId/id).");
    }

    private static boolean setFieldIfExists(Object target, String fieldName, Object value) {
        try {
            Field f = findField(target.getClass(), fieldName);
            if (f == null) return false;
            f.setAccessible(true);
            f.set(target, value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Object getFieldIfExists(Object target, String fieldName) {
        try {
            Field f = findField(target.getClass(), fieldName);
            if (f == null) return null;
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Field findField(Class<?> cls, String name) {
        Class<?> cur = cls;
        while (cur != null && cur != Object.class) {
            try {
                return cur.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                cur = cur.getSuperclass();
            }
        }
        return null;
    }
}
