package com.example.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.MetricData;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public final class DoubleCounterGenerator extends BaseGenerator {

    private static final Gson GSON = new Gson();

    @Override
    public void generateMetric() throws InterruptedException {
        DoubleCounter counter = meter
                .counterBuilder("testDoubleCounter")
                .ofDoubles()
                .setDescription("testDoubleCounter")
                .setUnit("1")
                .build();

        counter.add(35.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
//        counter.add(2.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
//        counter.add(1.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
//        counter.add(2.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));
//        counter.add(5.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
//        counter.add(4.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));

        Thread.sleep(1000); // wait 1 sec

        List<MetricData> metricDataList = metricExporter.getFinishedMetricItems();
        System.out.println("############################");
        for (MetricData data : metricDataList) {
            String jsonString = GSON.toJson(data);
            System.out.println(jsonString);
        }
        System.out.println("############################");

        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 3);
        Collection<DoublePointData> points = (Collection<DoublePointData>)metricData.getData().getPoints();
        points = points.stream()
                .sorted(Comparator.comparing(o -> o.getValue()))
                .collect(Collectors.toList());

        System.out.println("############################");
        points.forEach(data -> {
            String pointString = GSON.toJson(data);
            System.out.println(pointString);
        });
        System.out.println("############################");

        Iterator<DoublePointData> iterator = points.iterator();
        DoublePointData doublePointData = iterator.next();
        OffsetDateTime epochNanos = Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getEpochNanos())).atOffset(ZoneOffset.UTC);
        OffsetDateTime startEpochNanos = Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getStartEpochNanos())).atOffset(ZoneOffset.UTC);
        System.out.println("epochNanos1: " + epochNanos);
        System.out.println("startEpochNanos1: " + startEpochNanos);
        assert(doublePointData.getValue() == 2.0);
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "apple");
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "green");
        System.out.println("interval1: " + (epochNanos.minusNanos(doublePointData.getStartEpochNanos())).getSecond());

//        doublePointData = iterator.next();
//        epochNanos = Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getEpochNanos())).atOffset(ZoneOffset.UTC);
//        startEpochNanos = Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getStartEpochNanos())).atOffset(ZoneOffset.UTC);
//        System.out.println("epochNanos2: " + epochNanos);
//        System.out.println("startEpochNanos2: " + startEpochNanos);
//        assert(doublePointData.getValue() == 6.0);
//        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "apple");
//        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "red");
//        System.out.println("interval2: " + (epochNanos.minusNanos(doublePointData.getStartEpochNanos())).getSecond());
//
//
//        doublePointData = iterator.next();
//        epochNanos = Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getEpochNanos())).atOffset(ZoneOffset.UTC);
//        startEpochNanos = Instant.ofEpochMilli(NANOSECONDS.toMillis(doublePointData.getStartEpochNanos())).atOffset(ZoneOffset.UTC);
//        System.out.println("epochNanos3: " + epochNanos);
//        System.out.println("startEpochNanos3: " + startEpochNanos);
//        assert(doublePointData.getValue() == 7.0);
//        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "lemon");
//        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "yellow");
//        System.out.println("interval3: " + (epochNanos.minusNanos(doublePointData.getStartEpochNanos())).getSecond());

        metricExporter.reset();
    }
}
