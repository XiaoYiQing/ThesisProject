package sriracha.simulator.model.elements;

import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

public class Capacitor extends CircuitElement
{

    private int nPlus, nMinus;

    private double capacitance, initialVoltage;

    public Capacitor(String name, double capacitance)
    {
        super(name);
        this.capacitance = capacitance;
        initialVoltage = 0;
    }

    private Capacitor(String name, double capacitance, double ic)
    {
        super(name);
        this.capacitance = capacitance;
        this.initialVoltage = ic;
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
    public void applyDC(DCEquation equation)
    {
    }

    @Override
    public void applyAC(ACEquation equation)
    {
        equation.applyComplexMatrixStamp(nPlus, nPlus, capacitance);
        equation.applyComplexMatrixStamp(nPlus, nMinus, -capacitance);
        equation.applyComplexMatrixStamp(nMinus, nPlus, -capacitance);
        equation.applyComplexMatrixStamp(nMinus, nMinus, capacitance);

    }

    /* TODO */
    @Override
    public void applyTrans(TransEquation equation)
    {

        equation.applyComplexMatrixStamp(nPlus, nPlus, capacitance);
        equation.applyComplexMatrixStamp(nPlus, nMinus, -capacitance);
        equation.applyComplexMatrixStamp(nMinus, nPlus, -capacitance);
        equation.applyComplexMatrixStamp(nMinus, nMinus, capacitance);

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
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public Capacitor buildCopy(String name, CircuitElement referencedElement)
    {
        return new Capacitor(name, capacitance, initialVoltage);
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + capacitance;
    }
}
