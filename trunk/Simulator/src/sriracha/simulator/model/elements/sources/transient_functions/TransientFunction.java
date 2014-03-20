package sriracha.simulator.model.elements.sources.transient_functions;


import sriracha.simulator.model.elements.sources.SourceClass;

/**
 * Created by yiqing on 27/02/14.
 */
public abstract class TransientFunction {

    SourceClass srcClass;


    public TransientFunction(SourceClass myclass){
        this.srcClass = myclass;
    }

    /**
     * Obtain the contribution of this transient function to the b vector.
     * @param time time at which the equation is probed
     * @return value of the equation at the specified time
     */
    public abstract double probeValue(double time);

}
