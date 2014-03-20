package sriracha.simulator.solver.analysis.trans;

import sriracha.simulator.Options;
import sriracha.simulator.Simulator;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysisResults;
import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealVector;

/**
 * Created with IntelliJ IDEA.
 * User: sikram
 * Date: 10/1/13
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransAnalysis extends Analysis{

    private MathActivator activator = MathActivator.Activator;

    private TransEquation equation;
    // private TransEquation originalEquation;

    private double timeStart;

    private double timeEnd;

    double timeStep;

    /* TODO: Initial voltage is 0 for DC sources only */
    public IRealVector voltageVector;

    /* TODO: Initial voltage is 0 for DC sources only */
    //double initialVoltage = 0;

    //double currentVoltage = initialVoltage;

    public TransAnalysis(double timeStart, double timeEnd, double timeStep)
    {
        super(AnalysisType.TR);
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.timeStep = timeStep;
    }

    @Override
    public void extractSolvingInfo(Circuit circuit)
    {
        //we will be dealing with non-linear transient analysis very soon.
        if(circuit.isLinear())
            equation = TransEquation.generate(circuit, true);
        else
            equation = TransNonLinEquation.generate(circuit);
    }

    @Override
    public IAnalysisResults run()
    {
        voltageVector = activator.realVector(equation.getC().getNumberOfColumns());
        //the current voltage vector is initialized at 0.  It is currently specified
        //that the initial conditions are to be set to zero init. conditions.
        voltageVector.clear();
        TransResults results = new TransResults();
        double currentTime = timeStart;

        while(currentTime < timeEnd){
            if (Simulator.Instance.isCancelRequested())
                return null;

            if (Options.isPrintProgress())
                System.out.println("Transient solving point");

            IRealVector soln = equation.solve(timeStep, voltageVector, currentTime + timeStep);

            results.addVector(currentTime, soln);
            currentTime += timeStep;
            voltageVector.copy(soln);
        }

        return results;
    }

}
