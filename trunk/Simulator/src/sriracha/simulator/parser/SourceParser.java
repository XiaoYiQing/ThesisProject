package sriracha.simulator.parser;

import sriracha.math.MathActivator;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.ICollectElements;
import sriracha.simulator.model.elements.ctlsources.*;
import sriracha.simulator.model.elements.sources.CurrentSource;
import sriracha.simulator.model.elements.sources.Source;
import sriracha.simulator.model.elements.sources.SourceClass;
import sriracha.simulator.model.elements.sources.VoltageSource;
import sriracha.simulator.model.elements.sources.transient_functions.PulseTransFun;
import sriracha.simulator.model.elements.sources.transient_functions.SinTransFun;
import sriracha.simulator.model.elements.sources.transient_functions.TransientFunction;

import java.util.Arrays;


/**
 * Created by yiqing on 27/02/14.
 */
public class SourceParser {

    public static void createVCCS(ICollectElements elementCollection, String[]params)
    {
        VCCS vccs = new VCCS(params[0], CircuitBuilder.parseDouble(params[5]));
        createVoltageControlledSource(elementCollection, params[1], params[2], params[3], params[4], vccs);
    }

    public static void createVCVS(ICollectElements elementCollection, String[]params)
    {
        VCVS vcvs = new VCVS(params[0], CircuitBuilder.parseDouble(params[5]));
        createVoltageControlledSource(elementCollection, params[1], params[2], params[3], params[4], vcvs);
    }

    public static void createCCCS(ICollectElements elementCollection, String name, String node1, String node2, CircuitElement vSource, String value)
    {
        CCCS cccs = new CCCS(name, CircuitBuilder.parseDouble(value), (VoltageSource) vSource);

        createCurrentControlledSource(elementCollection, node1, node2, cccs);
    }


    public static void createCCVS(ICollectElements elementCollection, String name, String node1, String node2, CircuitElement vSource, String value)
    {
        CCVS ccvs = new CCVS(name, CircuitBuilder.parseDouble(value), (VoltageSource) vSource);
        SourceParser.createCurrentControlledSource(elementCollection, node1, node2, ccvs);
    }

    public static void createVoltageControlledSource(ICollectElements elementCollection, String node1, String node2, String control1, String control2, VCSource source)
    {
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        int controlNode1Index = elementCollection.assignNodeMapping(control1);
        int controlNode2Index = elementCollection.assignNodeMapping(control2);
        source.setNodeIndices(node1Index, node2Index, controlNode1Index, controlNode2Index);
        elementCollection.addElement(source);
    }

    public static void createCurrentControlledSource(ICollectElements elementCollection, String node1, String node2, CCSource source)
    {
        int node1Index = elementCollection.assignNodeMapping(node1);
        int node2Index = elementCollection.assignNodeMapping(node2);
        source.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(source);
    }

    /**
     * Generate the source according to the specifications in "params" and insert it into "elementCollection".
     * Note that the amount and order of information in params are assumed to be verified prior to this function
     * call.
     * @param elementCollection element collection in which the new source is to be added.
     * @param srcClass the type of source to be created.
     * @param params the source's characteristics.
     */
    public static void generateSource(ICollectElements elementCollection, SourceClass srcClass, String[]params){
        SourceValue sourceValue = new SourceValue();
        TransientFunction newTransFun = null;

        //The first three parameters MUST be the name, the positive node, the negative node.
        //Skip these three for remaining parameters, thus paramsIndex starts at 3
        int paramsIndex = 3;


        //Obtain the source DC and AC value, and its transient function if there applicable.
        while(paramsIndex < params.length){
            //Obtain DC value
            if(params[paramsIndex].equalsIgnoreCase("DC")){
                sourceValue.DC = CircuitBuilder.parseDouble(params[paramsIndex + 1]);

                paramsIndex = paramsIndex + 2;
                //Obtain AC value
            }else if(params[paramsIndex].equalsIgnoreCase("AC")){
                double amplitude = CircuitBuilder.parseDouble(params[paramsIndex + 1]);
                double phase = Math.toRadians(CircuitBuilder.parseDouble(params[paramsIndex + 3]));
                double real = amplitude * Math.cos(phase);
                double imaginary = amplitude * Math.sin(phase);
                sourceValue.AC = MathActivator.Activator.complex(real, imaginary);

                paramsIndex = paramsIndex + 4;
                //Obtain transient function
            }else{
                String[]subParams = Arrays.copyOfRange(params, paramsIndex + 1, params.length);
                if(params[paramsIndex].equalsIgnoreCase("SIN")){
                    newTransFun = new SinTransFun(subParams, srcClass);
                }else if(params[paramsIndex].equalsIgnoreCase("PULSE")){
                    newTransFun = new PulseTransFun(subParams, srcClass);
                }
                //give loop exit condition.
                paramsIndex = params.length;
            }//end of else
        }//end of while(paramsIndex < params.length)

        Source newSrc = null;
        String name = params[0];

        switch(srcClass){
            case currSrc: newSrc = new CurrentSource(name, sourceValue.DC, sourceValue.AC, newTransFun);    break;
            case voltSrc: newSrc = new VoltageSource(name, sourceValue.DC, sourceValue.AC, newTransFun);    break;
        }

        //Source's positive node
        int node1Index = elementCollection.assignNodeMapping(params[1]);
        //Souce's negative node
        int node2Index = elementCollection.assignNodeMapping(params[2]);
        //update node indexes
        newSrc.setNodeIndices(node1Index, node2Index);
        //Add new source to the collection
        elementCollection.addElement(newSrc);
    }//end of: generateSource(ICollectElements elementCollection, SourceClass srcClass, String[]params)
}
