package com.example;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricReaderFactory;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

public class MyMeter {

    private static Meter meter;
    private static InMemoryMetricExporter metricExporter;
    static MyMeter instance;

    public static Meter getMeter() {
        if (instance == null) {
            instance = new MyMeter();
        }
        return meter;
    }

    public static InMemoryMetricExporter getMetricExporter() {
        if (instance == null) {
            instance = new MyMeter();
        }
        return metricExporter;
    }

    private MyMeter() {
        metricExporter = new InMemoryMetricExporter();
        MetricReaderFactory metricReaderFactory = PeriodicMetricReader.newMetricReaderFactory(metricExporter);
        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(metricReaderFactory)
                .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().setMeterProvider(meterProvider).buildAndRegisterGlobal();  //GlobalOpenTelemetry.get();
        meter = openTelemetry.meterBuilder("my-instrumentation-library-name")
                .setInstrumentationVersion("1.0.0")
                .build();
    }
}