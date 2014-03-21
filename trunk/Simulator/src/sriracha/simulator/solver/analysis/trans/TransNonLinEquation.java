package sriracha.simulator.solver.analysis.trans;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.Options;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.NonLinCircuitElement;

import java.util.ArrayList;

/**
 * Created by yiqing on 01/03/14.
 */
public class TransNonLinEquation extends TransEquation {

    public static final double STD_H = 1e-9;
    public static final double STD_THRESHOLD = 9e-15;
    public static final int STD_DIVERGENCE_TOLERANCE = 5;
    public static final int STD_CONT_METHOD_ATTEMPTS = 3;

    /**
     * Factory object for the Math module's objects.
     */
    private MathActivator activator = MathActivator.Activator;

    private ArrayList<NonLinCircuitElement> nonLinearElem;


    private TransNonLinEquation(int circuitNodeCount) {
        super(circuitNodeCount, false);

        //Note: the array list initiate with a guessed size of amount of
        //non-linear circuit element. (guessing it as number of nodes)
        nonLinearElem = new ArrayList<NonLinCircuitElement>(circuitNodeCount);
    }

    public void applyNonLinearCircuitElem(NonLinCircuitElement input){
        nonLinearElem.add(input);
    }

    /**
     * This method acts as the official constructor of TransNonLinEquation objects.
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
    public static TransEquation generate(Circuit circuit)
    {
        TransNonLinEquation equation = new TransNonLinEquation(circuit.getMatrixSize());

        for (CircuitElement element : circuit.getElements())
        {
            element.applyTrans(equation);
        }
        return equation;
    }

    /* Solve: P*x(n+1) = Q for the given the current nodal voltages and the
   *  returns x(n+1) , i.e. nextVoltage
   * */
    IRealVector solve(double timeStep, IRealVector currentVoltage, double nextTime) {
        //Update the b vector for the "nextTime" specified.
        getNewBVector(nextTime);
        //(P = G + C/h)
        IRealMatrix P = buildMatrixP(timeStep);
        //Q = C/h*x(n) + b(n+1)
        IRealVector Q = buildVectorQ(timeStep, currentVoltage);
        if (Options.isPrintMatrix())
        {
            System.out.println(P);
            System.out.println("=\n");
            System.out.println(Q);
        }
        //P*x(n+1) = Q, solve for x(n+1)
        //return P.solve(Q);
        return myNewtonRapComp(P, Q, nonLinearElem);
    }

    public ArrayList<NonLinCircuitElement> getNonLinearElem() {
        return nonLinearElem;
    }

}
