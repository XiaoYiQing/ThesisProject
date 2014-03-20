package sriracha.simulator.model.elements.sources;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.model.CircuitElement;

import sriracha.simulator.model.elements.sources.transient_functions.TransientFunction;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;

public class CurrentSource extends Source
{


    public CurrentSource(String name, IComplex acPhasorValue)
    {

        super(name, 0, acPhasorValue, null);
    }

    public CurrentSource(String name, double dcValue)
    {
        super(name, dcValue, MathActivator.Activator.complex(0, 0), null);
    }

    public CurrentSource(String name, double dcValue, IComplex acPhasorValue)
    {
        super(name, dcValue, acPhasorValue, null);
    }

    public CurrentSource(String name, double dcValue, IComplex acPhasorValue, TransientFunction transfct){
        super(name, dcValue, acPhasorValue, transfct);
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
    public void modifyStamp(double newDCValue, DCEquation equation)
    {
        equation.applySourceVectorStamp(nMinus, newDCValue - dcValue);
        equation.applySourceVectorStamp(nPlus, dcValue - newDCValue);
    }

    @Override
    public void applyDC(DCEquation equation)
    {
        equation.applySourceVectorStamp(nMinus, dcValue);
        equation.applySourceVectorStamp(nPlus, -dcValue);
    }

    @Override
    public void applyAC(ACEquation equation)
    {
        equation.applySourceVectorStamp(nMinus, acPhasorValue);
        equation.applySourceVectorStamp(nPlus, acPhasorValue.opposite());
    }

    /* TODO */
    @Override
    public void applyTrans(TransEquation equation)
    {
        equation.applySourceVectorStamp(this);
    }

    public void updateSourceVector(TransEquation transEq, double time){
        double newVal = this.getTransientValue(time);
        transEq.updateSourceVector(nMinus, newVal);
        transEq.updateSourceVector(nPlus, -1 * newVal);
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
        return 0;
    }

    @Override
    public CurrentSource buildCopy(String name, CircuitElement referencedElement)
    {
        return new CurrentSource(name, dcValue, acPhasorValue);
    }

    @Override
    public String toString()
    {
        return super.toString() + " DC " + dcValue + " AC: " + acPhasorValue;
    }
}
