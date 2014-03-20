package sriracha.simulator.parser;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.ICollectElements;
import sriracha.simulator.model.elements.Capacitor;
import sriracha.simulator.model.elements.Diode;
import sriracha.simulator.model.elements.Inductor;
import sriracha.simulator.model.elements.Resistor;
import sriracha.simulator.model.elements.ctlsources.*;
import sriracha.simulator.model.elements.sources.CurrentSource;
import sriracha.simulator.model.elements.sources.VoltageSource;
import sriracha.simulator.model.models.CircuitElementModel;
import sriracha.simulator.model.models.DiodeModel;

/**
 * Created by yiqing on 26/02/14.
 */
public class ElementParser {



    public static void createResistor(ICollectElements elementCollection, String[]params)
    {
        Resistor r = new Resistor(params[0], CircuitBuilder.parseDouble(params[3]));
        int node1Index = elementCollection.assignNodeMapping(params[1]);
        int node2Index = elementCollection.assignNodeMapping(params[2]);
        r.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(r);
    }


    public static void createCapacitor(ICollectElements elementCollection, String[]params)
    {
        Capacitor c = new Capacitor(params[0], CircuitBuilder.parseDouble(params[3]));
        int node1Index = elementCollection.assignNodeMapping(params[1]);
        int node2Index = elementCollection.assignNodeMapping(params[2]);
        c.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(c);
    }

    public static void createInductor(ICollectElements elementCollection, String[]params)
    {
        Inductor i = new Inductor(params[0], CircuitBuilder.parseDouble(params[3]));
        int node1Index = elementCollection.assignNodeMapping(params[1]);
        int node2Index = elementCollection.assignNodeMapping(params[2]);
        i.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(i);
    }

    /**
     * Add a new diode circuit element to the circuit using the specified model "modelName"
     *
     * @param elementCollection The collection in which the diode is to be added
     * @param params params[0]: Name of this diode (Must be unique).
     *       params[1]: node1 cathode of the diode, where the current is heading.
     *       params[2]: node2 anode of the diode, where the current leaves.
     *       params[3]: modelName Name of the Diode model on which the new Diode is based on (Must exists).
     */
    public static void createDiode(ICollectElements elementCollection, String[]params, CircuitElementModel myModel){
        Diode d;

        if(myModel.getKey() == 'D'){
            d = new Diode(params[0], (DiodeModel)(myModel));
        }else{
            System.out.println("Warning, non diode MODEL specified for a diode element.\n"+
                    "Standard diode parameters applied.");
            d = new Diode(params[0]);
        }

        int node1Index = elementCollection.assignNodeMapping(params[1]);
        int node2Index = elementCollection.assignNodeMapping(params[2]);
        d.setNodeIndices(node1Index, node2Index);
        elementCollection.addElement(d);
    }


}
