package sriracha.simulator.solver.analysis.dc;

import sriracha.simulator.Options;
import sriracha.simulator.Simulator;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysisResults;


public class DCAnalysis extends Analysis
{
    private DCEquation equation;
    private DCEquation sweepEquation;
    private DCEquation originalEquation;

    DCSweep sweep;
    DCSweep sweep2;


    /**
     * @param sweep  the first sweep specified on the .DC line
     * @param sweep2 the optional second sweep specified, should be null if only 1 sweep was found
     */
    public DCAnalysis(DCSweep sweep, DCSweep sweep2)
    {
        super(AnalysisType.DC);
        this.sweep = sweep;
        this.sweep2 = sweep2;
    }

//    @Override
//    public void extractSolvingInfo(Circuit circuit)
//    {
//        originalEquation = DCEquation.generate(circuit);
//    }

    /**
     * Apply circuit element stamps from the circuit objects to the
     * "equation" object of this instance of DCAnalysis object.
     * @param circuit
     */
    @Override
    public void extractSolvingInfo(Circuit circuit)
    {
        if(circuit.isLinear())
            originalEquation = DCEquation.generate(circuit, true);
        else
            originalEquation = DCNonLinEquation.generate(circuit);
    }


    @Override
    public IAnalysisResults run()
    {
        DCResults results = new DCResults();

        if (sweep2 != null)
        {
            //long mode
            for (double j = sweep2.getStartValue(); j <= sweep2.getEndValue(); j += sweep2.getStep())
            {


                sweepEquation = originalEquation.clone();
                sweep2.getSource().modifyStamp(j, sweepEquation);

                for (double i = sweep.getStartValue(); i <= sweep.getEndValue(); i += sweep.getStep())
                {

                    if (Simulator.Instance.isCancelRequested()) return null;

                    if (Options.isPrintProgress()) System.out.println("DC cloning eq");
                    equation = sweepEquation.clone();//this system could be optimized at some point

                    sweep.getSource().modifyStamp(i, equation);
                    if (Options.isPrintProgress()) System.out.println("DC solving point");
                    results.addVector(i, equation.solve());

                }
            }


        } else {
            //short mode
            for (double i = sweep.getStartValue(); i <= sweep.getEndValue(); i += sweep.getStep())
            {
                if (Simulator.Instance.isCancelRequested()) return null;

                if (Options.isPrintProgress()) System.out.println("DC cloning eq");
                equation = originalEquation.clone();//this system could be optimized at some point
                sweep.getSource().modifyStamp(i, equation);
                if (Options.isPrintProgress()) System.out.println("DC solving point");
                results.addVector(i, equation.solve());

            }
        }


        return results;
    }
}
