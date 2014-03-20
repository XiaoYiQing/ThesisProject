import sriracha.simulator.IPrintData;
import sriracha.simulator.Options;
import sriracha.simulator.Simulator;
import sriracha.simulator.parser.CircuitBuilder;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.output.filtering.OutputFilter;

import java.io.IOException;
import java.util.List;

/**
 * Eventually this should be an interactive test console for playing around with various parts of the simulator.
 */
public class Console
{

    private static Simulator simulator;


    public static void main(String[] args) throws IOException
    {
        Initilialize();

        if (args.length > 0)
        {
            if (args[0].charAt(0) == '-')
            {
                //act according to arg
                String opt = args[0].substring(1);
                if (opt.equals("gp"))
                {
                    //gnuplot, prepare output files for gnuplot test
                    if (args.length < 3)
                    {
                        System.err.println("Insufficient args, should be:\n ... -gp netlist gnuplotCommandFile");
                        System.exit(1);
                    }
                    gnuplot(args[1], args[2]);
                } else if (opt.equals("b") || opt.equals("d") || opt.equals("p"))
                {
                    if (opt.equals("d") || opt.equals("p"))
                    {
                        if (opt.equals("d"))
                        {
                            Options.setPrintCircuit(true);
                            Options.setPrintMatrix(true);
                        }
                        Options.setPrintProgress(true);
                    }

                    //batch, run file contents and exit
                    if (args.length < 2)
                    {
                        System.err.println("Insufficient args, should be:\n ... -b netlist8");
                        System.exit(1);
                    }
                    runAndPrint(args[1]);
                    System.exit(0);
                }

            } else
            {
                //default is filename, load circuit

                runAndPrint(args[0]);
                // FileReader r = new FileReader(args[0]);
                // String netlist = r.getContents();
                // simulator.setNetlist(netlist);
            }
        } else
        {
            //Hello!
            //no args, running from IDE insert quick test stuff here ...
            runAndPrint("C:\\1- University stuff\\ECSE 499\\Tests\\bnetlist4.txt");
        }

        //start shell


    }


    private static void Initilialize()
    {
        simulator = Simulator.Instance;

        //lol
    }

    private static void loadNetslist(String netlist)
    {
        FileReader reader = new FileReader(netlist);
        simulator.setNetlist(reader.getContents());

    }

    private static void runAndPrint(String netlist)
    {
        loadNetslist(netlist);
        List<IPrintData> results = simulator.getAllResults();
        for (IPrintData d : results)
        {
            System.out.println(d);
        }
    }

    private static void gnuplot(String netlist, String gpCmdFile)
    {
        loadNetslist(netlist);

        List<IPrintData> results = simulator.getAllResults();

        GnuplotFileMaker gnu = new GnuplotFileMaker(results, gpCmdFile);
        gnu.writeFiles();
    }


    public static void testBasicParsing() throws IOException
    {
        String netlist =
                "V1 n1 0 AC 5 30\n" +
                        "R1 n2 0 5\n" +
                        "I1 n1 n2 3\n" +
                        "C1 n1 0 0.4\n" +
                        "L1 n1 0 0.2\n" +
                        ".AC DEC 10 1 10000\n" +
                        ".PRINT AC I(V1) VM(n2, n1)";

        CircuitBuilder builder = new CircuitBuilder(netlist);
        System.out.println(builder.getCircuit());

        for (Analysis analysis : builder.getAnalysisTypes())
            System.out.println(analysis);

        for (OutputFilter filter : builder.getOutputFilters())
            System.out.println(filter);
    }

    public static void testSubcircuitParsing() throws IOException
    {
        String netlist =
                ".SUBCKT TwoResistors nIn nOut\n" +
                        "R1 nIn n1 10\n" +
                        "R2 n1 nOut 20\n" +
                        ".ENDS\n" +
                        "V1 n1 0 AC 5 30\n" +
                        "R1 n2 0 5\n" +
                        "I1 n1 n2 3\n" +
                        "C1 n1 0 0.4\n" +
                        "XTR1 n1 0 TwoResistors";

        CircuitBuilder builder = new CircuitBuilder(netlist);
        System.out.println(builder.getCircuit());
    }
}
