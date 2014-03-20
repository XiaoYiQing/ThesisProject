package sriracha.simulator.parser;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.elements.sources.Source;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.ac.ACAnalysis;
import sriracha.simulator.solver.analysis.ac.ACSubType;
import sriracha.simulator.solver.analysis.dc.DCAnalysis;
import sriracha.simulator.solver.analysis.dc.DCSweep;
import sriracha.simulator.solver.analysis.trans.TransAnalysis;

/**
 * Created by yiqing on 26/02/14.
 */
public class AnalysisParser {

    public static DCAnalysis parseDCAnalysis(String line, Circuit circuit)
    {
        String[] params = line.split("\\s+");

        if (!(params.length == 5 || params.length == 9))
            throw new ParseException("Incorrect number of parameters for DC analysis: " + line);

        Source s1 = (Source) circuit.getElement(params[1]);

        DCSweep sweep1 = new DCSweep(s1, CircuitBuilder.parseDouble(params[2]), CircuitBuilder.parseDouble(params[3]),
                CircuitBuilder.parseDouble(params[4]));

        if (sweep1.getStep() == 0)
        {
            throw new ParseException("Step size must be larger than 0 for DC analysis");
        }
        if (sweep1.getEndValue() <= sweep1.getStartValue())
        {
            throw new ParseException("End Sweep value must be larger than Start Sweep value");
        }

        DCSweep sweep2 = null;
        if (params.length == 9)
        {
            sweep2 = new DCSweep((Source) circuit.getElement(params[5]), CircuitBuilder.parseDouble(params[6]),
                    CircuitBuilder.parseDouble(params[7]), Integer.parseInt(params[8]));

            if (sweep2.getStep() == 0)
            {
                throw new ParseException("Step size must be larger than 0 for DC analysis");
            }

            if (sweep2.getEndValue() <= sweep2.getStartValue())
            {
                throw new ParseException("End Sweep value must be larger than Start Sweep value");
            }
        }

        return new DCAnalysis(sweep1, sweep2);
    }



    public static ACAnalysis parseSmallSignal(String line)
    {
        String[] params = line.split("\\s+");

        if (params.length != 5)
            throw new ParseException("Incorrect number of parameters for AC analysis: " + line);

        ACSubType subType;

        if (params[1].equals("LIN"))
            subType = ACSubType.Linear;
        else if (params[1].equals("OCT"))
            subType = ACSubType.Octave;
        else if (params[1].equals("DEC"))
            subType = ACSubType.Decade;
        else
            throw new ParseException("Invalid scale format. Scale must be LIN, OCT, or DEC: " + line);

        int numPoints = Integer.parseInt(params[2]);
        double rangeStart = CircuitBuilder.parseDouble(params[3]);
        double rangeStop = CircuitBuilder.parseDouble(params[4]);

        if (numPoints == 0)
        {
            throw new ParseException("Must request more than 0 points for AC analysis");
        }

        return new ACAnalysis(subType, rangeStart, rangeStop, numPoints);
    }

    //    /* TODO: number of parameters? */
    public static TransAnalysis parseTransAnalysis(String line){
        String[] params = line.split("\\s+");

        if (params.length != 4)
            throw new ParseException("Incorrect number of parameters for Transient analysis: " + line);

        //The order of parameters is verified: rangeStop is supposed to come before rangeStart.
        double stepSize = CircuitBuilder.parseDouble(params[1]);
        double rangeStart = CircuitBuilder.parseDouble(params[3]);
        double rangeStop = CircuitBuilder.parseDouble(params[2]);

        if (stepSize == 0)
        {
            throw new ParseException("Step size must be greater than 0 for Transient analysis");
        }

        return new TransAnalysis(rangeStart, rangeStop, stepSize);

    }
}
