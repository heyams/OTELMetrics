package com.example.generators;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.MetricData;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public final class DoubleCounterGenerator extends BaseGenerator {
    @Override
    public void generateMetric() throws InterruptedException {
        DoubleCounter counter = meter
                .counterBuilder("testDoubleCounter")
                .ofDoubles()
                .setDescription("testDoubleCounter")
                .setUnit("1")
                .build();

        counter.add(1.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(2.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(1.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(2.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));
        counter.add(5.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(4.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getFinishedMetricItems();
        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 3);
        Collection<DoublePointData> points = (Collection<DoublePointData>)metricData.getData().getPoints();
        points = points.stream()
                .sorted(Comparator.comparing(o -> o.getValue()))
                .collect(Collectors.toList());

        List<String> list = null;
        Iterator<DoublePointData> iterator = points.iterator();
        DoublePointData doublePointData = iterator.next();
        System.out.println("epochNanos1: " + Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getEpochNanos())).atOffset(ZoneOffset.UTC));
        System.out.println("startEpochNanos1: " + Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getStartEpochNanos())).atOffset(ZoneOffset.UTC));
        assert(doublePointData.getValue() == 2.0);
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "apple");
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "green");

        doublePointData = iterator.next();
        System.out.println("epochNanos2: " + Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getEpochNanos())).atOffset(ZoneOffset.UTC));
        System.out.println("startEpochNanos2: " + Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getStartEpochNanos())).atOffset(ZoneOffset.UTC));
        assert(doublePointData.getValue() == 6.0);
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "apple");
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "red");

        doublePointData = iterator.next();
        System.out.println("epochNanos3: " + Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getEpochNanos())).atOffset(ZoneOffset.UTC));
        System.out.println("startEpochNanos3: " + Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getStartEpochNanos())).atOffset(ZoneOffset.UTC));
        assert(doublePointData.getValue() == 7.0);
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "lemon");
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "yellow");

        metricExporter.reset();
    }
}
