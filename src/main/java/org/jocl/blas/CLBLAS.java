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

import org.jocl.CL;
import org.jocl.CLException;
import org.jocl.LibUtils;
import org.jocl.Pointer;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_event;
import org.jocl.cl_mem;


/**
 * JOCLBlas
 */
public class CLBLAS
{
    // Initialization of the native library
    static
    {
        String libraryBaseName = "JOCLBLAS_0_0_1";
        String libraryName = 
            LibUtils.createPlatformLibraryName(libraryBaseName);
        String dependentLibraryNames[] = { "clBLAS" };
        try
        {
            LibUtils.loadLibrary(libraryName, dependentLibraryNames);
        }
        catch (UnsatisfiedLinkError e)
        {
            throw e;
        }
    }
    
    /**
     * Indicates whether exceptions are enabled. When exceptions are
     * enabled, CLException is thrown if a method is about to return
     * a result code that is not CL.CL_SUCCESS
     */
    private static boolean exceptionsEnabled = false;
    
    /**
     * Enables or disables exceptions. By default, the methods of this class
     * only return the error code from the underlying OpenCL function.
     * If exceptions are enabled, a CLException with a detailed error
     * message will be thrown if a method is about to return a result code
     * that is not CL_SUCCESS
     *
     * @param enabled Whether exceptions are enabled
     */
    public static void setExceptionsEnabled(boolean enabled)
    {
        exceptionsEnabled = enabled;
    }

    /**
     * If the given result is different to CL_SUCCESS and
     * exceptions have been enabled, this method will throw a
     * CLException with an error message that corresponds to the
     * given result code. Otherwise, the given result is simply
     * returned.
     *
     * @param result The result to check
     * @return The result that was given as the parameter
     * @throws CLException If exceptions have been enabled and
     * the given result code is not CL_SUCCESS
     */
    private static int checkResult(int result)
    {
        if (exceptionsEnabled && result != CL.CL_SUCCESS)
        {
            throw new CLException(CL.stringFor_errorCode(result), result);
        }
        return result;
    }
    
    /**
     * If the given result is <code>null</code> and exceptions have been 
     * enabled, then this method will throw a CLException. Otherwise, 
     * the given result is simply returned.
     *
     * @param result The result to check
     * @return The result that was given as the parameter
     * @throws CLException If exceptions have been enabled and
     * the given result is <code>null</code>
     */
    private static cl_mem checkResult(cl_mem result)
    {
        if (exceptionsEnabled && result == null)
        {
            throw new CLException("Could not create cl_mem");
        }
        return result;
    }
    
    
    public static final int JOCL_BLAS_STATUS_INTERNAL_ERROR = -32786;

    
    
    
    /**
     * Get the clblas library version info..
     *
     * @param major (out)        Location to store library's major version.
     * @param minor (out)        Location to store library's minor version.
     * @param patch (out)        Location to store library's patch version.
     *
     * @return always  clblasSuccess.
     *
     */
    public static int clblasGetVersion(
        int[] major, 
        int[] minor, 
        int[] patch)
    {
        return checkResult(clblasGetVersionNative(major, minor, patch));
    }
    private static native int clblasGetVersionNative(
        int[] major, 
        int[] minor, 
        int[] patch);


    /**
     * Initialize the clblas library..
     *
     * Must be called before any other clblas API function is invoked.
     * This function is not thread-safe.
     *
     * @return
     *   -  clblasSucces on success;
     *   -  clblasOutOfHostMemory if there is not enough of memory to allocate
     *     library's internal structures;
     *   -  clblasOutOfResources in case of requested resources scarcity.
     *
     */
    public static int clblasSetup()
    {
        return checkResult(clblasSetupNative());
    }
    private static native int clblasSetupNative();


    /**
     * Finalize the usage of the clblas library..
     *
     * Frees all memory allocated for different computational kernel and other
     * internal data.
     * This function is not thread-safe.
     *
     */
    public static void clblasTeardown()
    {
        clblasTeardownNative();
    }
    private static native void clblasTeardownNative();


