package sriracha.simulator.model;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.dc.DCNonLinEquation;
import sriracha.simulator.solver.analysis.trans.TransNonLinEquation;

/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 03/11/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NonLinCircuitElement extends CircuitElement{

    protected MathActivator activator = MathActivator.Activator;

    /**
     * Circuit Element Constructor
     *
     * @param name element name from netlist
     */
    protected NonLinCircuitElement(String name) {
        super(name);
    }

    /**
     * Apply the contribution of the non-linear circuit element to the equation.
     *
     * @param equation DCEquation object to be stamped
     */
    public abstract void applyDC(DCNonLinEquation equation);

    public abstract void applyTrans(TransNonLinEquation equation);

    /**
     * Method adds the contribution of this instance of non-linear circuit element
     * into the f vector.
     * @param f vector(array) in which the contribution is added.
     * @param x node voltage vector.
     */
    public abstract void getNonLinContribution(IComplexVector f, IComplexVector x);

    /**
     * Method adds the contribution of this instance of non-linear circuit element
     * into the f vector.
     * @param f vector(array) in which the contribution is added.
     * @param x node voltage vector.
     */
    public abstract void getNonLinContribution(IRealVector f, IRealVector x);

    /**
     * Method adds the contribution of this instance of non-linear circuit element
     * into the Jacobian matrix for the purpose of performing Newton Raphson.
     * @param J the Jacobian matrix in which the contribution is to be added.
     * @param x node voltage vector.
     */
    public abstract void getHessianContribution(IComplexMatrix J, IComplexVector x);

    /**
     * Method adds the contribution of this instance of non-linear circuit element
     * into the Jacobian matrix for the purpose of performing Newton Raphson.
     * @param J the Jacobian matrix in which the contribution is to be added.
     * @param x node voltage vector.
     */
    public abstract void getHessianContribution(IRealMatrix J, IRealVector x);

}