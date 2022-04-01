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

public class Program {

    public static void main(String[] args) {
        //TODO: SummaryData
        //TODO test long.max

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
        }
    }
}
