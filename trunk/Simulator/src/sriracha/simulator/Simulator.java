package sriracha.simulator;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.parser.CircuitBuilder;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysisResults;
import sriracha.simulator.solver.output.filtering.OutputFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main Class for interaction with the Simulator, abstracts away all the sub-components
 */
public class Simulator implements ISimulator
{

    public static Simulator Instance = new Simulator();

    private boolean cancelRequested;

    private Circuit circuit;

    private CircuitBuilder builder;

    private List<Analysis> requestedAnalysis;

    private List<OutputFilter> outputFilters;

    private HashMap<AnalysisType, IAnalysisResults> results;


    private Simulator()
    {
        requestedAnalysis = new ArrayList<Analysis>();
        outputFilters = new ArrayList<OutputFilter>();
        results = new HashMap<AnalysisType, IAnalysisResults>();

    }

    private boolean saveAll()
    {
        boolean success = true;
        for (Analysis analysis : requestedAnalysis)
        {
            success &= save(analysis);

            if (!success) break;
        }

        return success;
    }

    private boolean save(Analysis analysis)
    {
        IAnalysisResults res = analysis.run();

        if (res == null)
        {
            cancelRequested = false;
            return false;
        }

        results.put(analysis.getType(), res);
        return true;


    }

    /**
     * Sets a new Circuit, and remakes the this.generator and this.equation Fields
     *
     * @param circuit
     */
    private boolean setCircuit(Circuit circuit)
    {
        System.out.println("In Simulator => setCircuit(Circuit circuit)");
        this.circuit = circuit;
        circuit.assignAdditionalVarIndices();
        if (Options.isPrintCircuit())
        {
            System.out.println(circuit);
        }

        return saveAll();

    }


    /**
     * Parses the netlist and builds an internal representation
     *
     * @param netlist - the text circuit and analysis description
     */
    @Override
    public boolean setNetlist(String netlist)
    {
        System.out.println("In Simulator => setNetList(String netlist) before builder");
        clearData();


        //Build the circuit
        builder = new CircuitBuilder(netlist);
        setCircuit(builder.getCircuit());

        requestedAnalysis.addAll(builder.getAnalysisTypes());

        System.out.println("In Simulator => setNetList(String netlist) before request analysis");
        for (Analysis a : requestedAnalysis)
        {
            a.extractSolvingInfo(circuit);
        }

        System.out.println("In Simulator => setNetList(String netlist) before filters");
        outputFilters.addAll(builder.getOutputFilters());

        return saveAll();
    }


    @Override
    public boolean addAnalysis(String analysis)
    {
        Analysis a = builder.parseAnalysis(analysis, builder.getCircuit());
        requestedAnalysis.add(a);
        a.extractSolvingInfo(circuit);
        return save(a);
    }

    @Override
    public IPrintData requestPrintData(String filter)
    {
        OutputFilter f = builder.parsePrint(filter);
        outputFilters.add(f);
        IAnalysisResults r = results.get(f.getAnalysisType());
        return f.filterResults(r);
    }

    /**
     * list of computed and filtered results.
     * each IPrintData corresponds to a .PRINT statement
     * They are found in the list in the same order as
     * results were requested or found in the netlist.
     *
     * @return all computed results so far
     */
    @Override
    public List<IPrintData> getAllResults()
    {
        ArrayList<IPrintData> data = new ArrayList<IPrintData>();
        for (OutputFilter f : outputFilters)
        {
            IAnalysisResults result = results.get(f.getAnalysisType());
            data.add(f.filterResults(result));
        }
        return data;
    }


    private void clearData()
    {
        outputFilters = new ArrayList<OutputFilter>();
        results = new HashMap<AnalysisType, IAnalysisResults>();
        requestedAnalysis = new ArrayList<Analysis>();
    }

    /**
     * debug routine
     */
    public void printAllInfo()
    {

        System.out.println("Circuit:");
        System.out.println(circuit);
        System.out.println("\nRequested Analysis:");
        for (Analysis a : requestedAnalysis)
        {
            System.out.println(a + "\n");
        }
        System.out.println("\nOutput Filters:");
        for (OutputFilter f : outputFilters)
        {
            System.out.println(f + "\n");
        }


    }


    public boolean isCancelRequested()
    {
        return cancelRequested;
    }

    /**
     * In order to request that the simulator stop what it is doing
     * it is assumed this call comes in on a thread different than the one the simulator is running on.
     */
    @Override
    public void requestCancel()
    {
        cancelRequested = true;
    }
}
