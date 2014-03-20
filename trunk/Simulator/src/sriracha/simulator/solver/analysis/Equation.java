package sriracha.simulator.solver.analysis;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.model.NonLinCircuitElement;

import java.util.ArrayList;

/**
 * Created by yiqing on 01/03/14.
 */
public abstract class Equation {

    /**
     * Factory object for the Math module's objects.
     */
    private MathActivator activator = MathActivator.Activator;

    public static final double STD_H = 1e-9;
    public static final double STD_THRESHOLD = 9e-15;
    public static final int STD_DIVERGENCE_TOLERANCE = 5;
    public static final int STD_CONT_METHOD_ATTEMPTS = 3;

    protected boolean isLinear;
    protected IRealMatrix G;

    public Equation(boolean isLinear)
    {
        this.isLinear = isLinear;
    }

    public abstract void applyRealMatrixStamp(int i, int j, double value);

    public boolean isLinear(){
        return isLinear;
    }

    //The following is a temporary method to build up Newton Iteration solver.
    public IRealVector myNewtonRapComp(IRealMatrix G, IRealVector b,
                                       ArrayList<NonLinCircuitElement> nonLinearElem){

        int size = b.getDimension();

        //The scale factor for the b vector.
        double alpha = 0;
        //Amount of steps toward the final alpha = 1
        int steps = 100;
        //integer indicating whether the continuation method attempt was successful.
        //  (0 = success, -1 = failure)
        int success = -1;
        //integer indicating the amount of failed attempts at continuation method
        int failedAttempts = 0;

        //The node voltage vector (guess)
        IRealVector x0 = activator.realVector(size);
        //The final computed node voltage vector
        IRealVector answer = activator.realVector(size);

        while(success == -1 && failedAttempts < STD_CONT_METHOD_ATTEMPTS){
            //skip 0th step, where the answer is going to be a zero vector.
            for(int i = 0; i < steps; i++){

                alpha += 1.0/steps;

                success = myNewtonRap(G, (b.times(alpha)), nonLinearElem, x0, answer);
                //When divergence occurs, reset all values and chose a larger amounts of
                //steps to aim for a better change of convergence.  Restart continuation method.
                if(success == -1){
                    alpha = 0;
                    steps *= 10;
                    x0.clear();
                    failedAttempts++;
                    break;
                }

                x0.copy(answer);
            }
        }
        return answer;
    }

    /**
     * A Newton Raphson iteration method which is taylored to solve
     * the non-linear case.
     * @param G
     * @param b
     * @param xGuess initial guess of node voltages
     * @param answer the vector in which the final result is stored
     * @return signal variable indicating whether the N-R iter converged or not.
     *      (-1 = divergent, 0 = convergent)
     */
    public int myNewtonRap(IRealMatrix G, IRealVector b,
                           ArrayList<NonLinCircuitElement> nonLinearElem, IRealVector xGuess, IRealVector answer)
    {
        /*
        * phi(x) = Gx + f(x) - b
        * d(phi(x))/dx = G + df(x)/dx
        * */

        //dimension of the square matrix
        int n = b.getDimension();

        //flag indicating how many times iteration changes are divergent
        int flag = 0;

        double prevChangeMag = Integer.MAX_VALUE;
        double presentChangeMag;

        //initial guess
        IRealVector x0 = activator.realVector(n);
        x0.copy(xGuess);
        //The non-linear element contribution vector
        IRealVector f0 = activator.realVector(n);
        //The Hessian matrix
        IRealMatrix df0 = activator.realMatrix(n,n);

        IRealVector deltaX = activator.realVector(n);
        IRealVector phi = activator.realVector(n);
        //The Jacobian matrix
        IRealMatrix J = activator.realMatrix(n,n);

        do{

            f0.clear();
            df0.clear();

            //Get the non-linear contribution of the present node voltage guess vector
            for(int i = 0; i < nonLinearElem.size(); i++){
                nonLinearElem.get(i).getNonLinContribution(f0,x0);
                nonLinearElem.get(i).getHessianContribution(df0,x0);
            }

            //phi(x) = Gx + f(x) - b
            phi.copy((IRealVector)((G.times(x0)).plus(f0)).minus(b));
            //d(phi(x))/dx = G + df(x)/dx
            J.copy((IRealMatrix)G.plus(df0));


            J.inverse();

            //deltaX = -J' * phi(x)
            deltaX.copy((IRealVector)J.times(phi).times((-1)));
            x0 = (IRealVector)x0.plus(deltaX);

            presentChangeMag = deltaX.getMaxMag();
            if(presentChangeMag < prevChangeMag){
                flag = 0;
                prevChangeMag = presentChangeMag;
            }else{
                flag++;
                //Divergence detected when the largest magnitude of the correcting terms in
                //deltaX increased for some consecutive iterations.
                if(flag > STD_DIVERGENCE_TOLERANCE)
                    return -1;
                prevChangeMag = presentChangeMag;
            }

        }while(presentChangeMag > STD_THRESHOLD);

        answer.copy(x0);
        return 0;
    }

    public String toString(){
        String output = "";

        output = output + "Linear: " + isLinear() + "\n";
        output = output + this.getClass();

        return output;
    }
}
