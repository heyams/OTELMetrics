package com.example;

import com.example.generators.DoubleCounterGenerator;
import com.example.generators.DoubleGaugeGenerator;
import com.example.generators.DoubleHistogramGenerator;
import com.example.generators.LongCounterGenerator;
import com.example.generators.LongGaugeGenerator;
import com.example.generators.LongHistogramGenerator;

public class Program {

    public static void main(String[] args) {
        //TODO: SummaryData
        //TODO test long.max
        try {
            new LongCounterGenerator().generateMetric();
            new DoubleCounterGenerator().generateMetric();
            new LongGaugeGenerator().generateMetric();
            new DoubleGaugeGenerator().generateMetric();
            new DoubleHistogramGenerator().generateMetric();
            new LongHistogramGenerator().generateMetric();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
