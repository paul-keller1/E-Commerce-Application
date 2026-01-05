package com.app.service;

import com.app.benchmark.AbstractBenchmark;
import com.app.dto.ProductDTO;
import com.app.dto.ProductResponse;
import com.app.model.Category;
import com.app.model.Product;
import com.app.repository.CartItemRepo;
import com.app.repository.CartRepo;
import com.app.repository.CategoryRepo;
import com.app.repository.OrderItemRepo;
import com.app.repository.OrderRepo;
import com.app.repository.PaymentRepo;
import com.app.repository.ProductRepo;
import com.app.repository.UserRepo;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ProductServiceImplBenchmark extends AbstractBenchmark {

    private static ProductService productService;
    private static ProductRepo productRepo;
    private static CategoryRepo categoryRepo;
    private static CartRepo cartRepo;
    private static CartItemRepo cartItemRepo;
    private static UserRepo userRepo;
    private static OrderItemRepo orderItemRepo;
    private static PaymentRepo paymentRepo;
    private static OrderRepo orderRepo;
    private static EntityManager em;

    @Autowired
    void setBeans(
            ProductService svc,
            ProductRepo pRepo,
            CategoryRepo cRepo,
            CartRepo caRepo,
            CartItemRepo ciRepo,
            UserRepo uRepo,
            OrderItemRepo oiRepo,
            PaymentRepo payRepo,
            OrderRepo oRepo,
            EntityManager entityManager
    ) {
        ProductServiceImplBenchmark.productService = svc;
        ProductServiceImplBenchmark.productRepo = pRepo;
        ProductServiceImplBenchmark.categoryRepo = cRepo;
        ProductServiceImplBenchmark.cartRepo = caRepo;
        ProductServiceImplBenchmark.cartItemRepo = ciRepo;
        ProductServiceImplBenchmark.userRepo = uRepo;
        ProductServiceImplBenchmark.orderItemRepo = oiRepo;
        ProductServiceImplBenchmark.paymentRepo = payRepo;
        ProductServiceImplBenchmark.orderRepo = oRepo;
        ProductServiceImplBenchmark.em = entityManager;
    }

    @Param({"5"})
    public int seedProductCount;

    private Long categoryId;

    private Long invProductId;
    private MockMultipartFile invImage;

    private String sortField;

    @Setup(Level.Trial)
    public void trialSetup() {
        if (em != null) em.clear();

        orderItemRepo.deleteAllInBatch();
        orderRepo.deleteAllInBatch();
        paymentRepo.deleteAllInBatch();

        cartItemRepo.deleteAllInBatch();
        cartRepo.deleteAllInBatch();

        productRepo.deleteAllInBatch();
        categoryRepo.deleteAllInBatch();
        userRepo.deleteAllInBatch();

        if (em != null) em.clear();

        Category c = new Category();
        setFieldOrSetterIfExists(c, "categoryName", "Bench-Cat-" + UUID.randomUUID());
        setFieldOrSetterIfExists(c, "CategoryName", "Bench-Cat-" + UUID.randomUUID());
        c = categoryRepo.save(c);
        categoryId = getLongFieldOrGetter(c, "categoryId", "CategoryId", "id", "Id");

        for (int i = 0; i < seedProductCount; i++) {
            Product p = makeProductEntity("Seed-Prod-" + i, "Seed-Desc-" + i, 100, 99.99, 10.0);
            setFieldOrSetterIfExists(p, "image", "default.png");
            setFieldOrSetterIfExists(p, "Image", "default.png");
            setFieldOrSetterIfExists(p, "category", c);
            setFieldOrSetterIfExists(p, "Category", c);
            productRepo.save(p);
        }

        invImage = new MockMultipartFile(
                "file",
                "bench.png",
                "image/png",
                ("bench-img-" + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8)
        );

        sortField = chooseSortField(Product.class, List.of("productId", "ProductId", "specialPrice", "SpecialPrice", "price", "Price"));

        if (em != null) em.clear();
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
        Product p = makeProductEntity(
                "Inv-Prod-" + UUID.randomUUID(),
                "Inv-Desc-" + UUID.randomUUID(),
                100,
                123.45,
                5.0
        );

        Category c = categoryRepo.findById(categoryId).orElseThrow();
        setFieldOrSetterIfExists(p, "image", "default.png");
        setFieldOrSetterIfExists(p, "Image", "default.png");
        setFieldOrSetterIfExists(p, "category", c);
        setFieldOrSetterIfExists(p, "Category", c);

        p = productRepo.save(p);
        invProductId = getLongFieldOrGetter(p, "productId", "ProductId", "id", "Id");

        if (em != null) em.clear();
    }

    @TearDown(Level.Invocation)
    public void invocationTearDown() {
        if (em != null) em.clear();

        try {
            if (invProductId != null) {
                productRepo.deleteById(invProductId);
            }
        } catch (Exception ignored) {
        }

        if (em != null) em.clear();
    }

    @Benchmark
    public void addProduct_uniqueDescription(Blackhole bh) {
        ProductDTO dto = new ProductDTO();
        String nm = "Bench-Prod-" + UUID.randomUUID();
        String desc = "Bench-Desc-" + UUID.randomUUID();

        setFieldOrSetterIfExists(dto, "name", nm);
        setFieldOrSetterIfExists(dto, "Name", nm);
        setFieldOrSetterIfExists(dto, "description", desc);
        setFieldOrSetterIfExists(dto, "Description", desc);
        setFieldOrSetterIfExists(dto, "quantity", 100);
        setFieldOrSetterIfExists(dto, "Quantity", 100);
        setFieldOrSetterIfExists(dto, "price", 99.99);
        setFieldOrSetterIfExists(dto, "Price", 99.99);
        setFieldOrSetterIfExists(dto, "discount", 10.0);
        setFieldOrSetterIfExists(dto, "Discount", 10.0);

        bh.consume(productService.addProduct(categoryId, dto));
    }

    @Benchmark
    public void getAllProductsResponse_page0(Blackhole bh) {
        ProductResponse res = productService.getAllProductsResponse(0, 10, sortField, "asc");
        bh.consume(res);
    }

    @Benchmark
    public void getAllProductsFull(Blackhole bh) {
        bh.consume(productService.getAllProductsFull());
    }

    @Benchmark
    public void updateProduct_basic(Blackhole bh) {
        Product update = new Product();
        String nm = "Upd-" + UUID.randomUUID();
        String desc = "Upd-Desc-" + UUID.randomUUID();

        setFieldOrSetterIfExists(update, "name", nm);
        setFieldOrSetterIfExists(update, "Name", nm);
        setFieldOrSetterIfExists(update, "description", desc);
        setFieldOrSetterIfExists(update, "Description", desc);
        setFieldOrSetterIfExists(update, "quantity", 100);
        setFieldOrSetterIfExists(update, "Quantity", 100);
        setFieldOrSetterIfExists(update, "price", 111.11);
        setFieldOrSetterIfExists(update, "Price", 111.11);
        setFieldOrSetterIfExists(update, "discount", 7.0);
        setFieldOrSetterIfExists(update, "Discount", 7.0);

        bh.consume(productService.updateProduct(invProductId, update));
    }

    @Benchmark
    public void updateProductImage(Blackhole bh) throws Exception {
        bh.consume(productService.updateProductImage(invProductId, invImage));
    }

    @Benchmark
    public void deleteProduct_existing(Blackhole bh) {
        bh.consume(productService.deleteProduct(invProductId));
        invProductId = null;
    }

    private static String chooseSortField(Class<?> cls, List<String> candidates) {
        Set<String> fields = new HashSet<>();
        for (Field f : cls.getDeclaredFields()) fields.add(f.getName());
        for (String c : candidates) {
            if (fields.contains(c)) return c;
        }
        return candidates.getLast();
    }

    private static Product makeProductEntity(String name, String description, int quantity, double price, double discount) {
        Product p = new Product();

        setFieldOrSetterIfExists(p, "name", name);
        setFieldOrSetterIfExists(p, "Name", name);

        setFieldOrSetterIfExists(p, "description", description);
        setFieldOrSetterIfExists(p, "Description", description);

        setFieldOrSetterIfExists(p, "quantity", quantity);
        setFieldOrSetterIfExists(p, "Quantity", quantity);

        setFieldOrSetterIfExists(p, "price", price);
        setFieldOrSetterIfExists(p, "Price", price);

        setFieldOrSetterIfExists(p, "discount", discount);
        setFieldOrSetterIfExists(p, "Discount", discount);

        double specialPrice = price - ((discount * 0.01) * price);
        setFieldOrSetterIfExists(p, "specialPrice", specialPrice);
        setFieldOrSetterIfExists(p, "SpecialPrice", specialPrice);

        return p;
    }

    private static Object getFieldOrGetter(Object target, String name) {
        if (target == null) return null;
        try {
            Field f = findField(target.getClass(), name);
            if (f != null) {
                f.setAccessible(true);
                return f.get(target);
            }
        } catch (Exception ignored) {
        }
        try {
            Method m = target.getClass().getMethod("get" + name);
            return m.invoke(target);
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Long getLongFieldOrGetter(Object target, String... names) {
        for (String n : names) {
            Object v = getFieldOrGetter(target, n);
            if (v instanceof Number num) return num.longValue();
        }
        return null;
    }

    private static boolean setFieldOrSetterIfExists(Object target, String name, Object value) {
        if (target == null) return false;

        boolean ok = setFieldIfExists(target, name, value);
        if (ok) return true;

        String setter = "set" + name;
        try {
            for (Method m : target.getClass().getMethods()) {
                if (!m.getName().equals(setter)) continue;
                if (m.getParameterCount() != 1) continue;
                Class<?> pt = m.getParameterTypes()[0];
                Object coerced = coerce(value, pt);
                if (coerced == CoerceFail.INSTANCE) continue;
                m.invoke(target, coerced);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static boolean setFieldIfExists(Object target, String fieldName, Object value) {
        try {
            Field f = findField(target.getClass(), fieldName);
            if (f == null) return false;
            f.setAccessible(true);
            Object coerced = coerce(value, f.getType());
            if (coerced == CoerceFail.INSTANCE) return false;
            f.set(target, coerced);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Object coerce(Object value, Class<?> targetType) {
        if (value == null) {
            if (targetType.isPrimitive()) return CoerceFail.INSTANCE;
            return null;
        }
        if (targetType.isInstance(value)) return value;

        if ((targetType == int.class || targetType == Integer.class) && value instanceof Number n) return n.intValue();
        if ((targetType == long.class || targetType == Long.class) && value instanceof Number n) return n.longValue();
        if ((targetType == double.class || targetType == Double.class) && value instanceof Number n) return n.doubleValue();
        if ((targetType == float.class || targetType == Float.class) && value instanceof Number n) return n.floatValue();
        if (targetType == String.class) return String.valueOf(value);

        return CoerceFail.INSTANCE;
    }

    private enum CoerceFail { INSTANCE }

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
