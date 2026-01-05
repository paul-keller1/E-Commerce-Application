package com.app.service;

import com.app.benchmark.AbstractBenchmark;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class UserDetailsServiceImplBenchmark extends AbstractBenchmark {

    private static UserDetailsServiceImpl userDetailsService;
    private static UserRepo userRepo;

    private static CartItemRepo cartItemRepo;
    private static CartRepo cartRepo;
    private static OrderItemRepo orderItemRepo;
    private static OrderRepo orderRepo;
    private static PaymentRepo paymentRepo;
    private static ProductRepo productRepo;

    private static EntityManager em;

    @Autowired
    void setBeans(
            UserDetailsServiceImpl svc,
            UserRepo uRepo,
            CartItemRepo ciRepo,
            CartRepo cRepo,
            OrderItemRepo oiRepo,
            OrderRepo oRepo,
            PaymentRepo payRepo,
            ProductRepo pRepo,
            EntityManager entityManager
    ) {
        UserDetailsServiceImplBenchmark.userDetailsService = svc;
        UserDetailsServiceImplBenchmark.userRepo = uRepo;
        UserDetailsServiceImplBenchmark.cartItemRepo = ciRepo;
        UserDetailsServiceImplBenchmark.cartRepo = cRepo;
        UserDetailsServiceImplBenchmark.orderItemRepo = oiRepo;
        UserDetailsServiceImplBenchmark.orderRepo = oRepo;
        UserDetailsServiceImplBenchmark.paymentRepo = payRepo;
        UserDetailsServiceImplBenchmark.productRepo = pRepo;
        UserDetailsServiceImplBenchmark.em = entityManager;
    }

    private String existingEmail;
    private String missingEmail;

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

        existingEmail = "bench-user-" + UUID.randomUUID() + "@example.com";
        missingEmail = "missing-user-" + UUID.randomUUID() + "@example.com";

        User u = new User();
        setFieldOrSetterIfExists(u, "email", existingEmail);
        setFieldOrSetterIfExists(u, "Email", existingEmail);
        setFieldOrSetterIfExists(u, "username", "bench_" + UUID.randomUUID());
        setFieldOrSetterIfExists(u, "Username", "bench_" + UUID.randomUUID());
        setFieldOrSetterIfExists(u, "firstName", "Bench");
        setFieldOrSetterIfExists(u, "FirstName", "Bench");
        setFieldOrSetterIfExists(u, "lastName", "User");
        setFieldOrSetterIfExists(u, "LastName", "User");
        setFieldOrSetterIfExists(u, "password", "bench-password");
        setFieldOrSetterIfExists(u, "Password", "bench-password");

        userRepo.save(u);

        if (em != null) em.clear();
    }

    @Benchmark
    public void loadUserByUsername_existing(Blackhole bh) {
        UserDetails ud = userDetailsService.loadUserByUsername(existingEmail);
        bh.consume(ud);
    }

    @Benchmark
    public void loadUserByUsername_missing_throws(Blackhole bh) {
        try {
            bh.consume(userDetailsService.loadUserByUsername(missingEmail));
        } catch (RuntimeException ex) {
            bh.consume(ex.getClass());
        }
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
