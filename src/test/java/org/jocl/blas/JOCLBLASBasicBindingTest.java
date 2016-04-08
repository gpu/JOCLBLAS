package org.jocl.blas;
import static org.junit.Assert.assertTrue;

import org.jocl.blas.CLBLAS;
import org.junit.Test;

/**
 * Basic test of the bindings of the JCuda and JCudaDriver class
 */
public class JOCLBLASBasicBindingTest
{
    public static void main(String[] args)
    {
        JOCLBLASBasicBindingTest test = new JOCLBLASBasicBindingTest();
        test.testJOCLBLAS();
    }

    @Test
    public void testJOCLBLAS()
    {
        assertTrue(BasicBindingTest.testBinding(CLBLAS.class));
    }


}