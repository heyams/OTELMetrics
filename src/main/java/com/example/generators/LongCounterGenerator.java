package com.example.generators;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class LongCounterGenerator extends BaseGenerator {
    @Override
    public void generateMetric() throws InterruptedException {
        LongCounter counter = meter
                .counterBuilder("testLongCounter")
                .setDescription("testLongCounter")
                .setUnit("1")
                .build();

        counter.add(1, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(2, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(1, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(2, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));
        counter.add(5, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(4, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getExportedMetrics();
        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 3);
        Collection<LongPointData> points = (Collection<LongPointData>)metricData.getData().getPoints();
        points = points.stream()
                .sorted(Comparator.comparing(o -> o.getValue()))
                .collect(Collectors.toList());

        Iterator<LongPointData> iterator = points.iterator();
        LongPointData longPointData = iterator.next();
        assert(longPointData.getValue() == 2L);
        assert(longPointData.getAttributes().get(AttributeKey.stringKey("name")) == "apple");
        assert(longPointData.getAttributes().get(AttributeKey.stringKey("color")) == "green");

        longPointData = iterator.next();
        assert(longPointData.getValue() == 6L);
        assert(longPointData.getAttributes().get(AttributeKey.stringKey("name")) == "apple");
        assert(longPointData.getAttributes().get(AttributeKey.stringKey("color")) == "red");

        longPointData = iterator.next();
        assert(longPointData.getValue() == 7L);
        assert(longPointData.getAttributes().get(AttributeKey.stringKey("name")) == "lemon");
        assert(longPointData.getAttributes().get(AttributeKey.stringKey("color")) == "yellow");

        metricExporter.reset();
    }
}
