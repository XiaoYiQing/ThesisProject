package sriracha.simulator.parser;

/**
 * Created by yiqing on 26/02/14.
 */

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;

/**
 * Helper private class to store DC or AC, or both values as one object.
 * NOTE: AC variable is IComplex object, which contains real and imaginary part.
 */
public class SourceValue
{
    /**
     * dc value stored in SourceValue object
     */
    public double DC = 0;
    /**
     * ac value stored in SourceValue object
     */
    public IComplex AC = MathActivator.Activator.complex(0, 0);

    public SourceValue(){
        DC = 0;
        AC = MathActivator.Activator.complex(0, 0);
    }

    /**
     * Construct SourceValue object initializing dc value only.
     * @param dc dc value
     */
    public SourceValue(double dc)
    {
        DC = dc;
    }

    /**
     * Construct SourceValue object initializing ac value only.
     * @param ac ac value
     */
    public SourceValue(IComplex ac)
    {
        AC = ac;
    }

    /**
     * Construct SourceValue object  initializing both ac and dc values.
     * @param DC dc value
     * @param AC ac value
     */
    public SourceValue(double DC, IComplex AC)
    {
        this.DC = DC;
        this.AC = AC;
    }



}
