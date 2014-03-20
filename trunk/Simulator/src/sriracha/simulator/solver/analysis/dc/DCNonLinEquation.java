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
    public IRealVector solve()
    {

        //Note sure about this...
        if (Options.isPrintMatrix())
        {
            System.out.println(G);
            System.out.println("=\n");
            System.out.println(b);
        }

        return myNewtonRapComp(G, b, nonLinearElem);
    }



    public ArrayList<NonLinCircuitElement> getNonLinearElem() {
        return nonLinearElem;
    }

    public static void main(String[]args){

        IRealMatrix myG = MathActivator.Activator.realMatrix(3,3);
        myG.setValue(0,0,1);
        myG.setValue(0,1,-1);
        myG.setValue(1,0,-1);
        myG.setValue(1,1,2);
        myG.setValue(2,2,1);

        IRealVector myb = MathActivator.Activator.realVector(3);
        myb.setValue(0,2);

        DCNonLinEquation myEq = new DCNonLinEquation(3);
        myEq.G = myG;
        myEq.b = myb;

        Diode d1 = new Diode("D1",1e-5);
        Diode d2 = new Diode("D2",1e-4);
        Diode d3 = new Diode("D3",1e-6);

        d1.setNodeIndices(1,-1);
        d2.setNodeIndices(0,2);
        d3.setNodeIndices(2,-1);

        //IRealVector myF = MathActivator.Activator.realVector(3);
        //IRealVector myX = MathActivator.Activator.realVector(3);
        //IRealMatrix myJ = MathActivator.Activator.realMatrix(3, 3);
        IRealVector answer = MathActivator.Activator.realVector(3);

        //myX.setValue(0,0.1);
        //myX.setValue(1,0.1);

        myEq.applyNonLinearCircuitElem(d1);
        myEq.applyNonLinearCircuitElem(d2);
        myEq.applyNonLinearCircuitElem(d3);


        //myEq.myNewtonRap(myEq.G, myEq.b, myEq.getNonLinearElem(), myX,answer);

        //answer = myEq.myNewtonRapComp(myEq.G, myEq.b, myEq.getNonLinearElem());
        answer = myEq.solve();
        System.out.println(answer.getValue(0) + " \n" + answer.getValue(1) +
                "\n" + answer.getValue(2));

        /*d1.getNonLinContribution(myF, myX);
        d1.getHessianContribution(myJ, myX);
        System.out.println(myF);
        System.out.println(myJ);*/


    }
}