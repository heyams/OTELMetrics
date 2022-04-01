package com.example.generators;

import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;

import java.util.Collection;
import java.util.List;

public final class LongHistogramGenerator extends BaseGenerator {

    @Override
    public void generateMetric() throws InterruptedException {
        LongHistogram longHistogram = meter.histogramBuilder("testLongHistogram")
                .ofLongs()
                .setDescription("http.client.duration")
                .setUnit("ms")
                .build();
        longHistogram.record(123L);

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getFinishedMetricItems();
        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 1);
        Collection<HistogramPointData> points = (Collection<HistogramPointData>)metricData.getData().getPoints();
        assert(points.size() == 1);
        HistogramPointData histogramPointData = points.iterator().next();
        assert(histogramPointData.getSum() == 123L);

        metricExporter.reset();
    }
}
