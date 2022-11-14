package com.example;

import com.example.generators.BaseGenerator;
import com.example.generators.DoubleCounterGenerator;
import com.example.generators.DoubleGaugeGenerator;
import com.example.generators.DoubleHistogramGenerator;
import com.example.generators.LongCounterGenerator;
import com.example.generators.LongGaugeGenerator;
import com.example.generators.LongHistogramGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program {

    private static final Pattern hostPattern = Pattern.compile("^https?://(?:www\\.)?([^/.]+)");

    private static String getHost(String endpointUrl) {
        Matcher matcher = hostPattern.matcher(endpointUrl);

        if (matcher.find()) {
            return matcher.group(1);
        }

        // it's better to send bad endpointUrl to Statsbeat for troubleshooting.
        return endpointUrl;
    }

    private static final Object lock = new Object();
    public static void main(String[] args) {
        //TODO: SummaryData
        //TODO test long.max
        run();
    }

    private static void run() {
        synchronized (lock) {
            List<BaseGenerator> generatorList = new ArrayList<>();
            generatorList.add(new LongCounterGenerator());
            generatorList.add(new DoubleCounterGenerator());
            generatorList.add(new LongGaugeGenerator());
            generatorList.add(new DoubleGaugeGenerator());
            generatorList.add(new DoubleHistogramGenerator());
            generatorList.add(new LongHistogramGenerator());
            try {
                for (BaseGenerator generator : generatorList) {
                    generator.generateMetric();
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                throw new IllegalArgumentException(ex);
            }
        }
    }
}
