package com.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

public class FileServiceImplTest {


    private AutoCloseable autoCloseable;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileServiceImpl fileService;

    @TempDir
    Path tempDir;



    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
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
    // 1. uploadImage
    // ---------------------------------------------------------

    @Test
    void testUploadImage_Success() throws IOException {
        String originalFileName = "test-image.png";
        byte[] content = "dummy-image-content".getBytes(StandardCharsets.UTF_8);

        when(multipartFile.getOriginalFilename()).thenReturn(originalFileName);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        String savedFileName = fileService.uploadImage(tempDir.toString(), multipartFile);

        assertNotNull(savedFileName);
        assertTrue(savedFileName.endsWith(".png"));
        assertNotEquals(originalFileName, savedFileName);

        Path savedFilePath = tempDir.resolve(savedFileName);
        assertTrue(Files.exists(savedFilePath));
        assertArrayEquals(content, Files.readAllBytes(savedFilePath));
    }

    @Test
    void testUploadImage_CreatesFolderIfNotExists() throws IOException {
        String originalFileName = "another-image.jpg";
        byte[] content = "another-file".getBytes(StandardCharsets.UTF_8);

        when(multipartFile.getOriginalFilename()).thenReturn(originalFileName);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        Path newFolder = tempDir.resolve("new-folder");
        String newFolderPath = newFolder.toString();

        String savedFileName = fileService.uploadImage(newFolderPath, multipartFile);

        assertTrue(Files.exists(newFolder));
        Path savedFilePath = newFolder.resolve(savedFileName);
        assertTrue(Files.exists(savedFilePath));
    }

    @Test
    void testUploadImage_IOExceptionPropagated() throws IOException {
        String originalFileName = "broken.png";

        when(multipartFile.getOriginalFilename()).thenReturn(originalFileName);
        when(multipartFile.getInputStream()).thenThrow(new IOException("Cannot read input stream"));

        assertThrows(IOException.class, () -> fileService.uploadImage(tempDir.toString(), multipartFile));
    }

    // ---------------------------------------------------------
    // 2. getResource
    // ---------------------------------------------------------

    @Test
    void testGetResource_Success() throws IOException {
        String fileName = "existing.txt";
        String content = "hello-world";

        Path filePath = tempDir.resolve(fileName);
        Files.writeString(filePath, content);

        InputStream inputStream = fileService.getResource(tempDir.toString(), fileName);

        assertNotNull(inputStream);
        byte[] bytes = inputStream.readAllBytes();
        assertEquals(content, new String(bytes, StandardCharsets.UTF_8));
        inputStream.close();
    }

    @Test
    void testGetResource_FileNotFound() {
        String fileName = "not-existing-file.txt";

        assertThrows(FileNotFoundException.class, () -> fileService.getResource(tempDir.toString(), fileName));
    }
}
