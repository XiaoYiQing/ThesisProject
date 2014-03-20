package sriracha.simulator.parser;

import sriracha.simulator.model.SubCircuitTemplate;

import java.util.HashMap;

/**
 * Created by yiqing on 04/03/14.
 * This class is intended to help initialize a circuit builder so that it
 * is filled with default templates and information necessary to build a general
 * purpose circuit.
 */
public class CircuitBuilderInit {

    /**
     * initialize a circuit builder so that it is filled with default templates and information
     * necessary to build a general purpose circuit.
     * @param circBuild The target CircuiBuilder to be initialized.
     */
    public static void initCircuitBuilder(CircuitBuilder circBuild){
        addBasicTemplates(circBuild);
    }

    /**
     * Add the basic sub circuit templates for elements such as Op-Amps.
     * @param circBuild The target CircuiBuilder to be filled-in.
     */
    private static void addBasicTemplates(CircuitBuilder circBuild){
        String[]opamp741 = new String[7];

        //www.seas.upenn.edu/~jan/spice/spice.overview.html#Operational
        //in+ (=1), in- (=2), out (=3)
        opamp741[0] = ".subckt opamp741 1 2 3";
        opamp741[1] = "rin 1 2 2meg";
        opamp741[2] = "rout 6 3 75";
        opamp741[3] = "e 4 0 1 2 100k";
        opamp741[4] = "rbw 4 5 0.5meg";
        opamp741[5] = "cbw 5 0 31.85nf";
        opamp741[6] = "eout 6 0 5 0 1";
        /* Note that the ".END" line is omitted for the parseSubCircuitTemplate function. */

        circBuild.parseSubCircuitTemplate(opamp741);

        /*
        Ideal op-amps should have:
            - infinite voltage gain
            - Infinite input resistance
            - 0 output resistance
            - Infinite bandwidth
            - zero input offset voltage
        * */
        String[]opampIdeal = new String[2];
        opampIdeal[0] = ".subckt opampIdeal 1 2 3";
        opampIdeal[1] = "eout 3 0 0 2 100k";
        //opampIdeal[2] = "Rin 1 2 " + (Double.MAX_VALUE);

        circBuild.parseSubCircuitTemplate(opampIdeal);

    }
}