    /**
     * interchanges two vectors of float..
     *
     *
     * @param N (in)         Number of elements in vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSswap(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSswapNative(N, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSswapNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sswap.c
     * Example of how to use the @ref clblasSswap function.
     */
    /**
     * interchanges two vectors of double..
     *
     *
     * @param N (in)         Number of elements in vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSswap() function otherwise.
     *
     */
    public static int clblasDswap(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDswapNative(N, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDswapNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * interchanges two vectors of complex-float elements..
     *
     *
     * @param N (in)         Number of elements in vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSwap() function otherwise.
     *
     */
    public static int clblasCswap(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCswapNative(N, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCswapNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * interchanges two vectors of double-complex elements..
     *
     *
     * @param N (in)         Number of elements in vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasDwap() function otherwise.
     *
     */
    public static int clblasZswap(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZswapNative(N, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZswapNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Scales a float vector by a float constant.
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     -  incx zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSscal(
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSscalNative(N, alpha, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSscalNative(
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sscal.c
     * Example of how to use the @ref clblasSscal function.
     */
    /**
     * Scales a double vector by a double constant.
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSscal() function otherwise.
     *
     */
    public static int clblasDscal(
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDscalNative(N, alpha, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDscalNative(
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Scales a complex-float vector by a complex-float constant.
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSscal() function otherwise.
     *
     */
    public static int clblasCscal(
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCscalNative(N, alpha, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCscalNative(
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Scales a complex-double vector by a complex-double constant.
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasDscal() function otherwise.
     *
     */
    public static int clblasZscal(
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZscalNative(N, alpha, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZscalNative(
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Scales a complex-float vector by a float constant.
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     -  incx zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasCsscal(
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCsscalNative(N, alpha, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCsscalNative(
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_csscal.c
     * Example of how to use the @ref clblasCsscal function.
     */
    /**
     * Scales a complex-double vector by a double constant.
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasCsscal() function otherwise.
     *
     */
    public static int clblasZdscal(
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZdscalNative(N, alpha, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZdscalNative(
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Copies float elements from vector X to vector Y.
     *
     *   - \f$ Y \leftarrow X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasScopy(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasScopyNative(N, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasScopyNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_scopy.c
     * Example of how to use the @ref clblasScopy function.
     */
    /**
     * Copies double elements from vector X to vector Y.
     *
     *   - \f$ Y \leftarrow X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasScopy() function otherwise.
     *
     */
    public static int clblasDcopy(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDcopyNative(N, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDcopyNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Copies complex-float elements from vector X to vector Y.
     *
     *   - \f$ Y \leftarrow X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasScopy() function otherwise.
     *
     */
    public static int clblasCcopy(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCcopyNative(N, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCcopyNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Copies complex-double elements from vector X to vector Y.
     *
     *   - \f$ Y \leftarrow X \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasDcopy() function otherwise.
     *
     */
    public static int clblasZcopy(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZcopyNative(N, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZcopyNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Scale vector X of float elements and add to Y.
     *
     *   - \f$ Y \leftarrow \alpha X + Y \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSaxpy(
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSaxpyNative(N, alpha, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSaxpyNative(
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_saxpy.c
     * Example of how to use the @ref clblasSaxpy function.
     */
    /**
     * Scale vector X of double elements and add to Y.
     *
     *   - \f$ Y \leftarrow \alpha X + Y \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSaxpy() function otherwise.
     *
     */
    public static int clblasDaxpy(
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDaxpyNative(N, alpha, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDaxpyNative(
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Scale vector X of complex-float elements and add to Y.
     *
     *   - \f$ Y \leftarrow \alpha X + Y \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSaxpy() function otherwise.
     *
     */
    public static int clblasCaxpy(
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCaxpyNative(N, alpha, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCaxpyNative(
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Scale vector X of double-complex elements and add to Y.
     *
     *   - \f$ Y \leftarrow \alpha X + Y \f$
     *
     * @param N (in)         Number of elements in vector  X.
     * @param alpha (in)     The constant factor for vector  X.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasDaxpy() function otherwise.
     *
     */
    public static int clblasZaxpy(
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZaxpyNative(N, alpha, X, offx, incx, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZaxpyNative(
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * dot product of two vectors containing float elements.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param dotProduct (out)   Buffer object that will contain the dot-product value
     * @param offDP (in)         Offset to dot-product in  dotProduct buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param Y (in)             Buffer object storing the vector  Y.
     * @param offy (in)          Offset of first element of vector  Y in buffer object.
     *                          Counted in elements.
     * @param incy (in)          Increment for the elements of  Y. Must not be zero.
     * @param scratchBuff   Temporary (in) cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  X,  Y or  dotProduct object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSdot(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSdotNative(N, dotProduct, offDP, X, offx, incx, Y, offy, incy, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSdotNative(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sdot.c
     * Example of how to use the @ref clblasSdot function.
     */
    /**
     * dot product of two vectors containing double elements.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param dotProduct (out)   Buffer object that will contain the dot-product value
     * @param offDP (in)         Offset to dot-product in  dotProduct buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param Y (in)             Buffer object storing the vector  Y.
     * @param offy (in)          Offset of first element of vector  Y in buffer object.
     *                          Counted in elements.
     * @param incy (in)          Increment for the elements of  Y. Must not be zero.
     * @param scratchBuff   Temporary (in) cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     */
    public static int clblasDdot(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDdotNative(N, dotProduct, offDP, X, offx, incx, Y, offy, incy, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDdotNative(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * dot product of two vectors containing float-complex elements.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param dotProduct (out)   Buffer object that will contain the dot-product value
     * @param offDP (in)         Offset to dot-product in  dotProduct buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param Y (in)             Buffer object storing the vector  Y.
     * @param offy (in)          Offset of first element of vector  Y in buffer object.
     *                          Counted in elements.
     * @param incy (in)          Increment for the elements of  Y. Must not be zero.
     * @param scratchBuff (in)   Temporary cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     */
    public static int clblasCdotu(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCdotuNative(N, dotProduct, offDP, X, offx, incx, Y, offy, incy, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCdotuNative(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * dot product of two vectors containing double-complex elements.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param dotProduct (out)   Buffer object that will contain the dot-product value
     * @param offDP (in)         Offset to dot-product in  dotProduct buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param Y (in)             Buffer object storing the vector  Y.
     * @param offy (in)          Offset of first element of vector  Y in buffer object.
     *                          Counted in elements.
     * @param incy (in)          Increment for the elements of  Y. Must not be zero.
     * @param scratchBuff (in)   Temporary cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     */
    public static int clblasZdotu(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZdotuNative(N, dotProduct, offDP, X, offx, incx, Y, offy, incy, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZdotuNative(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * dot product of two vectors containing float-complex elements conjugating the first vector.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param dotProduct (out)   Buffer object that will contain the dot-product value
     * @param offDP (in)         Offset to dot-product in  dotProduct buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param Y (in)             Buffer object storing the vector  Y.
     * @param offy (in)          Offset of first element of vector  Y in buffer object.
     *                          Counted in elements.
     * @param incy (in)          Increment for the elements of  Y. Must not be zero.
     * @param scratchBuff (in)   Temporary cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     */
    public static int clblasCdotc(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCdotcNative(N, dotProduct, offDP, X, offx, incx, Y, offy, incy, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCdotcNative(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * dot product of two vectors containing double-complex elements conjugating the first vector.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param dotProduct (out)   Buffer object that will contain the dot-product value
     * @param offDP (in)         Offset to dot-product in  dotProduct buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param Y (in)             Buffer object storing the vector  Y.
     * @param offy (in)          Offset of first element of vector  Y in buffer object.
     *                          Counted in elements.
     * @param incy (in)          Increment for the elements of  Y. Must not be zero.
     * @param scratchBuff (in)   Temporary cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     */
    public static int clblasZdotc(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZdotcNative(N, dotProduct, offDP, X, offx, incx, Y, offy, incy, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZdotcNative(
        long N, 
        cl_mem dotProduct, 
        long offDP, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * construct givens plane rotation on float elements.
     *
     * @param SA (out)           Buffer object that contains SA
     * @param offSA (in)         Offset to SA in  SA buffer object.
     *                          Counted in elements.
     * @param SB (out)           Buffer object that contains SB
     * @param offSB (in)         Offset to SB in  SB buffer object.
     *                          Counted in elements.
     * @param C (out)            Buffer object that contains C
     * @param offC (in)          Offset to C in  C buffer object.
     *                          Counted in elements.
     * @param S (out)            Buffer object that contains S
     * @param offS (in)          Offset to S in  S buffer object.
     *                          Counted in elements.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidMemObject if either  SA,  SB,  C or  S object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSrotg(
        cl_mem SA, 
        long offSA, 
        cl_mem SB, 
        long offSB, 
        cl_mem C, 
        long offC, 
        cl_mem S, 
        long offS, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSrotgNative(SA, offSA, SB, offSB, C, offC, S, offS, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSrotgNative(
        cl_mem SA, 
        long offSA, 
        cl_mem SB, 
        long offSB, 
        cl_mem C, 
        long offC, 
        cl_mem S, 
        long offS, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_srotg.c
     * Example of how to use the @ref clblasSrotg function.
     */
    /**
     * construct givens plane rotation on double elements.
     *
     * @param DA (out)           Buffer object that contains DA
     * @param offDA (in)         Offset to DA in  DA buffer object.
     *                          Counted in elements.
     * @param DB (out)           Buffer object that contains DB
     * @param offDB (in)         Offset to DB in  DB buffer object.
     *                          Counted in elements.
     * @param C (out)            Buffer object that contains C
     * @param offC (in)          Offset to C in  C buffer object.
     *                          Counted in elements.
     * @param S (out)            Buffer object that contains S
     * @param offS (in)          Offset to S in  S buffer object.
     *                          Counted in elements.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrotg() function otherwise.
     *
     */
    public static int clblasDrotg(
        cl_mem DA, 
        long offDA, 
        cl_mem DB, 
        long offDB, 
        cl_mem C, 
        long offC, 
        cl_mem S, 
        long offS, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDrotgNative(DA, offDA, DB, offDB, C, offC, S, offS, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDrotgNative(
        cl_mem DA, 
        long offDA, 
        cl_mem DB, 
        long offDB, 
        cl_mem C, 
        long offC, 
        cl_mem S, 
        long offS, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * construct givens plane rotation on float-complex elements.
     *
     * @param CA (out)           Buffer object that contains CA
     * @param offCA (in)         Offset to CA in  CA buffer object.
     *                          Counted in elements.
     * @param CB (out)           Buffer object that contains CB
     * @param offCB (in)         Offset to CB in  CB buffer object.
     *                          Counted in elements.
     * @param C (out)            Buffer object that contains C. C is real.
     * @param offC (in)          Offset to C in  C buffer object.
     *                          Counted in elements.
     * @param S (out)            Buffer object that contains S
     * @param offS (in)          Offset to S in  S buffer object.
     *                          Counted in elements.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSrotg() function otherwise.
     *
     */
    public static int clblasCrotg(
        cl_mem CA, 
        long offCA, 
        cl_mem CB, 
        long offCB, 
        cl_mem C, 
        long offC, 
        cl_mem S, 
        long offS, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCrotgNative(CA, offCA, CB, offCB, C, offC, S, offS, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCrotgNative(
        cl_mem CA, 
        long offCA, 
        cl_mem CB, 
        long offCB, 
        cl_mem C, 
        long offC, 
        cl_mem S, 
        long offS, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * construct givens plane rotation on double-complex elements.
     *
     * @param CA (out)           Buffer object that contains CA
     * @param offCA (in)         Offset to CA in  CA buffer object.
     *                          Counted in elements.
     * @param CB (out)           Buffer object that contains CB
     * @param offCB (in)         Offset to CB in  CB buffer object.
     *                          Counted in elements.
     * @param C (out)            Buffer object that contains C. C is real.
     * @param offC (in)          Offset to C in  C buffer object.
     *                          Counted in elements.
     * @param S (out)            Buffer object that contains S
     * @param offS (in)          Offset to S in  S buffer object.
     *                          Counted in elements.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasDrotg() function otherwise.
     *
     */
    public static int clblasZrotg(
        cl_mem CA, 
        long offCA, 
        cl_mem CB, 
        long offCB, 
        cl_mem C, 
        long offC, 
        cl_mem S, 
        long offS, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZrotgNative(CA, offCA, CB, offCB, C, offC, S, offS, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZrotgNative(
        cl_mem CA, 
        long offCA, 
        cl_mem CB, 
        long offCB, 
        cl_mem C, 
        long offC, 
        cl_mem S, 
        long offS, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * construct the modified givens rotation on float elements.
     *
     * @param SD1 (out)          Buffer object that contains SD1
     * @param offSD1 (in)        Offset to SD1 in  SD1 buffer object.
     *                          Counted in elements.
     * @param SD2 (out)          Buffer object that contains SD2
     * @param offSD2 (in)        Offset to SD2 in  SD2 buffer object.
     *                          Counted in elements.
     * @param SX1 (out)          Buffer object that contains SX1
     * @param offSX1 (in)        Offset to SX1 in  SX1 buffer object.
     *                          Counted in elements.
     * @param SY1 (in)           Buffer object that contains SY1
     * @param offSY1 (in)        Offset to SY1 in  SY1 buffer object.
     *                          Counted in elements.
     * @param SPARAM (out)       Buffer object that contains SPARAM array of minimum length 5
                                SPARAM(0) = SFLAG
                                SPARAM(1) = SH11
                                SPARAM(2) = SH21
                                SPARAM(3) = SH12
                                SPARAM(4) = SH22
    
     * @param offSparam (in)     Offset to SPARAM in  SPARAM buffer object.
     *                          Counted in elements.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidMemObject if either  SX1,  SY1,  SD1,  SD2 or  SPARAM object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSrotmg(
        cl_mem SD1, 
        long offSD1, 
        cl_mem SD2, 
        long offSD2, 
        cl_mem SX1, 
        long offSX1, 
        cl_mem SY1, 
        long offSY1, 
        cl_mem SPARAM, 
        long offSparam, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSrotmgNative(SD1, offSD1, SD2, offSD2, SX1, offSX1, SY1, offSY1, SPARAM, offSparam, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSrotmgNative(
        cl_mem SD1, 
        long offSD1, 
        cl_mem SD2, 
        long offSD2, 
        cl_mem SX1, 
        long offSX1, 
        cl_mem SY1, 
        long offSY1, 
        cl_mem SPARAM, 
        long offSparam, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_srotmg.c
     * Example of how to use the @ref clblasSrotmg function.
     */
    /**
     * construct the modified givens rotation on double elements.
     *
     * @param DD1 (out)          Buffer object that contains DD1
     * @param offDD1 (in)        Offset to DD1 in  DD1 buffer object.
     *                          Counted in elements.
     * @param DD2 (out)          Buffer object that contains DD2
     * @param offDD2 (in)        Offset to DD2 in  DD2 buffer object.
     *                          Counted in elements.
     * @param DX1 (out)          Buffer object that contains DX1
     * @param offDX1 (in)        Offset to DX1 in  DX1 buffer object.
     *                          Counted in elements.
     * @param DY1 (in)           Buffer object that contains DY1
     * @param offDY1 (in)        Offset to DY1 in  DY1 buffer object.
     *                          Counted in elements.
     * @param DPARAM (out)       Buffer object that contains DPARAM array of minimum length 5
                                DPARAM(0) = DFLAG
                                DPARAM(1) = DH11
                                DPARAM(2) = DH21
                                DPARAM(3) = DH12
                                DPARAM(4) = DH22
    
     * @param offDparam (in)     Offset to DPARAM in  DPARAM buffer object.
     *                          Counted in elements.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrotmg() function otherwise.
     *
     */
    public static int clblasDrotmg(
        cl_mem DD1, 
        long offDD1, 
        cl_mem DD2, 
        long offDD2, 
        cl_mem DX1, 
        long offDX1, 
        cl_mem DY1, 
        long offDY1, 
        cl_mem DPARAM, 
        long offDparam, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDrotmgNative(DD1, offDD1, DD2, offDD2, DX1, offDX1, DY1, offDY1, DPARAM, offDparam, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDrotmgNative(
        cl_mem DD1, 
        long offDD1, 
        cl_mem DD2, 
        long offDD2, 
        cl_mem DX1, 
        long offDX1, 
        cl_mem DY1, 
        long offDY1, 
        cl_mem DPARAM, 
        long offDparam, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * applies a plane rotation for float elements.
     *
     * @param N (in)         Number of elements in vector  X and  Y.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param C (in)         C specifies the cosine, cos.
     * @param S (in)         S specifies the sine, sin.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSrot(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        float C, 
        float S, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSrotNative(N, X, offx, incx, Y, offy, incy, C, S, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSrotNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        float C, 
        float S, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_srot.c
     * Example of how to use the @ref clblasSrot function.
     */
    /**
     * applies a plane rotation for double elements.
     *
     * @param N (in)         Number of elements in vector  X and  Y.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param C (in)         C specifies the cosine, cos.
     * @param S (in)         S specifies the sine, sin.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrot() function otherwise.
     *
     */
    public static int clblasDrot(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        double C, 
        double S, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDrotNative(N, X, offx, incx, Y, offy, incy, C, S, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDrotNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        double C, 
        double S, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * applies a plane rotation for float-complex elements.
     *
     * @param N (in)         Number of elements in vector  X and  Y.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param C (in)         C specifies the cosine, cos. This number is real
     * @param S (in)         S specifies the sine, sin. This number is real
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSrot() function otherwise.
     *
     */
    public static int clblasCsrot(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        float C, 
        float S, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCsrotNative(N, X, offx, incx, Y, offy, incy, C, S, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCsrotNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        float C, 
        float S, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * applies a plane rotation for double-complex elements.
     *
     * @param N (in)         Number of elements in vector  X and  Y.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param C (in)         C specifies the cosine, cos. This number is real
     * @param S (in)         S specifies the sine, sin. This number is real
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrot() function otherwise.
     *
     */
    public static int clblasZdrot(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        double C, 
        double S, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZdrotNative(N, X, offx, incx, Y, offy, incy, C, S, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZdrotNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        double C, 
        double S, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * modified givens rotation for float elements.
     *
     * @param N (in)         Number of elements in vector  X and  Y.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param SPARAM (in)    Buffer object that contains SPARAM array of minimum length 5
     *                      SPARAM(1)=SFLAG
     *                      SPARAM(2)=SH11
     *                      SPARAM(3)=SH21
     *                      SPARAM(4)=SH12
     *                      SPARAM(5)=SH22
     * @param offSparam (in) Offset of first element of array  SPARAM in buffer object.
     *                      Counted in elements.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  X,  Y or  SPARAM object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSrotm(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem SPARAM, 
        long offSparam, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSrotmNative(N, X, offx, incx, Y, offy, incy, SPARAM, offSparam, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSrotmNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem SPARAM, 
        long offSparam, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_srotm.c
     * Example of how to use the @ref clblasSrotm function.
     */
    /**
     * modified givens rotation for double elements.
     *
     * @param N (in)         Number of elements in vector  X and  Y.
     * @param X (out)        Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (out)        Buffer object storing the vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param DPARAM (in)    Buffer object that contains SPARAM array of minimum length 5
     *                      DPARAM(1)=DFLAG
     *                      DPARAM(2)=DH11
     *                      DPARAM(3)=DH21
     *                      DPARAM(4)=DH12
     *                      DPARAM(5)=DH22
     * @param offDparam (in) Offset of first element of array  DPARAM in buffer object.
     *                      Counted in elements.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
    * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrotm() function otherwise.
     *
     */
    public static int clblasDrotm(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem DPARAM, 
        long offDparam, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDrotmNative(N, X, offx, incx, Y, offy, incy, DPARAM, offDparam, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDrotmNative(
        long N, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem DPARAM, 
        long offDparam, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * computes the euclidean norm of vector containing float elements.
     *
     *  NRM2 = sqrt( X' * X )
     *
     * @param N (in)             Number of elements in vector  X.
     * @param NRM2 (out)         Buffer object that will contain the NRM2 value
     * @param offNRM2 (in)       Offset to NRM2 value in  NRM2 buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff   Temporary (in) cl_mem scratch buffer object that can hold minimum of (2*N) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if any of  X or  NRM2 or  scratchBuff object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSnrm2(
        long N, 
        cl_mem NRM2, 
        long offNRM2, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSnrm2Native(N, NRM2, offNRM2, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSnrm2Native(
        long N, 
        cl_mem NRM2, 
        long offNRM2, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_snrm2.c
     * Example of how to use the @ref clblasSnrm2 function.
     */
    /**
     * computes the euclidean norm of vector containing double elements.
     *
     *  NRM2 = sqrt( X' * X )
     *
     * @param N (in)             Number of elements in vector  X.
     * @param NRM2 (out)         Buffer object that will contain the NRM2 value
     * @param offNRM2 (in)       Offset to NRM2 value in  NRM2 buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff   Temporary (in) cl_mem scratch buffer object that can hold minimum of (2*N) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSnrm2() function otherwise.
     *
     */
    public static int clblasDnrm2(
        long N, 
        cl_mem NRM2, 
        long offNRM2, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDnrm2Native(N, NRM2, offNRM2, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDnrm2Native(
        long N, 
        cl_mem NRM2, 
        long offNRM2, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * computes the euclidean norm of vector containing float-complex elements.
     *
     *  NRM2 = sqrt( X**H * X )
     *
     * @param N (in)             Number of elements in vector  X.
     * @param NRM2 (out)         Buffer object that will contain the NRM2 value.
     *                          Note that the answer of Scnrm2 is a real value.
     * @param offNRM2 (in)       Offset to NRM2 value in  NRM2 buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff   Temporary (in) cl_mem scratch buffer object that can hold minimum of (2*N) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSnrm2() function otherwise.
     *
     */
    public static int clblasScnrm2(
        long N, 
        cl_mem NRM2, 
        long offNRM2, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasScnrm2Native(N, NRM2, offNRM2, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasScnrm2Native(
        long N, 
        cl_mem NRM2, 
        long offNRM2, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * computes the euclidean norm of vector containing double-complex elements.
     *
     *  NRM2 = sqrt( X**H * X )
     *
     * @param N (in)             Number of elements in vector  X.
     * @param NRM2 (out)         Buffer object that will contain the NRM2 value.
     *                          Note that the answer of Dznrm2 is a real value.
     * @param offNRM2 (in)       Offset to NRM2 value in  NRM2 buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff   Temporary (in) cl_mem scratch buffer object that can hold minimum of (2*N) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSnrm2() function otherwise.
     *     executable.
     *
     */
    public static int clblasDznrm2(
        long N, 
        cl_mem NRM2, 
        long offNRM2, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDznrm2Native(N, NRM2, offNRM2, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDznrm2Native(
        long N, 
        cl_mem NRM2, 
        long offNRM2, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * index of max absolute value in a float array.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param iMax (out)         Buffer object storing the index of first absolute max.
     *                          The index will be of type unsigned int
     * @param offiMax (in)       Offset for storing index in the buffer iMax
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff (in)   Temprory cl_mem object to store intermediate results
                                It should be able to hold minimum of (2*N) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if any of  iMax  X or  scratchBuff object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if the context, the passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasiSamax(
        long N, 
        cl_mem iMax, 
        long offiMax, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasiSamaxNative(N, iMax, offiMax, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasiSamaxNative(
        long N, 
        cl_mem iMax, 
        long offiMax, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_isamax.c
     * Example of how to use the @ref clblasiSamax function.
     */
    /**
     * index of max absolute value in a double array.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param iMax (out)         Buffer object storing the index of first absolute max.
     *                          The index will be of type unsigned int
     * @param offiMax (in)       Offset for storing index in the buffer iMax
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff (in)   Temprory cl_mem object to store intermediate results
                                It should be able to hold minimum of (2*N) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasiSamax() function otherwise.
     *
     */
    public static int clblasiDamax(
        long N, 
        cl_mem iMax, 
        long offiMax, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasiDamaxNative(N, iMax, offiMax, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasiDamaxNative(
        long N, 
        cl_mem iMax, 
        long offiMax, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * index of max absolute value in a complex float array.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param iMax (out)         Buffer object storing the index of first absolute max.
     *                          The index will be of type unsigned int
     * @param offiMax (in)       Offset for storing index in the buffer iMax
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff (in)   Temprory cl_mem object to store intermediate results
                                It should be able to hold minimum of (2*N) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasiSamax() function otherwise.
     *
     */
    public static int clblasiCamax(
        long N, 
        cl_mem iMax, 
        long offiMax, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasiCamaxNative(N, iMax, offiMax, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasiCamaxNative(
        long N, 
        cl_mem iMax, 
        long offiMax, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * index of max absolute value in a complex double array.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param iMax (out)         Buffer object storing the index of first absolute max.
     *                          The index will be of type unsigned int
     * @param offiMax (in)       Offset for storing index in the buffer iMax
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff (in)   Temprory cl_mem object to store intermediate results
                                It should be able to hold minimum of (2*N) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasiSamax() function otherwise.
     *
     */
    public static int clblasiZamax(
        long N, 
        cl_mem iMax, 
        long offiMax, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasiZamaxNative(N, iMax, offiMax, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasiZamaxNative(
        long N, 
        cl_mem iMax, 
        long offiMax, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * absolute sum of values of a vector containing float elements.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param asum (out)         Buffer object that will contain the absoule sum value
     * @param offAsum (in)       Offset to absolute sum in  asum buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff (in)   Temporary cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if any of  X or  asum or  scratchBuff object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSasum(
        long N, 
        cl_mem asum, 
        long offAsum, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSasumNative(N, asum, offAsum, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSasumNative(
        long N, 
        cl_mem asum, 
        long offAsum, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sasum.c
     * Example of how to use the @ref clblasSasum function.
     */
    /**
     * absolute sum of values of a vector containing double elements.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param asum (out)         Buffer object that will contain the absoulte sum value
     * @param offAsum (in)       Offset to absoule sum in  asum buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff (in)   Temporary cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSasum() function otherwise.
     *
     */
    public static int clblasDasum(
        long N, 
        cl_mem asum, 
        long offAsum, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDasumNative(N, asum, offAsum, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDasumNative(
        long N, 
        cl_mem asum, 
        long offAsum, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * absolute sum of values of a vector containing float-complex elements.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param asum (out)         Buffer object that will contain the absolute sum value
     * @param offAsum (in)       Offset to absolute sum in  asum buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff (in)   Temporary cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   - the same error codes as the clblasSasum() function otherwise.
     *
     */
    public static int clblasScasum(
        long N, 
        cl_mem asum, 
        long offAsum, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasScasumNative(N, asum, offAsum, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasScasumNative(
        long N, 
        cl_mem asum, 
        long offAsum, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * absolute sum of values of a vector containing double-complex elements.
     *
     * @param N (in)             Number of elements in vector  X.
     * @param asum (out)         Buffer object that will contain the absolute sum value
     * @param offAsum (in)       Offset to absolute sum in  asum buffer object.
     *                          Counted in elements.
     * @param X (in)             Buffer object storing vector  X.
     * @param offx (in)          Offset of first element of vector  X in buffer object.
     *                          Counted in elements.
     * @param incx (in)          Increment for the elements of  X. Must not be zero.
     * @param scratchBuff (in)   Temporary cl_mem scratch buffer object of minimum size N
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSasum() function otherwise.
     *
     */
    public static int clblasDzasum(
        long N, 
        cl_mem asum, 
        long offAsum, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDzasumNative(N, asum, offAsum, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDzasumNative(
        long N, 
        cl_mem asum, 
        long offAsum, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a general rectangular matrix and.
     *        float elements. Extended version.
     *
     * Matrix-vector products:
     *   - \f$ y \leftarrow \alpha A x + eta y \f$
     *   - \f$ y \leftarrow \alpha A^T x + eta y \f$
     *
     * @param order (in)     Row/column order.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in
     *                      the buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when the
     *                      parameter is set to  clblasColumnMajor.
     * @param x (in)         Buffer object storing vector  x.
     * @param offx (in)      Offset of first element of vector  x in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  x. It cannot be zero.
     * @param beta (in)      The factor of the vector  y.
     * @param y (out)        Buffer object storing the vector  y.
     * @param offy (in)      Offset of first element of vector  y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if  offA exceeds the size of  A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     */
    public static int clblasSgemv(
        int order, 
        int transA, 
        long M, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSgemvNative(order, transA, M, N, alpha, A, offA, lda, x, offx, incx, beta, y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSgemvNative(
        int order, 
        int transA, 
        long M, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sgemv.c
     * This is an example of how to use the @ref clblasSgemvEx function.
     */
    /**
     * Matrix-vector product with a general rectangular matrix and.
     *        double elements. Extended version.
     *
     * Matrix-vector products:
     *   - \f$ y \leftarrow \alpha A x + eta y \f$
     *   - \f$ y \leftarrow \alpha A^T x + eta y \f$
     *
     * @param order (in)     Row/column order.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of  A in the buffer
     *                      object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For a detailed description,
     *                      see clblasSgemv().
     * @param x (in)         Buffer object storing vector  x.
     * @param offx (in)      Offset of first element of vector  x in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  x. It cannot be zero.
     * @param beta (in)      The factor of the vector  y.
     * @param y (out)        Buffer object storing the vector  y.
     * @param offy (in)      Offset of first element of vector  y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   -  clblasInvalidValue if  offA exceeds the size of  A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     */
    public static int clblasDgemv(
        int order, 
        int transA, 
        long M, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDgemvNative(order, transA, M, N, alpha, A, offA, lda, x, offx, incx, beta, y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDgemvNative(
        int order, 
        int transA, 
        long M, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a general rectangular matrix and.
     *        float complex elements. Extended version.
     *
     * Matrix-vector products:
     *   - \f$ y \leftarrow \alpha A x + eta y \f$
     *   - \f$ y \leftarrow \alpha A^T x + eta y \f$
     *
     * @param order (in)     Row/column order.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in
     *                      the buffer object. Counted in elements
     * @param lda (in)       Leading dimension of matrix  A. For a detailed description,
     *                      see clblasSgemv().
     * @param x (in)         Buffer object storing vector  x.
     * @param offx (in)      Offset of first element of vector  x in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  x. It cannot be zero.
     * @param beta (in)      The factor of the vector  y.
     * @param y (out)        Buffer object storing the vector  y.
     * @param offy (in)      Offset of first element of vector  y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if  offA exceeds the size of  A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     */
    public static int clblasCgemv(
        int order, 
        int transA, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCgemvNative(order, transA, M, N, alpha, A, offA, lda, x, offx, incx, beta, y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCgemvNative(
        int order, 
        int transA, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a general rectangular matrix and.
     *        double complex elements. Extended version.
     *
     * Matrix-vector products:
     *   - \f$ y \leftarrow \alpha A x + eta y \f$
     *   - \f$ y \leftarrow \alpha A^T x + eta y \f$
     *
     * @param order (in)     Row/column order.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in
     *                      the buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For a detailed description,
     *                      see clblasSgemv().
     * @param x (in)         Buffer object storing vector  x.
     * @param offx (in)      Offset of first element of vector  x in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  x. It cannot be zero.
     * @param beta (in)      The factor of the vector  y.
     * @param y (out)        Buffer object storing the vector  y.
     * @param offy (in)      Offset of first element of vector  y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   -  clblasInvalidValue if  offA exceeds the size of  A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     */
    public static int clblasZgemv(
        int order, 
        int transA, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZgemvNative(order, transA, M, N, alpha, A, offA, lda, x, offx, incx, beta, y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZgemvNative(
        int order, 
        int transA, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a symmetric matrix and float elements..
     *
     *
     * Matrix-vector products:
     * - \f$ y \leftarrow \alpha A x + eta y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in
     *                      the buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot less
     *                      than  N.
     * @param x (in)         Buffer object storing vector  x.
     * @param offx (in)      Offset of first element of vector  x in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  x. It cannot be zero.
     * @param beta (in)      The factor of vector  y.
     * @param y (out)        Buffer object storing vector  y.
     * @param offy (in)      Offset of first element of vector  y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if  offA exceeds the size of  A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     */
    public static int clblasSsymv(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSsymvNative(order, uplo, N, alpha, A, offA, lda, x, offx, incx, beta, y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSsymvNative(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_ssymv.c
     * This is an example of how to use the @ref clblasSsymv function.
     */
    /**
     * Matrix-vector product with a symmetric matrix and double elements..
     *
     *
     * Matrix-vector products:
     * - \f$ y \leftarrow \alpha A x + eta y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in
     *                      the buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot less
     *                      than  N.
     * @param x (in)         Buffer object storing vector  x.
     * @param offx (in)      Offset of first element of vector  x in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  x. It cannot be zero.
     * @param beta (in)      The factor of vector  y.
     * @param y (out)        Buffer object storing vector  y.
     * @param offy (in)      Offset of first element of vector  y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   -  clblasInvalidValue if  offA exceeds the size of  A buffer
     *     object;
     *   - the same error codes as the clblasSsymv() function otherwise.
     *
     */
    public static int clblasDsymv(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDsymvNative(order, uplo, N, alpha, A, offA, lda, x, offx, incx, beta, y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDsymvNative(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem x, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a hermitian matrix and float-complex elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offa      Offset (in) in number of elements for first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot less
     *                      than  N.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes or the vector sizes along with the increments lead to
     *       accessing outsize of any of the buffers;
     *   -  clblasInvalidMemObject if either  A,  X, or  Y object is
     *     invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasChemv(
        int order, 
        int uplo, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasChemvNative(order, uplo, N, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasChemvNative(
        int order, 
        int uplo, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a hermitian matrix and double-complex elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offa      Offset (in) in number of elements for first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot less
     *                      than  N.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChemv() function otherwise.
     *
     */
    public static int clblasZhemv(
        int order, 
        int uplo, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZhemvNative(order, uplo, N, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZhemvNative(
        int order, 
        int uplo, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_zhemv.cpp
     * Example of how to use the @ref clblasZhemv function.
     */
    /**
     * Matrix-vector product with a triangular matrix and.
     * float elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than  N
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasStrmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasStrmvNative(order, uplo, trans, diag, N, A, offa, lda, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasStrmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_strmv.c
     * Example of how to use the @ref clblasStrmv function.
     */
    /**
     * Matrix-vector product with a triangular matrix and.
     * double elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than  N
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStrmv() function otherwise.
     *
     */
    public static int clblasDtrmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDtrmvNative(order, uplo, trans, diag, N, A, offa, lda, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDtrmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a triangular matrix and.
     * float complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than  N
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasStrmv() function.
     */
    public static int clblasCtrmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCtrmvNative(order, uplo, trans, diag, N, A, offa, lda, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCtrmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a triangular matrix and.
     * double complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than  N
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDtrmv() function.
     */
    public static int clblasZtrmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZtrmvNative(order, uplo, trans, diag, N, A, offa, lda, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZtrmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular matrix problems with float elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than  N
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasStrsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasStrsvNative(order, uplo, trans, diag, N, A, offa, lda, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasStrsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_strsv.c
     * Example of how to use the @ref clblasStrsv function.
     */
    /**
     * solving triangular matrix problems with double elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than  N
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStrsv() function otherwise.
     *
     */
    public static int clblasDtrsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDtrsvNative(order, uplo, trans, diag, N, A, offa, lda, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDtrsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular matrix problems with float-complex elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than  N
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasStrsv() function.
     *
     */
    public static int clblasCtrsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCtrsvNative(order, uplo, trans, diag, N, A, offa, lda, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCtrsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular matrix problems with double-complex elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than  N
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDtrsv() function.
     *
     */
    public static int clblasZtrsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZtrsvNative(order, uplo, trans, diag, N, A, offa, lda, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZtrsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * vector-vector product with float elements and.
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     specifies the scalar alpha.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A (out)       Buffer object storing matrix  A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when the
     *                      parameter is set to  clblasColumnMajor.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  M,  N or
     *     - either  incx or  incy is zero, or
     *     - a leading dimension is invalid;
     *   -  clblasInvalidMemObject if A, X, or Y object is invalid,
     *     or an image object rather than the buffer one;
     *   -  clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSger(
        int order, 
        long M, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSgerNative(order, M, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSgerNative(
        int order, 
        long M, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sger.c
     * Example of how to use the @ref clblasSger function.
     */
    /**
     * vector-vector product with double elements and.
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     specifies the scalar alpha.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A (out)       Buffer object storing matrix  A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when the
     *                      parameter is set to  clblasColumnMajor.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSger() function otherwise.
     *
     */
    public static int clblasDger(
        int order, 
        long M, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDgerNative(order, M, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDgerNative(
        int order, 
        long M, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * vector-vector product with float complex elements and.
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     specifies the scalar alpha.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A (out)       Buffer object storing matrix  A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when the
     *                      parameter is set to  clblasColumnMajor.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  M,  N or
     *     - either  incx or  incy is zero, or
     *     - a leading dimension is invalid;
     *   -  clblasInvalidMemObject if A, X, or Y object is invalid,
     *     or an image object rather than the buffer one;
     *   -  clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasCgeru(
        int order, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCgeruNative(order, M, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCgeruNative(
        int order, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * vector-vector product with double complex elements and.
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     specifies the scalar alpha.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A      (out)   Buffer object storing matrix  A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when the
     *                      parameter is set to  clblasColumnMajor.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCgeru() function otherwise.
     *
     */
    public static int clblasZgeru(
        int order, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZgeruNative(order, M, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZgeruNative(
        int order, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * vector-vector product with float complex elements and.
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     specifies the scalar alpha.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A (out)       Buffer object storing matrix  A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when the
     *                      parameter is set to  clblasColumnMajor.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  M,  N or
     *     - either  incx or  incy is zero, or
     *     - a leading dimension is invalid;
     *   -  clblasInvalidMemObject if A, X, or Y object is invalid,
     *     or an image object rather than the buffer one;
     *   -  clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasCgerc(
        int order, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCgercNative(order, M, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCgercNative(
        int order, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * vector-vector product with double complex elements and.
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     specifies the scalar alpha.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A     Buffer (out) object storing matrix  A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when the
     *                      parameter is set to  clblasColumnMajor.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCgerc() function otherwise.
     *
     */
    public static int clblasZgerc(
        int order, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZgercNative(order, M, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZgercNative(
        int order, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Symmetric rank 1 operation with a general triangular matrix and.
     * float elements.
     *
     * Symmetric rank 1 operation:
     *   - \f$ A \leftarrow \alpha x x^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param A (out)       Buffer object storing matrix  A.
     * @param offa (in)      Offset of first element of matrix  A in buffer object.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A,  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSsyr(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSsyrNative(order, uplo, N, alpha, X, offx, incx, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSsyrNative(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Symmetric rank 1 operation with a general triangular matrix and.
     * double elements.
     *
     * Symmetric rank 1 operation:
     *   - \f$ A \leftarrow \alpha x x^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param A     Buffer (out) object storing matrix  A.
     * @param offa (in)      Offset of first element of matrix  A in buffer object.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSsyr() function otherwise.
     *
     */
    public static int clblasDsyr(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDsyrNative(order, uplo, N, alpha, X, offx, incx, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDsyrNative(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * hermitian rank 1 operation with a general triangular matrix and.
     * float-complex elements.
     *
     * hermitian rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A (a scalar float value)
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param A     Buffer (out) object storing matrix  A.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A,  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasCher(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCherNative(order, uplo, N, alpha, X, offx, incx, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCherNative(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_cher.c
     * Example of how to use the @ref clblasCher function.
     */
    /**
     * hermitian rank 1 operation with a general triangular matrix and.
     * double-complex elements.
     *
     * hermitian rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A (a scalar double value)
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param A     Buffer (out) object storing matrix  A.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCher() function otherwise.
     *
     */
    public static int clblasZher(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZherNative(order, uplo, N, alpha, X, offx, incx, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZherNative(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Symmetric rank 2 operation with a general triangular matrix and.
     * float elements.
     *
     * Symmetric rank 2 operation:
     *   - \f$ A \leftarrow \alpha x y^T + \alpha y x^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A (out)       Buffer object storing matrix  A.
     * @param offa (in)      Offset of first element of matrix  A in buffer object.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A,  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSsyr2(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSsyr2Native(order, uplo, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSsyr2Native(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Symmetric rank 2 operation with a general triangular matrix and.
     * double elements.
     *
     * Symmetric rank 2 operation:
     *   - \f$ A \leftarrow \alpha x y^T + \alpha y x^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A (out)       Buffer object storing matrix  A.
     * @param offa (in)      Offset of first element of matrix  A in buffer object.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A,  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasDsyr2(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDsyr2Native(order, uplo, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDsyr2Native(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Hermitian rank 2 operation with a general triangular matrix and.
     * float-compelx elements.
     *
     * Hermitian rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^H + \overline{ \alpha } Y X^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A     Buffer (out) object storing matrix  A.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A,  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasCher2(
        int order, 
        int uplo, 
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCher2Native(order, uplo, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCher2Native(
        int order, 
        int uplo, 
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
    * Hermitian rank 2 operation with a general triangular matrix and.
     * double-compelx elements.
     *
     * Hermitian rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^H + \overline{ \alpha } Y X^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param A     Buffer (out) object storing matrix  A.
     * @param offa (in)      Offset in number of elements for the first element in matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCher2() function otherwise.
     *
     */
    public static int clblasZher2(
        int order, 
        int uplo, 
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZher2Native(order, uplo, N, alpha, X, offx, incx, Y, offy, incy, A, offa, lda, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZher2Native(
        int order, 
        int uplo, 
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem A, 
        long offa, 
        long lda, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_zher2.c
     * Example of how to use the @ref clblasZher2 function.
     */
    /**
     * Matrix-vector product with a packed triangular matrix and.
     * float elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  AP is to be transposed.
     * @param diag              Specify (in) whether matrix  AP is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  A.
     * @param AP                Buffer (in) object storing matrix  AP in packed format.
     * @param offa              Offset (in) in number of elements for first element in matrix  AP.
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero
     *   -  clblasInvalidMemObject if either  AP or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasStpmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasStpmvNative(order, uplo, trans, diag, N, AP, offa, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasStpmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_stpmv.c
     * Example of how to use the @ref clblasStpmv function.
     */
    /**
     * Matrix-vector product with a packed triangular matrix and.
     * double elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  AP is to be transposed.
     * @param diag              Specify (in) whether matrix  AP is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  AP.
     * @param AP                Buffer (in) object storing matrix  AP in packed format.
     * @param offa              Offset (in) in number of elements for first element in matrix  AP.
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStpmv() function otherwise.
     *
     */
    public static int clblasDtpmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDtpmvNative(order, uplo, trans, diag, N, AP, offa, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDtpmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a packed triangular matrix and.
     * float-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  AP is to be transposed.
     * @param diag              Specify (in) whether matrix  AP is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  AP.
     * @param AP                Buffer (in) object storing matrix  AP in packed format.
     * @param offa              Offset (in) in number of elements for first element in matrix  AP.
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasStpmv() function.
     */
    public static int clblasCtpmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCtpmvNative(order, uplo, trans, diag, N, AP, offa, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCtpmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a packed triangular matrix and.
     * double-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  AP is to be transposed.
     * @param diag              Specify (in) whether matrix  AP is unit triangular.
     * @param N                 Number (in) of rows/columns in matrix  AP.
     * @param AP                Buffer (in) object storing matrix  AP in packed format.
     * @param offa              Offset (in) in number of elements for first element in matrix  AP.
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDtpmv() function.
     */
    public static int clblasZtpmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZtpmvNative(order, uplo, trans, diag, N, AP, offa, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZtpmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular packed matrix problems with float elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)              The triangle in matrix being referenced.
     * @param trans (in)             How matrix  A is to be transposed.
     * @param diag (in)              Specify whether matrix  A is unit triangular.
     * @param N (in)                 Number of rows/columns in matrix  A.
     * @param A (in)                 Buffer object storing matrix in packed format. A.
     * @param offa (in)              Offset in number of elements for first element in matrix  A.
     * @param X (out)                Buffer object storing vector  X.
     * @param offx (in)              Offset in number of elements for first element in vector  X.
     * @param incx (in)              Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasStpsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasStpsvNative(order, uplo, trans, diag, N, A, offa, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasStpsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_stpsv.c
     * Example of how to use the @ref clblasStpsv function.
     */
    /**
     * solving triangular packed matrix problems with double elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)              The triangle in matrix being referenced.
     * @param trans (in)             How matrix  A is to be transposed.
     * @param diag (in)              Specify whether matrix  A is unit triangular.
     * @param N (in)                 Number of rows/columns in matrix  A.
     * @param A (in)                 Buffer object storing matrix in packed format. A.
     * @param offa (in)              Offset in number of elements for first element in matrix  A.
     * @param X (out)                Buffer object storing vector  X.
     * @param offx (in)              Offset in number of elements for first element in vector  X.
     * @param incx (in)              Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasDtpsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDtpsvNative(order, uplo, trans, diag, N, A, offa, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDtpsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular packed matrix problems with float complex elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)              The triangle in matrix being referenced.
     * @param trans (in)             How matrix  A is to be transposed.
     * @param diag (in)              Specify whether matrix  A is unit triangular.
     * @param N (in)                 Number of rows/columns in matrix  A.
     * @param A (in)                 Buffer object storing matrix in packed format. A.
     * @param offa (in)              Offset in number of elements for first element in matrix  A.
     * @param X (out)                Buffer object storing vector  X.
     * @param offx (in)              Offset in number of elements for first element in vector  X.
     * @param incx (in)              Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasCtpsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCtpsvNative(order, uplo, trans, diag, N, A, offa, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCtpsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular packed matrix problems with double complex elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)              The triangle in matrix being referenced.
     * @param trans (in)             How matrix  A is to be transposed.
     * @param diag (in)              Specify whether matrix  A is unit triangular.
     * @param N (in)                 Number of rows/columns in matrix  A.
     * @param A (in)                 Buffer object storing matrix in packed format. A.
     * @param offa (in)              Offset in number of elements for first element in matrix  A.
     * @param X (out)                Buffer object storing vector  X.
     * @param offx (in)              Offset in number of elements for first element in vector  X.
     * @param incx (in)              Increment for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasZtpsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZtpsvNative(order, uplo, trans, diag, N, A, offa, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZtpsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        cl_mem A, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a symmetric packed-matrix and float elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in matrix  AP.
     * @param alpha (in)     The factor of matrix  AP.
     * @param AP (in)        Buffer object storing matrix  AP.
     * @param offa      Offset (in) in number of elements for first element in matrix  AP.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the matrix sizes or the vector sizes along with the increments lead to
     *       accessing outsize of any of the buffers;
     *   -  clblasInvalidMemObject if either  AP,  X, or  Y object is
     *     invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSspmv(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSspmvNative(order, uplo, N, alpha, AP, offa, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSspmvNative(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sspmv.c
     * This is an example of how to use the @ref clblasSspmv function.
     */
    /**
     * Matrix-vector product with a symmetric packed-matrix and double elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in matrix  AP.
     * @param alpha (in)     The factor of matrix  AP.
     * @param AP (in)        Buffer object storing matrix  AP.
     * @param offa      Offset (in) in number of elements for first element in matrix  AP.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSspmv() function otherwise.
     *
     */
    public static int clblasDspmv(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDspmvNative(order, uplo, N, alpha, AP, offa, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDspmvNative(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a packed hermitian matrix and float-complex elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in matrix  AP.
     * @param alpha (in)     The factor of matrix  AP.
     * @param AP (in)        Buffer object storing packed matrix  AP.
     * @param offa      Offset (in) in number of elements for first element in matrix  AP.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx or  incy is zero, or
     *     - the matrix sizes or the vector sizes along with the increments lead to
     *       accessing outsize of any of the buffers;
     *   -  clblasInvalidMemObject if either  AP,  X, or  Y object is
     *     invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasChpmv(
        int order, 
        int uplo, 
        long N, 
        float[] alpha, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasChpmvNative(order, uplo, N, alpha, AP, offa, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasChpmvNative(
        int order, 
        int uplo, 
        long N, 
        float[] alpha, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_chpmv.c
     * This is an example of how to use the @ref clblasChpmv function.
     */
    /**
     * Matrix-vector product with a packed hermitian matrix and double-complex elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in matrix  AP.
     * @param alpha (in)     The factor of matrix  AP.
     * @param AP (in)        Buffer object storing packed matrix  AP.
     * @param offa      Offset (in) in number of elements for first element in matrix  AP.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChpmv() function otherwise.
     *
     */
    public static int clblasZhpmv(
        int order, 
        int uplo, 
        long N, 
        double[] alpha, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZhpmvNative(order, uplo, N, alpha, AP, offa, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZhpmvNative(
        int order, 
        int uplo, 
        long N, 
        double[] alpha, 
        cl_mem AP, 
        long offa, 
        cl_mem X, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Symmetric rank 1 operation with a general triangular packed-matrix and.
     * float elements.
     *
     * Symmetric rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param AP (out)      Buffer object storing packed-matrix  AP.
     * @param offa (in)      Offset of first element of matrix  AP in buffer object.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx is zero
     *   -  clblasInvalidMemObject if either  AP,  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSspr(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSsprNative(order, uplo, N, alpha, X, offx, incx, AP, offa, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSsprNative(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sspr.c
     * Example of how to use the @ref clblasSspr function.
     */
    /**
     * Symmetric rank 1 operation with a general triangular packed-matrix and.
     * double elements.
     *
     * Symmetric rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param AP (out)      Buffer object storing packed-matrix  AP.
     * @param offa (in)      Offset of first element of matrix  AP in buffer object.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSspr() function otherwise.
     *
     */
    public static int clblasDspr(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDsprNative(order, uplo, N, alpha, X, offx, incx, AP, offa, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDsprNative(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * hermitian rank 1 operation with a general triangular packed-matrix and.
     * float-complex elements.
     *
     * hermitian rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A (a scalar float value)
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param AP (out)      Buffer object storing matrix  AP.
     * @param offa (in)      Offset in number of elements for the first element in matrix  AP.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  N is zero, or
     *     - either  incx is zero
     *   -  clblasInvalidMemObject if either  AP,  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasChpr(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasChprNative(order, uplo, N, alpha, X, offx, incx, AP, offa, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasChprNative(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_chpr.c
     * Example of how to use the @ref clblasChpr function.
     */
    /**
     * hermitian rank 1 operation with a general triangular packed-matrix and.
     * double-complex elements.
     *
     * hermitian rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A (a scalar float value)
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param AP (out)      Buffer object storing matrix  AP.
     * @param offa (in)      Offset in number of elements for the first element in matrix  AP.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChpr() function otherwise.
     *
     */
    public static int clblasZhpr(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZhprNative(order, uplo, N, alpha, X, offx, incx, AP, offa, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZhprNative(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Symmetric rank 2 operation with a general triangular packed-matrix and.
     * float elements.
     *
     * Symmetric rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^T + \alpha Y X^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param AP        Buffer (out) object storing packed-matrix  AP.
     * @param offa (in)      Offset of first element of matrix  AP in buffer object.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N is zero, or
     *     - either  incx or  incy is zero
     *   -  clblasInvalidMemObject if either  AP,  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSspr2(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSspr2Native(order, uplo, N, alpha, X, offx, incx, Y, offy, incy, AP, offa, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSspr2Native(
        int order, 
        int uplo, 
        long N, 
        float alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sspr2.c
     * Example of how to use the @ref clblasSspr2 function.
     */
    /**
     * Symmetric rank 2 operation with a general triangular packed-matrix and.
     * double elements.
     *
     * Symmetric rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^T + \alpha Y X^T + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param AP        Buffer (out) object storing packed-matrix  AP.
     * @param offa (in)      Offset of first element of matrix  AP in buffer object.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSspr2() function otherwise.
     *
     */
    public static int clblasDspr2(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDspr2Native(order, uplo, N, alpha, X, offx, incx, Y, offy, incy, AP, offa, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDspr2Native(
        int order, 
        int uplo, 
        long N, 
        double alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Hermitian rank 2 operation with a general triangular packed-matrix and.
     * float-compelx elements.
     *
     * Hermitian rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^H + \conjg( alpha ) Y X^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param AP        Buffer (out) object storing packed-matrix  AP.
     * @param offa (in)      Offset in number of elements for the first element in matrix  AP.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N is zero, or
     *     - either  incx or  incy is zero
     *   -  clblasInvalidMemObject if either  AP,  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasChpr2(
        int order, 
        int uplo, 
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasChpr2Native(order, uplo, N, alpha, X, offx, incx, Y, offy, incy, AP, offa, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasChpr2Native(
        int order, 
        int uplo, 
        long N, 
        float[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Hermitian rank 2 operation with a general triangular packed-matrix and.
     * double-compelx elements.
     *
     * Hermitian rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^H + \conjg( alpha ) Y X^H + A \f$
     *
     * @param order (in)     Row/column order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of columns in matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset in number of elements for the first element in vector  X.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param Y (in)         Buffer object storing vector  Y.
     * @param offy (in)      Offset in number of elements for the first element in vector  Y.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param AP        Buffer (out) object storing packed-matrix  AP.
     * @param offa (in)      Offset in number of elements for the first element in matrix  AP.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChpr2() function otherwise.
     *
     */
    public static int clblasZhpr2(
        int order, 
        int uplo, 
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZhpr2Native(order, uplo, N, alpha, X, offx, incx, Y, offy, incy, AP, offa, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZhpr2Native(
        int order, 
        int uplo, 
        long N, 
        double[] alpha, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem Y, 
        long offy, 
        int incy, 
        cl_mem AP, 
        long offa, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_zhpr2.c
     * Example of how to use the @ref clblasZhpr2 function.
     */
    /**
     * Matrix-vector product with a general rectangular banded matrix and.
     * float elements.
     *
     * Matrix-vector products:
     *   - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *   - \f$ Y \leftarrow \alpha A^T X + eta Y \f$
     *
     * @param order (in)     Row/column order.
     * @param trans (in)     How matrix  A is to be transposed.
     * @param M (in)         Number of rows in banded matrix  A.
     * @param N (in)         Number of columns in banded matrix  A.
     * @param KL (in)        Number of sub-diagonals in banded matrix  A.
     * @param KU (in)        Number of super-diagonals in banded matrix  A.
     * @param alpha (in)     The factor of banded matrix  A.
     * @param A (in)         Buffer object storing banded matrix  A.
     * @param offa (in)      Offset in number of elements for the first element in banded matrix  A.
     * @param lda (in)       Leading dimension of banded matrix  A. It cannot be less
     *                      than (  KL +  KU + 1 )
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param beta (in)      The factor of the vector  Y.
     * @param Y (out)        Buffer object storing the vector  y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  M or  N is zero, or
     *     - KL is greater than  M - 1, or
     *     - KU is greater than  N - 1, or
     *     - either  incx or  incy is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix size or the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   -  clblasInvalidMemObject if either  A,  X, or  Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSgbmv(
        int order, 
        int trans, 
        long M, 
        long N, 
        long KL, 
        long KU, 
        float alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSgbmvNative(order, trans, M, N, KL, KU, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSgbmvNative(
        int order, 
        int trans, 
        long M, 
        long N, 
        long KL, 
        long KU, 
        float alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sgbmv.c
     * Example of how to use the @ref clblasSgbmv function.
     */
    /**
     * Matrix-vector product with a general rectangular banded matrix and.
     * double elements.
     *
     * Matrix-vector products:
     *   - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *   - \f$ Y \leftarrow \alpha A^T X + eta Y \f$
     *
     * @param order (in)     Row/column order.
     * @param trans (in)     How matrix  A is to be transposed.
     * @param M (in)         Number of rows in banded matrix  A.
     * @param N (in)         Number of columns in banded matrix  A.
     * @param KL (in)        Number of sub-diagonals in banded matrix  A.
     * @param KU (in)        Number of super-diagonals in banded matrix  A.
     * @param alpha (in)     The factor of banded matrix  A.
     * @param A (in)         Buffer object storing banded matrix  A.
     * @param offa (in)      Offset in number of elements for the first element in banded matrix  A.
     * @param lda (in)       Leading dimension of banded matrix  A. It cannot be less
     *                      than (  KL +  KU + 1 )
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param beta (in)      The factor of the vector  Y.
     * @param Y (out)        Buffer object storing the vector  y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSgbmv() function otherwise.
     *
     */
    public static int clblasDgbmv(
        int order, 
        int trans, 
        long M, 
        long N, 
        long KL, 
        long KU, 
        double alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDgbmvNative(order, trans, M, N, KL, KU, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDgbmvNative(
        int order, 
        int trans, 
        long M, 
        long N, 
        long KL, 
        long KU, 
        double alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a general rectangular banded matrix and.
     * float-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *   - \f$ Y \leftarrow \alpha A^T X + eta Y \f$
     *
     * @param order (in)     Row/column order.
     * @param trans (in)     How matrix  A is to be transposed.
     * @param M (in)         Number of rows in banded matrix  A.
     * @param N (in)         Number of columns in banded matrix  A.
     * @param KL (in)        Number of sub-diagonals in banded matrix  A.
     * @param KU (in)        Number of super-diagonals in banded matrix  A.
     * @param alpha (in)     The factor of banded matrix  A.
     * @param A (in)         Buffer object storing banded matrix  A.
     * @param offa (in)      Offset in number of elements for the first element in banded matrix  A.
     * @param lda (in)       Leading dimension of banded matrix  A. It cannot be less
     *                      than (  KL +  KU + 1 )
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param beta (in)      The factor of the vector  Y.
     * @param Y (out)        Buffer object storing the vector  y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasSgbmv() function.
     *
     */
    public static int clblasCgbmv(
        int order, 
        int trans, 
        long M, 
        long N, 
        long KL, 
        long KU, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCgbmvNative(order, trans, M, N, KL, KU, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCgbmvNative(
        int order, 
        int trans, 
        long M, 
        long N, 
        long KL, 
        long KU, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a general rectangular banded matrix and.
     * double-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *   - \f$ Y \leftarrow \alpha A^T X + eta Y \f$
     *
     * @param order (in)     Row/column order.
     * @param trans (in)     How matrix  A is to be transposed.
     * @param M (in)         Number of rows in banded matrix  A.
     * @param N (in)         Number of columns in banded matrix  A.
     * @param KL (in)        Number of sub-diagonals in banded matrix  A.
     * @param KU (in)        Number of super-diagonals in banded matrix  A.
     * @param alpha (in)     The factor of banded matrix  A.
     * @param A (in)         Buffer object storing banded matrix  A.
     * @param offa (in)      Offset in number of elements for the first element in banded matrix  A.
     * @param lda (in)       Leading dimension of banded matrix  A. It cannot be less
     *                      than (  KL +  KU + 1 )
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of  X. Must not be zero.
     * @param beta (in)      The factor of the vector  Y.
     * @param Y (out)        Buffer object storing the vector  y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of  Y. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDgbmv() function.
     *
     */
    public static int clblasZgbmv(
        int order, 
        int trans, 
        long M, 
        long N, 
        long KL, 
        long KU, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZgbmvNative(order, trans, M, N, KL, KU, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZgbmvNative(
        int order, 
        int trans, 
        long M, 
        long N, 
        long KL, 
        long KU, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a triangular banded matrix and.
     * float elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in banded matrix  A.
     * @param K                 Number (in) of sub-diagonals/super-diagonals in triangular banded matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than (  K + 1 )
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - K is greater than  N - 1
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasStbmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasStbmvNative(order, uplo, trans, diag, N, K, A, offa, lda, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasStbmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_stbmv.c
     * Example of how to use the @ref clblasStbmv function.
     */
    /**
     * Matrix-vector product with a triangular banded matrix and.
     * double elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in banded matrix  A.
     * @param K                 Number (in) of sub-diagonals/super-diagonals in triangular banded matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than (  K + 1 )
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStbmv() function otherwise.
     *
     */
    public static int clblasDtbmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDtbmvNative(order, uplo, trans, diag, N, K, A, offa, lda, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDtbmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a triangular banded matrix and.
     * float-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in banded matrix  A.
     * @param K                 Number (in) of sub-diagonals/super-diagonals in triangular banded matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than (  K + 1 )
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
    * @return The same result as the clblasStbmv() function.
     *
     */
    public static int clblasCtbmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCtbmvNative(order, uplo, trans, diag, N, K, A, offa, lda, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCtbmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a triangular banded matrix and.
     * double-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in banded matrix  A.
     * @param K                 Number (in) of sub-diagonals/super-diagonals in triangular banded matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than (  K + 1 )
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param scratchBuff       Temporary (in) cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
    * @return The same result as the clblasDtbmv() function.
     *
     */
    public static int clblasZtbmv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZtbmvNative(order, uplo, trans, diag, N, K, A, offa, lda, X, offx, incx, scratchBuff, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZtbmvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        cl_mem scratchBuff, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a symmetric banded matrix and float elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in banded matrix  A.
     * @param K         Number (in) of sub-diagonals/super-diagonals in banded matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A         Buffer (in) object storing matrix  A.
     * @param offa      Offset (in) in number of elements for first element in matrix  A.
     * @param lda       Leading (in) dimension of matrix  A. It cannot be less
     *                      than (  K + 1 )
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - K is greater than  N - 1
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSsbmv(
        int order, 
        int uplo, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSsbmvNative(order, uplo, N, K, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSsbmvNative(
        int order, 
        int uplo, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_ssbmv.c
     * This is an example of how to use the @ref clblasSsbmv function.
     */
    /**
     * Matrix-vector product with a symmetric banded matrix and double elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in banded matrix  A.
     * @param K         Number (in) of sub-diagonals/super-diagonals in banded matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A         Buffer (in) object storing matrix  A.
     * @param offa      Offset (in) in number of elements for first element in matrix  A.
     * @param lda       Leading (in) dimension of matrix  A. It cannot be less
     *                      than (  K + 1 )
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSsbmv() function otherwise.
     *
     */
    public static int clblasDsbmv(
        int order, 
        int uplo, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDsbmvNative(order, uplo, N, K, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDsbmvNative(
        int order, 
        int uplo, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-vector product with a hermitian banded matrix and float elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in banded matrix  A.
     * @param K         Number (in) of sub-diagonals/super-diagonals in banded matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A         Buffer (in) object storing matrix  A.
     * @param offa      Offset (in) in number of elements for first element in matrix  A.
     * @param lda       Leading (in) dimension of matrix  A. It cannot be less
     *                      than (  K + 1 )
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - K is greater than  N - 1
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasChbmv(
        int order, 
        int uplo, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasChbmvNative(order, uplo, N, K, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasChbmvNative(
        int order, 
        int uplo, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        float[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_chbmv.c
     * This is an example of how to use the @ref clblasChbmv function.
     */
    /**
     * Matrix-vector product with a hermitian banded matrix and double elements..
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + eta Y \f$
     *
     * @param order (in)     Row/columns order.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param N (in)         Number of rows and columns in banded matrix  A.
     * @param K         Number (in) of sub-diagonals/super-diagonals in banded matrix  A.
     * @param alpha (in)     The factor of matrix  A.
     * @param A         Buffer (in) object storing matrix  A.
     * @param offa      Offset (in) in number of elements for first element in matrix  A.
     * @param lda       Leading (in) dimension of matrix  A. It cannot be less
     *                      than (  K + 1 )
     * @param X (in)         Buffer object storing vector  X.
     * @param offx (in)      Offset of first element of vector  X in buffer object.
     *                      Counted in elements.
     * @param incx (in)      Increment for the elements of vector  X. It cannot be zero.
     * @param beta (in)      The factor of vector  Y.
     * @param Y (out)        Buffer object storing vector  Y.
     * @param offy (in)      Offset of first element of vector  Y in buffer object.
     *                      Counted in elements.
     * @param incy (in)      Increment for the elements of vector  Y. It cannot be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChbmv() function otherwise.
     *
     */
    public static int clblasZhbmv(
        int order, 
        int uplo, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZhbmvNative(order, uplo, N, K, alpha, A, offa, lda, X, offx, incx, beta, Y, offy, incy, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZhbmvNative(
        int order, 
        int uplo, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        double[] beta, 
        cl_mem Y, 
        long offy, 
        int incy, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular banded matrix problems with float elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in banded matrix  A.
     * @param K                 Number (in) of sub-diagonals/super-diagonals in triangular banded matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than (  K + 1 )
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  incx is zero, or
     *     - K is greater than  N - 1
     *     - the leading dimension is invalid;
     *   -  clblasInvalidMemObject if either  A or  X object is
     *     Invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasStbsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasStbsvNative(order, uplo, trans, diag, N, K, A, offa, lda, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasStbsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_stbsv.c
     * This is an example of how to use the @ref clblasStbsv function.
     */
    /**
     * solving triangular banded matrix problems with double elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in banded matrix  A.
     * @param K                 Number (in) of sub-diagonals/super-diagonals in triangular banded matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than (  K + 1 )
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStbsv() function otherwise.
     *
     */
    public static int clblasDtbsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDtbsvNative(order, uplo, trans, diag, N, K, A, offa, lda, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDtbsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular banded matrix problems with float-complex elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in banded matrix  A.
     * @param K                 Number (in) of sub-diagonals/super-diagonals in triangular banded matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than (  K + 1 )
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasStbsv() function.
     *
     */
    public static int clblasCtbsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCtbsvNative(order, uplo, trans, diag, N, K, A, offa, lda, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCtbsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * solving triangular banded matrix problems with double-complex elements..
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param order             Row/column (in) order.
     * @param uplo              The (in) triangle in matrix being referenced.
     * @param trans             How (in) matrix  A is to be transposed.
     * @param diag              Specify (in) whether matrix  A is unit triangular.
     * @param N                 Number (in) of rows/columns in banded matrix  A.
     * @param K                 Number (in) of sub-diagonals/super-diagonals in triangular banded matrix  A.
     * @param A                 Buffer (in) object storing matrix  A.
     * @param offa              Offset (in) in number of elements for first element in matrix  A.
     * @param lda               Leading (in) dimension of matrix  A. It cannot be less
     *                              than (  K + 1 )
     * @param X             Buffer (out) object storing vector  X.
     * @param offx              Offset (in) in number of elements for first element in vector  X.
     * @param incx              Increment (in) for the elements of  X. Must not be zero.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDtbsv() function.
     *
     */
    public static int clblasZtbsv(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZtbsvNative(order, uplo, trans, diag, N, K, A, offa, lda, X, offx, incx, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZtbsvNative(
        int order, 
        int uplo, 
        int trans, 
        int diag, 
        long N, 
        long K, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem X, 
        long offx, 
        int incx, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-matrix product of general rectangular matrices with float.
     *        elements. Extended version.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + eta C \f$
     *   - \f$ C \leftarrow \alpha A B^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B^T + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param transB (in)    How matrix  B is to be transposed.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  B.
     * @param K (in)         Number of columns in matrix  A and rows in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  K when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when the
     *                      parameter is set to  clblasColumnMajor.
     * @param B (in)         Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  K
     *                      when it is set to  clblasColumnMajor.
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param  (in) offC     Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when
     *                      it is set to  clblasColumnMajorOrder.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA,  offB or  offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as clblasSgemm() otherwise.
     *
     */
    public static int clblasSgemm(
        int order, 
        int transA, 
        int transB, 
        long M, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        float beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSgemmNative(order, transA, transB, M, N, K, alpha, A, offA, lda, B, offB, ldb, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSgemmNative(
        int order, 
        int transA, 
        int transB, 
        long M, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        float beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_sgemm.c
     * This is an example of how to use the @ref clblasSgemmEx function.
     */
    /**
     * Matrix-matrix product of general rectangular matrices with double.
     *        elements. Extended version.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + eta C \f$
     *   - \f$ C \leftarrow \alpha A B^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B^T + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param transB (in)    How matrix  B is to be transposed.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  B.
     * @param K (in)         Number of columns in matrix  A and rows in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For detailed description,
     *                      see clblasSgemm().
     * @param B (in)         Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed description,
     *                      see clblasSgemm().
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offC (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. For detailed description,
     *                      see clblasSgemm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA,  offB or offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSgemm() function otherwise.
     *
     */
    public static int clblasDgemm(
        int order, 
        int transA, 
        int transB, 
        long M, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        double beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDgemmNative(order, transA, transB, M, N, K, alpha, A, offA, lda, B, offB, ldb, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDgemmNative(
        int order, 
        int transA, 
        int transB, 
        long M, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        double beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-matrix product of general rectangular matrices with float.
     *        complex elements. Extended version.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + eta C \f$
     *   - \f$ C \leftarrow \alpha A B^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B^T + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param transB (in)    How matrix  B is to be transposed.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  B.
     * @param K (in)         Number of columns in matrix  A and rows in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For detailed description,
     *                      see clblasSgemm().
     * @param B (in)         Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed description,
     *                      see clblasSgemm().
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offC (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. For detailed description,
     *                      see clblasSgemm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA,  offB or offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSgemm() function otherwise.
     *
     */
    public static int clblasCgemm(
        int order, 
        int transA, 
        int transB, 
        long M, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        float[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCgemmNative(order, transA, transB, M, N, K, alpha, A, offA, lda, B, offB, ldb, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCgemmNative(
        int order, 
        int transA, 
        int transB, 
        long M, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        float[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-matrix product of general rectangular matrices with double.
     *        complex elements. Exteneded version.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + eta C \f$
     *   - \f$ C \leftarrow \alpha A B^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B^T + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param transB (in)    How matrix  B is to be transposed.
     * @param M (in)         Number of rows in matrix  A.
     * @param N (in)         Number of columns in matrix  B.
     * @param K (in)         Number of columns in matrix  A and rows in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For detailed description,
     *                      see clblasSgemm().
     * @param B (in)         Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed description,
     *                      see clblasSgemm().
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offC (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. For detailed description,
     *                      see clblasSgemm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA,  offB or offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSgemm() function otherwise.
     *
     */
    public static int clblasZgemm(
        int order, 
        int transA, 
        int transB, 
        long M, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        double[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZgemmNative(order, transA, transB, M, N, K, alpha, A, offA, lda, B, offB, ldb, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZgemmNative(
        int order, 
        int transA, 
        int transB, 
        long M, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        double[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Multiplying a matrix by a triangular matrix with float elements..
     *        Extended version.
     *
     * Matrix-triangular matrix products:
     *   - \f$ B \leftarrow \alpha A B \f$
     *   - \f$ B \leftarrow \alpha A^T B \f$
     *   - \f$ B \leftarrow \alpha B A \f$
     *   - \f$ B \leftarrow \alpha B A^T \f$
     *
     * where  T is an upper or lower triangular matrix.
     *
     * @param order (in)     Row/column order.
     * @param side (in)      The side of triangular matrix.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param diag (in)      Specify whether matrix is unit triangular.
     * @param M (in)         Number of rows in matrix  B.
     * @param N (in)         Number of columns in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  M when the  side parameter is set to
     *                       clblasLeft,\n or less than  N when it is set
     *                      to  clblasRight.
     * @param B (out)        Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or not less than  M
     *                      when it is set to  clblasColumnMajor.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA or  offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as clblasStrmm() otherwise.
     *
     */
    public static int clblasStrmm(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasStrmmNative(order, side, uplo, transA, diag, M, N, alpha, A, offA, lda, B, offB, ldb, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasStrmmNative(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_strmm.c
     * This is an example of how to use the @ref clblasStrmmEx function.
     */
    /**
     * Multiplying a matrix by a triangular matrix with double elements..
     *        Extended version.
     *
     * Matrix-triangular matrix products:
     *   - \f$ B \leftarrow \alpha A B \f$
     *   - \f$ B \leftarrow \alpha A^T B \f$
     *   - \f$ B \leftarrow \alpha B A \f$
     *   - \f$ B \leftarrow \alpha B A^T \f$
     *
     * where  T is an upper or lower triangular matrix.
     *
     * @param order (in)     Row/column order.
     * @param side (in)      The side of triangular matrix.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param diag (in)      Specify whether matrix is unit triangular.
     * @param M (in)         Number of rows in matrix  B.
     * @param N (in)         Number of columns in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For detailed
     *                      description, see clblasStrmm().
     * @param B (out)        Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed
     *                      description, see clblasStrmm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA or  offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasStrmm() function otherwise.
     *
     */
    public static int clblasDtrmm(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDtrmmNative(order, side, uplo, transA, diag, M, N, alpha, A, offA, lda, B, offB, ldb, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDtrmmNative(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Multiplying a matrix by a triangular matrix with float complex.
     *        elements. Extended version.
     *
     * Matrix-triangular matrix products:
     *   - \f$ B \leftarrow \alpha A B \f$
     *   - \f$ B \leftarrow \alpha A^T B \f$
     *   - \f$ B \leftarrow \alpha B A \f$
     *   - \f$ B \leftarrow \alpha B A^T \f$
     *
     * where  T is an upper or lower triangular matrix.
     * @param order (in)     Row/column order.
     * @param side (in)      The side of triangular matrix.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param diag (in)      Specify whether matrix is unit triangular.
     * @param M (in)         Number of rows in matrix  B.
     * @param N (in)         Number of columns in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param A (in)         Buffer object storing matrix  A.
     * @param lda (in)       Leading dimension of matrix  A. For detailed
     *                      description, see clblasStrmm().
     * @param B (out)        Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed
     *                      description, see clblasStrmm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA or  offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as clblasStrmm() otherwise.
     *
     */
    public static int clblasCtrmm(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCtrmmNative(order, side, uplo, transA, diag, M, N, alpha, A, offA, lda, B, offB, ldb, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCtrmmNative(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Multiplying a matrix by a triangular matrix with double complex.
     *        elements. Extended version.
     *
     * Matrix-triangular matrix products:
     *   - \f$ B \leftarrow \alpha A B \f$
     *   - \f$ B \leftarrow \alpha A^T B \f$
     *   - \f$ B \leftarrow \alpha B A \f$
     *   - \f$ B \leftarrow \alpha B A^T \f$
     *
     * where  T is an upper or lower triangular matrix.
     *
     * @param order (in)     Row/column order.
     * @param side (in)      The side of triangular matrix.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param diag (in)      Specify whether matrix is unit triangular.
     * @param M (in)         Number of rows in matrix  B.
     * @param N (in)         Number of columns in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For detailed
     *                      description, see clblasStrmm().
     * @param B (out)        Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed
     *                      description, see clblasStrmm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA or  offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasStrmm() function otherwise.
     *
     */
    public static int clblasZtrmm(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZtrmmNative(order, side, uplo, transA, diag, M, N, alpha, A, offA, lda, B, offB, ldb, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZtrmmNative(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Solving triangular systems of equations with multiple right-hand.
     *        sides and float elements. Extended version.
     *
     * Solving triangular systems of equations:
     *   - \f$ B \leftarrow \alpha A^{-1} B \f$
     *   - \f$ B \leftarrow \alpha A^{-T} B \f$
     *   - \f$ B \leftarrow \alpha B A^{-1} \f$
     *   - \f$ B \leftarrow \alpha B A^{-T} \f$
     *
     * where  T is an upper or lower triangular matrix.
     *
     * @param order (in)     Row/column order.
     * @param side (in)      The side of triangular matrix.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param diag (in)      Specify whether matrix is unit triangular.
     * @param M (in)         Number of rows in matrix  B.
     * @param N (in)         Number of columns in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  M when the  side parameter is set to
     *                       clblasLeft,\n or less than  N
     *                      when it is set to  clblasRight.
     * @param B (out)        Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M
     *                      when it is set to  clblasColumnMajor.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA or  offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as clblasStrsm() otherwise.
     *
     */
    public static int clblasStrsm(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasStrsmNative(order, side, uplo, transA, diag, M, N, alpha, A, offA, lda, B, offB, ldb, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasStrsmNative(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_strsm.c
     * This is an example of how to use the @ref clblasStrsmEx function.
     */
    /**
     * Solving triangular systems of equations with multiple right-hand.
     *        sides and double elements. Extended version.
     *
     * Solving triangular systems of equations:
     *   - \f$ B \leftarrow \alpha A^{-1} B \f$
     *   - \f$ B \leftarrow \alpha A^{-T} B \f$
     *   - \f$ B \leftarrow \alpha B A^{-1} \f$
     *   - \f$ B \leftarrow \alpha B A^{-T} \f$
     *
     * where  T is an upper or lower triangular matrix.
     *
     * @param order (in)     Row/column order.
     * @param side (in)      The side of triangular matrix.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param diag (in)      Specify whether matrix is unit triangular.
     * @param M (in)         Number of rows in matrix  B.
     * @param N (in)         Number of columns in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For detailed
     *                      description, see clblasStrsm().
     * @param B (out)        Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed
     *                      description, see clblasStrsm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA or  offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasStrsm() function otherwise.
     *
     */
    public static int clblasDtrsm(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDtrsmNative(order, side, uplo, transA, diag, M, N, alpha, A, offA, lda, B, offB, ldb, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDtrsmNative(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Solving triangular systems of equations with multiple right-hand.
     *        sides and float complex elements. Extended version.
     *
     * Solving triangular systems of equations:
     *   - \f$ B \leftarrow \alpha A^{-1} B \f$
     *   - \f$ B \leftarrow \alpha A^{-T} B \f$
     *   - \f$ B \leftarrow \alpha B A^{-1} \f$
     *   - \f$ B \leftarrow \alpha B A^{-T} \f$
     *
     * where  T is an upper or lower triangular matrix.
     *
     * @param order (in)     Row/column order.
     * @param side (in)      The side of triangular matrix.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param diag (in)      Specify whether matrix is unit triangular.
     * @param M (in)         Number of rows in matrix  B.
     * @param N (in)         Number of columns in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For detailed
     *                      description, see clblasStrsm().
     * @param B (out)        Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed
     *                      description, see clblasStrsm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA or  offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as clblasStrsm() otherwise.
     *
     */
    public static int clblasCtrsm(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCtrsmNative(order, side, uplo, transA, diag, M, N, alpha, A, offA, lda, B, offB, ldb, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCtrsmNative(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Solving triangular systems of equations with multiple right-hand.
     *        sides and double complex elements. Extended version.
     *
     * Solving triangular systems of equations:
     *   - \f$ B \leftarrow \alpha A^{-1} B \f$
     *   - \f$ B \leftarrow \alpha A^{-T} B \f$
     *   - \f$ B \leftarrow \alpha B A^{-1} \f$
     *   - \f$ B \leftarrow \alpha B A^{-T} \f$
     *
     * where  T is an upper or lower triangular matrix.
     *
     * @param order (in)     Row/column order.
     * @param side (in)      The side of triangular matrix.
     * @param uplo (in)      The triangle in matrix being referenced.
     * @param transA (in)    How matrix  A is to be transposed.
     * @param diag (in)      Specify whether matrix is unit triangular.
     * @param M (in)         Number of rows in matrix  B.
     * @param N (in)         Number of columns in matrix  B.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offA (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. For detailed
     *                      description, see clblasStrsm().
     * @param B (out)        Buffer object storing matrix  B.
     * @param offB (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. For detailed
     *                      description, see clblasStrsm().
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA or  offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasStrsm() function otherwise
     *
     */
    public static int clblasZtrsm(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZtrsmNative(order, side, uplo, transA, diag, M, N, alpha, A, offA, lda, B, offB, ldb, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZtrsmNative(
        int order, 
        int side, 
        int uplo, 
        int transA, 
        int diag, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Rank-k update of a symmetric matrix with float elements..
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T A + eta C \f$
     *
     * where  C is a symmetric matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transA (in)     How matrix  A is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrix  A if it is not
     *                       transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrix  A.
     * @param A (in)          Buffer object storing the matrix  A.
     * @param offA (in)       Offset of the first element of the matrix  A in the
     *                       buffer object. Counted in elements.
     * @param lda (in)        Leading dimension of matrix  A. It cannot be
     *                       less than  K if  A is
     *                       in the row-major format, and less than  N
     *                       otherwise.
     * @param beta (in)       The factor of the matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offC (in)       Offset of the first element of the matrix  C in the
     *                       buffer object. Counted in elements.
     * @param ldc (in)        Leading dimension of matric  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA or  offC exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasSsyrk() function otherwise.
     *
     */
    public static int clblasSsyrk(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        float beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSsyrkNative(order, uplo, transA, N, K, alpha, A, offA, lda, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSsyrkNative(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        float beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_ssyrk.c
     * This is an example of how to use the @ref clblasSsyrkEx function.
     */
    /**
     * Rank-k update of a symmetric matrix with double elements..
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T A + eta C \f$
     *
     * where  C is a symmetric matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transA (in)     How matrix  A is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrix  A if it is not
     *                       transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrix  A.
     * @param A (in)          Buffer object storing the matrix  A.
     * @param offA (in)       Offset of the first element of the matrix  A in the
     *                       buffer object. Counted in elements.
     * @param lda (in)        Leading dimension of matrix  A. For detailed
     *                       description, see clblasSsyrk().
     * @param beta (in)       The factor of the matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offC (in)       Offset of the first element of the matrix  C in the
     *                       buffer object. Counted in elements.
     * @param ldc (in)        Leading dimension of matrix  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA or  offC exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasSsyrk() function otherwise.
     *
     */
    public static int clblasDsyrk(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        double beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDsyrkNative(order, uplo, transA, N, K, alpha, A, offA, lda, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDsyrkNative(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        double beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Rank-k update of a symmetric matrix with complex float elements..
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T A + eta C \f$
     *
     * where  C is a symmetric matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transA (in)     How matrix  A is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrix  A if it is not
     *                       transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrix  A.
     * @param A (in)          Buffer object storing the matrix  A.
     * @param offA (in)       Offset of the first element of the matrix  A in the
     *                       buffer object. Counted in elements.
     * @param lda (in)        Leading dimension of matrix  A. For detailed
     *                       description, see clblasSsyrk().
     * @param beta (in)       The factor of the matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offC (in)       Offset of the first element of the matrix  C in the
     *                       buffer object. Counted in elements.
     * @param ldc (in)        Leading dimension of matrix  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA or  offC exceeds the size
     *        of the respective buffer object;
     *   -  clblasInvalidValue if  transA is set to \ref clblasConjTrans.
     *   - the same error codes as the clblasSsyrk() function otherwise.
     *
     */
    public static int clblasCsyrk(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        float[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCsyrkNative(order, uplo, transA, N, K, alpha, A, offA, lda, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCsyrkNative(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        float[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Rank-k update of a symmetric matrix with complex double elements..
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T A + eta C \f$
     *
     * where  C is a symmetric matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transA (in)     How matrix  A is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrix  A if it is not
     *                       transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrix  A.
     * @param A (in)          Buffer object storing the matrix  A.
     * @param offA (in)       Offset of the first element of the matrix  A in the
     *                       buffer object. Counted in elements.
     * @param lda (in)        Leading dimension of matrix  A. For detailed
     *                       description, see clblasSsyrk().
     * @param beta (in)       The factor of the matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offC (in)       Offset of the first element of the matrix  C in the
     *                       buffer object. Counted in elements.
     * @param ldc (in)        Leading dimension of matrix  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *         point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA or  offC exceeds the size
     *        of the respective buffer object;
     *   -  clblasInvalidValue if  transA is set to \ref clblasConjTrans.
     *   - the same error codes as the clblasSsyrk() function otherwise.
     *
     */
    public static int clblasZsyrk(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        double[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZsyrkNative(order, uplo, transA, N, K, alpha, A, offA, lda, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZsyrkNative(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        double[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Rank-2k update of a symmetric matrix with float elements..
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^T + \alpha B A^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \alpha B^T A eta C \f$
     *
     * where  C is a symmetric matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transAB (in)    How matrices  A and  B is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrices  A and  B if they
     *                       are not transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrices  A and  B.
     * @param A (in)          Buffer object storing matrix  A.
     * @param offA (in)       Offset of the first element of the matrix  A in the
     *                       buffer object. Counted in elements.
     * @param lda (in)        Leading dimension of matrix  A. It cannot be less
     *                       than  K if  A is
     *                       in the row-major format, and less than  N
     *                       otherwise.
     * @param B (in)          Buffer object storing matrix  B.
     * @param offB (in)       Offset of the first element of the matrix  B in the
     *                       buffer object. Counted in elements.
     * @param ldb (in)        Leading dimension of matrix  B. It cannot be less
     *                       less than  K if  B matches to the op( B) matrix
     *                       in the row-major format, and less than  N
     *                       otherwise.
     * @param beta (in)       The factor of matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offC (in)       Offset of the first element of the matrix  C in the
     *                       buffer object. Counted in elements.
     * @param ldc (in)        Leading dimension of matrix  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA,  offB or  offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSsyr2k() function otherwise.
     *
     */
    public static int clblasSsyr2k(
        int order, 
        int uplo, 
        int transAB, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        float beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSsyr2kNative(order, uplo, transAB, N, K, alpha, A, offA, lda, B, offB, ldb, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSsyr2kNative(
        int order, 
        int uplo, 
        int transAB, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        float beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_ssyr2k.c
     * This is an example of how to use the @ref clblasSsyr2kEx function.
     */
    /**
     * Rank-2k update of a symmetric matrix with double elements..
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^T + \alpha B A^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \alpha B^T A eta C \f$
     *
     * where  C is a symmetric matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transAB (in)    How matrices  A and  B is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrices  A and  B if they
     *                       are not transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrices  A and  B.
     * @param A (in)          Buffer object storing matrix  A.
     * @param offA (in)       Offset of the first element of the matrix  A in the
     *                       buffer object. Counted in elements.
     * @param lda (in)        Leading dimension of matrix  A. For detailed
     *                       description, see clblasSsyr2k().
     * @param B (in)          Buffer object storing matrix  B.
     * @param offB (in)       Offset of the first element of the matrix  B in the
     *                       buffer object. Counted in elements.
     * @param ldb (in)        Leading dimension of matrix  B. For detailed
     *                       description, see clblasSsyr2k().
     * @param beta (in)       The factor of matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offC (in)       Offset of the first element of the matrix  C in the
     *                       buffer object. Counted in elements.
     * @param ldc (in)        Leading dimension of matrix  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA,  offB or  offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSsyr2k() function otherwise.
     *
     */
    public static int clblasDsyr2k(
        int order, 
        int uplo, 
        int transAB, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        double beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDsyr2kNative(order, uplo, transAB, N, K, alpha, A, offA, lda, B, offB, ldb, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDsyr2kNative(
        int order, 
        int uplo, 
        int transAB, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        double beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Rank-2k update of a symmetric matrix with complex float elements..
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^T + \alpha B A^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \alpha B^T A eta C \f$
     *
     * where  C is a symmetric matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transAB (in)    How matrices  A and  B is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrices  A and  B if they
     *                       are not transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrices  A and  B.
     * @param A (in)          Buffer object storing matrix  A.
     * @param offA (in)       Offset of the first element of the matrix  A in the
     *                       buffer object. Counted in elements.
     * @param lda (in)        Leading dimension of matrix  A. For detailed
     *                       description, see clblasSsyr2k().
     * @param B (in)          Buffer object storing matrix  B.
     * @param offB (in)       Offset of the first element of the matrix  B in the
     *                       buffer object. Counted in elements.
     * @param ldb (in)        Leading dimension of matrix  B. For detailed
     *                       description, see clblasSsyr2k().
     * @param beta (in)       The factor of matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offC (in)       Offset of the first element of the matrix  C in the
     *                       buffer object. Counted in elements.
     * @param ldc (in)        Leading dimension of matrix  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidValue if either  offA,  offB or  offC exceeds
     *        the size of the respective buffer object;
     *   -  clblasInvalidValue if  transAB is set to \ref clblasConjTrans.
     *   - the same error codes as the clblasSsyr2k() function otherwise.
     *
     */
    public static int clblasCsyr2k(
        int order, 
        int uplo, 
        int transAB, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        float[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCsyr2kNative(order, uplo, transAB, N, K, alpha, A, offA, lda, B, offB, ldb, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCsyr2kNative(
        int order, 
        int uplo, 
        int transAB, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        float[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Rank-2k update of a symmetric matrix with complex double elements..
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^T + \alpha B A^T + eta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \alpha B^T A eta C \f$
     *
     * where  C is a symmetric matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transAB (in)    How matrices  A and  B is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrices  A and  B if they
     *                       are not transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrices  A and  B.
     * @param A (in)          Buffer object storing matrix  A.
     * @param offA (in)       Offset of the first element of the matrix  A in the
     *                       buffer object. Counted in elements.
     * @param lda (in)        Leading dimension of matrix  A. For detailed
     *                       description, see clblasSsyr2k().
     * @param B (in)          Buffer object storing matrix  B.
     * @param offB (in)       Offset of the first element of the matrix  B in the
     *                       buffer object. Counted in elements.
     * @param ldb (in)        Leading dimension of matrix  B. For detailed
     *                       description, see clblasSsyr2k().
     * @param beta (in)       The factor of matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offC (in)       Offset of the first element of the matrix  C in the
     *                       buffer object. Counted in elements.
     * @param ldc (in)        Leading dimension of matrix  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   -  clblasInvalidValue if either  offA,  offB or  offC exceeds
     *        the size of the respective buffer object;
     *   -  clblasInvalidValue if  transAB is set to \ref clblasConjTrans.
     *   - the same error codes as the clblasSsyr2k() function otherwise.
     *
     */
    public static int clblasZsyr2k(
        int order, 
        int uplo, 
        int transAB, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        double[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZsyr2kNative(order, uplo, transAB, N, K, alpha, A, offA, lda, B, offB, ldb, beta, C, offC, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZsyr2kNative(
        int order, 
        int uplo, 
        int transAB, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offA, 
        long lda, 
        cl_mem B, 
        long offB, 
        long ldb, 
        double[] beta, 
        cl_mem C, 
        long offC, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-matrix product of symmetric rectangular matrices with float.
     * elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha B A + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param side      The (in) side of triangular matrix.
     * @param uplo      The (in) triangle in matrix being referenced.
     * @param M (in)         Number of rows in matrices  B and  C.
     * @param N (in)         Number of columns in matrices  B and  C.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offa (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  M when the  side parameter is set to
     *                       clblasLeft,\n or less than  N when the
     *                      parameter is set to  clblasRight.
     * @param B (in)         Buffer object storing matrix  B.
     * @param offb (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M
     *                      when it is set to  clblasColumnMajor.
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offc (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when
     *                      it is set to  clblasColumnMajorOrder.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events             (in)  Event objects per each command queue that identify
     *                                a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  M or  N is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes lead to accessing outsize of any of the buffers;
     *   -  clblasInvalidMemObject if A, B, or C object is invalid,
     *     or an image object rather than the buffer one;
     *   -  clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasSsymm(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        float beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasSsymmNative(order, side, uplo, M, N, alpha, A, offa, lda, B, offb, ldb, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasSsymmNative(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        float alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        float beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_ssymm.c
     * This is an example of how to use the @ref clblasSsymm function.
     */
    /**
     * Matrix-matrix product of symmetric rectangular matrices with double.
     * elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha B A + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param side      The (in) side of triangular matrix.
     * @param uplo      The (in) triangle in matrix being referenced.
     * @param M (in)         Number of rows in matrices  B and  C.
     * @param N (in)         Number of columns in matrices  B and  C.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offa (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  M when the  side parameter is set to
     *                       clblasLeft,\n or less than  N when the
     *                      parameter is set to  clblasRight.
     * @param B (in)         Buffer object storing matrix  B.
     * @param offb (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M
     *                      when it is set to  clblasColumnMajor.
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offc (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when
     *                      it is set to  clblasColumnMajorOrder.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events             (in)  Event objects per each command queue that identify
     *                                a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSsymm() function otherwise.
     *
     */
    public static int clblasDsymm(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        double beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasDsymmNative(order, side, uplo, M, N, alpha, A, offa, lda, B, offb, ldb, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasDsymmNative(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        double alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        double beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-matrix product of symmetric rectangular matrices with.
     * float-complex elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha B A + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param side      The (in) side of triangular matrix.
     * @param uplo      The (in) triangle in matrix being referenced.
     * @param M (in)         Number of rows in matrices  B and  C.
     * @param N (in)         Number of columns in matrices  B and  C.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offa (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  M when the  side parameter is set to
     *                       clblasLeft,\n or less than  N when the
     *                      parameter is set to  clblasRight.
     * @param B (in)         Buffer object storing matrix  B.
     * @param offb (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M
     *                      when it is set to  clblasColumnMajor.
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offc (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when
     *                      it is set to  clblasColumnMajorOrder.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events             (in)  Event objects per each command queue that identify
     *                                a particular kernel execution instance.
     *
     * @return The same result as the clblasSsymm() function.
     *
     */
    public static int clblasCsymm(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        float[] beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCsymmNative(order, side, uplo, M, N, alpha, A, offa, lda, B, offb, ldb, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCsymmNative(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        float[] beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-matrix product of symmetric rectangular matrices with.
     * double-complex elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha B A + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param side      The (in) side of triangular matrix.
     * @param uplo      The (in) triangle in matrix being referenced.
     * @param M (in)         Number of rows in matrices  B and  C.
     * @param N (in)         Number of columns in matrices  B and  C.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offa (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  M when the  side parameter is set to
     *                       clblasLeft,\n or less than  N when the
     *                      parameter is set to  clblasRight.
     * @param B (in)         Buffer object storing matrix  B.
     * @param offb (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M
     *                      when it is set to  clblasColumnMajor.
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offc (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when
     *                      it is set to  clblasColumnMajorOrder.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events             (in)  Event objects per each command queue that identify
     *                                a particular kernel execution instance.
     *
     * @return The same result as the clblasDsymm() function.
     *
     */
    public static int clblasZsymm(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        double[] beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZsymmNative(order, side, uplo, M, N, alpha, A, offa, lda, B, offb, ldb, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZsymmNative(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        double[] beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Matrix-matrix product of hermitian rectangular matrices with.
     * float-complex elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha B A + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param side      The (in) side of triangular matrix.
     * @param uplo      The (in) triangle in matrix being referenced.
     * @param M (in)         Number of rows in matrices  B and  C.
     * @param N (in)         Number of columns in matrices  B and  C.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offa (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  M when the  side parameter is set to
     *                       clblasLeft,\n or less than  N when the
     *                      parameter is set to  clblasRight.
     * @param B (in)         Buffer object storing matrix  B.
     * @param offb (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M
     *                      when it is set to  clblasColumnMajor.
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offc (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when
     *                      it is set to  clblasColumnMajorOrder.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     -  M or  N is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes lead to accessing outsize of any of the buffers;
     *   -  clblasInvalidMemObject if A, B, or C object is invalid,
     *     or an image object rather than the buffer one;
     *   -  clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   -  clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   -  clblasCompilerNotAvailable if a compiler is not available;
     *   -  clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     */
    public static int clblasChemm(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        float[] beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasChemmNative(order, side, uplo, M, N, alpha, A, offa, lda, B, offb, ldb, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasChemmNative(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        float[] beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_chemm.cpp
     * This is an example of how to use the @ref clblasChemm function.
     */
    /**
     * Matrix-matrix product of hermitian rectangular matrices with.
     * double-complex elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + eta C \f$
     *   - \f$ C \leftarrow \alpha B A + eta C \f$
     *
     * @param order (in)     Row/column order.
     * @param side      The (in) side of triangular matrix.
     * @param uplo      The (in) triangle in matrix being referenced.
     * @param M (in)         Number of rows in matrices  B and  C.
     * @param N (in)         Number of columns in matrices  B and  C.
     * @param alpha (in)     The factor of matrix  A.
     * @param A (in)         Buffer object storing matrix  A.
     * @param offa (in)      Offset of the first element of the matrix  A in the
     *                      buffer object. Counted in elements.
     * @param lda (in)       Leading dimension of matrix  A. It cannot be less
     *                      than  M when the  side parameter is set to
     *                       clblasLeft,\n or less than  N when the
     *                      parameter is set to  clblasRight.
     * @param B (in)         Buffer object storing matrix  B.
     * @param offb (in)      Offset of the first element of the matrix  B in the
     *                      buffer object. Counted in elements.
     * @param ldb (in)       Leading dimension of matrix  B. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M
     *                      when it is set to  clblasColumnMajor.
     * @param beta (in)      The factor of matrix  C.
     * @param C (out)        Buffer object storing matrix  C.
     * @param offc (in)      Offset of the first element of the matrix  C in the
     *                      buffer object. Counted in elements.
     * @param ldc (in)       Leading dimension of matrix  C. It cannot be less
     *                      than  N when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  M when
     *                      it is set to  clblasColumnMajorOrder.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChemm() function otherwise.
     *
     */
    public static int clblasZhemm(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        double[] beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZhemmNative(order, side, uplo, M, N, alpha, A, offa, lda, B, offb, ldb, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZhemmNative(
        int order, 
        int side, 
        int uplo, 
        long M, 
        long N, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        double[] beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Rank-k update of a hermitian matrix with float-complex elements..
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^H + eta C \f$
     *   - \f$ C \leftarrow \alpha A^H A + eta C \f$
     *
     * where  C is a hermitian matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transA (in)     How matrix  A is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrix  A if it is not
     *                       transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrix  A.
     * @param A (in)          Buffer object storing the matrix  A.
     * @param offa (in)       Offset in number of elements for the first element in matrix  A.
     * @param lda (in)        Leading dimension of matrix  A. It cannot be
     *                       less than  K if  A is
     *                       in the row-major format, and less than  N
     *                       otherwise.
     * @param beta (in)       The factor of the matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offc (in)       Offset in number of elements for the first element in matrix  C.
     * @param ldc (in)        Leading dimension of matric  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  K is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes lead to accessing outsize of any of the buffers;
     *   -  clblasInvalidMemObject if either  A or  C object is
     *     invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released.
     *
     */
    public static int clblasCherk(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        float beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCherkNative(order, uplo, transA, N, K, alpha, A, offa, lda, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCherkNative(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        float alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        float beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_cherk.cpp
     * This is an example of how to use the @ref clblasCherk function.
     */
    /**
     * Rank-k update of a hermitian matrix with double-complex elements..
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^H + eta C \f$
     *   - \f$ C \leftarrow \alpha A^H A + eta C \f$
     *
     * where  C is a hermitian matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param transA (in)     How matrix  A is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrix  A if it is not
     *                       transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrix  A.
     * @param A (in)          Buffer object storing the matrix  A.
     * @param offa (in)       Offset in number of elements for the first element in matrix  A.
     * @param lda (in)        Leading dimension of matrix  A. It cannot be
     *                       less than  K if  A is
     *                       in the row-major format, and less than  N
     *                       otherwise.
     * @param beta (in)       The factor of the matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offc (in)       Offset in number of elements for the first element in matrix  C.
     * @param ldc (in)        Leading dimension of matric  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCherk() function otherwise.
     *
     */
    public static int clblasZherk(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        double beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZherkNative(order, uplo, transA, N, K, alpha, A, offa, lda, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZherkNative(
        int order, 
        int uplo, 
        int transA, 
        long N, 
        long K, 
        double alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        double beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Rank-2k update of a hermitian matrix with float-complex elements..
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^H + conj( \alpha ) B A^H + eta C \f$
     *   - \f$ C \leftarrow \alpha A^H B + conj( \alpha ) B^H A + eta C \f$
     *
     * where  C is a hermitian matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param trans (in)      How matrix  A is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrix  A if it is not
     *                       transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrix  A.
     * @param A (in)          Buffer object storing the matrix  A.
     * @param offa (in)       Offset in number of elements for the first element in matrix  A.
     * @param lda (in)        Leading dimension of matrix  A. It cannot be
     *                       less than  K if  A is
     *                       in the row-major format, and less than  N
     *                       otherwise. Vice-versa for transpose case.
     * @param B (in)          Buffer object storing the matrix  B.
     * @param offb (in)       Offset in number of elements for the first element in matrix  B.
     * @param ldb (in)        Leading dimension of matrix  B. It cannot be
     *                       less than  K if  B is
     *                       in the row-major format, and less than  N
     *                       otherwise. Vice-versa for transpose case
     * @param beta (in)       The factor of the matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offc (in)       Offset in number of elements for the first element in matrix  C.
     * @param ldc (in)        Leading dimension of matric  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasNotInitialized if clblasSetup() was not called;
     *   -  clblasInvalidValue if invalid parameters are passed:
     *     - either  N or  K is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes lead to accessing outsize of any of the buffers;
     *   -  clblasInvalidMemObject if either  A ,  B or  C object is
     *     invalid, or an image object rather than the buffer one;
     *   -  clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   -  clblasInvalidCommandQueue if the passed command queue is invalid;
     *   -  clblasInvalidContext if a context a passed command queue belongs to
     *     was released.
     *
     */
    public static int clblasCher2k(
        int order, 
        int uplo, 
        int trans, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        float beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCher2kNative(order, uplo, trans, N, K, alpha, A, offa, lda, B, offb, ldb, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCher2kNative(
        int order, 
        int uplo, 
        int trans, 
        long N, 
        long K, 
        float[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        float beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * @example example_cher2k.c
     * This is an example of how to use the @ref clblasCher2k function.
     */
    /**
     * Rank-2k update of a hermitian matrix with double-complex elements..
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^H + conj( \alpha ) B A^H + eta C \f$
     *   - \f$ C \leftarrow \alpha A^H B + conj( \alpha ) B^H A + eta C \f$
     *
     * where  C is a hermitian matrix.
     *
     * @param order (in)      Row/column order.
     * @param uplo (in)       The triangle in matrix  C being referenced.
     * @param trans (in)      How matrix  A is to be transposed.
     * @param N (in)          Number of rows and columns in matrix  C.
     * @param K (in)          Number of columns of the matrix  A if it is not
     *                       transposed, and number of rows otherwise.
     * @param alpha (in)      The factor of matrix  A.
     * @param A (in)          Buffer object storing the matrix  A.
     * @param offa (in)       Offset in number of elements for the first element in matrix  A.
     * @param lda (in)        Leading dimension of matrix  A. It cannot be
     *                       less than  K if  A is
     *                       in the row-major format, and less than  N
     *                       otherwise. Vice-versa for transpose case.
     * @param B (in)          Buffer object storing the matrix  B.
     * @param offb (in)       Offset in number of elements for the first element in matrix  B.
     * @param ldb (in)        Leading dimension of matrix  B. It cannot be
     *                       less than  K if B is
     *                       in the row-major format, and less than  N
     *                       otherwise. Vice-versa for transpose case.
     * @param beta (in)       The factor of the matrix  C.
     * @param C (out)         Buffer object storing matrix  C.
     * @param offc (in)       Offset in number of elements for the first element in matrix  C.
     * @param ldc (in)        Leading dimension of matric  C. It cannot be less
     *                       than  N.
     * @param numCommandQueues (in)    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param commandQueues (in)       OpenCL command queues.
     * @param numEventsInWaitList (in) Number of events in the event wait list.
     * @param eventWaitList (in)       Event wait list.
     * @param events (in)     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   -  clblasSuccess on success;
     *   -  clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCher2k() function otherwise.
     *
     */
    public static int clblasZher2k(
        int order, 
        int uplo, 
        int trans, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        double beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasZher2kNative(order, uplo, trans, N, K, alpha, A, offa, lda, B, offb, ldb, beta, C, offc, ldc, numCommandQueues, commandQueues, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasZher2kNative(
        int order, 
        int uplo, 
        int trans, 
        long N, 
        long K, 
        double[] alpha, 
        cl_mem A, 
        long offa, 
        long lda, 
        cl_mem B, 
        long offb, 
        long ldb, 
        double beta, 
        cl_mem C, 
        long offc, 
        long ldc, 
        int numCommandQueues, 
        cl_command_queue[] commandQueues, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Helper function to compute leading dimension and size of a matrix.
     *
     * @param order matrix (in) ordering
     * @param rows  number (in) of rows
     * @param columns   number (in) of column
     * @param elemsize  element (in) size
     * @param padding   additional (in) padding on the leading dimension
     * @param ld    if (out) non-NULL *ld is filled with the leading dimension
     *          in elements
     * @param fullsize  if (out) non-NULL *fullsize is filled with the byte size
     *
     * @return
     *   -  clblasSuccess for success
     *   -  clblasInvalidValue if:
     *   -  elementsize is 0
     *   -  row and  colums are both equal to 0
     */
    public static int clblasMatrixSizeInfo(
        int order, 
        long rows, 
        long columns, 
        long elemsize, 
        long padding, 
        long[] ld, 
        long[] fullsize)
    {
        return checkResult(clblasMatrixSizeInfoNative(order, rows, columns, elemsize, padding, ld, fullsize));
    }
    private static native int clblasMatrixSizeInfoNative(
        int order, 
        long rows, 
        long columns, 
        long elemsize, 
        long padding, 
        long[] ld, 
        long[] fullsize);


    /**
     * Allocates matrix on device and computes ld and size.
     *
     * @param context   OpenCL (in) context
     * @param order Row/column (in) order.
     * @param rows  number (in) of rows
     * @param columns   number (in) of columns
     * @param elemsize  element (in) size
     * @param padding   additional (in) padding on the leading dimension
     * @param ld    if (out) non-NULL *ld is filled with the leading dimension
     *          in elements
     * @param fullsize  if (out) non-NULL *fullsize is filled with the byte size
     * @param err   Error (in) code (see  clCreateBuffer() )
     * 
     * @return
     *   - OpenCL memory object of the allocated matrix
     */
    public static cl_mem clblasCreateMatrix(
        cl_context context, 
        int order, 
        long rows, 
        long columns, 
        long elemsize, 
        long padding, 
        long[] ld, 
        long[] fullsize, 
        int[] err)
    {
        return checkResult(clblasCreateMatrixNative(context, order, rows, columns, elemsize, padding, ld, fullsize, err));
    }
    private static native cl_mem clblasCreateMatrixNative(
        cl_context context, 
        int order, 
        long rows, 
        long columns, 
        long elemsize, 
        long padding, 
        long[] ld, 
        long[] fullsize, 
        int[] err);


    /**
     * Allocates matrix on device with specified size and ld and computes its size.
     *
     * @param context   OpenCL (in) context
     * @param order Row/column (in) order.
     * @param rows  number (in) of rows
     * @param columns   number (in) of columns
     * @param elemsize  element (in) size
     * @param padding   additional (in) padding on the leading dimension
     * @param ld    the (out) length of the leading dimensions. It cannot
     *                      be less than  columns when the  order parameter is set to
     *                       clblasRowMajor,\n or less than  rows when the
     *                      parameter is set to  clblasColumnMajor.
     * @param fullsize  if (out) non-NULL *fullsize is filled with the byte size
     * @param err   Error (in) code (see  clCreateBuffer() )
     * 
     * @return
     *   - OpenCL memory object of the allocated matrix
     */
    public static cl_mem clblasCreateMatrixWithLd(
        cl_context context, 
        int order, 
        long rows, 
        long columns, 
        long elemsize, 
        long ld, 
        long[] fullsize, 
        int[] err)
    {
        return checkResult(clblasCreateMatrixWithLdNative(context, order, rows, columns, elemsize, ld, fullsize, err));
    }
    private static native cl_mem clblasCreateMatrixWithLdNative(
        cl_context context, 
        int order, 
        long rows, 
        long columns, 
        long elemsize, 
        long ld, 
        long[] fullsize, 
        int[] err);


    /**
     * Allocates matrix on device and initialize from existing similar matrix.
     *    on host. See  clblasCreateMatrixBuffer().
     *
     * @param ld    leading (in) dimension in elements
     * @param host (in)     base address of host matrix data
     * @param off_host (in)     host matrix offset in elements
     * @param ld_host (in)  leading dimension of host matrix in elements
     * @param command_queue (in)        specifies the OpenCL queue
     * @param numEventsInWaitList (in)  specifies the number of OpenCL events
     *                          to wait for
     * @param eventWaitList (in)        specifies the list of OpenCL events to
     *                  wait for
     *                  
     * @return
     *   - OpenCL memory object of the allocated matrix
     */
    public static cl_mem clblasCreateMatrixFromHost(
        cl_context context, 
        int order, 
        long rows, 
        long columns, 
        long elemsize, 
        long ld, 
        Pointer host, 
        long off_host, 
        long ld_host, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        int[] err)
    {
        return checkResult(clblasCreateMatrixFromHostNative(context, order, rows, columns, elemsize, ld, host, off_host, ld_host, command_queue, numEventsInWaitList, eventWaitList, err));
    }
    private static native cl_mem clblasCreateMatrixFromHostNative(
        cl_context context, 
        int order, 
        long rows, 
        long columns, 
        long elemsize, 
        long ld, 
        Pointer host, 
        long off_host, 
        long ld_host, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        int[] err);


    /**
     * Copies synchronously a sub-matrix from host (A) to device (B)..
     * 
     * @param order         matrix (in) ordering
     * @param element_size      element (in) size
     * @param A             specifies (in) the source matrix on the host
     * @param offA          specifies (in) the offset of matrix A in
     *                  elements
     * @param ldA           specifies (in) the leading dimension of
     *                  matrix A in elements
     * @param nrA           specifies (in) the number of rows of A
     *                  in elements
     * @param ncA           specifies (in) the number of columns of A
     *                  in elements
     * @param xA            specifies (in) the top-left x position to
     *                  copy from A
     * @param yA            specifies (in) the top-left y position to
     *                  copy from A
     * @param B             specifies (in) the destination matrix on the
     *                  device
     * @param offB          specifies (in) the offset of matrix B in
     *                  elements
     * @param ldB (in)          specifies the leading dimension of
     *                  matrix B in bytes
     * @param nrB (in)          specifies the number of rows of B
     *                  in elements
     * @param ncB (in)          specifies the number of columns of B
     *                  in elements
     * @param xB (in)           specifies the top-left x position to
     *                  copy from B
     * @param yB (in)           specifies the top-left y position to
     *                  copy from B
     * @param nx (in)           specifies the number of elements to
     *                  copy according to the x dimension (rows)
     * @param ny (in)           specifies the number of elements to
     *                  copy according to the y dimension 
     *                  (columns)
     * @param command_queue (in)        specifies the OpenCL queue
     * @param numEventsInWaitList (in)  specifies the number of OpenCL events
     *                          to wait for
     * @param eventWaitList (in)        specifies the list of OpenCL events to
     *                  wait for
     *
     * @return
     *   -  clblasSuccess for success
     *   -  clblasInvalidValue if:
     *  -  xA +  offA +  nx is superior to number of columns of A
     *      -  xB +  offB +  nx is superior to number of columns of B
     *      -  yA +  ny is superior to number of rows of A
     *      -  yB +  ny is superior to number of rows of B
     */
    public static int clblasWriteSubMatrix(
        int order, 
        long element_size, 
        Pointer A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasWriteSubMatrixNative(order, element_size, A, offA, ldA, nrA, ncA, xA, yA, B, offB, ldB, nrB, ncB, xB, yB, nx, ny, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasWriteSubMatrixNative(
        int order, 
        long element_size, 
        Pointer A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a sub-matrix from host (A) to device (B). .
     *    See  clblasWriteSubMatrix().
     *
     * @param event (out)   Event objects per each command queue that identify a
     *          particular kernel execution instance.
     */
    public static int clblasWriteSubMatrixAsync(
        int order, 
        long element_size, 
        Pointer A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event)
    {
        return checkResult(clblasWriteSubMatrixAsyncNative(order, element_size, A, offA, ldA, nrA, ncA, xA, yA, B, offB, ldB, nrB, ncB, xB, yB, nx, ny, command_queue, numEventsInWaitList, eventWaitList, event));
    }
    private static native int clblasWriteSubMatrixAsyncNative(
        int order, 
        long element_size, 
        Pointer A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event);


    /**
     * Copies a sub-matrix from device (A) to host (B). .
     *    See  clblasWriteSubMatrix().
     * 
     * @param A     specifies (in) the source matrix on the device
     * @param B     specifies (in) the destination matrix on the host
     *
     * @return
     *   - see  clblasWriteSubMatrix()
     */
    public static int clblasReadSubMatrix(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        Pointer B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasReadSubMatrixNative(order, element_size, A, offA, ldA, nrA, ncA, xA, yA, B, offB, ldB, nrB, ncB, xB, yB, nx, ny, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasReadSubMatrixNative(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        Pointer B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a sub-matrix from device (A) to host (B). .
     *    See  clblasReadSubMatrix() and  clblasWriteSubMatrixAsync().
     */
    public static int clblasReadSubMatrixAsync(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        Pointer B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event)
    {
        return checkResult(clblasReadSubMatrixAsyncNative(order, element_size, A, offA, ldA, nrA, ncA, xA, yA, B, offB, ldB, nrB, ncB, xB, yB, nx, ny, command_queue, numEventsInWaitList, eventWaitList, event));
    }
    private static native int clblasReadSubMatrixAsyncNative(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        Pointer B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event);


    /**
     * Copies a sub-matrix from device (A) to device (B). .
     *    See  clblasWriteSubMatrix().
     * 
     * @param A     specifies (in) the source matrix on the device
     * @param B     specifies (in) the destination matrix on the device
     *
     * @return
     *   - see  clblasWriteSubMatrix()
     */
    public static int clblasCopySubMatrix(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasCopySubMatrixNative(order, element_size, A, offA, ldA, nrA, ncA, xA, yA, B, offB, ldB, nrB, ncB, xB, yB, nx, ny, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasCopySubMatrixNative(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a sub-matrix from device (A) to device (B). .
     *        See  clblasCopySubMatrix() and  clblasWriteSubMatrixAsync().
     */
    public static int clblasCopySubMatrixAsync(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event)
    {
        return checkResult(clblasCopySubMatrixAsyncNative(order, element_size, A, offA, ldA, nrA, ncA, xA, yA, B, offB, ldB, nrB, ncB, xB, yB, nx, ny, command_queue, numEventsInWaitList, eventWaitList, event));
    }
    private static native int clblasCopySubMatrixAsyncNative(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        long nrB, 
        long ncB, 
        long xB, 
        long yB, 
        long nx, 
        long ny, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event);


    /**
     * Copies synchronously a vector from host (A) to device (B). .
     *    See  clblasWriteSubMatrix().
     * 
     * @param A     specifies (in) the source vector on the host
     * @param B     specifies (in) the destination vector on the device
     *
     * @return
     *   - see  clblasWriteSubMatrix()
     */
    public static int clblasWriteVector(
        long nb_elem, 
        long element_size, 
        Pointer A, 
        long offA, 
        cl_mem B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasWriteVectorNative(nb_elem, element_size, A, offA, B, offB, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasWriteVectorNative(
        long nb_elem, 
        long element_size, 
        Pointer A, 
        long offA, 
        cl_mem B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a vector from host (A) to device (B). .
     *    See  clblasWriteVector() and  clblasWriteSubMatrixAsync().
     */
    public static int clblasWriteVectorAsync(
        long nb_elem, 
        long element_size, 
        Pointer A, 
        long offA, 
        cl_mem B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasWriteVectorAsyncNative(nb_elem, element_size, A, offA, B, offB, command_queue, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasWriteVectorAsyncNative(
        long nb_elem, 
        long element_size, 
        Pointer A, 
        long offA, 
        cl_mem B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Copies synchronously a vector from device (A) to host (B). .
     *    See  clblasReadSubMatrix().
     * 
     * @param A     specifies (in) the source vector on the device
     * @param B     specifies (in) the destination vector on the host
     *
     * @return
     *   - see  clblasReadSubMatrix()
     */
    public static int clblasReadVector(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        Pointer B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasReadVectorNative(nb_elem, element_size, A, offA, B, offB, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasReadVectorNative(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        Pointer B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a vector from device (A) to host (B). .
     *    See  clblasReadVector() and  clblasWriteSubMatrixAsync().
     */
    public static int clblasReadVectorAsync(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        Pointer B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasReadVectorAsyncNative(nb_elem, element_size, A, offA, B, offB, command_queue, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasReadVectorAsyncNative(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        Pointer B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Copies synchronously a vector from device (A) to device (B). .
     *    See  clblasCopySubMatrix().
     * 
     * @param A     specifies (in) the source vector on the device
     * @param B     specifies (in) the destination vector on the device
     *
     * @return
     *   - see  clblasCopySubMatrix()
     */
    public static int clblasCopyVector(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        cl_mem B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasCopyVectorNative(nb_elem, element_size, A, offA, B, offB, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasCopyVectorNative(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        cl_mem B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a vector from device (A) to device (B). .
     *    See  clblasCopyVector() and  clblasWriteSubMatrixAsync().
     */
    public static int clblasCopyVectorAsync(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        cl_mem B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCopyVectorAsyncNative(nb_elem, element_size, A, offA, B, offB, command_queue, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCopyVectorAsyncNative(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        cl_mem B, 
        long offB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Copies synchronously a whole matrix from host (A) to device (B). .
     *        See  clblasWriteSubMatrix().
     * 
     * @param A     specifies (in) the source matrix on the host
     * @param B     specifies (in) the destination matrix on the device
     *
     * @return
     *   - see  clblasWriteSubMatrix()
     */
    public static int clblasWriteMatrix(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        Pointer A, 
        long offA, 
        long ldA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasWriteMatrixNative(order, sx, sy, element_size, A, offA, ldA, B, offB, ldB, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasWriteMatrixNative(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        Pointer A, 
        long offA, 
        long ldA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a vector from host (A) to device (B). .
     *        See  clblasWriteMatrix() and  clblasWriteSubMatrixAsync().
     */
    public static int clblasWriteMatrixAsync(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        Pointer A, 
        long offA, 
        long ldA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasWriteMatrixAsyncNative(order, sx, sy, element_size, A, offA, ldA, B, offB, ldB, command_queue, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasWriteMatrixAsyncNative(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        Pointer A, 
        long offA, 
        long ldA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Copies synchronously a whole matrix from device (A) to host (B). .
     *    See  clblasReadSubMatrix().
     * 
     * @param A     specifies (in) the source vector on the device
     * @param B     specifies (in) the destination vector on the host
     *
     * @return
     *   - see  clblasReadSubMatrix()
     */
    public static int clblasReadMatrix(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        Pointer B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasReadMatrixNative(order, sx, sy, element_size, A, offA, ldA, B, offB, ldB, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasReadMatrixNative(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        Pointer B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a vector from device (A) to host (B). .
     *        See  clblasReadMatrix() and  clblasWriteSubMatrixAsync().
     */
    public static int clblasReadMatrixAsync(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        Pointer B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasReadMatrixAsyncNative(order, sx, sy, element_size, A, offA, ldA, B, offB, ldB, command_queue, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasReadMatrixAsyncNative(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        Pointer B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Copies synchronously a whole matrix from device (A) to device (B). .
     *    See  clblasCopySubMatrix().
     * 
     * @param A     specifies (in) the source matrix on the device
     * @param B     specifies (in) the destination matrix on the device
     *
     * @return
     *   - see  clblasCopySubMatrix()
     */
    public static int clblasCopyMatrix(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasCopyMatrixNative(order, sx, sy, element_size, A, offA, ldA, B, offB, ldB, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasCopyMatrixNative(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Copies asynchronously a vector from device (A) to device (B). .
     *        See  clblasCopyMatrix() and  clblasWriteSubMatrixAsync().
     */
    public static int clblasCopyMatrixAsync(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events)
    {
        return checkResult(clblasCopyMatrixAsyncNative(order, sx, sy, element_size, A, offA, ldA, B, offB, ldB, command_queue, numEventsInWaitList, eventWaitList, events));
    }
    private static native int clblasCopyMatrixAsyncNative(
        int order, 
        long sx, 
        long sy, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        cl_mem B, 
        long offB, 
        long ldB, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] events);


    /**
     * Fill synchronously a vector with a pattern of a size element_size_bytes.
     * 
     * @param nb_elem (in)             specifies the number of element in buffer A
     * @param element_size (in)        specifies the size of one element of A. Supported sizes correspond
     *                                element size used in clBLAS (1,2,4,8,16)
     * @param A      (in)          specifies the source vector on the device
     * @param offA (in)                specifies the offset of matrix A in
     *                elements
     * @param pattern (in)             specifies the host address of the pattern to fill with (element_size_bytes)
     * @param command_queue (in)      specifies the OpenCL queue
     * @param numEventsInWaitList (in) specifies the number of OpenCL events
     *                    to wait for
     * @param eventWaitList (in)      specifies the list of OpenCL events to
     *                wait for
     * @return
     *   - see  clblasWriteSubMatrix()
     */
    public static int clblasFillVector(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        Pointer host, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasFillVectorNative(nb_elem, element_size, A, offA, host, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasFillVectorNative(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        Pointer host, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Fill asynchronously a vector with a pattern of a size element_size_bytes.
     *    See  clblasFillVector().
     */
    public static int clblasFillVectorAsync(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        Pointer pattern, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event)
    {
        return checkResult(clblasFillVectorAsyncNative(nb_elem, element_size, A, offA, pattern, command_queue, numEventsInWaitList, eventWaitList, event));
    }
    private static native int clblasFillVectorAsyncNative(
        long nb_elem, 
        long element_size, 
        cl_mem A, 
        long offA, 
        Pointer pattern, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event);


    /**
     * Fill synchronously a matrix with a pattern of a size element_size_bytes.
     * 
     * @param order (in)               specifies the matrix order
     * @param element_size (in)        specifies the size of one element of A. Supported sizes correspond
     *                                element size used in clBLAS (1,2,4,8,16)
     * @param A      (in)          specifies the source vector on the device
     * @param offA (in)                specifies the offset of matrix A in
     * @param ldA (in)                 specifies the leading dimension of A
     * @param nrA (in)                 specifies the number of row in A
     * @param ncA (in)                 specifies the number of column in A
     * @param pattern (in)             specifies the host address of the pattern to fill with (element_size_bytes)
     * @param command_queue (in)      specifies the OpenCL queue
     * @param numEventsInWaitList (in) specifies the number of OpenCL events to wait for
     * @param eventWaitList (in)      specifies the list of OpenCL events to wait for
     * @return
     *   - see  clblasWriteSubMatrix()
     */
    public static int clblasFillMatrix(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        Pointer pattern, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasFillMatrixNative(order, element_size, A, offA, ldA, nrA, ncA, pattern, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasFillMatrixNative(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        Pointer pattern, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Partially fill a sub-matrix with a pattern of a size element_size_bytes .
     *        
     * 
     * @param order (in)               specifies the matrix order
     * @param element_size (in)        specifies the size of one element of A. Supported values
     *                                are to element sizes used in clBLAS - that is 1, 2, 4, 8 or 16 
     * @param offA (in)                specifies the offset of matrix A in elements
     * @param ldA (in)                 specifies the leading dimension of A in elements
     * @param nrA        (in)  specifies the number of rows of A
     *                in elements
     * @param ncA        (in)  specifies the number of columns of A
     *                in elements
     * @param xA         (in)  specifies the top-left x position to
     *                copy from A
     * @param yA         (in)  specifies the top-left y position to
     *                copy from A
     * @param nx (in)         specifies the number of elements to
     *                copy according to the x dimension (rows)
     * @param ny (in)         specifies the number of elements to
     *                copy according to the y dimension 
     *                (columns)
     * @param pattern (in)             specifies the host address of the pattern to fill with (element_size_bytes)
     * @param command_queue (in)      specifies the OpenCL queue
     * @param numEventsInWaitList (in) specifies the number of OpenCL events to wait for
     * @param eventWaitList (in)      specifies the list of OpenCL events to wait for
     * @return
     *   - see  clblasWriteSubMatrix()
     */
    public static int clblasFillSubMatrix(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        long nx, 
        long ny, 
        Pointer pattern, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList)
    {
        return checkResult(clblasFillSubMatrixNative(order, element_size, A, offA, ldA, nrA, ncA, xA, yA, nx, ny, pattern, command_queue, numEventsInWaitList, eventWaitList));
    }
    private static native int clblasFillSubMatrixNative(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long nrA, 
        long ncA, 
        long xA, 
        long yA, 
        long nx, 
        long ny, 
        Pointer pattern, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList);


    /**
     * Asynchronous asynchronously fill a sub-matrix with a pattern of a size element_size_bytes  .
     *    See  clblasFillSubMatrix().
     */
    public static int clblasFillSubMatrixAsync(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long sxA, 
        long syA, 
        int xA, 
        int yA, 
        long nx, 
        long ny, 
        Pointer host, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event)
    {
        return checkResult(clblasFillSubMatrixAsyncNative(order, element_size, A, offA, ldA, sxA, syA, xA, yA, nx, ny, host, command_queue, numEventsInWaitList, eventWaitList, event));
    }
    private static native int clblasFillSubMatrixAsyncNative(
        int order, 
        long element_size, 
        cl_mem A, 
        long offA, 
        long ldA, 
        long sxA, 
        long syA, 
        int xA, 
        int yA, 
        long nx, 
        long ny, 
        Pointer host, 
        cl_command_queue command_queue, 
        int numEventsInWaitList, 
        cl_event[] eventWaitList, 
        cl_event[] event);


    /**
     * Private constructor to prevent instantiation
     */
    private CLBLAS()
    {
        // Private constructor to prevent instantiation
    }
}

