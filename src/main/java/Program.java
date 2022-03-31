import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricReaderFactory;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Program {

    private static Meter meter;
    private static InMemoryMetricExporter metricExporter;

    static {
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

    private static void testLongCounter() throws InterruptedException {
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

    private static void testLongGauge() throws InterruptedException {
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

    private static void testDoubleCounter() throws InterruptedException {
        DoubleCounter counter = (DoubleCounter)meter
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

        List<MetricData> metricDataList = metricExporter.getExportedMetrics();
        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 3);
        Collection<DoublePointData> points = (Collection<DoublePointData>)metricData.getData().getPoints();
        points = points.stream()
                .sorted(Comparator.comparing(o -> o.getValue()))
                .collect(Collectors.toList());

        Iterator<DoublePointData> iterator = points.iterator();
        DoublePointData doublePointData = iterator.next();
        assert(doublePointData.getValue() == 2.0);
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "apple");
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "green");

        doublePointData = iterator.next();
        assert(doublePointData.getValue() == 6.0);
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "apple");
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "red");

        doublePointData = iterator.next();
        assert(doublePointData.getValue() == 7.0);
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("name")) == "lemon");
        assert(doublePointData.getAttributes().get(AttributeKey.stringKey("color")) == "yellow");

        metricExporter.reset();
    }

    private static void testDoubleGauge() throws InterruptedException {
        meter.gaugeBuilder("testDoubleGauge")
            .setDescription("the current temperature")
            .setUnit("C")
            .buildWithCallback(
                    m -> {
                        m.record(2.0, Attributes.of(AttributeKey.stringKey("thing"), "engine"));
                    });

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getExportedMetrics();
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

    private static void testDoubleHistogram() throws InterruptedException {
        DoubleHistogram doubleHistogram = meter.histogramBuilder("testDoubleHistogram")
                .setDescription("http.client.duration")
                .setUnit("ms")
                .build();
        doubleHistogram.record(123.0);

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getExportedMetrics();
        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 1);
        Collection<HistogramPointData> points = (Collection<HistogramPointData>)metricData.getData().getPoints();
        assert(points.size() == 1);
        HistogramPointData histogramPointData = points.iterator().next();
        assert(histogramPointData.getSum() == 123.0);

        metricExporter.reset();
    }

    private static void testLongHistogram() throws InterruptedException {
        LongHistogram longHistogram = meter.histogramBuilder("testLongHistogram")
                .ofLongs()
                .setDescription("http.client.duration")
                .setUnit("ms")
                .build();
        longHistogram.record(123L);

        Thread.sleep(90 * 1000); // wait 90 seconds

        List<MetricData> metricDataList = metricExporter.getExportedMetrics();
        assert(metricDataList.size() == 1);
        MetricData metricData = metricDataList.get(0);
        assert(metricData.getData().getPoints().size() == 1);
        Collection<HistogramPointData> points = (Collection<HistogramPointData>)metricData.getData().getPoints();
        assert(points.size() == 1);
        HistogramPointData histogramPointData = points.iterator().next();
        assert(histogramPointData.getSum() == 123L);

        metricExporter.reset();
    }

    public static void main(String[] args) {
        //TODO: SummaryData
        //TODO test long.max
        try {
//            testLongCounter();
            testDoubleCounter();
//            testLongGauge();
//            testDoubleGauge();
//            testDoubleHistogram();
//            testLongHistogram();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
