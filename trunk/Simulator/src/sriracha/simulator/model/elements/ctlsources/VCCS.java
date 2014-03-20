package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

public class VCCS extends VCSource
{


    private int currentIndex;


    /**
     * Voltage controlled Current Source
     * Vccs: I = gm * v0
     * where v0 is a voltage elsewhere in the circuit
     * I is the current from the source
     *
     * @param name - VCCS name from netlist
     * @param gm   - factor in source current equation
     */
    public VCCS(String name, double gm)
    {
        super(name, gm);
    }


    @Override
    public void applyAC(ACEquation equation)
    {
        equation.applyRealMatrixStamp(currentIndex, ncPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, ncMinus, -1);
        equation.applyRealMatrixStamp(currentIndex, currentIndex, -1 / gm);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);

    }

    @Override
    public void applyDC(DCEquation equation)
    {

        equation.applyRealMatrixStamp(currentIndex, ncPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, ncMinus, -1);
        equation.applyRealMatrixStamp(currentIndex, currentIndex, -1 / gm);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);
    }

    /* TODO */
    @Override
    public void applyTrans(TransEquation equation)
    {

        equation.applyRealMatrixStamp(currentIndex, ncPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, ncMinus, -1);
        equation.applyRealMatrixStamp(currentIndex, currentIndex, -1 / gm);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);
    }

    @Override
    public int getNodeCount()
    {
        return 2;
    }

    @Override
    public int getExtraVariableCount()
    {
        return 1;
    }

    @Override
    public void setFirstVarIndex(int i)
    {
        currentIndex = i;
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public VCCS buildCopy(String name, CircuitElement referencedElement)
    {
        return new VCCS(name, gm);
    }

}
