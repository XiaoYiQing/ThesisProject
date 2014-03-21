
package sriracha.simulator.parser;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.model.*;
import sriracha.simulator.model.elements.sources.SourceClass;
import sriracha.simulator.model.models.CircuitElementModel;
import sriracha.simulator.model.elements.Capacitor;
import sriracha.simulator.model.elements.Inductor;
import sriracha.simulator.model.elements.Resistor;
import sriracha.simulator.model.elements.Diode;
import sriracha.simulator.model.elements.ctlsources.*;
import sriracha.simulator.model.elements.sources.CurrentSource;
import sriracha.simulator.model.elements.sources.Source;
import sriracha.simulator.model.elements.sources.VoltageSource;
import sriracha.simulator.model.models.DiodeModel;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.ac.ACAnalysis;
import sriracha.simulator.solver.analysis.ac.ACSubType;
import sriracha.simulator.solver.analysis.dc.DCAnalysis;
import sriracha.simulator.solver.analysis.trans.TransAnalysis;
import sriracha.simulator.solver.analysis.dc.DCSweep;
import sriracha.simulator.solver.output.filtering.*;

import java.util.*;

public class CircuitBuilder
{
    private HashMap<String, SubCircuitTemplate> subcircuitTemplates = new HashMap<String, SubCircuitTemplate>();

    private Circuit circuit;
    private ArrayList<Analysis> analysisTypes = new ArrayList<Analysis>();
    private ArrayList<OutputFilter> outputFilters = new ArrayList<OutputFilter>();

    /**
     * List of circuit element models, such as models for diodes and BJTs.
     */
    private HashMap<String, CircuitElementModel>  circuitElementModels = new HashMap<String, CircuitElementModel>();

    public Circuit getCircuit()
    {
        return circuit;
    }

    public List<Analysis> getAnalysisTypes()
    {
        return Collections.unmodifiableList(analysisTypes);
    }

    public List<OutputFilter> getOutputFilters()
    {
        return Collections.unmodifiableList(outputFilters);
    }

    public CircuitBuilder(String netlist)
    {
        ArrayList<String>initialList = new ArrayList<String>();
        String[] lines = netlist.split("\\r?\\n");

        CircuitBuilderInit.initCircuitBuilder(this);

        /*for(int i = 0; i < lines.length; i++){
            initialList.add(lines[i]);
            if(i == 0){
                CircuitBuilderInit.addBasicTemplates(initialList);
            }
        }

        lines = new String[initialList.size()];
        for(int i = 0; i < lines.length; i++){
            lines[i] = initialList.get(i);
        }*/

        ArrayList<String> sourceLines = new ArrayList<String>();
        ArrayList<String> dependentSourceLines = new ArrayList<String>();
        ArrayList<String> otherLines = new ArrayList<String>();

        circuit = new Circuit(lines[0]);
        for (int i = 1; i < lines.length; i++)
        {
            String line = lines[i];
            String upperLine = line.toUpperCase();

            //Create a new subcircuit template.
            if (upperLine.startsWith(".SUBCKT"))
            {
                ArrayList<String> subCircuitLines = new ArrayList<String>();
                while (i < lines.length && !lines[i].toUpperCase().startsWith(".ENDS"))
                {
                    //If String is empty, part of a comment, or an empty space, move to next String
                    if (lines[i].length() == 0 || lines[i].charAt(0) == '*' || Character.isWhitespace(lines[i].charAt(0)))
                    {
                        i++;
                        continue;
                    }

                    subCircuitLines.add(lines[i]);
                    i++;
                }
                parseSubCircuitTemplate(subCircuitLines.toArray(new String[subCircuitLines.size()]));

            }else if(upperLine.startsWith(".MODEL")){
                CircuitElementModel newModel = parseCircuitElementModel(upperLine);
                if(!circuitElementModels.containsKey(newModel.getName())){
                    circuitElementModels.put(newModel.getName(), newModel);
                }else
                    System.out.println("Model name already in use.");
            }
            if (upperLine.startsWith("V") || upperLine.startsWith("I"))
                sourceLines.add(line);
            else if (upperLine.startsWith("E") || upperLine.startsWith("F")
                    || upperLine.startsWith("G") || upperLine.startsWith("H"))
                dependentSourceLines.add(line);
            else
                otherLines.add(line);
        }

        ArrayList<String> linesList = new ArrayList<String>();
        linesList.addAll(sourceLines);
        linesList.addAll(dependentSourceLines);
        linesList.addAll(otherLines);

        lines = new String[linesList.size()];
        linesList.toArray(lines);

        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];

