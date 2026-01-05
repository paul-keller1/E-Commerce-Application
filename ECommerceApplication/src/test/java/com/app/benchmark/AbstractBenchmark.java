package com.app.benchmark;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractBenchmark {

    private static final int MEASUREMENT_ITERATIONS = 3;
    private static final int WARMUP_ITERATIONS = 3;

    @Test
    void executeJmhRunner() throws RunnerException {
        Options opt = new OptionsBuilder()
                // include only benchmarks from the concrete subclass
                .include("\\." + this.getClass().getSimpleName() + "\\.")
                .warmupIterations(WARMUP_ITERATIONS)
                .measurementIterations(MEASUREMENT_ITERATIONS)
                .warmupTime(org.openjdk.jmh.runner.options.TimeValue.milliseconds(50))
                .measurementTime(org.openjdk.jmh.runner.options.TimeValue.milliseconds(50))

                // IMPORTANT: no forks, otherwise Spring-injected statics won't be visible
                .forks(0)

                // single-threaded for deterministic-ish runs
                .threads(1)

                .shouldDoGC(true)
                .shouldFailOnError(true)

                .resultFormat(ResultFormatType.JSON)
                //.result("/dev/null") // change to e.g. "target/jmh-results.json" if desired
                .result("target/jmh-results.json")

                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }
}
