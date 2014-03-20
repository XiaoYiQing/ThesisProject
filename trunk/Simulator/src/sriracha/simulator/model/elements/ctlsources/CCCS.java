package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.elements.sources.VoltageSource;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

public class CCCS extends CCSource
{

    private int currentIndex;

    /**
     * Current controlled Current Source
     * Is = gm * i0
     *
     * @param name - name from netlist
     * @param gm   - factor in source equation
     */
    public CCCS(String name, double gm, VoltageSource source)
    {
        super(name, gm, source);
    }

    @Override
    public void applyAC(ACEquation equation)
    {
        equation.applyRealMatrixStamp(nPlus, dummySource.getCurrentVarIndex(), gm);
        equation.applyRealMatrixStamp(nMinus, dummySource.getCurrentVarIndex(), -gm);
    }

    @Override
    public void applyDC(DCEquation equation)
    {

        equation.applyRealMatrixStamp(nPlus, dummySource.getCurrentVarIndex(), gm);
        equation.applyRealMatrixStamp(nMinus, dummySource.getCurrentVarIndex(), -gm);

    }

    /* TODO */
    @Override
    public void applyTrans(TransEquation equation)
    {

        equation.applyRealMatrixStamp(nPlus, dummySource.getCurrentVarIndex(), gm);
        equation.applyRealMatrixStamp(nMinus, dummySource.getCurrentVarIndex(), -gm);

    }

    @Override
    public int getNodeCount()
    {
        return 2;
    }

    @Override
    public int getExtraVariableCount()
    {
        return 0;
        //  return 1;
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public CCCS buildCopy(String name, CircuitElement referencedElement)
    {
        return new CCCS(name, gm, (VoltageSource) referencedElement);
    }


    @Override
    public void setFirstVarIndex(int i)
    {
        currentIndex = i;
    }
}
