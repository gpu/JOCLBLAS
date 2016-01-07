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
import static org.jocl.CL.*;

/**
 * clblas error codes definition, incorporating OpenCL error
 * definitions.<br>
 * <br>
 * This enumeration is a subset of the OpenCL error codes extended with some
 * additional extra codes.  For example, CL_OUT_OF_HOST_MEMORY, which is
 * defined in cl.h is aliased as clblasOutOfHostMemory.
 */
public class clblasStatus
{
    public static final int clblasSuccess = CL_SUCCESS;
    public static final int clblasInvalidValue = CL_INVALID_VALUE;
    public static final int clblasInvalidCommandQueue = CL_INVALID_COMMAND_QUEUE;
    public static final int clblasInvalidContext = CL_INVALID_CONTEXT;
    public static final int clblasInvalidMemObject = CL_INVALID_MEM_OBJECT;
    public static final int clblasInvalidDevice = CL_INVALID_DEVICE;
    public static final int clblasInvalidEventWaitList = CL_INVALID_EVENT_WAIT_LIST;
    public static final int clblasOutOfResources = CL_OUT_OF_RESOURCES;
    public static final int clblasOutOfHostMemory = CL_OUT_OF_HOST_MEMORY;
    public static final int clblasInvalidOperation = CL_INVALID_OPERATION;
    public static final int clblasCompilerNotAvailable = CL_COMPILER_NOT_AVAILABLE;
    public static final int clblasBuildProgramFailure = CL_BUILD_PROGRAM_FAILURE;

    // Extended error codes 
    
    /**
     * Functionality is not implemented 
     */
    public static final int clblasNotImplemented = -1024;
    /**
     * clblas library is not initialized yet 
     */
    public static final int clblasNotInitialized = -1023;
    /**
     * Matrix A is not a valid memory object 
     */
    public static final int clblasInvalidMatA = -1022;
    /**
     * Matrix B is not a valid memory object 
     */
    public static final int clblasInvalidMatB = -1021;
    /**
     * Matrix C is not a valid memory object 
     */
    public static final int clblasInvalidMatC = -1020;
    /**
     * Vector X is not a valid memory object 
     */
    public static final int clblasInvalidVecX = -1019;
    /**
     * Vector Y is not a valid memory object 
     */
    public static final int clblasInvalidVecY = -1018;
    /**
     * An input dimension (M,N,K) is invalid 
     */
    public static final int clblasInvalidDim = -1017;
    /**
     * Leading dimension A must not be less than the size of the first dimension 
     */
    public static final int clblasInvalidLeadDimA = -1016;
    /**
     * Leading dimension B must not be less than the size of the second dimension 
     */
    public static final int clblasInvalidLeadDimB = -1015;
    /**
     * Leading dimension C must not be less than the size of the third dimension 
     */
    public static final int clblasInvalidLeadDimC = -1014;
    /**
     * The increment for a vector X must not be 0 
     */
    public static final int clblasInvalidIncX = -1013;
    /**
     * The increment for a vector Y must not be 0 
     */
    public static final int clblasInvalidIncY = -1012;
    /**
     * The memory object for Matrix A is too small 
     */
    public static final int clblasInsufficientMemMatA = -1011;
    /**
     * The memory object for Matrix B is too small 
     */
    public static final int clblasInsufficientMemMatB = -1010;
    /**
     * The memory object for Matrix C is too small 
     */
    public static final int clblasInsufficientMemMatC = -1009;
    /**
     * The memory object for Vector X is too small 
     */
    public static final int clblasInsufficientMemVecX = -1008;
    /**
     * The memory object for Vector Y is too small 
     */
    public static final int clblasInsufficientMemVecY = -1007;

    /**
     * Private constructor to prevent instantiation
     */
    private clblasStatus(){}

    /**
     * Returns a string representation of the given constant
     *
     * @return A string representation of the given constant
     */
    public static String stringFor(int n)
    {
        switch (n)
        {
            case clblasSuccess: return "clblasSuccess";
            case clblasInvalidValue: return "clblasInvalidValue";
            case clblasInvalidCommandQueue: return "clblasInvalidCommandQueue";
            case clblasInvalidContext: return "clblasInvalidContext";
            case clblasInvalidMemObject: return "clblasInvalidMemObject";
            case clblasInvalidDevice: return "clblasInvalidDevice";
            case clblasInvalidEventWaitList: return "clblasInvalidEventWaitList";
            case clblasOutOfResources: return "clblasOutOfResources";
            case clblasOutOfHostMemory: return "clblasOutOfHostMemory";
            case clblasInvalidOperation: return "clblasInvalidOperation";
            case clblasCompilerNotAvailable: return "clblasCompilerNotAvailable";
            case clblasBuildProgramFailure: return "clblasBuildProgramFailure";
            case clblasNotImplemented: return "clblasNotImplemented";
            case clblasNotInitialized: return "clblasNotInitialized";
            case clblasInvalidMatA: return "clblasInvalidMatA";
            case clblasInvalidMatB: return "clblasInvalidMatB";
            case clblasInvalidMatC: return "clblasInvalidMatC";
            case clblasInvalidVecX: return "clblasInvalidVecX";
            case clblasInvalidVecY: return "clblasInvalidVecY";
            case clblasInvalidDim: return "clblasInvalidDim";
            case clblasInvalidLeadDimA: return "clblasInvalidLeadDimA";
            case clblasInvalidLeadDimB: return "clblasInvalidLeadDimB";
            case clblasInvalidLeadDimC: return "clblasInvalidLeadDimC";
            case clblasInvalidIncX: return "clblasInvalidIncX";
            case clblasInvalidIncY: return "clblasInvalidIncY";
            case clblasInsufficientMemMatA: return "clblasInsufficientMemMatA";
            case clblasInsufficientMemMatB: return "clblasInsufficientMemMatB";
            case clblasInsufficientMemMatC: return "clblasInsufficientMemMatC";
            case clblasInsufficientMemVecX: return "clblasInsufficientMemVecX";
            case clblasInsufficientMemVecY: return "clblasInsufficientMemVecY";
        }
        return "INVALID clblasStatus: "+n;
    }
}

