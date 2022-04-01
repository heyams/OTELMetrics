package com.example.generators;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.MetricData;

import java.util.Collection;
import java.util.List;

public final class DoubleGaugeGenerator extends BaseGenerator {
    @Override
    public void generateMetric() throws InterruptedException {
        meter.gaugeBuilder("testDoubleGauge")
                .setDescription("the current temperature")
                .setUnit("C")
                .buildWithCallback(
                        m -> {
                            m.record(2.0, Attributes.of(AttributeKey.stringKey("thing"), "engine"));
                        });

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getFinishedMetricItems();
        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 1);
        Collection<DoublePointData> points = (Collection<DoublePointData>)metricData.getData().getPoints();
        assert(points.size() == 1);
        DoublePointData doublePointData = points.iterator().next();
        assert(doublePointData.getValue() == 2.0);
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("thing")) == "engine");

        metricExporter.reset();
    }
}
