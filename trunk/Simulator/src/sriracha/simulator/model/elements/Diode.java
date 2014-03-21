package sriracha.simulator.model.elements;

import sriracha.math.interfaces.*;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.NonLinCircuitElement;
import sriracha.simulator.model.models.DiodeModel;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;
import sriracha.simulator.solver.analysis.dc.DCNonLinEquation;
import sriracha.simulator.solver.analysis.trans.TransEquation;
import sriracha.simulator.solver.analysis.trans.TransNonLinEquation;

/**
 * Diode circuit element using the equation: I = Is*(exp(V/Vt)-1)
 */
public class Diode extends NonLinCircuitElement {

    /**
     * Standard: 25mV
     */
    public static final double STD_VT = 0.025;
    /**
     * Standard saturation current: 1e-14A
     */
    public static final double STD_IS = 0.00000000000001;
    /**
     * Diode's anode
     */
    protected int nodeA;
    /**
     * Diode's cathode
     */
    protected int nodeB;
    private double is;
    private double vt;

    public Diode(String name){
        super(name);
        is = STD_IS;
        vt = STD_VT;
    }


    public Diode(String name, double Is){
        super(name);
        this.is = Is;
        vt = STD_VT;
    }

    public Diode(String name, double is, double vt){
        super(name);
        this.is = is;
        this.vt = vt;
    }


    public Diode(String name, DiodeModel model){
        super(name);
        this.is = model.getIs();
        this.vt = model.getVt();
    }

    @Override
    public void getNonLinContribution(IComplexVector f, IComplexVector x){

        //Note: A node value of -1 is ground by default.
        //If a node is -1, then its voltage is 0 and has no contribution

        double value = 0;
        if(nodeA == -1){
            value = is*(Math.exp((x.getValue(nodeB).opposite()).getReal()/vt)-1);
            f.addValue(nodeB, activator.complex(-value,0));
        }else if(nodeB == -1){
            value = is*(Math.exp(x.getValue(nodeA).getReal()/vt)-1);
            f.addValue(nodeA, activator.complex(value,0));
        }else{
            value = is*(Math.exp((x.getValue(nodeA).minus(x.getValue(nodeB))).getReal()/vt)-1);
            f.addValue(nodeA, activator.complex(value,0));
            f.addValue(nodeB, activator.complex(-value,0));
        }

    }

    @Override
    public void getNonLinContribution(IRealVector f, IRealVector x){

        //Note: A node value of -1 is ground by default.
        //If a node is -1, then its voltage is 0 and has no contribution

        double value = 0;
        if(nodeA == -1){
            value = is*Math.exp((-1*(x.getValue(nodeB))/vt)-1);
            f.addValue(nodeB, -value);
        }else if(nodeB == -1){
            value = is*(Math.exp(x.getValue(nodeA)/vt)-1);
            f.addValue(nodeA, value);
        }else{
            value = is*(Math.exp((x.getValue(nodeA)-x.getValue(nodeB))/vt)-1);
            f.addValue(nodeA, value);
            f.addValue(nodeB, -value);
        }

    }

    @Override
    public void getHessianContribution(IComplexMatrix J, IComplexVector x){
        double value;

        if(nodeA == -1){
            value = is/vt*Math.exp((x.getValue(nodeB).opposite()).getReal()/vt);
            J.addValue(nodeB, nodeB, value, 0);
        }else if(nodeB == -1){
            value = is/vt*Math.exp((x.getValue(nodeA)).getReal()/vt);
            J.addValue(nodeA, nodeA, value, 0);
        }else{
            value = is/vt*Math.exp((x.getValue(nodeA).minus(x.getValue(nodeB))).getReal()/vt);

            J.addValue(nodeA, nodeA, value, 0);
            J.addValue(nodeA, nodeB, -value, 0);
            J.addValue(nodeB, nodeA, -value, 0);
            J.addValue(nodeB, nodeB, value, 0);
        }

    }

    @Override
    public void getHessianContribution(IRealMatrix J, IRealVector x){
        double value;

        if(nodeA == -1){
            value = is/vt*Math.exp(-x.getValue(nodeB)/vt);
            J.addValue(nodeB, nodeB, value);
        }else if(nodeB == -1){
            value = is/vt*Math.exp((x.getValue(nodeA))/vt);
            J.addValue(nodeA, nodeA, value);
        }else{
            value = is/vt*Math.exp((x.getValue(nodeA)-(x.getValue(nodeB)))/vt);

            J.addValue(nodeA, nodeA, value);
            J.addValue(nodeA, nodeB, -value);
            J.addValue(nodeB, nodeA, -value);
            J.addValue(nodeB, nodeB, value);
        }

    }

    @Override
    public void setNodeIndices(int... indices){
        nodeA = indices[1];
        nodeB = indices[0];
    }

    @Override
    protected int[] getNodeIndices() {
        return new int[]{nodeA, nodeB};
    }

    @Override
    public int getNodeCount() {
        return 2;
    }

    @Override
    public int getExtraVariableCount() {
        return 0;
    }

    @Override
    protected CircuitElement buildCopy(String name, CircuitElement referencedElement) {
        return new Diode(this.name, this.is, this.vt);
    }

    @Override
    public void applyDC(DCEquation equation) {
        if(!equation.isLinear())
            applyDC((DCNonLinEquation)equation);
    }

    @Override
    public void applyDC(DCNonLinEquation equation){
        equation.applyNonLinearCircuitElem(this);
    }

    @Override
    public void applyAC(ACEquation equation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void applyTrans(TransEquation equation)
    {
        if(!equation.isLinear())
            applyTrans((TransNonLinEquation) equation);
    }

    @Override
    public void applyTrans(TransNonLinEquation equation)
    {
        equation.applyNonLinearCircuitElem(this);
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + is + " " + vt;
    }


    public double getVt() {
        return vt;
    }

    public double getIs() {
        return is;
    }


}