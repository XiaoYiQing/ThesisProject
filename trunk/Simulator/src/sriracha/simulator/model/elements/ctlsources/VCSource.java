package sriracha.simulator.model.elements.ctlsources;

/**
 * Base class for Controlled sources.
 */
public abstract class VCSource extends ControlledSource
{

    /**
     * control nodes positive and negative
     * positive current flows rom positive to negative node
     */
    int ncPlus;
    int ncMinus;

    /**
     * Constructor for controlled sources
     *
     * @param gm - factor in source equation
     */
    protected VCSource(String name, double gm)
    {
        super(name, gm);
    }


    /**
     * Set the indices that correspond to the circuit element's nodes.
     * The nodes are assumed to be in the order they are in the netlist.
     * (-1 is always ground)
     *
     * @param indices the ordered node indices
     */
    @Override
    public void setNodeIndices(int... indices)
    {

        super.setNodeIndices(indices);
        ncPlus = indices[2];
        ncMinus = indices[3];
    }


    /**
     * @return an array containing the matrix indices for the nodes in this circuit element
     */
    @Override
    public int[] getNodeIndices()
    {
        return new int[]{nPlus, nMinus, ncPlus, ncMinus};
    }

    public String toString(){
        return name + " " + nPlus + " " + nMinus + " " + ncPlus + " " + ncMinus + " " + gm;
    }

}
