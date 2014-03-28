package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.Options;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.NonLinCircuitElement;
import sriracha.simulator.model.elements.Diode;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 31/10/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DCNonLinEquation extends DCEquation{


    /**
     * Factory object for the Math module's objects.
     */
    private MathActivator activator = MathActivator.Activator;

    private ArrayList<NonLinCircuitElement> nonLinearElem;
    /**
     * private constructor creating a new DCNonLinEquation object with matrix equation
     * size indicated by circuitNodeCount.
     * @param circuitNodeCount
     */
    public DCNonLinEquation(int circuitNodeCount)
    {
        super(circuitNodeCount, false);

        //Note: the array list initiate with a guessed size of amount of
        //non-linear circuit element. (guessing it as number of nodes)
        nonLinearElem = new ArrayList<NonLinCircuitElement>(circuitNodeCount);
    }

    public void applyNonLinearCircuitElem(NonLinCircuitElement input){
        nonLinearElem.add(input);
    }

    public DCNonLinEquation clone()
    {
        DCNonLinEquation clone = new DCNonLinEquation(this.circuitNodeCount);
        clone.G.copy(this.G);
        clone.b.copy(this.b);
        clone.nonLinearElem = (ArrayList<NonLinCircuitElement>)nonLinearElem.clone();
        return clone;
    }

    /**
     * This method acts as the official constructor of DCNonLinEquation objects.
     * The method apply the stamps of the circuit elements to the matrix equations.
     * The "applyDC" method of circuit elements will call the "applyRealMatrixStamp" or
     * "applySourceVectorStamp" method of DCEquation class through the elements of
     * the circuit.
     *
     * @param circuit Target circuit object from which circuit elements are obtained.
     *                It is expected to have already been set up with all the
     *                extra variables present.
     * @return
     */
    public static DCEquation generate(Circuit circuit)
    {
        DCNonLinEquation equation = new DCNonLinEquation(circuit.getMatrixSize());

        for (CircuitElement element : circuit.getElements())
        {
            element.applyDC(equation);
        }
        return equation;
    }

    /**
     * Solve for non-linear DC point analysis.  Assumes 0 initial
     * conditions.
     * @return
     */
    public IRealVector solve(IRealVector prevVector, boolean firstIteration)
    {

        //Note sure about this...
        if (Options.isPrintMatrix())
        {
            System.out.println(G);
            System.out.println("=\n");
            System.out.println(b);
        }

        return myNewtonRapComp(G, b, nonLinearElem, prevVector, firstIteration);
    }



    public ArrayList<NonLinCircuitElement> getNonLinearElem() {
        return nonLinearElem;
    }
}