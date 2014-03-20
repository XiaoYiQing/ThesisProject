package sriracha.simulator.model.elements;

import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

public class Inductor extends CircuitElement
{

    private int nPlus, nMinus;

    private double inductance;

    private int currentIndex;

    /**
     * flows from nPlus, through Inductor, out nMinus
     */
    private double initialCurrent;


    /**
     * @param inductance - inductance
     */
    public Inductor(String name, double inductance)
    {
        super(name);
        this.inductance = inductance;
        initialCurrent = 0;
    }

    /**
     * @param inductance - inductance
     * @param ic         - the initial current flowing through the inductor. for not yet implemented transient analysis
     */
    public Inductor(String name, double inductance, double ic)
    {
        super(name);
        this.inductance = inductance;
        initialCurrent = ic;
    }


    @Override
    public void applyDC(DCEquation equation)
    {

        equation.applyRealMatrixStamp(currentIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -1);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);
    }

    @Override
    public void applyAC(ACEquation equation)
    {
        equation.applyRealMatrixStamp(currentIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -1);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);

        equation.applyComplexMatrixStamp(currentIndex, currentIndex, -inductance);
    }

    /* TODO */
    @Override
    public void applyTrans(TransEquation equation)
    {

        equation.applyRealMatrixStamp(currentIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -1);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);

        equation.applyComplexMatrixStamp(currentIndex, currentIndex, -inductance);
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

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public Inductor buildCopy(String name, CircuitElement referencedElement)
    {
        return new Inductor(name, inductance, initialCurrent);
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + inductance;
    }

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
     * @return an array containing the matrix indices for the nodes in this circuit element
     */
    @Override
    public int[] getNodeIndices()
    {
        return new int[]{nPlus, nMinus};
    }

    @Override
    public void setFirstVarIndex(int i)
    {
        currentIndex = i;
    }
}
