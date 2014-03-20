package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.elements.sources.VoltageSource;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

public class CCVS extends CCSource
{

    private int currentOutIndex;


    /**
     * Current Controlled Voltage Source
     * Vs = gm * i0
     *
     * @param name   - CCVS netlist name
     * @param gm     - factor in source equation
     * @param source - dummy source that defines branch current.
     */
    public CCVS(String name, double gm, VoltageSource source)
    {
        super(name, gm, source);
    }

    @Override
    public void applyAC(ACEquation equation)
    {
        equation.applyRealMatrixStamp(currentOutIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentOutIndex, nMinus, -1);
        equation.applyRealMatrixStamp(currentOutIndex, dummySource.getCurrentVarIndex(), -gm);

        equation.applyRealMatrixStamp(nPlus, currentOutIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentOutIndex, -1);


    }

    @Override
    public void applyDC(DCEquation equation)
    {

        equation.applyRealMatrixStamp(currentOutIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentOutIndex, nMinus, -1);
        equation.applyRealMatrixStamp(currentOutIndex, dummySource.getCurrentVarIndex(), -gm);

        equation.applyRealMatrixStamp(nPlus, currentOutIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentOutIndex, -1);

    }

    /* TODO */
    @Override
    public void applyTrans(TransEquation equation)
    {

        equation.applyRealMatrixStamp(currentOutIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentOutIndex, nMinus, -1);
        equation.applyRealMatrixStamp(currentOutIndex, dummySource.getCurrentVarIndex(), -gm);

        equation.applyRealMatrixStamp(nPlus, currentOutIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentOutIndex, -1);

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

    public void setDummySource(VoltageSource source)
    {
        dummySource = source;
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards.
     * furthermore, for current controlled sources the new dummy source will have to be linked
     */
    @Override
    public CCVS buildCopy(String name, CircuitElement referencedElement)
    {
        return new CCVS(name, gm, (VoltageSource) referencedElement);
    }


    @Override
    public void setFirstVarIndex(int i)
    {
        currentOutIndex = i;
    }
}
