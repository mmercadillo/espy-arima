package org.espy.lab;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public final class Experiment {

    private final String timeSeriesSuiteAbsoluteFilePath;

    private final List<TimeSeriesProcessor> processors;

    public Experiment(String timeSeriesSuiteAbsoluteFilePath, List<TimeSeriesProcessor> processors) {
        this.timeSeriesSuiteAbsoluteFilePath = timeSeriesSuiteAbsoluteFilePath;
        this.processors = ImmutableList.copyOf(processors);
    }

    public static Experiment unmarshal(Scanner scanner) {
        String filePath = scanner.nextLine();
        return new Experiment(filePath, ImmutableList.of(new CheckArimaProcessor()));
    }

    public void marshal(PrintWriter writer) {
        writer.println(timeSeriesSuiteAbsoluteFilePath);
        Iterator<TimeSeriesProcessor> iterator = processors.iterator();
        while (iterator.hasNext()) {
            TimeSeriesProcessor processor = iterator.next();
            processor.marshal(writer);
            if (iterator.hasNext()) {
                writer.println();
            }
        }
    }

    public ExperimentReport run() throws FileNotFoundException {
        TimeSeriesSuite suite;
        try (Scanner scanner = new Scanner(new File(timeSeriesSuiteAbsoluteFilePath))) {
            suite = TimeSeriesSuite.unmarshal(scanner);
        }
        List<ProcessorReport> reports = new ArrayList<>();
        for (TimeSeriesSample sample : suite.getSamples()) {
            for (TimeSeriesProcessor processor : processors) {
                reports.add(processor.run(sample));
            }
        }
        return new DefaultExperimentReport(reports);
    }
}