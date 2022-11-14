package com.example.generators;

import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;

import java.util.Collection;
import java.util.List;

public final class DoubleHistogramGenerator extends BaseGenerator {

    @Override
    public void generateMetric() throws InterruptedException {
        DoubleHistogram doubleHistogram = meter.histogramBuilder("testDoubleHistogram")
                .setDescription("http.client.duration")
                .setUnit("ms")
                .build();
        doubleHistogram.record(123.0);

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getFinishedMetricItems();
        assert(metricDataList.size() == 1);
        System.out.println(metricDataList.get(0));
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 1);
        Collection<HistogramPointData> points = (Collection<HistogramPointData>)metricData.getData().getPoints();
        assert(points.size() == 1);
        System.out.println(points.iterator().next());
        HistogramPointData histogramPointData = points.iterator().next();
        assert(histogramPointData.getSum() == 123.0);

        metricExporter.reset();
    }
}
