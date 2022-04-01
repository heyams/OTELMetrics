package com.example.generators;

import com.example.InMemoryMetricExporter;
import com.example.MyMeter;
import io.opentelemetry.api.metrics.Meter;

public abstract class BaseGenerator {
    protected static final Meter meter = MyMeter.getMeter();
    protected static final InMemoryMetricExporter metricExporter = MyMeter.getMetricExporter();

    public abstract void generateMetric() throws InterruptedException;
}
