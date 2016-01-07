/*
 * JOCLBLAS - Java bindings for clBLAS
 *
 * Copyright (c) 2015-2016 Marco Hutter - http://www.jocl.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.jocl.blas;

/** 
 * The constants in this class indicate the side matrix A is located 
 * relative to matrix B during multiplication. 
 */
public class clblasSide
{
    /**
     * Multiply general matrix by symmetric,
     * Hermitian or triangular matrix on the left. 
     */
    public static final int clblasLeft = 0;
    /**
     * Multiply general matrix by symmetric,
     * Hermitian or triangular matrix on the right. 
     */
    public static final int clblasRight = 1;

    /**
     * Private constructor to prevent instantiation
     */
    private clblasSide(){}

    /**
     * Returns a string representation of the given constant
     *
     * @return A string representation of the given constant
     */
    public static String stringFor(int n)
    {
        switch (n)
        {
            case clblasLeft: return "clblasLeft";
            case clblasRight: return "clblasRight";
        }
        return "INVALID clblasSide: "+n;
    }
}

