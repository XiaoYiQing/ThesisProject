package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.elements.sources.VoltageSource;


public abstract class CCSource extends ControlledSource
{

    /**
     * current controlled sources are specified with a dummy source in order to
     * identify the branch current they are dependent on.
     * <p/>
     * NOTE 1: this dummy source is known as a referenced element, referece elements must always be
     * declared before the element that refers to them in the netlist
     * NOTE 2: if the owner of a referenced element is defined in a subcircuit the referenced element
     * must be in the subcircuit as well.
     */
    protected VoltageSource dummySource;

    /**
     * Circuit Element Constructor
     *
     * @param name element name from netlist
     */
    protected CCSource(String name, double gm, VoltageSource source)
    {
        super(name, gm);
        dummySource = source;
    }

    @Override
    protected int[] getNodeIndices()
    {
        return new int[]{nPlus, nMinus};
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name + " ");
        int[] nodes = getNodeIndices();
        for (int i : nodes)
        {
            sb.append(i);
            sb.append(' ');
        }

        sb.append(dummySource.name);
        sb.append(' ');
        sb.append(gm);

        return sb.toString();
    }

    @Override
    public CircuitElement getReferencedElement()
    {
        return dummySource;
    }

}
