package org.espy.lab.arima.generator;

import org.espy.lab.arima.generator.coefficient.ArimaCoefficientsGenerator;
import org.espy.lab.arima.sample.metadata.ArimaTimeSeriesSampleMetadata;
import org.espy.lab.generator.GeneratorContext;
import org.espy.lab.generator.TimeSeriesGenerator;
import org.espy.lab.sample.TimeSeriesSample;

import java.util.Random;

public class ArimaGenerator implements TimeSeriesGenerator {

    private final int p;

    private final int d;

    private final int q;

    private final ArimaCoefficientsGenerator coefficientsGenerator;

    private final ArimaGeneratorEngine generatorEngine;

    public ArimaGenerator(int p, int d, int q,
                          ArimaCoefficientsGenerator coefficientsGenerator,
                          ArimaGeneratorEngine generatorEngine) {
        // TODO: 4/2/2017 add preconditions
        this.p = p;
        this.d = d;
        this.q = q;
        this.coefficientsGenerator = coefficientsGenerator;
        this.generatorEngine = generatorEngine;
    }

    @Override public TimeSeriesSample generate(GeneratorContext generatorContext) {
        Random random = generatorContext.getRandom();
        double[] arCoefficients = coefficientsGenerator.generateArCoefficients(p, random);
        double[] maCoefficients = coefficientsGenerator.generateMaCoefficients(q, random, arCoefficients);
        double constant = coefficientsGenerator.generateConstant(random);
        double expectation = coefficientsGenerator.generateShockExpectation(random);
        double variation = coefficientsGenerator.generateShockVariation(random);
        ArimaTimeSeriesSampleMetadata metadata = new ArimaTimeSeriesSampleMetadata(
                arCoefficients,
                d,
                maCoefficients,
                constant,
                expectation,
                variation,
                generatorContext.getObservedPartLength(),
                generatorContext.getUnobservedPartLength()
        );
        GeneratedParts parts = generatorEngine.generate(metadata, random);
        return new TimeSeriesSample(metadata, parts.observedPart, parts.unobservedPart);
    }

    public interface ArimaGeneratorEngine {

        GeneratedParts generate(ArimaTimeSeriesSampleMetadata metadata, Random random);
    }

    public static final class GeneratedParts {

        public final double[] observedPart;

        public final double[] unobservedPart;

        public GeneratedParts(double[] observedPart, double[] unobservedPart) {
            this.observedPart = observedPart;
            this.unobservedPart = unobservedPart;
        }
    }
}
