package com.app.service;

import com.app.benchmark.AbstractBenchmark;
import com.app.dto.OrderDTO;
import com.app.dto.OrderResponse;
import com.app.model.Cart;
import com.app.model.CartItem;
import com.app.model.Order;
import com.app.model.Product;
import com.app.model.User;
import com.app.repository.CartItemRepo;
import com.app.repository.CartRepo;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class OrderServiceImplBenchmark extends AbstractBenchmark {

    private static OrderService orderService;
    private static UserRepo userRepo;
    private static CartRepo cartRepo;
    private static CartItemRepo cartItemRepo;
    private static ProductRepo productRepo;
    private static OrderRepo orderRepo;
    private static OrderItemRepo orderItemRepo;
    private static PaymentRepo paymentRepo;
    private static EntityManager em;

    @Autowired
    void setBeans(
            OrderService svc,
            UserRepo uRepo,
            CartRepo cRepo,
            CartItemRepo ciRepo,
            ProductRepo pRepo,
            OrderRepo oRepo,
            OrderItemRepo oiRepo,
            PaymentRepo payRepo,
            EntityManager entityManager
    ) {
        OrderServiceImplBenchmark.orderService = svc;
        OrderServiceImplBenchmark.userRepo = uRepo;
        OrderServiceImplBenchmark.cartRepo = cRepo;
        OrderServiceImplBenchmark.cartItemRepo = ciRepo;
        OrderServiceImplBenchmark.productRepo = pRepo;
        OrderServiceImplBenchmark.orderRepo = oRepo;
        OrderServiceImplBenchmark.orderItemRepo = oiRepo;
        OrderServiceImplBenchmark.paymentRepo = payRepo;
        OrderServiceImplBenchmark.em = entityManager;
    }

    @Param({"5"})
    public int cartItemsPerOrder;

    @Param({"5"})
    public int seedOrderCount;

    private String seedEmail;
    private Long seedOrderId;

    private String invEmail;
    private Long invUserId;
    private Long invCartId;
    private Long invOrderId;

    @Setup(Level.Trial)
    public void trialSetup() {
        if (em != null) em.clear();

        orderItemRepo.deleteAllInBatch();
        orderRepo.deleteAllInBatch();
        paymentRepo.deleteAllInBatch();

        cartItemRepo.deleteAllInBatch();
        cartRepo.deleteAllInBatch();
        productRepo.deleteAllInBatch();
        userRepo.deleteAllInBatch();

        if (em != null) em.clear();

        seedEmail = "seed-user-" + UUID.randomUUID() + "@example.com";
        User seedUser = makeUser(seedEmail);
        seedUser = userRepo.save(seedUser);

        for (int i = 0; i < seedOrderCount; i++) {
            Order o = new Order();
            setFieldOrSetterIfExists(o, "email", seedEmail);
            setFieldOrSetterIfExists(o, "Email", seedEmail);
            setFieldOrSetterIfExists(o, "orderStatus", "Order Accepted !");
            setFieldOrSetterIfExists(o, "OrderStatus", "Order Accepted !");
            setFieldOrSetterIfExists(o, "totalAmount", 0.0);
            setFieldOrSetterIfExists(o, "TotalAmount", 0.0);
            o = orderRepo.save(o);
            seedOrderId = getLongFieldOrGetter(o, "orderId", "OrderId", "id", "Id");
        }

        if (em != null) em.clear();
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
        if (em != null) em.clear();

        invEmail = "inv-user-" + UUID.randomUUID() + "@example.com";
        User invUser = makeUser(invEmail);
        invUser = userRepo.save(invUser);
        invUserId = getLongFieldOrGetter(invUser, "userId", "UserId", "id", "Id");

        Cart invCart = new Cart();
        setFieldOrSetterIfExists(invCart, "user", invUser);
        setFieldOrSetterIfExists(invCart, "User", invUser);
        setFieldOrSetterIfExists(invCart, "totalPrice", 0.0);
        setFieldOrSetterIfExists(invCart, "TotalPrice", 0.0);
        invCart = cartRepo.save(invCart);
        invCartId = invCart.getCartId();

        double total = 0.0;

        for (int i = 0; i < cartItemsPerOrder; i++) {
            Product p = makeProductEntity("Inv-Prod-" + i + "-" + UUID.randomUUID());
            p = productRepo.save(p);

            CartItem ci = new CartItem();
            setFieldOrSetterIfExists(ci, "cart", invCart);
            setFieldOrSetterIfExists(ci, "Cart", invCart);
            setFieldOrSetterIfExists(ci, "product", p);
            setFieldOrSetterIfExists(ci, "Product", p);
            setFieldOrSetterIfExists(ci, "quantity", 1);
            setFieldOrSetterIfExists(ci, "Quantity", 1);
            setFieldOrSetterIfExists(ci, "discount", getNumericFieldOrZero(p, "discount", "Discount"));
            setFieldOrSetterIfExists(ci, "productPrice", getNumericFieldOrZero(p, "specialPrice", "SpecialPrice"));
            cartItemRepo.save(ci);

            total += getNumericFieldOrZero(p, "specialPrice", "SpecialPrice");
        }

        invCart.setTotalPrice(total);
        cartRepo.save(invCart);

        invOrderId = null;

        if (em != null) em.clear();
    }

    @TearDown(Level.Invocation)
    public void invocationTearDown() {
        if (em != null) em.clear();

        try {
            if (invCartId != null) {
                cartItemRepo.deleteAllInBatch();
                cartRepo.deleteById(invCartId);
            }
        } catch (Exception ignored) {
        }

        try {
            if (invUserId != null) {
                userRepo.deleteById(invUserId);
            }
        } catch (Exception ignored) {
        }

        if (em != null) em.clear();
    }

    @Benchmark
    public void placeOrder_happyPath(Blackhole bh) {
        OrderDTO dto = orderService.placeOrder(invEmail, invCartId, "CARD");
        bh.consume(dto);
        Long oid = getLongFieldOrGetter(dto, "orderId", "OrderId", "id", "Id");
        invOrderId = oid;
    }

    @Benchmark
    public void getOrdersByUser_seed(Blackhole bh) {
        bh.consume(orderService.getOrdersByUser(seedEmail));
    }

    @Benchmark
    public void getOrder_seedExisting(Blackhole bh) {
        if (seedOrderId == null) {
            bh.consume(0);
            return;
        }
        bh.consume(orderService.getOrder(seedEmail, seedOrderId));
    }

    @Benchmark
    public void getAllOrders_page0(Blackhole bh) {
        OrderResponse res = orderService.getAllOrders(0, 10, "orderId", "asc");
        bh.consume(res);
    }

    @Benchmark
    public void updateOrder_seed(Blackhole bh) {
        if (seedOrderId == null) {
            bh.consume(0);
            return;
        }
        bh.consume(orderService.updateOrder(seedEmail, seedOrderId, "Shipped"));
    }

    private static User makeUser(String email) {
        User u = new User();
        setFieldOrSetterIfExists(u, "email", email);
        setFieldOrSetterIfExists(u, "Email", email);
        setFieldOrSetterIfExists(u, "username", "bench_" + UUID.randomUUID());
        setFieldOrSetterIfExists(u, "Username", "bench_" + UUID.randomUUID());
        setFieldOrSetterIfExists(u, "firstName", "Bench");
        setFieldOrSetterIfExists(u, "FirstName", "Bench");
        setFieldOrSetterIfExists(u, "lastName", "User");
        setFieldOrSetterIfExists(u, "LastName", "User");
        setFieldOrSetterIfExists(u, "password", "bench-password");
        setFieldOrSetterIfExists(u, "Password", "bench-password");
        return u;
    }

    private static Product makeProductEntity(String name) {
        Product p = new Product();

        setFieldOrSetterIfExists(p, "name", name);
        setFieldOrSetterIfExists(p, "Name", name);

        setFieldOrSetterIfExists(p, "description", "Bench description " + name);
        setFieldOrSetterIfExists(p, "Description", "Bench description " + name);

        setFieldOrSetterIfExists(p, "quantity", 100);
        setFieldOrSetterIfExists(p, "Quantity", 100);

        setFieldOrSetterIfExists(p, "discount", 0.0);
        setFieldOrSetterIfExists(p, "Discount", 0.0);

        setFieldOrSetterIfExists(p, "specialPrice", 9.99);
        setFieldOrSetterIfExists(p, "SpecialPrice", 9.99);

        setFieldOrSetterIfExists(p, "price", 9.99);
        setFieldOrSetterIfExists(p, "Price", 9.99);

        return p;
    }

    private static double getNumericFieldOrZero(Object target, String f1, String f2) {
        Object v = getFieldOrGetter(target, f1);
        if (v == null) v = getFieldOrGetter(target, f2);
        if (v instanceof Number n) return n.doubleValue();
        return 0.0;
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
            String getter = "get" + name;
            Method m = target.getClass().getMethod(getter);
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
