package com.app.service;

import com.app.benchmark.AbstractBenchmark;
import com.app.dto.CategoryDTO;
import com.app.dto.CategoryResponse;
import com.app.model.Category;
import com.app.model.Product;
import com.app.repository.CategoryRepo;
import com.app.repository.ProductRepo;

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
import java.util.stream.Collectors;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CategoryServiceImplBenchmark extends AbstractBenchmark {

    private static CategoryService categoryService;
    private static CategoryRepo categoryRepo;
    private static ProductRepo productRepo;
    private static EntityManager em;

    @Autowired
    void setBeans(CategoryService svc, CategoryRepo cRepo, ProductRepo pRepo, EntityManager entityManager) {
        CategoryServiceImplBenchmark.categoryService = svc;
        CategoryServiceImplBenchmark.categoryRepo = cRepo;
        CategoryServiceImplBenchmark.productRepo = pRepo;
        CategoryServiceImplBenchmark.em = entityManager;
    }

    @Param({"5"})
    public int seedCategoryCount;

    @Param({"5"})
    public int productsPerDeletableCategory;

    private String sortBy;
    private String sortOrder;

    private Long invocationCategoryIdEmpty;
    private Long invocationCategoryIdWithProducts;

    @Setup(Level.Trial)
    public void trialSetup() {
        if (em != null) em.clear();

        if (productRepo != null) productRepo.deleteAllInBatch();
        categoryRepo.deleteAllInBatch();

        if (em != null) em.clear();

        sortBy = pickSortableCategoryFieldOrFallback();
        sortOrder = "asc";

        for (int i = 0; i < seedCategoryCount; i++) {
            categoryRepo.save(makeCategoryEntity("Seed-Cat-" + i));
        }

        if (em != null) em.clear();
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
        Category empty = categoryRepo.save(makeCategoryEntity("Inv-Empty-" + UUID.randomUUID()));
        invocationCategoryIdEmpty = empty.getCategoryId();

        Category withProducts = categoryRepo.save(makeCategoryEntity("Inv-Del-" + UUID.randomUUID()));
        invocationCategoryIdWithProducts = withProducts.getCategoryId();

        if (productsPerDeletableCategory > 0) {
            for (int i = 0; i < productsPerDeletableCategory; i++) {
                Product p = makeProductEntity("Inv-Prod-" + i + "-" + UUID.randomUUID());

                setFieldOrSetterIfExists(p, "category", withProducts);
                setFieldOrSetterIfExists(p, "Category", withProducts);

                setFieldOrSetterIfExists(p, "categories", Set.of(withProducts));
                setFieldOrSetterIfExists(p, "Categories", Set.of(withProducts));

                setFieldOrSetterIfExists(p, "categoryId", invocationCategoryIdWithProducts);
                setFieldOrSetterIfExists(p, "CategoryId", invocationCategoryIdWithProducts);

                productRepo.save(p);
                tryAttachProductToCategory(withProducts, p);
            }
            categoryRepo.save(withProducts);
        }

        if (em != null) em.clear();
    }

    @TearDown(Level.Invocation)
    public void invocationTearDown() {
        if (em != null) em.clear();

        if (invocationCategoryIdEmpty != null) {
            safeDeleteCategoryById(invocationCategoryIdEmpty);
        }
        if (invocationCategoryIdWithProducts != null) {
            safeDeleteProductsForCategoryId(invocationCategoryIdWithProducts);
            safeDeleteCategoryById(invocationCategoryIdWithProducts);
        }

        if (em != null) em.clear();
    }

    @Benchmark
    public void createCategory_unique(Blackhole bh) {
        CategoryDTO dto = new CategoryDTO();
        setFieldOrSetterIfExists(dto, "categoryName", "Bench-Cat-" + UUID.randomUUID());
        setFieldOrSetterIfExists(dto, "CategoryName", "Bench-Cat-" + UUID.randomUUID());
        bh.consume(categoryService.createCategory(dto));
    }

    @Benchmark
    public void createCategory_duplicate_throws(Blackhole bh) {
        CategoryDTO dto = new CategoryDTO();
        setFieldOrSetterIfExists(dto, "categoryName", "Seed-Cat-0");
        setFieldOrSetterIfExists(dto, "CategoryName", "Seed-Cat-0");
        try {
            bh.consume(categoryService.createCategory(dto));
        } catch (RuntimeException ex) {
            bh.consume(ex.getClass());
        }
    }

    @Benchmark
    public void getCategories_page0(Blackhole bh) {
        CategoryResponse res = categoryService.getCategories(0, 10, sortBy, sortOrder);
        bh.consume(res);
    }

    @Benchmark
    public void updateCategory_rename_unique(Blackhole bh) {
        Category update = new Category();
        String nm = "Upd-" + UUID.randomUUID();
        setFieldOrSetterIfExists(update, "categoryName", nm);
        setFieldOrSetterIfExists(update, "CategoryName", nm);
        bh.consume(categoryService.updateCategory(update, invocationCategoryIdEmpty));
    }

    @Benchmark
    public void updateCategory_rename_duplicate_throws(Blackhole bh) {
        Category update = new Category();
        setFieldOrSetterIfExists(update, "categoryName", "Seed-Cat-1");
        setFieldOrSetterIfExists(update, "CategoryName", "Seed-Cat-1");
        try {
            bh.consume(categoryService.updateCategory(update, invocationCategoryIdEmpty));
        } catch (RuntimeException ex) {
            bh.consume(ex.getClass());
        }
    }

    @Benchmark
    public void deleteCategory_existing(Blackhole bh) {
        bh.consume(categoryService.deleteCategory(invocationCategoryIdWithProducts));
        invocationCategoryIdWithProducts = null;
    }

    private static Category makeCategoryEntity(String name) {
        Category c = new Category();
        setFieldOrSetterIfExists(c, "categoryName", name);
        setFieldOrSetterIfExists(c, "CategoryName", name);
        setFieldOrSetterIfExists(c, "name", name);
        setFieldOrSetterIfExists(c, "Name", name);
        return c;
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

    private static void tryAttachProductToCategory(Category category, Product product) {
        try {
            Field f = findField(category.getClass(), "products");
            if (f == null) f = findField(category.getClass(), "Products");
            if (f == null) return;
            f.setAccessible(true);
            Object val = f.get(category);
            if (val instanceof List<?> list) {
                @SuppressWarnings("unchecked")
                List<Product> products = (List<Product>) list;
                products.add(product);
            }
        } catch (Exception ignored) {
        }
    }

    private static void safeDeleteProductsForCategoryId(Long categoryId) {
        try {
            if (productRepo != null) productRepo.deleteAllInBatch();
        } catch (Exception ignored) {
        }
    }

    private static void safeDeleteCategoryById(Long categoryId) {
        try {
            categoryRepo.deleteById(categoryId);
        } catch (Exception ignored) {
        }
    }

    private String pickSortableCategoryFieldOrFallback() {
        Set<String> names = Arrays.stream(Category.class.getDeclaredFields())
                .filter(f -> !java.util.Collection.class.isAssignableFrom(f.getType()))
                .map(Field::getName)
                .collect(Collectors.toSet());

        for (String preferred : List.of("categoryId", "CategoryId", "id", "Id", "categoryName", "CategoryName", "name", "Name")) {
            if (names.contains(preferred)) return preferred;
        }
        return names.stream().findFirst().orElse("categoryId");
    }

    private static boolean setFieldOrSetterIfExists(Object target, String name, Object value) {
        if (target == null) return false;

        boolean ok = setFieldIfExists(target, name, value);
        if (ok) return true;

        String setter = "set" + name;
        try {
            Method[] ms = target.getClass().getMethods();
            for (Method m : ms) {
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

        if ((targetType == String.class)) return String.valueOf(value);

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
