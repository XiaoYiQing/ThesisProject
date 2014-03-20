package sriracha.simulator.model.elements.sources.transient_functions;

import sriracha.simulator.model.elements.sources.SourceClass;
import sriracha.simulator.parser.CircuitBuilder;

/**
 * Created by yiqing on 27/02/14.
 */
public class SinTransFun extends TransientFunction {
    /**
     * Offset voltage or current in V oe A, by default 0.
     */
    private double vo = 0;

    /**
     * RMS amplitude in V or A, by default 1.
     */
    private double va = 1;

    /**
     * Frequency in Hz, by default 1 kHz
     */
    private double freq = 1e3;

    /**
     * Time delay before beginning the sinusoidal variation in seconds, by default 0.
     * NOTE that total value is 0V or 0A until delay is finished.
     */
    private double td = 0;

    /**
     * Damping factor in units of 1/seconds, by default 0
     */
    private double theta = 0;

    /**
     * Phase delay in units of degrees, by default 0.  This value is evaluated independently of
     * the AC phasor when transient analysis is performed.
     */
    private double phi = 0;

    public SinTransFun(String[]params, SourceClass srcClass){
        super(srcClass);
        int index = 0;

        while(index < params.length){
            switch(index){
                case 0: vo = CircuitBuilder.parseDouble(params[index]);     break;
                case 1: va = CircuitBuilder.parseDouble(params[index]);     break;
                case 2: freq = CircuitBuilder.parseDouble(params[index]);   break;
                case 3: td = CircuitBuilder.parseDouble(params[index]);     break;
                case 4: theta = CircuitBuilder.parseDouble(params[index]);  break;
                case 5: phi = CircuitBuilder.parseDouble(params[index]);    break;
            }
            index++;
        }
    }

    /**
     * Obtain the volt/current value of the sine wave function at the specified time.
     * The equation is vo + va* exp[-(time-td)*theta]*sin[2*PI*(freq*(time-td)+phi/360)]
     * @param time time at which the equation is probed
     * @return The voltage or current value of the transient function at the specified time.
     */
    @Override
    public double probeValue(double time) {
        double answer = 0;
        answer = vo + va*Math.exp(-(time - td)*theta)*Math.sin(2*Math.PI*(freq*(time-td) + phi/360));

        return answer;
    }
}
