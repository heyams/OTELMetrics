import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongGaugeBuilder;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.ObservableLongGauge;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricReaderFactory;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

public class Program {

    private static Meter meter;

    static {
        LoggingMetricExporter metricExporter = LoggingMetricExporter.create();
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
                .counterBuilder("MyFruitCounter")
                .setDescription("MyFruitCounter")
                .setUnit("1")
                .build();

        counter.add(1, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(2, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(1, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(2, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));
        counter.add(5, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(4, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));

        Thread.sleep(60 * 2 * 1000); // wait for 2 min

        // this should produce the following 3 meters output from OpenTelemetry SDK
//        counter.add(6, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
//        counter.add(7, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
//        counter.add(2, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));
    }

    private static void testLongGauge() throws InterruptedException {
        meter.gaugeBuilder("temperature")
            .ofLongs()
            .setDescription("the current temperature")
            .setUnit("C")
            .buildWithCallback(
                    m -> {
                        m.record(1);
                        m.record(2, Attributes.of(AttributeKey.stringKey("thing"), "engine"));
                    });

        Thread.sleep(60 * 2 * 1000); // wait for 2 min
    }

    private static void testDoubleCounter() throws InterruptedException {
        DoubleCounter counter = (DoubleCounter)meter
                .counterBuilder("MyFruitCounter")
                .ofDoubles()
                .setDescription("MyFruitCounter")
                .setUnit("1")
                .build();

        counter.add(1.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(2.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(1.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(2.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));
        counter.add(5.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(4.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));

        Thread.sleep(60 * 2 * 1000); // wait for 2 min

        // this should produce the following 3 meters output from OpenTelemetry SDK
//        counter.add(6.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
//        counter.add(7.0, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
//        counter.add(2.0, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));
    }

    public static void main(String[] args) {
        //TODO: LongGaugeBuilder
        //TODO: DoubleGaugeBuilder
        //TODO: SummaryData
        //TODO test long.max

        try {
//            testLongCounter();
//            testDoubleCounter();
            testLongGauge();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
