package com.app.services;

import com.app.model.Category;
import com.app.model.Product;
import com.app.exceptions.APIException;
import com.app.payloads.CategoryDTO;
import com.app.payloads.CategoryResponse;
import com.app.repositories.CategoryRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    private AutoCloseable autoCloseable;

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private ProductService productService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("E-Books");

        Product product = new Product();
        product.setCategory(category);
    }

    @AfterEach
    void tearDown() {
        try {
            autoCloseable.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    // ---------------------------------------------------------
    // 1. createCategory
    // ---------------------------------------------------------

    @Test
    void createCategoryTest_SUCCESS() {
        when(categoryRepo.findByCategoryName(category.getCategoryName())).thenReturn(null);
        when(categoryRepo.save(category)).thenReturn(category);

        assertEquals(modelMapper.map(category, CategoryDTO.class), categoryService.createCategory(category));
    }

    @Test
    void createCategory_CategoryAlreadyExists_ThrowsAPIException() {
        Category anotherCategory = new Category();
        anotherCategory.setCategoryName(category.getCategoryName());

        when(categoryRepo.findByCategoryName(anotherCategory.getCategoryName()))
                .thenReturn(category);

        assertThrows(APIException.class, () -> categoryService.createCategory(anotherCategory));
    }

    @Test
    void createCategory_CategoryNameTooShort_ThrowsAPIException() {
        category.setCategoryName("a");
        when(categoryRepo.findByCategoryName(category.getCategoryName())).thenReturn(null);
        when(categoryRepo.save(category)).thenThrow(APIException.class);

        assertThrows(APIException.class, () -> categoryService.createCategory(category));
    }


    // ---------------------------------------------------------
    // 2. getCategories
    // ---------------------------------------------------------

    @Test
    void getCategories_SUCCESS() {
        List<Category> categories = new ArrayList<>();

        for (long i = 2; i <= 11; i++) {
            Category c = new Category();
            c.setCategoryId(i);
            c.setCategoryName("Category " + i);
            categories.add(c);
        }

        int pageNumber = 2;
        int pageSize = 5;

        categories.add(category);
        when(categoryRepo.count()).thenReturn(11L);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageSize, categories.size());
        Page<Category> page = new PageImpl<>(categories.subList(start, end), pageable, categories.size());

        Sort sortByAndOrder = Sort.by("categoryName").ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        when(categoryRepo.findAll(pageDetails)).thenReturn(page);

        CategoryResponse expectedResponse = new CategoryResponse();

        List<CategoryDTO> categoryDTOs = List.of(modelMapper.map(category, CategoryDTO.class));

        expectedResponse.setContent(categoryDTOs);
        expectedResponse.setPageNumber(2);
        expectedResponse.setPageSize(5);
        expectedResponse.setTotalElements(11L);
        expectedResponse.setTotalPages(3);
        expectedResponse.setLastPage(true);

        assertEquals(expectedResponse, categoryService.getCategories(2, 5, "categoryName", "asc"));
    }

    // tests if sorting actually works
    @Test
    void getCategories_SUCCESS_SortDescCategoryId() {
        int pageNumber = 0;
        int pageSize = 5;

        List<Category> categories = new ArrayList<>();
        for (long i = 1; i <= 6; i++) {
            Category c = new Category();
            c.setCategoryId(i);
            c.setCategoryName("Category " + i);
            categories.add(c);
        }

        when(categoryRepo.count()).thenReturn((long) categories.size());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("categoryId").descending());

        List<Category> pageContent = new ArrayList<>(categories.subList(1, 6)); // 2..6
        Collections.reverse(pageContent); // 6..2

        Page<Category> page = new PageImpl<>(pageContent, pageable, categories.size());
        when(categoryRepo.findAll(pageable)).thenReturn(page);

        CategoryResponse result =
                categoryService.getCategories(pageNumber, pageSize, "categoryId", "desc");

        assertEquals(5, result.getContent().size());
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(6L, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertFalse(result.isLastPage());

        List<Long> ids = result.getContent().stream()
                .map(CategoryDTO::getCategoryId)
                .collect(Collectors.toList());

        assertEquals(List.of(6L, 5L, 4L, 3L, 2L), ids);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(categoryRepo).findAll(captor.capture());

        Sort.Order order = captor.getValue().getSort().getOrderFor("categoryId");
        assertNotNull(order);
        assertTrue(order.isDescending());
    }

    @Test
    void getCategories_InvalidSortField_ThrowsAPIException() {
        APIException ex = assertThrows(APIException.class, () -> categoryService.getCategories(0, 5, "nonExistingField", "asc"));
        assertTrue(ex.getMessage().contains("Invalid sort field"));
    }

    @Test
    void getCategories_InvalidSortOrder_ThrowsAPIException() {
        APIException ex = assertThrows(APIException.class, () -> categoryService.getCategories(0, 5, "categoryName", "abc"));
        assertTrue(ex.getMessage().contains("Invalid sort order"));
    }

    @Test
    void getCategories_NoCategories_ThrowsAPIException() {
        when(categoryRepo.count()).thenReturn(0L);

        APIException ex = assertThrows(APIException.class, () -> categoryService.getCategories(0, 5, "categoryName", "asc"));

        assertTrue(ex.getMessage().contains("No category is created till now"));
    }

    @Test
    void getCategories_PageNumberTooHigh_ThrowsAPIException() {
        when(categoryRepo.count()).thenReturn(11L);

        APIException ex = assertThrows(APIException.class, () -> categoryService.getCategories(3, 5, "categoryName", "asc"));

        assertTrue(ex.getMessage().contains("Page number out of range"));
    }

    @Test
    void getCategories_NegativePageNumber_ThrowsIllegalArgumentException() {
        when(categoryRepo.count()).thenReturn(10L);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.getCategories(-1, 5, "categoryName", "asc")
        );
        assertTrue(ex.getMessage().contains("Page index must not be less than zero") || ex.getMessage().contains("must not be negative"));
    }


    // ---------------------------------------------------------
    // 3. updateCategory
    // ---------------------------------------------------------

    @Test
    void updateCategory_SUCCESS() {
        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("Old Name");

        Category updateRequest = new Category();
        updateRequest.setCategoryName("New Name");

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepo.findByCategoryName(updateRequest.getCategoryName())).thenReturn(null);
        when(categoryRepo.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDTO result = categoryService.updateCategory(updateRequest, categoryId);

        assertEquals(categoryId, result.getCategoryId());
        assertEquals("New Name", result.getCategoryName());

    }

    @Test
    void updateCategory_CategoryNotFound_ThrowsAPIException() {
        Long categoryId = 99L;

        Category updateRequest = new Category();
        updateRequest.setCategoryName("Doesn't matter");

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class, () -> categoryService.updateCategory(updateRequest, categoryId));

        assertTrue(ex.getMessage().contains("Category with the id"));
    }

    @Test
    void updateCategory_CategoryNameAlreadyExistsForDifferentId_ThrowsAPIException() {
        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("Old Name");

        Category updateRequest = new Category();
        updateRequest.setCategoryName("Clashing Name");

        Category otherCategory = new Category();
        otherCategory.setCategoryId(2L);
        otherCategory.setCategoryName("Clashing Name");

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepo.findByCategoryName(updateRequest.getCategoryName())).thenReturn(otherCategory);

        APIException ex = assertThrows(APIException.class, () -> categoryService.updateCategory(updateRequest, categoryId)
        );

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void updateCategory_SameNameSameId_AllowsUpdate() {
        Long categoryId = 1L;

        Category existingCategory = new Category();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName("Same Name");

        Category updateRequest = new Category();
        updateRequest.setCategoryName("Same Name");

        Category sameNameCategory = new Category();
        sameNameCategory.setCategoryId(categoryId);
        sameNameCategory.setCategoryName("Same Name");

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepo.findByCategoryName(updateRequest.getCategoryName())).thenReturn(sameNameCategory);
        when(categoryRepo.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDTO result = categoryService.updateCategory(updateRequest, categoryId);

        assertEquals(categoryId, result.getCategoryId());
        assertEquals("Same Name", result.getCategoryName());
    }


    // ---------------------------------------------------------
    // 4. deleteCategory
    // ---------------------------------------------------------

    @Test
    void deleteCategory_SUCCESS() {
        Long categoryId = 1L;

        Category categoryToDelete = new Category();
        categoryToDelete.setCategoryId(categoryId);
        categoryToDelete.setCategoryName("E-Books");

        Product p1 = new Product();
        p1.setProductId(10L);
        p1.setCategory(categoryToDelete);

        Product p2 = new Product();
        p2.setProductId(11L);
        p2.setCategory(categoryToDelete);

        List<Product> products = new ArrayList<>();
        products.add(p1);
        products.add(p2);
        categoryToDelete.setProducts(products);

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(categoryToDelete));

        String result = categoryService.deleteCategory(categoryId);

        assertEquals("Category with categoryId: " + categoryId + " deleted successfully !!!", result);
    }

    @Test
    void deleteCategory_CategoryNotFound_ThrowsAPIException() {
        Long categoryId = 99L;

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class, () -> categoryService.deleteCategory(categoryId));

        assertTrue(ex.getMessage().contains("Category with the id"));
    }
}
