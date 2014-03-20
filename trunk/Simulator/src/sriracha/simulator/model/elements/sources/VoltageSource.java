package sriracha.simulator.model.elements.sources;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.model.CircuitElement;

import sriracha.simulator.model.elements.sources.transient_functions.TransientFunction;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

public class VoltageSource extends Source
{


    public VoltageSource(String name, double dcValue)
    {
        super(name, dcValue, MathActivator.Activator.complex(0, 0), null);
    }

    public VoltageSource(String name, IComplex acPhasorValue)
    {
        super(name, 0, acPhasorValue, null);
    }

    public VoltageSource(String name, double dcValue, IComplex acPhasorValue)
    {
        super(name, dcValue, acPhasorValue, null);
    }

    public VoltageSource(String name, double dcValue, IComplex acPhasorValue, TransientFunction transfct){
        super(name, dcValue, acPhasorValue, transfct);
    }

    private int currentIndex;

    @Override
    public void modifyStamp(double newDCValue, DCEquation equation)
    {
        equation.applySourceVectorStamp(currentIndex, newDCValue - dcValue);
    }

    @Override
    public void applyDC(DCEquation equation)
    {
        equation.applyRealMatrixStamp(currentIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -1);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);

        equation.applySourceVectorStamp(currentIndex, dcValue);
    }

    @Override
    public void applyAC(ACEquation equation)
    {
        equation.applyRealMatrixStamp(currentIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -1);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);

        equation.applySourceVectorStamp(currentIndex, acPhasorValue);
    }

    /* TODO */
    @Override
    public void applyTrans(TransEquation equation)
    {
        equation.applyRealMatrixStamp(currentIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -1);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);

        equation.applySourceVectorStamp(this);
    }


    public void updateSourceVector(TransEquation transEq, double time){
        double newVal = this.getTransientValue(time);
        transEq.updateSourceVector(currentIndex, newVal);
    }

    public double getTransientValue(double time){
        if(transfun == null){
            return 0;
        }
        return this.transfun.probeValue(time);
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
    public VoltageSource buildCopy(String name, CircuitElement referencedElement)
    {
        return new VoltageSource(name, dcValue, acPhasorValue);
    }

    @Override
    public String toString()
    {
        return super.toString() + " DC: " + dcValue + " AC: " + acPhasorValue;
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

    /**
     * Voltage sources require the addition of a variable representing the current
     * flowing through the source (positive current flows into the positive terminal through the source and out of the negative terminal)
     * @return index for current variable
     */
    public int getCurrentVarIndex()
    {
        return currentIndex;
    }

}
