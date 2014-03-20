package sriracha.simulator.model.elements;

import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

public class Resistor extends CircuitElement
{

    protected int nodeA, nodeB;

    /**
     * in ohms
     */
    private double resistance;

    private double G;

    /**
     * Standard Resistor Constructor
     *
     * @param resistance - resistance in ohms
     */
    public Resistor(String name, double resistance)
    {
        super(name);
        this.resistance = resistance;
        G = 1.0 / resistance;

    }


    @Override
    public void applyDC(DCEquation equation)
    {

        equation.applyRealMatrixStamp(nodeA, nodeA, G);
        equation.applyRealMatrixStamp(nodeB, nodeB, G);
        equation.applyRealMatrixStamp(nodeA, nodeB, -G);
        equation.applyRealMatrixStamp(nodeB, nodeA, -G);
    }

    @Override
    public void applyAC(ACEquation equation)
    {
        equation.applyRealMatrixStamp(nodeA, nodeA, G);
        equation.applyRealMatrixStamp(nodeB, nodeB, G);
        equation.applyRealMatrixStamp(nodeA, nodeB, -G);
        equation.applyRealMatrixStamp(nodeB, nodeA, -G);
    }

    @Override
    public void applyTrans(TransEquation equation)
    {
        equation.applyRealMatrixStamp(nodeA, nodeA, G);
        equation.applyRealMatrixStamp(nodeB, nodeB, G);
        equation.applyRealMatrixStamp(nodeA, nodeB, -G);
        equation.applyRealMatrixStamp(nodeB, nodeA, -G);
    }

    @Override
    public void setNodeIndices(int... indices)
    {
        nodeA = indices[0];
        nodeB = indices[1];
    }

    @Override
    public int[] getNodeIndices()
    {
        return new int[]{nodeA, nodeB};
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

    @Override
    public Resistor buildCopy(String name, CircuitElement referencedElement)
    {
        return new Resistor(name, resistance);
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + resistance;
    }
}
