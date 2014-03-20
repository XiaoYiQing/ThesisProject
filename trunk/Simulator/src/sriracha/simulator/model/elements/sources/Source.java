package sriracha.simulator.model.elements.sources;

import sriracha.math.interfaces.IComplex;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.elements.sources.transient_functions.TransientFunction;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

/**
 * base class for all normal sources, Not Controlled sources
 */
public abstract class Source extends CircuitElement
{


    /**
     * Node indices for source
     * for current source: Current flows from nPlus, through source, out nMinus
     */
    protected int nPlus, nMinus;

    protected double dcValue = 0;

    protected TransientFunction transfun;

    protected IComplex acPhasorValue;

    //protected double transValue;


    /**
     * @param name element name from netlist
     */
    protected Source(String name, double dcValue, IComplex acPhasorValue, TransientFunction transientFct)
    {
        super(name);
        this.dcValue = dcValue;
        this.acPhasorValue = acPhasorValue;
        this.transfun = transientFct;
    }

    /* TODO */
    /**
     * @param name element name from netlist
     */
    /*protected Source(String name, double dcValue, IComplex acPhasorValue, double transValue)
    {
        super(name);
        this.dcValue = dcValue;
        this.acPhasorValue = acPhasorValue;
        this.transValue = transValue;
    }  */


    /**
     * When performing DC sweep for this source this method is called
     * to change its stamp in the equation with this new dc value. it is assumed that
     * the passed equation was stamped with the sources default value, and not the previous one
     * in the sweep since the source does not know what that was.
     *
     * @param newDCValue - new DC value for source
     * @param equation   - original equation.
     */
    public abstract void modifyStamp(double newDCValue, DCEquation equation);

    /**
     * Set the indices that correspond to the circuit element's nodes.
     * The nodes are assumed to be in the order they are in the netlist.
     * (-1 is always ground)
     *
     * @param indices the ordered node indices
     */
    @Override
    public void setNodeIndices(int... indices)
    {
        nPlus = indices[0];
        nMinus = indices[1];
    }

    /**
     * Obtain the magnitude of the source output for transient analysis for the specified time.
     * @param time specified time at which the source transient value is probed
     * @return source transient value at time "time". Returns 0 if no transient function specified.
     */
    public abstract double getTransientValue(double time);

    /**
     * Update the source vector of the target transient equation at the specified time.
     * @param transEq target transient equation
     * @param time time at which the transient source value is probed.
     */
    public abstract void updateSourceVector(TransEquation transEq, double time);

}