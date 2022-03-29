import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.api.metrics.DoubleGaugeBuilder;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongGaugeBuilder;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.data.SummaryData;
import io.opentelemetry.sdk.metrics.export.MetricReaderFactory;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

public class Program {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("hello world");

        LoggingMetricExporter metricExporter = LoggingMetricExporter.create();
        MetricReaderFactory metricReaderFactory = PeriodicMetricReader.newMetricReaderFactory(metricExporter);
        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(metricReaderFactory)
                .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().setMeterProvider(meterProvider).buildAndRegisterGlobal();  //GlobalOpenTelemetry.get();
        Meter meter = openTelemetry.meterBuilder("my-instrumentation-library-name")
                .setInstrumentationVersion("1.0.0")
                .build();

        LongGaugeBuilder
        DoubleGaugeBuilder
        SummaryData
        DoubleCounter doubleCounter;
        LongCounter counter = meter
                .counterBuilder("MyFruitCounter")
                .setDescription("MyFruitCounter")
                .setUnit("1")
                .build();

        counter.add(Long.MAX_VALUE, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(2, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(1, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
        counter.add(2, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));
        counter.add(5, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
        counter.add(4, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));

        Thread.sleep(60 * 1000); // wait for 1 min

        // this should produce the following 3 meters output from OpenTelemetry SDK
//        counter.add(6, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "red"));
//        counter.add(7, Attributes.of(AttributeKey.stringKey("name"), "lemon", AttributeKey.stringKey("color"), "yellow"));
//        counter.add(2, Attributes.of(AttributeKey.stringKey("name"), "apple", AttributeKey.stringKey("color"), "green"));

    }
}
