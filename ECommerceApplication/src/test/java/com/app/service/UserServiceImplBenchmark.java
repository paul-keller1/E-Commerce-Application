package com.app.service;

import com.app.benchmark.AbstractBenchmark;
import com.app.dto.AddressDTO;
import com.app.dto.UserCreateDTO;
import com.app.dto.UserDTO;
import com.app.model.Address;
import com.app.model.Cart;
import com.app.model.User;
import com.app.repository.AddressRepo;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class UserServiceImplBenchmark extends AbstractBenchmark {

    private static UserService userService;

    private static UserRepo userRepo;
    private static AddressRepo addressRepo;

    private static CartRepo cartRepo;
    private static CartItemRepo cartItemRepo;

    private static ProductRepo productRepo;

    private static OrderItemRepo orderItemRepo;
    private static OrderRepo orderRepo;
    private static PaymentRepo paymentRepo;

    private static PasswordEncoder passwordEncoder;
    private static EntityManager em;

    @Autowired
    void setBeans(
            UserService svc,
            UserRepo uRepo,
            AddressRepo aRepo,
            CartRepo cRepo,
            CartItemRepo ciRepo,
            ProductRepo pRepo,
            OrderItemRepo oiRepo,
            OrderRepo oRepo,
            PaymentRepo payRepo,
            PasswordEncoder pe,
            EntityManager entityManager
    ) {
        UserServiceImplBenchmark.userService = svc;
        UserServiceImplBenchmark.userRepo = uRepo;
        UserServiceImplBenchmark.addressRepo = aRepo;
        UserServiceImplBenchmark.cartRepo = cRepo;
        UserServiceImplBenchmark.cartItemRepo = ciRepo;
        UserServiceImplBenchmark.productRepo = pRepo;
        UserServiceImplBenchmark.orderItemRepo = oiRepo;
        UserServiceImplBenchmark.orderRepo = oRepo;
        UserServiceImplBenchmark.paymentRepo = payRepo;
        UserServiceImplBenchmark.passwordEncoder = pe;
        UserServiceImplBenchmark.em = entityManager;
    }

    @Param({"5"})
    public int seedUserCount;

    private Long existingUserId;
    private String existingUserEmail;
    private String userSortField;

    private UserCreateDTO invCreateDto;
    private UserCreateDTO invCreateDtoDuplicateEmail;
    private UserDTO invUpdateDto;

    private static final SecureRandom RNG = new SecureRandom();
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";

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
        addressRepo.deleteAllInBatch();

        if (em != null) em.clear();

        userSortField = chooseUserSortField();

        for (int i = 0; i < seedUserCount; i++) {
            String email = "seed" + i + "." + UUID.randomUUID() + "@example.com";

            User u = new User();
            setFieldOrSetterIfExists(u, "email", email);
            setFieldOrSetterIfExists(u, "Email", email);

            String fn = lettersOnly(8);
            String ln = lettersOnly(10);

            setFieldOrSetterIfExists(u, "firstName", fn);
            setFieldOrSetterIfExists(u, "FirstName", fn);

            setFieldOrSetterIfExists(u, "lastName", ln);
            setFieldOrSetterIfExists(u, "LastName", ln);

            setFieldOrSetterIfExists(u, "mobileNumber", "0123456789");
            setFieldOrSetterIfExists(u, "MobileNumber", "0123456789");

            String enc = passwordEncoder != null ? passwordEncoder.encode("seedpassword") : "seedpassword";
            setFieldOrSetterIfExists(u, "password", enc);
            setFieldOrSetterIfExists(u, "Password", enc);

            Address addr = new Address("DE", "BE", "Berlin", "10115", "Street" + i, "Building" + i);
            setFieldOrSetterIfExists(u, "addresses", List.of(addr));
            setFieldOrSetterIfExists(u, "Addresses", List.of(addr));

            Cart cart = new Cart();
            setFieldOrSetterIfExists(cart, "user", u);
            setFieldOrSetterIfExists(cart, "User", u);
            setFieldOrSetterIfExists(cart, "totalPrice", 0.0);
            setFieldOrSetterIfExists(cart, "TotalPrice", 0.0);

            setFieldOrSetterIfExists(u, "cart", cart);
            setFieldOrSetterIfExists(u, "Cart", cart);

            userRepo.save(u);
        }

        User any = userRepo.findAll().stream().findFirst().orElseThrow();
        existingUserId = getLongFieldOrGetter(any, "userId", "UserId", "id", "Id");

        Object eml = getFieldOrGetter(any, "email");
        if (eml == null) eml = getFieldOrGetter(any, "Email");
        existingUserEmail = eml != null ? eml.toString() : "seed@example.com";

        if (em != null) em.clear();
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
        invCreateDto = makeCreateDto("bench." + UUID.randomUUID() + "@example.com");
        invCreateDtoDuplicateEmail = makeCreateDto(existingUserEmail);

        invUpdateDto = new UserDTO();

        String fn = lettersOnly(10);
        String ln = lettersOnly(12);

        setFieldOrSetterIfExists(invUpdateDto, "firstName", fn);
        setFieldOrSetterIfExists(invUpdateDto, "FirstName", fn);

        setFieldOrSetterIfExists(invUpdateDto, "lastName", ln);
        setFieldOrSetterIfExists(invUpdateDto, "LastName", ln);

        setFieldOrSetterIfExists(invUpdateDto, "mobileNumber", "0987654321");
        setFieldOrSetterIfExists(invUpdateDto, "MobileNumber", "0987654321");

        setFieldOrSetterIfExists(invUpdateDto, "email", "upd." + UUID.randomUUID() + "@example.com");
        setFieldOrSetterIfExists(invUpdateDto, "Email", "upd." + UUID.randomUUID() + "@example.com");

        setFieldOrSetterIfExists(invUpdateDto, "password", "newpassword");
        setFieldOrSetterIfExists(invUpdateDto, "Password", "newpassword");

        AddressDTO addrDto = new AddressDTO();
        addrDto.setCountry("DE");
        addrDto.setState("BY");
        addrDto.setCity("Munich");
        addrDto.setPincode("80331");
        addrDto.setStreet("MainStreet");
        addrDto.setBuildingName("AlphaHouse");
        invUpdateDto.setAddress(addrDto);

        if (em != null) em.clear();
    }

    @Benchmark
    public void registerUser_unique(Blackhole bh) {
        bh.consume(userService.registerUser(invCreateDto));
    }

    @Benchmark
    public void registerUser_duplicateEmail_throws(Blackhole bh) {
        try {
            bh.consume(userService.registerUser(invCreateDtoDuplicateEmail));
        } catch (RuntimeException ex) {
            bh.consume(ex.getClass());
        }
    }

    @Benchmark
    public void getAllUsers_page0(Blackhole bh) {
        bh.consume(userService.getAllUsers(0, 10, userSortField, "asc"));
    }

    @Benchmark
    public void getUserById_existing(Blackhole bh) {
        bh.consume(userService.getUserById(existingUserId));
    }

    @Benchmark
    public void updateUser_existing(Blackhole bh) {
        bh.consume(userService.updateUser(existingUserId, invUpdateDto));
    }

    @Benchmark
    public void deleteUser_existing(Blackhole bh) {
        UserDTO created = userService.registerUser(makeCreateDto("del." + UUID.randomUUID() + "@example.com"));
        Long id = extractUserIdFromUserDTO(created);
        bh.consume(userService.deleteUser(id));
    }

    private static String lettersOnly(int len) {
        int n = Math.max(1, Math.min(len, 30));
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(ALPHA.charAt(RNG.nextInt(ALPHA.length())));
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private String chooseUserSortField() {
        Set<String> fields = new HashSet<>();
        for (Field f : User.class.getDeclaredFields()) fields.add(f.getName());
        if (fields.contains("userId")) return "userId";
        if (fields.contains("id")) return "id";
        if (fields.contains("email")) return "email";
        if (!fields.isEmpty()) return fields.iterator().next();
        return "email";
    }

    private static UserCreateDTO makeCreateDto(String email) {
        UserCreateDTO dto = new UserCreateDTO();

        setFieldOrSetterIfExists(dto, "firstName", lettersOnly(8));
        setFieldOrSetterIfExists(dto, "FirstName", lettersOnly(8));

        setFieldOrSetterIfExists(dto, "lastName", lettersOnly(10));
        setFieldOrSetterIfExists(dto, "LastName", lettersOnly(10));

        setFieldOrSetterIfExists(dto, "mobileNumber", "0123456789");
        setFieldOrSetterIfExists(dto, "MobileNumber", "0123456789");

        setFieldOrSetterIfExists(dto, "email", email);
        setFieldOrSetterIfExists(dto, "Email", email);

        setFieldOrSetterIfExists(dto, "password", "benchpassword");
        setFieldOrSetterIfExists(dto, "Password", "benchpassword");

        AddressDTO addr = new AddressDTO();
        addr.setCountry("DE");
        addr.setState("BE");
        addr.setCity("Berlin");
        addr.setPincode("10115");
        addr.setStreet("Street");
        addr.setBuildingName("Building");

        try {
            Method m = dto.getClass().getMethod("setAddress", AddressDTO.class);
            m.invoke(dto, addr);
        } catch (Exception ignored) {
            setFieldOrSetterIfExists(dto, "address", addr);
            setFieldOrSetterIfExists(dto, "Address", addr);
        }

        return dto;
    }

    private static Long extractUserIdFromUserDTO(UserDTO dto) {
        Object v = getFieldOrGetter(dto, "userId");
        if (v == null) v = getFieldOrGetter(dto, "UserId");
        if (v == null) v = getFieldOrGetter(dto, "id");
        if (v == null) v = getFieldOrGetter(dto, "Id");
        if (v instanceof Number n) return n.longValue();
        return null;
    }

    private static Object getFieldOrGetter(Object target, String name) {
        if (target == null) return null;
        try {
            Field f = findField(target.getClass(), name);
            if (f != null) {
                f.setAccessible(true);
                return f.get(target);
            }
        } catch (Exception ignored) { }
        try {
            Method m = target.getClass().getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
            return m.invoke(target);
        } catch (Exception ignored) { }
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
        } catch (Exception ignored) { }
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
