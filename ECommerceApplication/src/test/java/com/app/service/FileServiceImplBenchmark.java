package com.app.service;

import com.app.benchmark.AbstractBenchmark;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class FileServiceImplBenchmark extends AbstractBenchmark {

    private static FileService fileService;

    private Path tempDir;
    private MockMultipartFile multipartFile;
    private String uploadedFileName;

    @Autowired
    void setBeans(FileService svc) {
        FileServiceImplBenchmark.fileService = svc;
    }

    @Setup(Level.Trial)
    public void trialSetup() throws Exception {
        tempDir = Files.createTempDirectory("file-bench");

        byte[] content = ("bench-content-" + UUID.randomUUID())
                .getBytes(StandardCharsets.UTF_8);

        multipartFile = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                content
        );
    }

    @Setup(Level.Invocation)
    public void invocationSetup() throws Exception {
        uploadedFileName = fileService.uploadImage(
                tempDir.toAbsolutePath().toString(),
                multipartFile
        );
    }

    @TearDown(Level.Invocation)
    public void invocationTearDown() throws Exception {
        if (uploadedFileName != null) {
            Files.deleteIfExists(tempDir.resolve(uploadedFileName));
        }
    }

    @TearDown(Level.Trial)
    public void trialTearDown() throws Exception {
        if (tempDir != null && Files.exists(tempDir)) {
            try (var walk = Files.walk(tempDir)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.deleteIfExists(p);
                            } catch (Exception ignored) {
                            }
                        });
            }
        }
    }

    @Benchmark
    public void uploadImage(Blackhole bh) throws Exception {
        bh.consume(
                fileService.uploadImage(
                        tempDir.toAbsolutePath().toString(),
                        multipartFile
                )
        );
    }

    @Benchmark
    public void getResource_existing(Blackhole bh) throws Exception {
        try (InputStream is = fileService.getResource(
                tempDir.toAbsolutePath().toString(),
                uploadedFileName
        )) {
            bh.consume(is.read());
        }
    }
}
