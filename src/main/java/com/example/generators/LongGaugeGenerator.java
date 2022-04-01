package com.example.generators;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;

import java.util.Collection;
import java.util.List;

public final class LongGaugeGenerator extends BaseGenerator {
    @Override
    public void generateMetric() throws InterruptedException {
        meter.gaugeBuilder("testLongGauge")
                .ofLongs()
                .setDescription("the current temperature")
                .setUnit("C")
                .buildWithCallback(
                        m -> {
                            m.record(2, Attributes.of(AttributeKey.stringKey("thing"), "engine"));
                        });

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getExportedMetrics();
        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 1);
        Collection<LongPointData> points = (Collection<LongPointData>)metricData.getData().getPoints();
        assert(points.size() == 1);
        LongPointData longPointData = points.iterator().next();
        assert(longPointData.getValue() == 2L);
        assert(longPointData.getAttributes().get(AttributeKey.stringKey("thing")) == "engine");

        metricExporter.reset();
    }
}
