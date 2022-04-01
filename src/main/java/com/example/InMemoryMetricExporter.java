package com.example;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// test OpenTelemetry integration
public final class InMemoryMetricExporter implements MetricExporter {
    private final Queue<MetricData> exportedMetrics = new ConcurrentLinkedQueue<>();

    public InMemoryMetricExporter() {}

    public List<MetricData> getExportedMetrics() {
        return List.copyOf(exportedMetrics);
    }

    public void reset() {
        exportedMetrics.clear();
    }

    @Override
    public CompletableResultCode export(Collection<MetricData> metrics) {
        exportedMetrics.addAll(metrics);
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        exportedMetrics.clear();
        return CompletableResultCode.ofSuccess();
    }
}