            if (line.equals(".END")) break;

            if (line.length() == 0 || line.charAt(0) == '*' || Character.isWhitespace(line.charAt(0)))
                continue;

            if (line.charAt(0) != '.'){
                parseCircuitElement(circuit, line);
            }
            /*else if (line.startsWith(".SUBCKT"))
            {
                int startingLine = i;
                ArrayList<String> subCircuitLines = new ArrayList<String>();
                while (!lines[i].startsWith(".ENDS"))
                {
                    if (lines[i].length() == 0 || lines[i].charAt(0) == '*' || Character.isWhitespace(lines[i].charAt(0)))
                    {
                        i++;
                        continue;
                    }


                    subCircuitLines.add(lines[i]);
                    i++;
                }
                parseSubCircuitTemplate(subCircuitLines.toArray(new String[subCircuitLines.size()]));
            } */
            else if (line.startsWith(".AC") || line.startsWith(".DC") || line.startsWith(".TR"))
            {
                analysisTypes.add(parseAnalysis(line, this.circuit));
            }
            else if (line.startsWith(".PRINT"))
            {
                outputFilters.add(parsePrint(line));
            }

        }
    }


    public OutputFilter parsePrint(String line)
    {
        String[] params = tokenizeLine(line);

        if (params.length < 3)
            throw new ParseException("Not enough parameters for .PRINT: " + line);

        String printTypeStr = params[1];
        AnalysisType printType;

        if (printTypeStr.equalsIgnoreCase("AC"))
            printType = AnalysisType.AC;
        else if (printTypeStr.equalsIgnoreCase("DC"))
            printType = AnalysisType.DC;
        else if (printTypeStr.equalsIgnoreCase("TR"))
            printType = AnalysisType.TR;
            //else if (printTypeStr.equalsIgnoreCase("TRAN") || printTypeStr.equalsIgnoreCase("NOISE") || printTypeStr.equalsIgnoreCase("DISTO"))
        else if (printTypeStr.equalsIgnoreCase("NOISE") || printTypeStr.equalsIgnoreCase("DISTO"))
            throw new UnsupportedOperationException("This format of analysis is currently not supported: " + line);
        else
            throw new ParseException("Invalid Plot analysis format: " + line);

        ArrayList<NodeInfo> resultInfoList = new ArrayList<NodeInfo>();
        for (int i = 2; i < params.length; i++)
        {
            char firstChar = Character.toUpperCase(params[i].charAt(0));
            if (firstChar == 'V' || firstChar == 'I')
            {
                NodeDataFormat dataFormat = StringToOutputType(params[i].substring(1, params[i].indexOf('(')), line);
                String[] nodeList = parseBracketContents(params[i].substring(params[i].indexOf('('), params[i].length()));

                for (String node : nodeList)
                    if (node.length() == 0)
                        throw new ParseException("Expected a node name: " + line);

                if (firstChar == 'V')
                {
                    if (nodeList.length == 1)
                        resultInfoList.add(new VoltageInfo(dataFormat, circuit.getNodeIndex(nodeList[0])));
                    else if (nodeList.length == 2)
                        resultInfoList.add(new VoltageInfo(dataFormat, circuit.getNodeIndex(nodeList[0]), circuit.getNodeIndex(nodeList[1])));
                    else
                        throw new ParseException("Voltages can only be requested between 1 or 2 nodes: " + line);
                }
                else if (firstChar == 'I')
                {
                    if (nodeList.length != 1 || Character.toUpperCase(nodeList[0].charAt(0)) != 'V')
                        throw new ParseException("Currents can only be requested at a single voltage source: " + line);

                    resultInfoList.add(new CurrentInfo(dataFormat, nodeList[0], circuit));
                }
            }
            else
                throw new UnsupportedOperationException("The expression '" + params[i] + "' is not supported. Line: " + line);
        }

        OutputFilter outputFilter = new OutputFilter(printType);
        for (NodeInfo info : resultInfoList)
            outputFilter.addData(info);

        return outputFilter;
    }

    public CircuitElementModel parseCircuitElementModel(String line){

        CircuitElementModel newModel;
        newModel = CircuitElementModel.generateModel(line);
        return newModel;
    }

    private NodeDataFormat StringToOutputType(String outputString, String line)
    {
        if (outputString.equals(""))
            return NodeDataFormat.Complex;
        else if (outputString.equalsIgnoreCase("R"))
            return NodeDataFormat.Real;
        else if (outputString.equalsIgnoreCase("I"))
            return NodeDataFormat.Imaginary;
        else if (outputString.equalsIgnoreCase("M"))
            return NodeDataFormat.Magnitude;
        else if (outputString.equalsIgnoreCase("P"))
            return NodeDataFormat.Phase;
        else if (outputString.equalsIgnoreCase("DB"))
            return NodeDataFormat.Decibels;
        else if (outputString.equalsIgnoreCase("t"))
            return NodeDataFormat.Transient;

        throw new ParseException("Invalid output format: " + line);
    }

    private String[] parseBracketContents(String bracketString)
    {
        bracketString = bracketString.trim();

        if (bracketString.charAt(0) != '(' || bracketString.charAt(bracketString.length() - 1) != ')')
            throw new ParseException("Expected an expression enclosed in brackets. Found: " + bracketString);

        // strip brackets
        bracketString = bracketString.substring(1, bracketString.length() - 1);

        return bracketString.split(",\\s*");
    }

    private String[] tokenizeLine(String line)
    {
        ArrayList<String> params = new ArrayList<String>();

        String currentParam = "";
        int bracketLevel = 0;
        for (int i = 0; i < line.length(); i++)
        {
            if (Character.isWhitespace(line.charAt(i)) && bracketLevel == 0)
            {
                if (currentParam.length() > 0)
                    params.add(currentParam);

                currentParam = "";
            }
            else
            {
                if (line.charAt(i) == '(')
                    bracketLevel++;
                else if (line.charAt(i) == ')')
                    bracketLevel--;

                currentParam += line.charAt(i);
            }
        }

        if (currentParam.length() > 0)
            params.add(currentParam);

        if (bracketLevel != 0)
            throw new ParseException("Unmatched brackets: " + line);

        return params.toArray(new String[params.size()]);
    }

    public void parseSubCircuitTemplate(String[] lines)
    {
        String[] params = lines[0].split("\\s+");

        if (params.length < 3)
            throw new ParseException("Not enough parameters for a subcircuit template: " + lines[0]);

        String name = params[1];
        SubCircuitTemplate subCircuit = new SubCircuitTemplate(name, params.length - 2);
        for (int i = 2; i < params.length; i++)
            subCircuit.assignNodeMapping(params[i]);

        // skip the first line (the SUBCKT line)
        for (int i = 1; i < lines.length; i++)
            parseCircuitElement(subCircuit, lines[i]);

        subcircuitTemplates.put(name, subCircuit);

    }

    public static Analysis parseAnalysis(String line, Circuit circuit)
    {
        if (line.startsWith(".AC"))
            return AnalysisParser.parseSmallSignal(line);
        else if (line.startsWith(".DC"))
            return AnalysisParser.parseDCAnalysis(line, circuit);
        else if (line.startsWith(".TR")) {
            return AnalysisParser.parseTransAnalysis(line); }
        else
            throw new UnsupportedOperationException("This format of analysis is currently not supported: " + line);
    }

    private void parseCircuitElement(ICollectElements elementCollection, String line)
    {
        char elementType = Character.toLowerCase(line.charAt(0));

        String[] params = line.split("\\s+");

        if (params.length < 4)
            throw new ParseException("Not enough parameters for a circuit element: " + line);

        String[] additionalParams = Arrays.copyOfRange(params, 3, params.length);
        switch (elementType)
        {
            case 'r':
                ElementParser.createResistor(elementCollection, params);
                break;

            case 'i':
                SourceParser.generateSource(elementCollection, SourceClass.currSrc, params);
                break;

            case 'v':
                SourceParser.generateSource(elementCollection, SourceClass.voltSrc, params);
                break;

            case 'l':
                ElementParser.createInductor(elementCollection, params);
                break;

            case 'c':
                ElementParser.createCapacitor(elementCollection, params);
                break;

            case 'd':
                ElementParser.createDiode(elementCollection, params, circuitElementModels.get(params[3]));
                break;

            case 'g':
                SourceParser.createVCCS(elementCollection, params);
                break;

            case 'e':
                SourceParser.createVCVS(elementCollection, params);
                break;

            case 'f':
                SourceParser.createCCCS(elementCollection, params[0], params[1], params[2], circuit.getElement(params[3]), params[4]);
                break;

            case 'h':
                SourceParser.createCCVS(elementCollection, params[0], params[1], params[2], circuit.getElement(params[3]), params[4]);
                break;

            case 'x':
                createSubcircuit(elementCollection, params[0], params[params.length - 1], Arrays.copyOfRange(params, 1, params.length - 1));
                break;

            default:
                throw new ParseException("Unrecognized element format: " + elementType);
        }
    }

    private void createSubcircuit(ICollectElements elementCollection, String name, String subcircuitName, String[] nodes)
    {

        if (!subcircuitTemplates.containsKey(subcircuitName))
            throw new ParseException("Subcircuit template not found: " + subcircuitName);

        SubCircuit sc = new SubCircuit(name, subcircuitTemplates.get(subcircuitName));

        int[] nodeIndices = new int[nodes.length];
        for (int i = 0; i < nodes.length; i++)
            nodeIndices[i] = elementCollection.assignNodeMapping(nodes[i]);

        sc.setNodeIndices(nodeIndices);
        elementCollection.addElement(sc);

    }

    public HashMap<String, SubCircuitTemplate> getSubCircuitTemplates(){
        return this.subcircuitTemplates;
    }

    public HashMap<String, CircuitElementModel> getCircuitElementModels(){
        return this.circuitElementModels;
    }

    /**
     * parses a double and allows for Spice compatible postfixes (case insensitive)
     * T = 10^12
     * G = 10^9
     * Meg = 10^6
     * K = 10^3
     * m = 10^-3
     * u = 10^-6
     * n = 10^-9
     * p = 10^-12
     * f = 10^-15
     * <p/>
     * the string can be followed with any text after these as long as what comes before is parsable
     *
     * @param str - the string to parse
     * @return result as a double
     */
    public static double parseDouble(String str)
    {

        int i = 0;
        char c = str.charAt(0);
        while ((Character.isDigit(c) || c == '.' || c == '-' || Character.toLowerCase(c) == 'e') && ++i < str.length())
        {
            c = str.charAt(i);
        }

        String str1 = str.substring(0, i), str2 = str.substring(i);

        double factor = 1;

        str2 = str2.toLowerCase();

        if (str2.startsWith("t"))
            factor = Math.pow(10, 12);
        else if (str2.startsWith("g"))
            factor = Math.pow(10, 9);
        else if (str2.startsWith("meg"))
            factor = Math.pow(10, 6);
        else if (str2.startsWith("k"))
            factor = Math.pow(10, 3);
        else if (str2.startsWith("m"))
            factor = Math.pow(10, -3);
        else if (str2.startsWith("u"))
            factor = Math.pow(10, -6);
        else if (str2.startsWith("n"))
            factor = Math.pow(10, -9);
        else if (str2.startsWith("p"))
            factor = Math.pow(10, -12);
        else if (str2.startsWith("f"))
            factor = Math.pow(10, -15);


        return factor * Double.parseDouble(str1);

    }
}