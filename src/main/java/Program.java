import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricReaderFactory;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

public class Program {

    private static Meter meter;

    private static void initMeter() {
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
        initMeter();

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

    public static void main(String[] args) {
        //TODO: LongGaugeBuilder
        //TODO: DoubleGaugeBuilder
        //TODO: SummaryData
        //TODO: DoubleCounter
        //TODO test long.max

        try {
            testLongCounter();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
