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
    
    public static final int JOCL_BLAS_STATUS_INTERNAL_ERROR = -32786;

    
    
    
    /**@}*/
    /**
     * @defgroup VERSION Version information
     */
    /**@{*/
    /**
     * <pre>
     * Get the clblas library version info.
     *
     * @param (out) major        Location to store library's major version.
     * @param (out) minor        Location to store library's minor version.
     * @param (out) patch        Location to store library's patch version.
     *
     * @returns always \b clblasSuccess.
     *
     * @ingroup VERSION
     * </pre>
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


    /**@}*/
    /**
     * @defgroup INIT Initialize library
     */
    /**@{*/
    /**
     * <pre>
     * Initialize the clblas library.
     *
     * Must be called before any other clblas API function is invoked.
     * @note This function is not thread-safe.
     *
     * @return
     *   - \b clblasSucces on success;
     *   - \b clblasOutOfHostMemory if there is not enough of memory to allocate
     *     library's internal structures;
     *   - \b clblasOutOfResources in case of requested resources scarcity.
     *
     * @ingroup INIT
     * </pre>
     */
    public static int clblasSetup()
    {
        return checkResult(clblasSetupNative());
    }
    private static native int clblasSetupNative();


    /**
     * <pre>
     * Finalize the usage of the clblas library.
     *
     * Frees all memory allocated for different computational kernel and other
     * internal data.
     * @note This function is not thread-safe.
     *
     * @ingroup INIT
     * </pre>
     */
    public static void clblasTeardown()
    {
        clblasTeardownNative();
    }
    private static native void clblasTeardownNative();


    /**@}*/
    /**
     * <pre>
     * @defgroup BLAS1 BLAS-1 functions
     *
     * The Level 1 Basic Linear Algebra Subprograms are functions that perform
     * vector-vector operations.
     * </pre>
     */
    /**@{*/
    /**@}*/
    /**
     * <pre>
     * @defgroup SWAP SWAP  - Swap elements from 2 vectors
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * interchanges two vectors of float.
     *
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SWAP
     * </pre>
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
     * <pre>
     * @example example_sswap.c
     * Example of how to use the @ref clblasSswap function.
     * </pre>
     */
    /**
     * <pre>
     * interchanges two vectors of double.
     *
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSswap() function otherwise.
     *
     * @ingroup SWAP
     * </pre>
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
     * <pre>
     * interchanges two vectors of complex-float elements.
     *
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSwap() function otherwise.
     *
     * @ingroup SWAP
     * </pre>
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
     * <pre>
     * interchanges two vectors of double-complex elements.
     *
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasDwap() function otherwise.
     *
     * @ingroup SWAP
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SCAL SCAL  - Scales a vector by a constant
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Scales a float vector by a float constant
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - \b incx zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SCAL
     * </pre>
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
     * <pre>
     * @example example_sscal.c
     * Example of how to use the @ref clblasSscal function.
     * </pre>
     */
    /**
     * <pre>
     * Scales a double vector by a double constant
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSscal() function otherwise.
     *
     * @ingroup SCAL
     * </pre>
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
     * <pre>
     * Scales a complex-float vector by a complex-float constant
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSscal() function otherwise.
     *
     * @ingroup SCAL
     * </pre>
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
     * <pre>
     * Scales a complex-double vector by a complex-double constant
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasDscal() function otherwise.
     *
     * @ingroup SCAL
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SSCAL SSCAL  - Scales a complex vector by a real constant
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Scales a complex-float vector by a float constant
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - \b incx zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SSCAL
     * </pre>
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
     * <pre>
     * @example example_csscal.c
     * Example of how to use the @ref clblasCsscal function.
     * </pre>
     */
    /**
     * <pre>
     * Scales a complex-double vector by a double constant
     *
     *   - \f$ X \leftarrow \alpha X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasCsscal() function otherwise.
     *
     * @ingroup SSCAL
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup COPY COPY  - Copies elements from vector X to vector Y
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Copies float elements from vector X to vector Y
     *
     *   - \f$ Y \leftarrow X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup COPY
     * </pre>
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
     * <pre>
     * @example example_scopy.c
     * Example of how to use the @ref clblasScopy function.
     * </pre>
     */
    /**
     * <pre>
     * Copies double elements from vector X to vector Y
     *
     *   - \f$ Y \leftarrow X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasScopy() function otherwise.
     *
     * @ingroup COPY
     * </pre>
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
     * <pre>
     * Copies complex-float elements from vector X to vector Y
     *
     *   - \f$ Y \leftarrow X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasScopy() function otherwise.
     *
     * @ingroup COPY
     * </pre>
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
     * <pre>
     * Copies complex-double elements from vector X to vector Y
     *
     *   - \f$ Y \leftarrow X \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasDcopy() function otherwise.
     *
     * @ingroup COPY
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup AXPY AXPY  - Scale X and add to Y
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Scale vector X of float elements and add to Y
     *
     *   - \f$ Y \leftarrow \alpha X + Y \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup AXPY
     * </pre>
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
     * <pre>
     * @example example_saxpy.c
     * Example of how to use the @ref clblasSaxpy function.
     * </pre>
     */
    /**
     * <pre>
     * Scale vector X of double elements and add to Y
     *
     *   - \f$ Y \leftarrow \alpha X + Y \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSaxpy() function otherwise.
     *
     * @ingroup AXPY
     * </pre>
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
     * <pre>
     * Scale vector X of complex-float elements and add to Y
     *
     *   - \f$ Y \leftarrow \alpha X + Y \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSaxpy() function otherwise.
     *
     * @ingroup AXPY
     * </pre>
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
     * <pre>
     * Scale vector X of double-complex elements and add to Y
     *
     *   - \f$ Y \leftarrow \alpha X + Y \f$
     *
     * @param (in) N         Number of elements in vector \b X.
     * @param (in) alpha     The constant factor for vector \b X.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasDaxpy() function otherwise.
     *
     * @ingroup AXPY
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup DOT DOT  - Dot product of two vectors
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * dot product of two vectors containing float elements
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) dotProduct   Buffer object that will contain the dot-product value
     * @param (in) offDP         Offset to dot-product in \b dotProduct buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) Y             Buffer object storing the vector \b Y.
     * @param (in) offy          Offset of first element of vector \b Y in buffer object.
     *                          Counted in elements.
     * @param (in) incy          Increment for the elements of \b Y. Must not be zero.
     * @param (in) scratchBuff  Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b X, \b Y or \b dotProduct object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup DOT
     * </pre>
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
     * <pre>
     * @example example_sdot.c
     * Example of how to use the @ref clblasSdot function.
     * </pre>
     */
    /**
     * <pre>
     * dot product of two vectors containing double elements
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) dotProduct   Buffer object that will contain the dot-product value
     * @param (in) offDP         Offset to dot-product in \b dotProduct buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) Y             Buffer object storing the vector \b Y.
     * @param (in) offy          Offset of first element of vector \b Y in buffer object.
     *                          Counted in elements.
     * @param (in) incy          Increment for the elements of \b Y. Must not be zero.
     * @param (in) scratchBuff  Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     * @ingroup DOT
     * </pre>
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
     * <pre>
     * dot product of two vectors containing float-complex elements
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) dotProduct   Buffer object that will contain the dot-product value
     * @param (in) offDP         Offset to dot-product in \b dotProduct buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) Y             Buffer object storing the vector \b Y.
     * @param (in) offy          Offset of first element of vector \b Y in buffer object.
     *                          Counted in elements.
     * @param (in) incy          Increment for the elements of \b Y. Must not be zero.
     * @param (in) scratchBuff   Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     * @ingroup DOT
     * </pre>
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
     * <pre>
     * dot product of two vectors containing double-complex elements
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) dotProduct   Buffer object that will contain the dot-product value
     * @param (in) offDP         Offset to dot-product in \b dotProduct buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) Y             Buffer object storing the vector \b Y.
     * @param (in) offy          Offset of first element of vector \b Y in buffer object.
     *                          Counted in elements.
     * @param (in) incy          Increment for the elements of \b Y. Must not be zero.
     * @param (in) scratchBuff   Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     * @ingroup DOT
     * </pre>
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
     * <pre>
     * dot product of two vectors containing float-complex elements conjugating the first vector
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) dotProduct   Buffer object that will contain the dot-product value
     * @param (in) offDP         Offset to dot-product in \b dotProduct buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) Y             Buffer object storing the vector \b Y.
     * @param (in) offy          Offset of first element of vector \b Y in buffer object.
     *                          Counted in elements.
     * @param (in) incy          Increment for the elements of \b Y. Must not be zero.
     * @param (in) scratchBuff   Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     * @ingroup DOT
     * </pre>
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
     * <pre>
     * dot product of two vectors containing double-complex elements conjugating the first vector
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) dotProduct   Buffer object that will contain the dot-product value
     * @param (in) offDP         Offset to dot-product in \b dotProduct buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) Y             Buffer object storing the vector \b Y.
     * @param (in) offy          Offset of first element of vector \b Y in buffer object.
     *                          Counted in elements.
     * @param (in) incy          Increment for the elements of \b Y. Must not be zero.
     * @param (in) scratchBuff   Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSdot() function otherwise.
     *
     * @ingroup DOT
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup ROTG ROTG  - Constructs givens plane rotation
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * construct givens plane rotation on float elements
     *
     * @param (out) SA           Buffer object that contains SA
     * @param (in) offSA         Offset to SA in \b SA buffer object.
     *                          Counted in elements.
     * @param (out) SB           Buffer object that contains SB
     * @param (in) offSB         Offset to SB in \b SB buffer object.
     *                          Counted in elements.
     * @param (out) C            Buffer object that contains C
     * @param (in) offC          Offset to C in \b C buffer object.
     *                          Counted in elements.
     * @param (out) S            Buffer object that contains S
     * @param (in) offS          Offset to S in \b S buffer object.
     *                          Counted in elements.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidMemObject if either \b SA, \b SB, \b C or \b S object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup ROTG
     * </pre>
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
     * <pre>
     * @example example_srotg.c
     * Example of how to use the @ref clblasSrotg function.
     * </pre>
     */
    /**
     * <pre>
     * construct givens plane rotation on double elements
     *
     * @param (out) DA           Buffer object that contains DA
     * @param (in) offDA         Offset to DA in \b DA buffer object.
     *                          Counted in elements.
     * @param (out) DB           Buffer object that contains DB
     * @param (in) offDB         Offset to DB in \b DB buffer object.
     *                          Counted in elements.
     * @param (out) C            Buffer object that contains C
     * @param (in) offC          Offset to C in \b C buffer object.
     *                          Counted in elements.
     * @param (out) S            Buffer object that contains S
     * @param (in) offS          Offset to S in \b S buffer object.
     *                          Counted in elements.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrotg() function otherwise.
     *
     * @ingroup ROTG
     * </pre>
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
     * <pre>
     * construct givens plane rotation on float-complex elements
     *
     * @param (out) CA           Buffer object that contains CA
     * @param (in) offCA         Offset to CA in \b CA buffer object.
     *                          Counted in elements.
     * @param (out) CB           Buffer object that contains CB
     * @param (in) offCB         Offset to CB in \b CB buffer object.
     *                          Counted in elements.
     * @param (out) C            Buffer object that contains C. C is real.
     * @param (in) offC          Offset to C in \b C buffer object.
     *                          Counted in elements.
     * @param (out) S            Buffer object that contains S
     * @param (in) offS          Offset to S in \b S buffer object.
     *                          Counted in elements.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSrotg() function otherwise.
     *
     * @ingroup ROTG
     * </pre>
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
     * <pre>
     * construct givens plane rotation on double-complex elements
     *
     * @param (out) CA           Buffer object that contains CA
     * @param (in) offCA         Offset to CA in \b CA buffer object.
     *                          Counted in elements.
     * @param (out) CB           Buffer object that contains CB
     * @param (in) offCB         Offset to CB in \b CB buffer object.
     *                          Counted in elements.
     * @param (out) C            Buffer object that contains C. C is real.
     * @param (in) offC          Offset to C in \b C buffer object.
     *                          Counted in elements.
     * @param (out) S            Buffer object that contains S
     * @param (in) offS          Offset to S in \b S buffer object.
     *                          Counted in elements.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasDrotg() function otherwise.
     *
     * @ingroup ROTG
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup ROTMG ROTMG  - Constructs the modified givens rotation
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * construct the modified givens rotation on float elements
     *
     * @param (out) SD1          Buffer object that contains SD1
     * @param (in) offSD1        Offset to SD1 in \b SD1 buffer object.
     *                          Counted in elements.
     * @param (out) SD2          Buffer object that contains SD2
     * @param (in) offSD2        Offset to SD2 in \b SD2 buffer object.
     *                          Counted in elements.
     * @param (out) SX1          Buffer object that contains SX1
     * @param (in) offSX1        Offset to SX1 in \b SX1 buffer object.
     *                          Counted in elements.
     * @param (in) SY1           Buffer object that contains SY1
     * @param (in) offSY1        Offset to SY1 in \b SY1 buffer object.
     *                          Counted in elements.
     * @param (out) SPARAM       Buffer object that contains SPARAM array of minimum length 5
                                SPARAM(0) = SFLAG
                                SPARAM(1) = SH11
                                SPARAM(2) = SH21
                                SPARAM(3) = SH12
                                SPARAM(4) = SH22
    
     * @param (in) offSparam     Offset to SPARAM in \b SPARAM buffer object.
     *                          Counted in elements.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidMemObject if either \b SX1, \b SY1, \b SD1, \b SD2 or \b SPARAM object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup ROTMG
     * </pre>
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
     * <pre>
     * @example example_srotmg.c
     * Example of how to use the @ref clblasSrotmg function.
     * </pre>
     */
    /**
     * <pre>
     * construct the modified givens rotation on double elements
     *
     * @param (out) DD1          Buffer object that contains DD1
     * @param (in) offDD1        Offset to DD1 in \b DD1 buffer object.
     *                          Counted in elements.
     * @param (out) DD2          Buffer object that contains DD2
     * @param (in) offDD2        Offset to DD2 in \b DD2 buffer object.
     *                          Counted in elements.
     * @param (out) DX1          Buffer object that contains DX1
     * @param (in) offDX1        Offset to DX1 in \b DX1 buffer object.
     *                          Counted in elements.
     * @param (in) DY1           Buffer object that contains DY1
     * @param (in) offDY1        Offset to DY1 in \b DY1 buffer object.
     *                          Counted in elements.
     * @param (out) DPARAM       Buffer object that contains DPARAM array of minimum length 5
                                DPARAM(0) = DFLAG
                                DPARAM(1) = DH11
                                DPARAM(2) = DH21
                                DPARAM(3) = DH12
                                DPARAM(4) = DH22
    
     * @param (in) offDparam     Offset to DPARAM in \b DPARAM buffer object.
     *                          Counted in elements.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrotmg() function otherwise.
     *
     * @ingroup ROTMG
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup ROT ROT  - Apply givens rotation
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * applies a plane rotation for float elements
     *
     * @param (in) N         Number of elements in vector \b X and \b Y.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) C         C specifies the cosine, cos.
     * @param (in) S         S specifies the sine, sin.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup ROT
     * </pre>
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
     * <pre>
     * @example example_srot.c
     * Example of how to use the @ref clblasSrot function.
     * </pre>
     */
    /**
     * <pre>
     * applies a plane rotation for double elements
     *
     * @param (in) N         Number of elements in vector \b X and \b Y.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) C         C specifies the cosine, cos.
     * @param (in) S         S specifies the sine, sin.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrot() function otherwise.
     *
     * @ingroup ROT
     * </pre>
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
     * <pre>
     * applies a plane rotation for float-complex elements
     *
     * @param (in) N         Number of elements in vector \b X and \b Y.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) C         C specifies the cosine, cos. This number is real
     * @param (in) S         S specifies the sine, sin. This number is real
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSrot() function otherwise.
     *
     * @ingroup ROT
     * </pre>
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
     * <pre>
     * applies a plane rotation for double-complex elements
     *
     * @param (in) N         Number of elements in vector \b X and \b Y.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) C         C specifies the cosine, cos. This number is real
     * @param (in) S         S specifies the sine, sin. This number is real
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrot() function otherwise.
     *
     * @ingroup ROT
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup ROTM ROTM  - Apply modified givens rotation for points in the plane
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * modified givens rotation for float elements
     *
     * @param (in) N         Number of elements in vector \b X and \b Y.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) SPARAM    Buffer object that contains SPARAM array of minimum length 5
     *                      SPARAM(1)=SFLAG
     *                      SPARAM(2)=SH11
     *                      SPARAM(3)=SH21
     *                      SPARAM(4)=SH12
     *                      SPARAM(5)=SH22
     * @param (in) offSparam Offset of first element of array \b SPARAM in buffer object.
     *                      Counted in elements.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b X, \b Y or \b SPARAM object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup ROTM
     * </pre>
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
     * <pre>
     * @example example_srotm.c
     * Example of how to use the @ref clblasSrotm function.
     * </pre>
     */
    /**
     * <pre>
     * modified givens rotation for double elements
     *
     * @param (in) N         Number of elements in vector \b X and \b Y.
     * @param (out) X        Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) Y        Buffer object storing the vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) DPARAM    Buffer object that contains SPARAM array of minimum length 5
     *                      DPARAM(1)=DFLAG
     *                      DPARAM(2)=DH11
     *                      DPARAM(3)=DH21
     *                      DPARAM(4)=DH12
     *                      DPARAM(5)=DH22
     * @param (in) offDparam Offset of first element of array \b DPARAM in buffer object.
     *                      Counted in elements.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
    * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSrotm() function otherwise.
     *
     * @ingroup ROTM
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup NRM2 NRM2  - Euclidean norm of a vector
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * computes the euclidean norm of vector containing float elements
     *
     *  NRM2 = sqrt( X' * X )
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) NRM2         Buffer object that will contain the NRM2 value
     * @param (in) offNRM2       Offset to NRM2 value in \b NRM2 buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff  Temporary cl_mem scratch buffer object that can hold minimum of (2*N) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if any of \b X or \b NRM2 or \b scratchBuff object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup NRM2
     * </pre>
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
     * <pre>
     * @example example_snrm2.c
     * Example of how to use the @ref clblasSnrm2 function.
     * </pre>
     */
    /**
     * <pre>
     * computes the euclidean norm of vector containing double elements
     *
     *  NRM2 = sqrt( X' * X )
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) NRM2         Buffer object that will contain the NRM2 value
     * @param (in) offNRM2       Offset to NRM2 value in \b NRM2 buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff  Temporary cl_mem scratch buffer object that can hold minimum of (2*N) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSnrm2() function otherwise.
     *
     * @ingroup NRM2
     * </pre>
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
     * <pre>
     * computes the euclidean norm of vector containing float-complex elements
     *
     *  NRM2 = sqrt( X**H * X )
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) NRM2         Buffer object that will contain the NRM2 value.
     *                          Note that the answer of Scnrm2 is a real value.
     * @param (in) offNRM2       Offset to NRM2 value in \b NRM2 buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff  Temporary cl_mem scratch buffer object that can hold minimum of (2*N) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSnrm2() function otherwise.
     *
     * @ingroup NRM2
     * </pre>
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
     * <pre>
     * computes the euclidean norm of vector containing double-complex elements
     *
     *  NRM2 = sqrt( X**H * X )
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) NRM2         Buffer object that will contain the NRM2 value.
     *                          Note that the answer of Dznrm2 is a real value.
     * @param (in) offNRM2       Offset to NRM2 value in \b NRM2 buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff  Temporary cl_mem scratch buffer object that can hold minimum of (2*N) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSnrm2() function otherwise.
     *     executable.
     *
     * @ingroup NRM2
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup iAMAX iAMAX  - Index of max absolute value
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * index of max absolute value in a float array
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) iMax         Buffer object storing the index of first absolute max.
     *                          The index will be of type unsigned int
     * @param (in) offiMax       Offset for storing index in the buffer iMax
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff   Temprory cl_mem object to store intermediate results
                                It should be able to hold minimum of (2*N) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if any of \b iMax \b X or \b scratchBuff object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if the context, the passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup iAMAX
     * </pre>
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
     * <pre>
     * @example example_isamax.c
     * Example of how to use the @ref clblasiSamax function.
     * </pre>
     */
    /**
     * <pre>
     * index of max absolute value in a double array
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) iMax         Buffer object storing the index of first absolute max.
     *                          The index will be of type unsigned int
     * @param (in) offiMax       Offset for storing index in the buffer iMax
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff   Temprory cl_mem object to store intermediate results
                                It should be able to hold minimum of (2*N) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasiSamax() function otherwise.
     *
     * @ingroup iAMAX
     * </pre>
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
     * <pre>
     * index of max absolute value in a complex float array
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) iMax         Buffer object storing the index of first absolute max.
     *                          The index will be of type unsigned int
     * @param (in) offiMax       Offset for storing index in the buffer iMax
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff   Temprory cl_mem object to store intermediate results
                                It should be able to hold minimum of (2*N) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasiSamax() function otherwise.
     *
     * @ingroup iAMAX
     * </pre>
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
     * <pre>
     * index of max absolute value in a complex double array
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) iMax         Buffer object storing the index of first absolute max.
     *                          The index will be of type unsigned int
     * @param (in) offiMax       Offset for storing index in the buffer iMax
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff   Temprory cl_mem object to store intermediate results
                                It should be able to hold minimum of (2*N) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasiSamax() function otherwise.
     *
     * @ingroup iAMAX
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup ASUM ASUM  - Sum of absolute values
     * @ingroup BLAS1
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * absolute sum of values of a vector containing float elements
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) asum         Buffer object that will contain the absoule sum value
     * @param (in) offAsum       Offset to absolute sum in \b asum buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff   Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx is zero, or
     *     - the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if any of \b X or \b asum or \b scratchBuff object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup ASUM
     * </pre>
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
     * <pre>
     * @example example_sasum.c
     * Example of how to use the @ref clblasSasum function.
     * </pre>
     */
    /**
     * <pre>
     * absolute sum of values of a vector containing double elements
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) asum         Buffer object that will contain the absoulte sum value
     * @param (in) offAsum       Offset to absoule sum in \b asum buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff   Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSasum() function otherwise.
     *
     * @ingroup ASUM
     * </pre>
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
     * <pre>
     * absolute sum of values of a vector containing float-complex elements
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) asum         Buffer object that will contain the absolute sum value
     * @param (in) offAsum       Offset to absolute sum in \b asum buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff   Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - the same error codes as the clblasSasum() function otherwise.
     *
     * @ingroup ASUM
     * </pre>
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
     * <pre>
     * absolute sum of values of a vector containing double-complex elements
     *
     * @param (in) N             Number of elements in vector \b X.
     * @param (out) asum         Buffer object that will contain the absolute sum value
     * @param (in) offAsum       Offset to absolute sum in \b asum buffer object.
     *                          Counted in elements.
     * @param (in) X             Buffer object storing vector \b X.
     * @param (in) offx          Offset of first element of vector \b X in buffer object.
     *                          Counted in elements.
     * @param (in) incx          Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff   Temporary cl_mem scratch buffer object of minimum size N
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - the same error codes as the clblasSasum() function otherwise.
     *
     * @ingroup ASUM
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup BLAS2 BLAS-2 functions
     *
     * The Level 2 Basic Linear Algebra Subprograms are functions that perform
     * matrix-vector operations.
     * </pre>
     */
    /**@{*/
    /**@}*/
    /**
     * <pre>
     * @defgroup GEMV GEMV  - General matrix-Vector multiplication
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a general rectangular matrix and
     *        float elements. Extended version.
     *
     * Matrix-vector products:
     *   - \f$ y \leftarrow \alpha A x + \beta y \f$
     *   - \f$ y \leftarrow \alpha A^T x + \beta y \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in
     *                      the buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (in) x         Buffer object storing vector \b x.
     * @param (in) offx      Offset of first element of vector \b x in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b x. It cannot be zero.
     * @param (in) beta      The factor of the vector \b y.
     * @param (out) y        Buffer object storing the vector \b y.
     * @param (in) offy      Offset of first element of vector \b y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if \b offA exceeds the size of \b A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     * @ingroup GEMV
     * </pre>
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
     * <pre>
     * @example example_sgemv.c
     * This is an example of how to use the @ref clblasSgemvEx function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a general rectangular matrix and
     *        double elements. Extended version.
     *
     * Matrix-vector products:
     *   - \f$ y \leftarrow \alpha A x + \beta y \f$
     *   - \f$ y \leftarrow \alpha A^T x + \beta y \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of \b A in the buffer
     *                      object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For a detailed description,
     *                      see clblasSgemv().
     * @param (in) x         Buffer object storing vector \b x.
     * @param (in) offx      Offset of first element of vector \b x in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b x. It cannot be zero.
     * @param (in) beta      The factor of the vector \b y.
     * @param (out) y        Buffer object storing the vector \b y.
     * @param (in) offy      Offset of first element of vector \b y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - \b clblasInvalidValue if \b offA exceeds the size of \b A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     * @ingroup GEMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a general rectangular matrix and
     *        float complex elements. Extended version.
     *
     * Matrix-vector products:
     *   - \f$ y \leftarrow \alpha A x + \beta y \f$
     *   - \f$ y \leftarrow \alpha A^T x + \beta y \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in
     *                      the buffer object. Counted in elements
     * @param (in) lda       Leading dimension of matrix \b A. For a detailed description,
     *                      see clblasSgemv().
     * @param (in) x         Buffer object storing vector \b x.
     * @param (in) offx      Offset of first element of vector \b x in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b x. It cannot be zero.
     * @param (in) beta      The factor of the vector \b y.
     * @param (out) y        Buffer object storing the vector \b y.
     * @param (in) offy      Offset of first element of vector \b y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if \b offA exceeds the size of \b A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     * @ingroup GEMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a general rectangular matrix and
     *        double complex elements. Extended version.
     *
     * Matrix-vector products:
     *   - \f$ y \leftarrow \alpha A x + \beta y \f$
     *   - \f$ y \leftarrow \alpha A^T x + \beta y \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in
     *                      the buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For a detailed description,
     *                      see clblasSgemv().
     * @param (in) x         Buffer object storing vector \b x.
     * @param (in) offx      Offset of first element of vector \b x in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b x. It cannot be zero.
     * @param (in) beta      The factor of the vector \b y.
     * @param (out) y        Buffer object storing the vector \b y.
     * @param (in) offy      Offset of first element of vector \b y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support the
     *     floating point arithmetic with double precision;
     *   - \b clblasInvalidValue if \b offA exceeds the size of \b A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     * @ingroup GEMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SYMV SYMV  - Symmetric matrix-Vector multiplication
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a symmetric matrix and float elements.
     *
     *
     * Matrix-vector products:
     * - \f$ y \leftarrow \alpha A x + \beta y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in
     *                      the buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot less
     *                      than \b N.
     * @param (in) x         Buffer object storing vector \b x.
     * @param (in) offx      Offset of first element of vector \b x in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b x. It cannot be zero.
     * @param (in) beta      The factor of vector \b y.
     * @param (out) y        Buffer object storing vector \b y.
     * @param (in) offy      Offset of first element of vector \b y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if \b offA exceeds the size of \b A buffer
     *     object;
     *   - the same error codes as the clblasSgemv() function otherwise.
     *
     * @ingroup SYMV
     * </pre>
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
     * <pre>
     * @example example_ssymv.c
     * This is an example of how to use the @ref clblasSsymv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a symmetric matrix and double elements.
     *
     *
     * Matrix-vector products:
     * - \f$ y \leftarrow \alpha A x + \beta y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in
     *                      the buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot less
     *                      than \b N.
     * @param (in) x         Buffer object storing vector \b x.
     * @param (in) offx      Offset of first element of vector \b x in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b x. It cannot be zero.
     * @param (in) beta      The factor of vector \b y.
     * @param (out) y        Buffer object storing vector \b y.
     * @param (in) offy      Offset of first element of vector \b y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - \b clblasInvalidValue if \b offA exceeds the size of \b A buffer
     *     object;
     *   - the same error codes as the clblasSsymv() function otherwise.
     *
     * @ingroup SYMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HEMV HEMV  - Hermitian matrix-vector multiplication
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a hermitian matrix and float-complex elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offa     Offset in number of elements for first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot less
     *                      than \b N.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes or the vector sizes along with the increments lead to
     *       accessing outsize of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b A, \b X, or \b Y object is
     *     invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup HEMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a hermitian matrix and double-complex elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offa     Offset in number of elements for first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot less
     *                      than \b N.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChemv() function otherwise.
     *
     * @ingroup HEMV
     * </pre>
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
     * <pre>
     * @example example_zhemv.cpp
     * Example of how to use the @ref clblasZhemv function.
     * </pre>
     */
    /**@}*/
    /**
     * <pre>
     * @defgroup TRMV TRMV  - Triangular matrix vector multiply
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a triangular matrix and
     * float elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than \b N
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TRMV
     * </pre>
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
     * <pre>
     * @example example_strmv.c
     * Example of how to use the @ref clblasStrmv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a triangular matrix and
     * double elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than \b N
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStrmv() function otherwise.
     *
     * @ingroup TRMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a triangular matrix and
     * float complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than \b N
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasStrmv() function.
     * @ingroup TRMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a triangular matrix and
     * double complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than \b N
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDtrmv() function.
     * @ingroup TRMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup TRSV TRSV  - Triangular matrix vector Solve
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * solving triangular matrix problems with float elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than \b N
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TRSV
     * </pre>
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
     * <pre>
     * @example example_strsv.c
     * Example of how to use the @ref clblasStrsv function.
     * </pre>
     */
    /**
     * <pre>
     * solving triangular matrix problems with double elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than \b N
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStrsv() function otherwise.
     *
     * @ingroup TRSV
     * </pre>
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
     * <pre>
     * solving triangular matrix problems with float-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than \b N
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasStrsv() function.
     *
     * @ingroup TRSV
     * </pre>
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
     * <pre>
     * solving triangular matrix problems with double-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than \b N
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDtrsv() function.
     *
     * @ingroup TRSV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup GER GER   - General matrix rank 1 operation
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * vector-vector product with float elements and
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     specifies the scalar alpha.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b M, \b N or
     *     - either \b incx or \b incy is zero, or
     *     - a leading dimension is invalid;
     *   - \b clblasInvalidMemObject if A, X, or Y object is invalid,
     *     or an image object rather than the buffer one;
     *   - \b clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup GER
     * </pre>
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
     * <pre>
     * @example example_sger.c
     * Example of how to use the @ref clblasSger function.
     * </pre>
     */
    /**
     * <pre>
     * vector-vector product with double elements and
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     specifies the scalar alpha.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSger() function otherwise.
     *
     * @ingroup GER
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup GERU GERU  - General matrix rank 1 operation
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * vector-vector product with float complex elements and
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     specifies the scalar alpha.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b M, \b N or
     *     - either \b incx or \b incy is zero, or
     *     - a leading dimension is invalid;
     *   - \b clblasInvalidMemObject if A, X, or Y object is invalid,
     *     or an image object rather than the buffer one;
     *   - \b clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup GERU
     * </pre>
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
     * <pre>
     * vector-vector product with double complex elements and
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     specifies the scalar alpha.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A          Buffer object storing matrix \b A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCgeru() function otherwise.
     *
     * @ingroup GERU
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup GERC GERC  - General matrix rank 1 operation
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * vector-vector product with float complex elements and
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     specifies the scalar alpha.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b M, \b N or
     *     - either \b incx or \b incy is zero, or
     *     - a leading dimension is invalid;
     *   - \b clblasInvalidMemObject if A, X, or Y object is invalid,
     *     or an image object rather than the buffer one;
     *   - \b clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup GERC
     * </pre>
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
     * <pre>
     * vector-vector product with double complex elements and
     * performs the rank 1 operation A
     *
     * Vector-vector products:
     *   - \f$ A \leftarrow \alpha X Y^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     specifies the scalar alpha.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A. On exit, A is
     *                      overwritten by the updated matrix.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCgerc() function otherwise.
     *
     * @ingroup GERC
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SYR SYR   - Symmetric rank 1 update
     *
     * The Level 2 Basic Linear Algebra Subprograms are functions that perform
     * symmetric rank 1 update operations.
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Symmetric rank 1 operation with a general triangular matrix and
     * float elements.
     *
     * Symmetric rank 1 operation:
     *   - \f$ A \leftarrow \alpha x x^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A.
     * @param (in) offa      Offset of first element of matrix \b A in buffer object.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A, \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SYR
     * </pre>
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
     * <pre>
     * Symmetric rank 1 operation with a general triangular matrix and
     * double elements.
     *
     * Symmetric rank 1 operation:
     *   - \f$ A \leftarrow \alpha x x^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A.
     * @param (in) offa      Offset of first element of matrix \b A in buffer object.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSsyr() function otherwise.
     *
     * @ingroup SYR
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HER HER   - Hermitian rank 1 operation
     *
     * The Level 2 Basic Linear Algebra Subprogram functions that perform
     * hermitian rank 1 operations.
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * hermitian rank 1 operation with a general triangular matrix and
     * float-complex elements.
     *
     * hermitian rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A (a scalar float value)
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A, \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup HER
     * </pre>
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
     * <pre>
     * @example example_cher.c
     * Example of how to use the @ref clblasCher function.
     * </pre>
     */
    /**
     * <pre>
     * hermitian rank 1 operation with a general triangular matrix and
     * double-complex elements.
     *
     * hermitian rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A (a scalar double value)
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCher() function otherwise.
     *
     * @ingroup HER
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SYR2 SYR2  - Symmetric rank 2 update
     *
     * The Level 2 Basic Linear Algebra Subprograms are functions that perform
     * symmetric rank 2 update operations.
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Symmetric rank 2 operation with a general triangular matrix and
     * float elements.
     *
     * Symmetric rank 2 operation:
     *   - \f$ A \leftarrow \alpha x y^T + \alpha y x^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A.
     * @param (in) offa      Offset of first element of matrix \b A in buffer object.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A, \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SYR2
     * </pre>
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
     * <pre>
     * Symmetric rank 2 operation with a general triangular matrix and
     * double elements.
     *
     * Symmetric rank 2 operation:
     *   - \f$ A \leftarrow \alpha x y^T + \alpha y x^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A.
     * @param (in) offa      Offset of first element of matrix \b A in buffer object.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A, \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SYR2
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HER2 HER2  - Hermitian rank 2 update
     *
     * The Level 2 Basic Linear Algebra Subprograms are functions that perform
     * hermitian rank 2 update operations.
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Hermitian rank 2 operation with a general triangular matrix and
     * float-compelx elements.
     *
     * Hermitian rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^H + \overline{ \alpha } Y X^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A, \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup HER2
     * </pre>
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
     * <pre>
    * Hermitian rank 2 operation with a general triangular matrix and
     * double-compelx elements.
     *
     * Hermitian rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^H + \overline{ \alpha } Y X^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) A       Buffer object storing matrix \b A.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCher2() function otherwise.
     *
     * @ingroup HER2
     * </pre>
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
     * <pre>
     * @example example_zher2.c
     * Example of how to use the @ref clblasZher2 function.
     * </pre>
     */
    /**@}*/
    /**
     * <pre>
     * @defgroup TPMV TPMV  - Triangular packed matrix-vector multiply
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a packed triangular matrix and
     * float elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b AP is to be transposed.
     * @param (in) diag             Specify whether matrix \b AP is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b A.
     * @param (in) AP               Buffer object storing matrix \b AP in packed format.
     * @param (in) offa             Offset in number of elements for first element in matrix \b AP.
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero
     *   - \b clblasInvalidMemObject if either \b AP or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TPMV
     * </pre>
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
     * <pre>
     * @example example_stpmv.c
     * Example of how to use the @ref clblasStpmv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a packed triangular matrix and
     * double elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b AP is to be transposed.
     * @param (in) diag             Specify whether matrix \b AP is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b AP.
     * @param (in) AP               Buffer object storing matrix \b AP in packed format.
     * @param (in) offa             Offset in number of elements for first element in matrix \b AP.
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStpmv() function otherwise.
     *
     * @ingroup TPMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a packed triangular matrix and
     * float-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b AP is to be transposed.
     * @param (in) diag             Specify whether matrix \b AP is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b AP.
     * @param (in) AP               Buffer object storing matrix \b AP in packed format.
     * @param (in) offa             Offset in number of elements for first element in matrix \b AP.
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasStpmv() function.
     * @ingroup TPMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a packed triangular matrix and
     * double-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b AP is to be transposed.
     * @param (in) diag             Specify whether matrix \b AP is unit triangular.
     * @param (in) N                    Number of rows/columns in matrix \b AP.
     * @param (in) AP               Buffer object storing matrix \b AP in packed format.
     * @param (in) offa             Offset in number of elements for first element in matrix \b AP.
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDtpmv() function.
     * @ingroup TPMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup TPSV TPSV  - Triangular packed matrix vector solve
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * solving triangular packed matrix problems with float elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo              The triangle in matrix being referenced.
     * @param (in) trans             How matrix \b A is to be transposed.
     * @param (in) diag              Specify whether matrix \b A is unit triangular.
     * @param (in) N                 Number of rows/columns in matrix \b A.
     * @param (in) A                 Buffer object storing matrix in packed format.\b A.
     * @param (in) offa              Offset in number of elements for first element in matrix \b A.
     * @param (out) X                Buffer object storing vector \b X.
     * @param (in) offx              Offset in number of elements for first element in vector \b X.
     * @param (in) incx              Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TPSV
     * </pre>
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
     * <pre>
     * @example example_stpsv.c
     * Example of how to use the @ref clblasStpsv function.
     * </pre>
     */
    /**
     * <pre>
     * solving triangular packed matrix problems with double elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo              The triangle in matrix being referenced.
     * @param (in) trans             How matrix \b A is to be transposed.
     * @param (in) diag              Specify whether matrix \b A is unit triangular.
     * @param (in) N                 Number of rows/columns in matrix \b A.
     * @param (in) A                 Buffer object storing matrix in packed format.\b A.
     * @param (in) offa              Offset in number of elements for first element in matrix \b A.
     * @param (out) X                Buffer object storing vector \b X.
     * @param (in) offx              Offset in number of elements for first element in vector \b X.
     * @param (in) incx              Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TPSV
     * </pre>
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
     * <pre>
     * solving triangular packed matrix problems with float complex elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo              The triangle in matrix being referenced.
     * @param (in) trans             How matrix \b A is to be transposed.
     * @param (in) diag              Specify whether matrix \b A is unit triangular.
     * @param (in) N                 Number of rows/columns in matrix \b A.
     * @param (in) A                 Buffer object storing matrix in packed format.\b A.
     * @param (in) offa              Offset in number of elements for first element in matrix \b A.
     * @param (out) X                Buffer object storing vector \b X.
     * @param (in) offx              Offset in number of elements for first element in vector \b X.
     * @param (in) incx              Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TPSV
     * </pre>
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
     * <pre>
     * solving triangular packed matrix problems with double complex elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo              The triangle in matrix being referenced.
     * @param (in) trans             How matrix \b A is to be transposed.
     * @param (in) diag              Specify whether matrix \b A is unit triangular.
     * @param (in) N                 Number of rows/columns in matrix \b A.
     * @param (in) A                 Buffer object storing matrix in packed format.\b A.
     * @param (in) offa              Offset in number of elements for first element in matrix \b A.
     * @param (out) X                Buffer object storing vector \b X.
     * @param (in) offx              Offset in number of elements for first element in vector \b X.
     * @param (in) incx              Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TPSV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SPMV SPMV  - Symmetric packed matrix vector multiply
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a symmetric packed-matrix and float elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in matrix \b AP.
     * @param (in) alpha     The factor of matrix \b AP.
     * @param (in) AP        Buffer object storing matrix \b AP.
     * @param (in) offa     Offset in number of elements for first element in matrix \b AP.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the matrix sizes or the vector sizes along with the increments lead to
     *       accessing outsize of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b AP, \b X, or \b Y object is
     *     invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SPMV
     * </pre>
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
     * <pre>
     * @example example_sspmv.c
     * This is an example of how to use the @ref clblasSspmv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a symmetric packed-matrix and double elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in matrix \b AP.
     * @param (in) alpha     The factor of matrix \b AP.
     * @param (in) AP        Buffer object storing matrix \b AP.
     * @param (in) offa     Offset in number of elements for first element in matrix \b AP.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSspmv() function otherwise.
     *
     * @ingroup SPMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HPMV HPMV  - Hermitian packed matrix-vector multiplication
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a packed hermitian matrix and float-complex elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in matrix \b AP.
     * @param (in) alpha     The factor of matrix \b AP.
     * @param (in) AP        Buffer object storing packed matrix \b AP.
     * @param (in) offa     Offset in number of elements for first element in matrix \b AP.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx or \b incy is zero, or
     *     - the matrix sizes or the vector sizes along with the increments lead to
     *       accessing outsize of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b AP, \b X, or \b Y object is
     *     invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup HPMV
     * </pre>
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
     * <pre>
     * @example example_chpmv.c
     * This is an example of how to use the @ref clblasChpmv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a packed hermitian matrix and double-complex elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in matrix \b AP.
     * @param (in) alpha     The factor of matrix \b AP.
     * @param (in) AP        Buffer object storing packed matrix \b AP.
     * @param (in) offa     Offset in number of elements for first element in matrix \b AP.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChpmv() function otherwise.
     *
     * @ingroup HPMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SPR SPR   - Symmetric packed matrix rank 1 update
     *
     * The Level 2 Basic Linear Algebra Subprograms are functions that perform
     * symmetric rank 1 update operations on packed matrix
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Symmetric rank 1 operation with a general triangular packed-matrix and
     * float elements.
     *
     * Symmetric rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) AP      Buffer object storing packed-matrix \b AP.
     * @param (in) offa      Offset of first element of matrix \b AP in buffer object.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx is zero
     *   - \b clblasInvalidMemObject if either \b AP, \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SPR
     * </pre>
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
     * <pre>
     * @example example_sspr.c
     * Example of how to use the @ref clblasSspr function.
     * </pre>
     */
    /**
     * <pre>
     * Symmetric rank 1 operation with a general triangular packed-matrix and
     * double elements.
     *
     * Symmetric rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) AP      Buffer object storing packed-matrix \b AP.
     * @param (in) offa      Offset of first element of matrix \b AP in buffer object.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSspr() function otherwise.
     *
     * @ingroup SPR
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HPR HPR   - Hermitian packed matrix rank 1 update
     *
     * The Level 2 Basic Linear Algebra Subprogram functions that perform
     * hermitian rank 1 operations on packed matrix
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * hermitian rank 1 operation with a general triangular packed-matrix and
     * float-complex elements.
     *
     * hermitian rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A (a scalar float value)
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) AP      Buffer object storing matrix \b AP.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b AP.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b N is zero, or
     *     - either \b incx is zero
     *   - \b clblasInvalidMemObject if either \b AP, \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup HPR
     * </pre>
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
     * <pre>
     * @example example_chpr.c
     * Example of how to use the @ref clblasChpr function.
     * </pre>
     */
    /**
     * <pre>
     * hermitian rank 1 operation with a general triangular packed-matrix and
     * double-complex elements.
     *
     * hermitian rank 1 operation:
     *   - \f$ A \leftarrow \alpha X X^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A (a scalar float value)
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (out) AP      Buffer object storing matrix \b AP.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b AP.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChpr() function otherwise.
     *
     * @ingroup HPR
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SPR2 SPR2  - Symmetric packed matrix rank 2 update
     *
     * The Level 2 Basic Linear Algebra Subprograms are functions that perform
     * symmetric rank 2 update operations on packed matrices
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Symmetric rank 2 operation with a general triangular packed-matrix and
     * float elements.
     *
     * Symmetric rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^T + \alpha Y X^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) AP      Buffer object storing packed-matrix \b AP.
     * @param (in) offa      Offset of first element of matrix \b AP in buffer object.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N is zero, or
     *     - either \b incx or \b incy is zero
     *   - \b clblasInvalidMemObject if either \b AP, \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SPR2
     * </pre>
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
     * <pre>
     * @example example_sspr2.c
     * Example of how to use the @ref clblasSspr2 function.
     * </pre>
     */
    /**
     * <pre>
     * Symmetric rank 2 operation with a general triangular packed-matrix and
     * double elements.
     *
     * Symmetric rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^T + \alpha Y X^T + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) AP      Buffer object storing packed-matrix \b AP.
     * @param (in) offa      Offset of first element of matrix \b AP in buffer object.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSspr2() function otherwise.
     *
     * @ingroup SPR2
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HPR2 HPR2  - Hermitian packed matrix rank 2 update
     *
     * The Level 2 Basic Linear Algebra Subprograms are functions that perform
     * hermitian rank 2 update operations on packed matrices
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Hermitian rank 2 operation with a general triangular packed-matrix and
     * float-compelx elements.
     *
     * Hermitian rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^H + \conjg( alpha ) Y X^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) AP      Buffer object storing packed-matrix \b AP.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b AP.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N is zero, or
     *     - either \b incx or \b incy is zero
     *   - \b clblasInvalidMemObject if either \b AP, \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup HPR2
     * </pre>
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
     * <pre>
     * Hermitian rank 2 operation with a general triangular packed-matrix and
     * double-compelx elements.
     *
     * Hermitian rank 2 operation:
     *   - \f$ A \leftarrow \alpha X Y^H + \conjg( alpha ) Y X^H + A \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of columns in matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset in number of elements for the first element in vector \b X.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) Y         Buffer object storing vector \b Y.
     * @param (in) offy      Offset in number of elements for the first element in vector \b Y.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (out) AP      Buffer object storing packed-matrix \b AP.
     * @param (in) offa      Offset in number of elements for the first element in matrix \b AP.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChpr2() function otherwise.
     *
     * @ingroup HPR2
     * </pre>
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
     * <pre>
     * @example example_zhpr2.c
     * Example of how to use the @ref clblasZhpr2 function.
     * </pre>
     */
    /**@}*/
    /**
     * <pre>
     * @defgroup GBMV GBMV  - General banded matrix-vector multiplication
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a general rectangular banded matrix and
     * float elements.
     *
     * Matrix-vector products:
     *   - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *   - \f$ Y \leftarrow \alpha A^T X + \beta Y \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) trans     How matrix \b A is to be transposed.
     * @param (in) M         Number of rows in banded matrix \b A.
     * @param (in) N         Number of columns in banded matrix \b A.
     * @param (in) KL        Number of sub-diagonals in banded matrix \b A.
     * @param (in) KU        Number of super-diagonals in banded matrix \b A.
     * @param (in) alpha     The factor of banded matrix \b A.
     * @param (in) A         Buffer object storing banded matrix \b A.
     * @param (in) offa      Offset in number of elements for the first element in banded matrix \b A.
     * @param (in) lda       Leading dimension of banded matrix \b A. It cannot be less
     *                      than ( \b KL + \b KU + 1 )
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) beta      The factor of the vector \b Y.
     * @param (out) Y        Buffer object storing the vector \b y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b M or \b N is zero, or
     *     - KL is greater than \b M - 1, or
     *     - KU is greater than \b N - 1, or
     *     - either \b incx or \b incy is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix size or the vector sizes along with the increments lead to
     *       accessing outside of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b A, \b X, or \b Y object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup GBMV
     * </pre>
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
     * <pre>
     * @example example_sgbmv.c
     * Example of how to use the @ref clblasSgbmv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a general rectangular banded matrix and
     * double elements.
     *
     * Matrix-vector products:
     *   - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *   - \f$ Y \leftarrow \alpha A^T X + \beta Y \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) trans     How matrix \b A is to be transposed.
     * @param (in) M         Number of rows in banded matrix \b A.
     * @param (in) N         Number of columns in banded matrix \b A.
     * @param (in) KL        Number of sub-diagonals in banded matrix \b A.
     * @param (in) KU        Number of super-diagonals in banded matrix \b A.
     * @param (in) alpha     The factor of banded matrix \b A.
     * @param (in) A         Buffer object storing banded matrix \b A.
     * @param (in) offa      Offset in number of elements for the first element in banded matrix \b A.
     * @param (in) lda       Leading dimension of banded matrix \b A. It cannot be less
     *                      than ( \b KL + \b KU + 1 )
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) beta      The factor of the vector \b Y.
     * @param (out) Y        Buffer object storing the vector \b y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSgbmv() function otherwise.
     *
     * @ingroup GBMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a general rectangular banded matrix and
     * float-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *   - \f$ Y \leftarrow \alpha A^T X + \beta Y \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) trans     How matrix \b A is to be transposed.
     * @param (in) M         Number of rows in banded matrix \b A.
     * @param (in) N         Number of columns in banded matrix \b A.
     * @param (in) KL        Number of sub-diagonals in banded matrix \b A.
     * @param (in) KU        Number of super-diagonals in banded matrix \b A.
     * @param (in) alpha     The factor of banded matrix \b A.
     * @param (in) A         Buffer object storing banded matrix \b A.
     * @param (in) offa      Offset in number of elements for the first element in banded matrix \b A.
     * @param (in) lda       Leading dimension of banded matrix \b A. It cannot be less
     *                      than ( \b KL + \b KU + 1 )
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) beta      The factor of the vector \b Y.
     * @param (out) Y        Buffer object storing the vector \b y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasSgbmv() function.
     *
     * @ingroup GBMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a general rectangular banded matrix and
     * double-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *   - \f$ Y \leftarrow \alpha A^T X + \beta Y \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) trans     How matrix \b A is to be transposed.
     * @param (in) M         Number of rows in banded matrix \b A.
     * @param (in) N         Number of columns in banded matrix \b A.
     * @param (in) KL        Number of sub-diagonals in banded matrix \b A.
     * @param (in) KU        Number of super-diagonals in banded matrix \b A.
     * @param (in) alpha     The factor of banded matrix \b A.
     * @param (in) A         Buffer object storing banded matrix \b A.
     * @param (in) offa      Offset in number of elements for the first element in banded matrix \b A.
     * @param (in) lda       Leading dimension of banded matrix \b A. It cannot be less
     *                      than ( \b KL + \b KU + 1 )
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of \b X. Must not be zero.
     * @param (in) beta      The factor of the vector \b Y.
     * @param (out) Y        Buffer object storing the vector \b y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of \b Y. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDgbmv() function.
     *
     * @ingroup GBMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup TBMV TBMV  - Triangular banded matrix vector multiply
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a triangular banded matrix and
     * float elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in banded matrix \b A.
     * @param (in) K                    Number of sub-diagonals/super-diagonals in triangular banded matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than ( \b K + 1 )
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - K is greater than \b N - 1
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TBMV
     * </pre>
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
     * <pre>
     * @example example_stbmv.c
     * Example of how to use the @ref clblasStbmv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a triangular banded matrix and
     * double elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in banded matrix \b A.
     * @param (in) K                    Number of sub-diagonals/super-diagonals in triangular banded matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than ( \b K + 1 )
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStbmv() function otherwise.
     *
     * @ingroup TBMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a triangular banded matrix and
     * float-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in banded matrix \b A.
     * @param (in) K                    Number of sub-diagonals/super-diagonals in triangular banded matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than ( \b K + 1 )
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
    * @return The same result as the clblasStbmv() function.
     *
     * @ingroup TBMV
     * </pre>
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
     * <pre>
     * Matrix-vector product with a triangular banded matrix and
     * double-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ X \leftarrow  A X \f$
     *   - \f$ X \leftarrow  A^T X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in banded matrix \b A.
     * @param (in) K                    Number of sub-diagonals/super-diagonals in triangular banded matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than ( \b K + 1 )
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) scratchBuff      Temporary cl_mem scratch buffer object which can hold a
     *                              minimum of (1 + (N-1)*abs(incx)) elements
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
    * @return The same result as the clblasDtbmv() function.
     *
     * @ingroup TBMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SBMV SBMV  - Symmetric banded matrix-vector multiplication
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a symmetric banded matrix and float elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in banded matrix \b A.
     * @param (in) K            Number of sub-diagonals/super-diagonals in banded matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A            Buffer object storing matrix \b A.
     * @param (in) offa     Offset in number of elements for first element in matrix \b A.
     * @param (in) lda      Leading dimension of matrix \b A. It cannot be less
     *                      than ( \b K + 1 )
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - K is greater than \b N - 1
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SBMV
     * </pre>
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
     * <pre>
     * @example example_ssbmv.c
     * This is an example of how to use the @ref clblasSsbmv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a symmetric banded matrix and double elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in banded matrix \b A.
     * @param (in) K            Number of sub-diagonals/super-diagonals in banded matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A            Buffer object storing matrix \b A.
     * @param (in) offa     Offset in number of elements for first element in matrix \b A.
     * @param (in) lda      Leading dimension of matrix \b A. It cannot be less
     *                      than ( \b K + 1 )
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSsbmv() function otherwise.
     *
     * @ingroup SBMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HBMV HBMV  - Hermitian banded matrix-vector multiplication
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-vector product with a hermitian banded matrix and float elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in banded matrix \b A.
     * @param (in) K            Number of sub-diagonals/super-diagonals in banded matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A            Buffer object storing matrix \b A.
     * @param (in) offa     Offset in number of elements for first element in matrix \b A.
     * @param (in) lda      Leading dimension of matrix \b A. It cannot be less
     *                      than ( \b K + 1 )
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - K is greater than \b N - 1
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup HBMV
     * </pre>
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
     * <pre>
     * @example example_chbmv.c
     * This is an example of how to use the @ref clblasChbmv function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-vector product with a hermitian banded matrix and double elements.
     *
     * Matrix-vector products:
     * - \f$ Y \leftarrow \alpha A X + \beta Y \f$
     *
     * @param (in) order     Row/columns order.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) N         Number of rows and columns in banded matrix \b A.
     * @param (in) K            Number of sub-diagonals/super-diagonals in banded matrix \b A.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A            Buffer object storing matrix \b A.
     * @param (in) offa     Offset in number of elements for first element in matrix \b A.
     * @param (in) lda      Leading dimension of matrix \b A. It cannot be less
     *                      than ( \b K + 1 )
     * @param (in) X         Buffer object storing vector \b X.
     * @param (in) offx      Offset of first element of vector \b X in buffer object.
     *                      Counted in elements.
     * @param (in) incx      Increment for the elements of vector \b X. It cannot be zero.
     * @param (in) beta      The factor of vector \b Y.
     * @param (out) Y        Buffer object storing vector \b Y.
     * @param (in) offy      Offset of first element of vector \b Y in buffer object.
     *                      Counted in elements.
     * @param (in) incy      Increment for the elements of vector \b Y. It cannot be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChbmv() function otherwise.
     *
     * @ingroup HBMV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup TBSV TBSV  - Solving triangular banded matrix
     * @ingroup BLAS2
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * solving triangular banded matrix problems with float elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in banded matrix \b A.
     * @param (in) K                    Number of sub-diagonals/super-diagonals in triangular banded matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than ( \b K + 1 )
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b incx is zero, or
     *     - K is greater than \b N - 1
     *     - the leading dimension is invalid;
     *   - \b clblasInvalidMemObject if either \b A or \b X object is
     *     Invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs
     *     to was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup TBSV
     * </pre>
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
     * <pre>
     * @example example_stbsv.c
     * This is an example of how to use the @ref clblasStbsv function.
     * </pre>
     */
    /**
     * <pre>
     * solving triangular banded matrix problems with double elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in banded matrix \b A.
     * @param (in) K                    Number of sub-diagonals/super-diagonals in triangular banded matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than ( \b K + 1 )
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasStbsv() function otherwise.
     *
     * @ingroup TBSV
     * </pre>
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
     * <pre>
     * solving triangular banded matrix problems with float-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in banded matrix \b A.
     * @param (in) K                    Number of sub-diagonals/super-diagonals in triangular banded matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than ( \b K + 1 )
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasStbsv() function.
     *
     * @ingroup TBSV
     * </pre>
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
     * <pre>
     * solving triangular banded matrix problems with double-complex elements.
     *
     * Matrix-vector products:
     *   - \f$ A X \leftarrow  X \f$
     *   - \f$ A^T X \leftarrow  X \f$
     *
     * @param (in) order                Row/column order.
     * @param (in) uplo             The triangle in matrix being referenced.
     * @param (in) trans                How matrix \b A is to be transposed.
     * @param (in) diag             Specify whether matrix \b A is unit triangular.
     * @param (in) N                    Number of rows/columns in banded matrix \b A.
     * @param (in) K                    Number of sub-diagonals/super-diagonals in triangular banded matrix \b A.
     * @param (in) A                    Buffer object storing matrix \b A.
     * @param (in) offa             Offset in number of elements for first element in matrix \b A.
     * @param (in) lda              Leading dimension of matrix \b A. It cannot be less
     *                              than ( \b K + 1 )
     * @param (out) X               Buffer object storing vector \b X.
     * @param (in) offx             Offset in number of elements for first element in vector \b X.
     * @param (in) incx             Increment for the elements of \b X. Must not be zero.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return The same result as the clblasDtbsv() function.
     *
     * @ingroup TBSV
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup BLAS3 BLAS-3 functions
     *
     * The Level 3 Basic Linear Algebra Subprograms are funcions that perform
     * matrix-matrix operations.
     * </pre>
     */
    /**@{*/
    /**@}*/
    /**
     * <pre>
     * @defgroup GEMM GEMM - General matrix-matrix multiplication
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-matrix product of general rectangular matrices with float
     *        elements. Extended version.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \beta C \f$
     *   - \f$ C \leftarrow \alpha A B^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B^T + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) transB    How matrix \b B is to be transposed.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) K         Number of columns in matrix \b A and rows in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b K when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b K
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in)  offC     Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when
     *                      it is set to \b clblasColumnMajorOrder.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA, \b offB or \b offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as clblasSgemm() otherwise.
     *
     * @ingroup GEMM
     * </pre>
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
     * <pre>
     * @example example_sgemm.c
     * This is an example of how to use the @ref clblasSgemmEx function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-matrix product of general rectangular matrices with double
     *        elements. Extended version.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \beta C \f$
     *   - \f$ C \leftarrow \alpha A B^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B^T + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) transB    How matrix \b B is to be transposed.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) K         Number of columns in matrix \b A and rows in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed description,
     *                      see clblasSgemm().
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed description,
     *                      see clblasSgemm().
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offC      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. For detailed description,
     *                      see clblasSgemm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA, \b offB or offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSgemm() function otherwise.
     *
     * @ingroup GEMM
     * </pre>
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
     * <pre>
     * Matrix-matrix product of general rectangular matrices with float
     *        complex elements. Extended version.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \beta C \f$
     *   - \f$ C \leftarrow \alpha A B^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B^T + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) transB    How matrix \b B is to be transposed.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) K         Number of columns in matrix \b A and rows in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed description,
     *                      see clblasSgemm().
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed description,
     *                      see clblasSgemm().
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offC      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. For detailed description,
     *                      see clblasSgemm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA, \b offB or offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSgemm() function otherwise.
     *
     * @ingroup GEMM
     * </pre>
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
     * <pre>
     * Matrix-matrix product of general rectangular matrices with double
     *        complex elements. Exteneded version.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \beta C \f$
     *   - \f$ C \leftarrow \alpha A B^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B^T + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) transB    How matrix \b B is to be transposed.
     * @param (in) M         Number of rows in matrix \b A.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) K         Number of columns in matrix \b A and rows in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed description,
     *                      see clblasSgemm().
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed description,
     *                      see clblasSgemm().
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offC      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. For detailed description,
     *                      see clblasSgemm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA, \b offB or offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSgemm() function otherwise.
     *
     * @ingroup GEMM
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup TRMM TRMM - Triangular matrix-matrix multiplication
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Multiplying a matrix by a triangular matrix with float elements.
     *        Extended version.
     *
     * Matrix-triangular matrix products:
     *   - \f$ B \leftarrow \alpha A B \f$
     *   - \f$ B \leftarrow \alpha A^T B \f$
     *   - \f$ B \leftarrow \alpha B A \f$
     *   - \f$ B \leftarrow \alpha B A^T \f$
     *
     * where \b T is an upper or lower triangular matrix.
     *
     * @param (in) order     Row/column order.
     * @param (in) side      The side of triangular matrix.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) diag      Specify whether matrix is unit triangular.
     * @param (in) M         Number of rows in matrix \b B.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b M when the \b side parameter is set to
     *                      \b clblasLeft,\n or less than \b N when it is set
     *                      to \b clblasRight.
     * @param (out) B        Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or not less than \b M
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA or \b offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as clblasStrmm() otherwise.
     *
     * @ingroup TRMM
     * </pre>
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
     * <pre>
     * @example example_strmm.c
     * This is an example of how to use the @ref clblasStrmmEx function.
     * </pre>
     */
    /**
     * <pre>
     * Multiplying a matrix by a triangular matrix with double elements.
     *        Extended version.
     *
     * Matrix-triangular matrix products:
     *   - \f$ B \leftarrow \alpha A B \f$
     *   - \f$ B \leftarrow \alpha A^T B \f$
     *   - \f$ B \leftarrow \alpha B A \f$
     *   - \f$ B \leftarrow \alpha B A^T \f$
     *
     * where \b T is an upper or lower triangular matrix.
     *
     * @param (in) order     Row/column order.
     * @param (in) side      The side of triangular matrix.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) diag      Specify whether matrix is unit triangular.
     * @param (in) M         Number of rows in matrix \b B.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed
     *                      description, see clblasStrmm().
     * @param (out) B        Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed
     *                      description, see clblasStrmm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA or \b offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasStrmm() function otherwise.
     *
     * @ingroup TRMM
     * </pre>
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
     * <pre>
     * Multiplying a matrix by a triangular matrix with float complex
     *        elements. Extended version.
     *
     * Matrix-triangular matrix products:
     *   - \f$ B \leftarrow \alpha A B \f$
     *   - \f$ B \leftarrow \alpha A^T B \f$
     *   - \f$ B \leftarrow \alpha B A \f$
     *   - \f$ B \leftarrow \alpha B A^T \f$
     *
     * where \b T is an upper or lower triangular matrix.
     * @param (in) order     Row/column order.
     * @param (in) side      The side of triangular matrix.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) diag      Specify whether matrix is unit triangular.
     * @param (in) M         Number of rows in matrix \b B.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed
     *                      description, see clblasStrmm().
     * @param (out) B        Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed
     *                      description, see clblasStrmm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA or \b offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as clblasStrmm() otherwise.
     *
     * @ingroup TRMM
     * </pre>
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
     * <pre>
     * Multiplying a matrix by a triangular matrix with double complex
     *        elements. Extended version.
     *
     * Matrix-triangular matrix products:
     *   - \f$ B \leftarrow \alpha A B \f$
     *   - \f$ B \leftarrow \alpha A^T B \f$
     *   - \f$ B \leftarrow \alpha B A \f$
     *   - \f$ B \leftarrow \alpha B A^T \f$
     *
     * where \b T is an upper or lower triangular matrix.
     *
     * @param (in) order     Row/column order.
     * @param (in) side      The side of triangular matrix.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) diag      Specify whether matrix is unit triangular.
     * @param (in) M         Number of rows in matrix \b B.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed
     *                      description, see clblasStrmm().
     * @param (out) B        Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed
     *                      description, see clblasStrmm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA or \b offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasStrmm() function otherwise.
     *
     * @ingroup TRMM
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup TRSM TRSM - Solving triangular systems of equations
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Solving triangular systems of equations with multiple right-hand
     *        sides and float elements. Extended version.
     *
     * Solving triangular systems of equations:
     *   - \f$ B \leftarrow \alpha A^{-1} B \f$
     *   - \f$ B \leftarrow \alpha A^{-T} B \f$
     *   - \f$ B \leftarrow \alpha B A^{-1} \f$
     *   - \f$ B \leftarrow \alpha B A^{-T} \f$
     *
     * where \b T is an upper or lower triangular matrix.
     *
     * @param (in) order     Row/column order.
     * @param (in) side      The side of triangular matrix.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) diag      Specify whether matrix is unit triangular.
     * @param (in) M         Number of rows in matrix \b B.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b M when the \b side parameter is set to
     *                      \b clblasLeft,\n or less than \b N
     *                      when it is set to \b clblasRight.
     * @param (out) B        Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA or \b offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as clblasStrsm() otherwise.
     *
     * @ingroup TRSM
     * </pre>
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
     * <pre>
     * @example example_strsm.c
     * This is an example of how to use the @ref clblasStrsmEx function.
     * </pre>
     */
    /**
     * <pre>
     * Solving triangular systems of equations with multiple right-hand
     *        sides and double elements. Extended version.
     *
     * Solving triangular systems of equations:
     *   - \f$ B \leftarrow \alpha A^{-1} B \f$
     *   - \f$ B \leftarrow \alpha A^{-T} B \f$
     *   - \f$ B \leftarrow \alpha B A^{-1} \f$
     *   - \f$ B \leftarrow \alpha B A^{-T} \f$
     *
     * where \b T is an upper or lower triangular matrix.
     *
     * @param (in) order     Row/column order.
     * @param (in) side      The side of triangular matrix.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) diag      Specify whether matrix is unit triangular.
     * @param (in) M         Number of rows in matrix \b B.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed
     *                      description, see clblasStrsm().
     * @param (out) B        Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed
     *                      description, see clblasStrsm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA or \b offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasStrsm() function otherwise.
     *
     * @ingroup TRSM
     * </pre>
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
     * <pre>
     * Solving triangular systems of equations with multiple right-hand
     *        sides and float complex elements. Extended version.
     *
     * Solving triangular systems of equations:
     *   - \f$ B \leftarrow \alpha A^{-1} B \f$
     *   - \f$ B \leftarrow \alpha A^{-T} B \f$
     *   - \f$ B \leftarrow \alpha B A^{-1} \f$
     *   - \f$ B \leftarrow \alpha B A^{-T} \f$
     *
     * where \b T is an upper or lower triangular matrix.
     *
     * @param (in) order     Row/column order.
     * @param (in) side      The side of triangular matrix.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) diag      Specify whether matrix is unit triangular.
     * @param (in) M         Number of rows in matrix \b B.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed
     *                      description, see clblasStrsm().
     * @param (out) B        Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed
     *                      description, see clblasStrsm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA or \b offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as clblasStrsm() otherwise.
     *
     * @ingroup TRSM
     * </pre>
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
     * <pre>
     * Solving triangular systems of equations with multiple right-hand
     *        sides and double complex elements. Extended version.
     *
     * Solving triangular systems of equations:
     *   - \f$ B \leftarrow \alpha A^{-1} B \f$
     *   - \f$ B \leftarrow \alpha A^{-T} B \f$
     *   - \f$ B \leftarrow \alpha B A^{-1} \f$
     *   - \f$ B \leftarrow \alpha B A^{-T} \f$
     *
     * where \b T is an upper or lower triangular matrix.
     *
     * @param (in) order     Row/column order.
     * @param (in) side      The side of triangular matrix.
     * @param (in) uplo      The triangle in matrix being referenced.
     * @param (in) transA    How matrix \b A is to be transposed.
     * @param (in) diag      Specify whether matrix is unit triangular.
     * @param (in) M         Number of rows in matrix \b B.
     * @param (in) N         Number of columns in matrix \b B.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offA      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. For detailed
     *                      description, see clblasStrsm().
     * @param (out) B        Buffer object storing matrix \b B.
     * @param (in) offB      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. For detailed
     *                      description, see clblasStrsm().
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA or \b offB exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasStrsm() function otherwise
     *
     * @ingroup TRSM
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SYRK SYRK - Symmetric rank-k update of a matrix
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Rank-k update of a symmetric matrix with float elements.
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T A + \beta C \f$
     *
     * where \b C is a symmetric matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transA     How matrix \b A is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrix \b A if it is not
     *                       transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrix \b A.
     * @param (in) A          Buffer object storing the matrix \b A.
     * @param (in) offA       Offset of the first element of the matrix \b A in the
     *                       buffer object. Counted in elements.
     * @param (in) lda        Leading dimension of matrix \b A. It cannot be
     *                       less than \b K if \b A is
     *                       in the row-major format, and less than \b N
     *                       otherwise.
     * @param (in) beta       The factor of the matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offC       Offset of the first element of the matrix \b C in the
     *                       buffer object. Counted in elements.
     * @param (in) ldc        Leading dimension of matric \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA or \b offC exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasSsyrk() function otherwise.
     *
     * @ingroup SYRK
     * </pre>
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
     * <pre>
     * @example example_ssyrk.c
     * This is an example of how to use the @ref clblasSsyrkEx function.
     * </pre>
     */
    /**
     * <pre>
     * Rank-k update of a symmetric matrix with double elements.
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T A + \beta C \f$
     *
     * where \b C is a symmetric matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transA     How matrix \b A is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrix \b A if it is not
     *                       transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrix \b A.
     * @param (in) A          Buffer object storing the matrix \b A.
     * @param (in) offA       Offset of the first element of the matrix \b A in the
     *                       buffer object. Counted in elements.
     * @param (in) lda        Leading dimension of matrix \b A. For detailed
     *                       description, see clblasSsyrk().
     * @param (in) beta       The factor of the matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offC       Offset of the first element of the matrix \b C in the
     *                       buffer object. Counted in elements.
     * @param (in) ldc        Leading dimension of matrix \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA or \b offC exceeds the size
     *        of the respective buffer object;
     *   - the same error codes as the clblasSsyrk() function otherwise.
     *
     * @ingroup SYRK
     * </pre>
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
     * <pre>
     * Rank-k update of a symmetric matrix with complex float elements.
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T A + \beta C \f$
     *
     * where \b C is a symmetric matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transA     How matrix \b A is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrix \b A if it is not
     *                       transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrix \b A.
     * @param (in) A          Buffer object storing the matrix \b A.
     * @param (in) offA       Offset of the first element of the matrix \b A in the
     *                       buffer object. Counted in elements.
     * @param (in) lda        Leading dimension of matrix \b A. For detailed
     *                       description, see clblasSsyrk().
     * @param (in) beta       The factor of the matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offC       Offset of the first element of the matrix \b C in the
     *                       buffer object. Counted in elements.
     * @param (in) ldc        Leading dimension of matrix \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA or \b offC exceeds the size
     *        of the respective buffer object;
     *   - \b clblasInvalidValue if \b transA is set to \ref clblasConjTrans.
     *   - the same error codes as the clblasSsyrk() function otherwise.
     *
     * @ingroup SYRK
     * </pre>
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
     * <pre>
     * Rank-k update of a symmetric matrix with complex double elements.
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T A + \beta C \f$
     *
     * where \b C is a symmetric matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transA     How matrix \b A is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrix \b A if it is not
     *                       transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrix \b A.
     * @param (in) A          Buffer object storing the matrix \b A.
     * @param (in) offA       Offset of the first element of the matrix \b A in the
     *                       buffer object. Counted in elements.
     * @param (in) lda        Leading dimension of matrix \b A. For detailed
     *                       description, see clblasSsyrk().
     * @param (in) beta       The factor of the matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offC       Offset of the first element of the matrix \b C in the
     *                       buffer object. Counted in elements.
     * @param (in) ldc        Leading dimension of matrix \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *         point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA or \b offC exceeds the size
     *        of the respective buffer object;
     *   - \b clblasInvalidValue if \b transA is set to \ref clblasConjTrans.
     *   - the same error codes as the clblasSsyrk() function otherwise.
     *
     * @ingroup SYRK
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SYR2K SYR2K - Symmetric rank-2k update to a matrix
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Rank-2k update of a symmetric matrix with float elements.
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^T + \alpha B A^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \alpha B^T A \beta C \f$
     *
     * where \b C is a symmetric matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transAB    How matrices \b A and \b B is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrices \b A and \b B if they
     *                       are not transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrices \b A and \b B.
     * @param (in) A          Buffer object storing matrix \b A.
     * @param (in) offA       Offset of the first element of the matrix \b A in the
     *                       buffer object. Counted in elements.
     * @param (in) lda        Leading dimension of matrix \b A. It cannot be less
     *                       than \b K if \b A is
     *                       in the row-major format, and less than \b N
     *                       otherwise.
     * @param (in) B          Buffer object storing matrix \b B.
     * @param (in) offB       Offset of the first element of the matrix \b B in the
     *                       buffer object. Counted in elements.
     * @param (in) ldb        Leading dimension of matrix \b B. It cannot be less
     *                       less than \b K if \b B matches to the op(\b B) matrix
     *                       in the row-major format, and less than \b N
     *                       otherwise.
     * @param (in) beta       The factor of matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offC       Offset of the first element of the matrix \b C in the
     *                       buffer object. Counted in elements.
     * @param (in) ldc        Leading dimension of matrix \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA, \b offB or \b offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSsyr2k() function otherwise.
     *
     * @ingroup SYR2K
     * </pre>
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
     * <pre>
     * @example example_ssyr2k.c
     * This is an example of how to use the @ref clblasSsyr2kEx function.
     * </pre>
     */
    /**
     * <pre>
     * Rank-2k update of a symmetric matrix with double elements.
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^T + \alpha B A^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \alpha B^T A \beta C \f$
     *
     * where \b C is a symmetric matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transAB    How matrices \b A and \b B is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrices \b A and \b B if they
     *                       are not transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrices \b A and \b B.
     * @param (in) A          Buffer object storing matrix \b A.
     * @param (in) offA       Offset of the first element of the matrix \b A in the
     *                       buffer object. Counted in elements.
     * @param (in) lda        Leading dimension of matrix \b A. For detailed
     *                       description, see clblasSsyr2k().
     * @param (in) B          Buffer object storing matrix \b B.
     * @param (in) offB       Offset of the first element of the matrix \b B in the
     *                       buffer object. Counted in elements.
     * @param (in) ldb        Leading dimension of matrix \b B. For detailed
     *                       description, see clblasSsyr2k().
     * @param (in) beta       The factor of matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offC       Offset of the first element of the matrix \b C in the
     *                       buffer object. Counted in elements.
     * @param (in) ldc        Leading dimension of matrix \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA, \b offB or \b offC exceeds
     *        the size of the respective buffer object;
     *   - the same error codes as the clblasSsyr2k() function otherwise.
     *
     * @ingroup SYR2K
     * </pre>
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
     * <pre>
     * Rank-2k update of a symmetric matrix with complex float elements.
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^T + \alpha B A^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \alpha B^T A \beta C \f$
     *
     * where \b C is a symmetric matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transAB    How matrices \b A and \b B is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrices \b A and \b B if they
     *                       are not transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrices \b A and \b B.
     * @param (in) A          Buffer object storing matrix \b A.
     * @param (in) offA       Offset of the first element of the matrix \b A in the
     *                       buffer object. Counted in elements.
     * @param (in) lda        Leading dimension of matrix \b A. For detailed
     *                       description, see clblasSsyr2k().
     * @param (in) B          Buffer object storing matrix \b B.
     * @param (in) offB       Offset of the first element of the matrix \b B in the
     *                       buffer object. Counted in elements.
     * @param (in) ldb        Leading dimension of matrix \b B. For detailed
     *                       description, see clblasSsyr2k().
     * @param (in) beta       The factor of matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offC       Offset of the first element of the matrix \b C in the
     *                       buffer object. Counted in elements.
     * @param (in) ldc        Leading dimension of matrix \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidValue if either \b offA, \b offB or \b offC exceeds
     *        the size of the respective buffer object;
     *   - \b clblasInvalidValue if \b transAB is set to \ref clblasConjTrans.
     *   - the same error codes as the clblasSsyr2k() function otherwise.
     *
     * @ingroup SYR2K
     * </pre>
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
     * <pre>
     * Rank-2k update of a symmetric matrix with complex double elements.
     *        Extended version.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^T + \alpha B A^T + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^T B + \alpha B^T A \beta C \f$
     *
     * where \b C is a symmetric matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transAB    How matrices \b A and \b B is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrices \b A and \b B if they
     *                       are not transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrices \b A and \b B.
     * @param (in) A          Buffer object storing matrix \b A.
     * @param (in) offA       Offset of the first element of the matrix \b A in the
     *                       buffer object. Counted in elements.
     * @param (in) lda        Leading dimension of matrix \b A. For detailed
     *                       description, see clblasSsyr2k().
     * @param (in) B          Buffer object storing matrix \b B.
     * @param (in) offB       Offset of the first element of the matrix \b B in the
     *                       buffer object. Counted in elements.
     * @param (in) ldb        Leading dimension of matrix \b B. For detailed
     *                       description, see clblasSsyr2k().
     * @param (in) beta       The factor of matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offC       Offset of the first element of the matrix \b C in the
     *                       buffer object. Counted in elements.
     * @param (in) ldc        Leading dimension of matrix \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *        point arithmetic with double precision;
     *   - \b clblasInvalidValue if either \b offA, \b offB or \b offC exceeds
     *        the size of the respective buffer object;
     *   - \b clblasInvalidValue if \b transAB is set to \ref clblasConjTrans.
     *   - the same error codes as the clblasSsyr2k() function otherwise.
     *
     * @ingroup SYR2K
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup SYMM SYMM  - Symmetric matrix-matrix multiply
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-matrix product of symmetric rectangular matrices with float
     * elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha B A + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) side     The side of triangular matrix.
     * @param (in) uplo     The triangle in matrix being referenced.
     * @param (in) M         Number of rows in matrices \b B and \b C.
     * @param (in) N         Number of columns in matrices \b B and \b C.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offa      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b M when the \b side parameter is set to
     *                      \b clblasLeft,\n or less than \b N when the
     *                      parameter is set to \b clblasRight.
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offb      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offc      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when
     *                      it is set to \b clblasColumnMajorOrder.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events             Event objects per each command queue that identify
     *                                a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b M or \b N is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes lead to accessing outsize of any of the buffers;
     *   - \b clblasInvalidMemObject if A, B, or C object is invalid,
     *     or an image object rather than the buffer one;
     *   - \b clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup SYMM
     * </pre>
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
     * <pre>
     * @example example_ssymm.c
     * This is an example of how to use the @ref clblasSsymm function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-matrix product of symmetric rectangular matrices with double
     * elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha B A + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) side     The side of triangular matrix.
     * @param (in) uplo     The triangle in matrix being referenced.
     * @param (in) M         Number of rows in matrices \b B and \b C.
     * @param (in) N         Number of columns in matrices \b B and \b C.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offa      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b M when the \b side parameter is set to
     *                      \b clblasLeft,\n or less than \b N when the
     *                      parameter is set to \b clblasRight.
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offb      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offc      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when
     *                      it is set to \b clblasColumnMajorOrder.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events             Event objects per each command queue that identify
     *                                a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasSsymm() function otherwise.
     *
     * @ingroup SYMM
     * </pre>
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
     * <pre>
     * Matrix-matrix product of symmetric rectangular matrices with
     * float-complex elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha B A + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) side     The side of triangular matrix.
     * @param (in) uplo     The triangle in matrix being referenced.
     * @param (in) M         Number of rows in matrices \b B and \b C.
     * @param (in) N         Number of columns in matrices \b B and \b C.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offa      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b M when the \b side parameter is set to
     *                      \b clblasLeft,\n or less than \b N when the
     *                      parameter is set to \b clblasRight.
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offb      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offc      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when
     *                      it is set to \b clblasColumnMajorOrder.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events             Event objects per each command queue that identify
     *                                a particular kernel execution instance.
     *
     * @return The same result as the clblasSsymm() function.
     *
     * @ingroup SYMM
     * </pre>
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
     * <pre>
     * Matrix-matrix product of symmetric rectangular matrices with
     * double-complex elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha B A + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) side     The side of triangular matrix.
     * @param (in) uplo     The triangle in matrix being referenced.
     * @param (in) M         Number of rows in matrices \b B and \b C.
     * @param (in) N         Number of columns in matrices \b B and \b C.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offa      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b M when the \b side parameter is set to
     *                      \b clblasLeft,\n or less than \b N when the
     *                      parameter is set to \b clblasRight.
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offb      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offc      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when
     *                      it is set to \b clblasColumnMajorOrder.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events             Event objects per each command queue that identify
     *                                a particular kernel execution instance.
     *
     * @return The same result as the clblasDsymm() function.
     *
     * @ingroup SYMM
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HEMM HEMM  - Hermitian matrix-matrix multiplication
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Matrix-matrix product of hermitian rectangular matrices with
     * float-complex elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha B A + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) side     The side of triangular matrix.
     * @param (in) uplo     The triangle in matrix being referenced.
     * @param (in) M         Number of rows in matrices \b B and \b C.
     * @param (in) N         Number of columns in matrices \b B and \b C.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offa      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b M when the \b side parameter is set to
     *                      \b clblasLeft,\n or less than \b N when the
     *                      parameter is set to \b clblasRight.
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offb      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offc      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when
     *                      it is set to \b clblasColumnMajorOrder.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - \b M or \b N is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes lead to accessing outsize of any of the buffers;
     *   - \b clblasInvalidMemObject if A, B, or C object is invalid,
     *     or an image object rather than the buffer one;
     *   - \b clblasOutOfResources if you use image-based function implementation
     *     and no suitable scratch image available;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released;
     *   - \b clblasInvalidOperation if kernel compilation relating to a previous
     *     call has not completed for any of the target devices;
     *   - \b clblasCompilerNotAvailable if a compiler is not available;
     *   - \b clblasBuildProgramFailure if there is a failure to build a program
     *     executable.
     *
     * @ingroup HEMM
     * </pre>
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
     * <pre>
     * @example example_chemm.cpp
     * This is an example of how to use the @ref clblasChemm function.
     * </pre>
     */
    /**
     * <pre>
     * Matrix-matrix product of hermitian rectangular matrices with
     * double-complex elements.
     *
     * Matrix-matrix products:
     *   - \f$ C \leftarrow \alpha A B + \beta C \f$
     *   - \f$ C \leftarrow \alpha B A + \beta C \f$
     *
     * @param (in) order     Row/column order.
     * @param (in) side     The side of triangular matrix.
     * @param (in) uplo     The triangle in matrix being referenced.
     * @param (in) M         Number of rows in matrices \b B and \b C.
     * @param (in) N         Number of columns in matrices \b B and \b C.
     * @param (in) alpha     The factor of matrix \b A.
     * @param (in) A         Buffer object storing matrix \b A.
     * @param (in) offa      Offset of the first element of the matrix \b A in the
     *                      buffer object. Counted in elements.
     * @param (in) lda       Leading dimension of matrix \b A. It cannot be less
     *                      than \b M when the \b side parameter is set to
     *                      \b clblasLeft,\n or less than \b N when the
     *                      parameter is set to \b clblasRight.
     * @param (in) B         Buffer object storing matrix \b B.
     * @param (in) offb      Offset of the first element of the matrix \b B in the
     *                      buffer object. Counted in elements.
     * @param (in) ldb       Leading dimension of matrix \b B. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M
     *                      when it is set to \b clblasColumnMajor.
     * @param (in) beta      The factor of matrix \b C.
     * @param (out) C        Buffer object storing matrix \b C.
     * @param (in) offc      Offset of the first element of the matrix \b C in the
     *                      buffer object. Counted in elements.
     * @param (in) ldc       Leading dimension of matrix \b C. It cannot be less
     *                      than \b N when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b M when
     *                      it is set to \b clblasColumnMajorOrder.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasChemm() function otherwise.
     *
     * @ingroup HEMM
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HERK HERK  - Hermitian rank-k update to a matrix
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Rank-k update of a hermitian matrix with float-complex elements.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^H + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^H A + \beta C \f$
     *
     * where \b C is a hermitian matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transA     How matrix \b A is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrix \b A if it is not
     *                       transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrix \b A.
     * @param (in) A          Buffer object storing the matrix \b A.
     * @param (in) offa       Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda        Leading dimension of matrix \b A. It cannot be
     *                       less than \b K if \b A is
     *                       in the row-major format, and less than \b N
     *                       otherwise.
     * @param (in) beta       The factor of the matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offc       Offset in number of elements for the first element in matrix \b C.
     * @param (in) ldc        Leading dimension of matric \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b K is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes lead to accessing outsize of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b A or \b C object is
     *     invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released.
     *
     * @ingroup HERK
     * </pre>
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
     * <pre>
     * @example example_cherk.cpp
     * This is an example of how to use the @ref clblasCherk function.
     * </pre>
     */
    /**
     * <pre>
     * Rank-k update of a hermitian matrix with double-complex elements.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A A^H + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^H A + \beta C \f$
     *
     * where \b C is a hermitian matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) transA     How matrix \b A is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrix \b A if it is not
     *                       transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrix \b A.
     * @param (in) A          Buffer object storing the matrix \b A.
     * @param (in) offa       Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda        Leading dimension of matrix \b A. It cannot be
     *                       less than \b K if \b A is
     *                       in the row-major format, and less than \b N
     *                       otherwise.
     * @param (in) beta       The factor of the matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offc       Offset in number of elements for the first element in matrix \b C.
     * @param (in) ldc        Leading dimension of matric \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCherk() function otherwise.
     *
     * @ingroup HERK
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * @defgroup HER2K HER2K  - Hermitian rank-2k update to a matrix
     * @ingroup BLAS3
     * </pre>
     */
    /**@{*/
    /**
     * <pre>
     * Rank-2k update of a hermitian matrix with float-complex elements.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^H + conj( \alpha ) B A^H + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^H B + conj( \alpha ) B^H A + \beta C \f$
     *
     * where \b C is a hermitian matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) trans      How matrix \b A is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrix \b A if it is not
     *                       transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrix \b A.
     * @param (in) A          Buffer object storing the matrix \b A.
     * @param (in) offa       Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda        Leading dimension of matrix \b A. It cannot be
     *                       less than \b K if \b A is
     *                       in the row-major format, and less than \b N
     *                       otherwise. Vice-versa for transpose case.
     * @param (in) B          Buffer object storing the matrix \b B.
     * @param (in) offb       Offset in number of elements for the first element in matrix \b B.
     * @param (in) ldb        Leading dimension of matrix \b B. It cannot be
     *                       less than \b K if \b B is
     *                       in the row-major format, and less than \b N
     *                       otherwise. Vice-versa for transpose case
     * @param (in) beta       The factor of the matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offc       Offset in number of elements for the first element in matrix \b C.
     * @param (in) ldc        Leading dimension of matric \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasNotInitialized if clblasSetup() was not called;
     *   - \b clblasInvalidValue if invalid parameters are passed:
     *     - either \b N or \b K is zero, or
     *     - any of the leading dimensions is invalid;
     *     - the matrix sizes lead to accessing outsize of any of the buffers;
     *   - \b clblasInvalidMemObject if either \b A , \b B or \b C object is
     *     invalid, or an image object rather than the buffer one;
     *   - \b clblasOutOfHostMemory if the library can't allocate memory for
     *     internal structures;
     *   - \b clblasInvalidCommandQueue if the passed command queue is invalid;
     *   - \b clblasInvalidContext if a context a passed command queue belongs to
     *     was released.
     *
     * @ingroup HER2K
     * </pre>
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
     * <pre>
     * @example example_cher2k.c
     * This is an example of how to use the @ref clblasCher2k function.
     * </pre>
     */
    /**
     * <pre>
     * Rank-2k update of a hermitian matrix with double-complex elements.
     *
     * Rank-k updates:
     *   - \f$ C \leftarrow \alpha A B^H + conj( \alpha ) B A^H + \beta C \f$
     *   - \f$ C \leftarrow \alpha A^H B + conj( \alpha ) B^H A + \beta C \f$
     *
     * where \b C is a hermitian matrix.
     *
     * @param (in) order      Row/column order.
     * @param (in) uplo       The triangle in matrix \b C being referenced.
     * @param (in) trans      How matrix \b A is to be transposed.
     * @param (in) N          Number of rows and columns in matrix \b C.
     * @param (in) K          Number of columns of the matrix \b A if it is not
     *                       transposed, and number of rows otherwise.
     * @param (in) alpha      The factor of matrix \b A.
     * @param (in) A          Buffer object storing the matrix \b A.
     * @param (in) offa       Offset in number of elements for the first element in matrix \b A.
     * @param (in) lda        Leading dimension of matrix \b A. It cannot be
     *                       less than \b K if \b A is
     *                       in the row-major format, and less than \b N
     *                       otherwise. Vice-versa for transpose case.
     * @param (in) B          Buffer object storing the matrix \b B.
     * @param (in) offb       Offset in number of elements for the first element in matrix \b B.
     * @param (in) ldb        Leading dimension of matrix \b B. It cannot be
     *                       less than \b K if B is
     *                       in the row-major format, and less than \b N
     *                       otherwise. Vice-versa for transpose case.
     * @param (in) beta       The factor of the matrix \b C.
     * @param (out) C         Buffer object storing matrix \b C.
     * @param (in) offc       Offset in number of elements for the first element in matrix \b C.
     * @param (in) ldc        Leading dimension of matric \b C. It cannot be less
     *                       than \b N.
     * @param (in) numCommandQueues    Number of OpenCL command queues in which the
     *                                task is to be performed.
     * @param (in) commandQueues       OpenCL command queues.
     * @param (in) numEventsInWaitList Number of events in the event wait list.
     * @param (in) eventWaitList       Event wait list.
     * @param (in) events     Event objects per each command queue that identify
     *                       a particular kernel execution instance.
     *
     * @return
     *   - \b clblasSuccess on success;
     *   - \b clblasInvalidDevice if a target device does not support floating
     *     point arithmetic with double precision;
     *   - the same error codes as the clblasCher2k() function otherwise.
     *
     * @ingroup HER2K
     * </pre>
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


    /**@}*/
    /**
     * <pre>
     * Helper function to compute leading dimension and size of a matrix
     *
     * @param (in) order    matrix ordering
     * @param (in) rows number of rows
     * @param (in) columns  number of column
     * @param (in) elemsize element size
     * @param (in) padding  additional padding on the leading dimension
     * @param (out) ld  if non-NULL *ld is filled with the leading dimension  
     *          in elements
     * @param (out) fullsize    if non-NULL *fullsize is filled with the byte size
     *
     * @return
     *   - \b clblasSuccess for success
     *   - \b clblasInvalidValue if:
     *   - \b elementsize is 0
     *   - \b row and \b colums are both equal to 0
     * </pre>
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
     * <pre>
     * Allocates matrix on device and computes ld and size
     *
     * @param (in) context  OpenCL context
     * @param (in) order    Row/column order.
     * @param (in) rows number of rows
     * @param (in) columns  number of columns
     * @param (in) elemsize element size
     * @param (in) padding  additional padding on the leading dimension
     * @param (out) ld  if non-NULL *ld is filled with the leading dimension  
     *          in elements
     * @param (out) fullsize    if non-NULL *fullsize is filled with the byte size
     * @param (in) err  Error code (see \b clCreateBuffer() )
     * 
     * @return
     *   - OpenCL memory object of the allocated matrix
     * </pre>
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
        return clblasCreateMatrixNative(context, order, rows, columns, elemsize, padding, ld, fullsize, err);
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
     * <pre>
     * Allocates matrix on device with specified size and ld and computes its size
     *
     * @param (in) context  OpenCL context
     * @param (in) order    Row/column order.
     * @param (in) rows number of rows
     * @param (in) columns  number of columns 
     * @param (in) elemsize element size
     * @param (in) padding  additional padding on the leading dimension
     * @param (out) ld  the length of the leading dimensions. It cannot 
     *                      be less than \b columns when the \b order parameter is set to
     *                      \b clblasRowMajor,\n or less than \b rows when the
     *                      parameter is set to \b clblasColumnMajor.
     * @param (out) fullsize    if non-NULL *fullsize is filled with the byte size
     * @param (in) err  Error code (see \b clCreateBuffer() )
     * 
     * @return
     *   - OpenCL memory object of the allocated matrix
     * </pre>
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
        return clblasCreateMatrixWithLdNative(context, order, rows, columns, elemsize, ld, fullsize, err);
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
     * <pre>
     * Allocates matrix on device and initialize from existing similar matrix
     *    on host. See \b clblasCreateMatrixBuffer().
     *
     * @param (in) ld   leading dimension in elements
     * @param (in) host     base address of host matrix data
     * @param (in) off_host     host matrix offset in elements
     * @param (in) ld_host  leading dimension of host matrix in elements
     * @param (in) command_queue        specifies the OpenCL queue
     * @param (in) numEventsInWaitList  specifies the number of OpenCL events 
     *                          to wait for
     * @param (in) eventWaitList        specifies the list of OpenCL events to 
     *                  wait for
     *                  
     * @return
     *   - OpenCL memory object of the allocated matrix
     * </pre>
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
        return clblasCreateMatrixFromHostNative(context, order, rows, columns, elemsize, ld, host, off_host, ld_host, command_queue, numEventsInWaitList, eventWaitList, err);
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
     * <pre>
     * Copies synchronously a sub-matrix from host (A) to device (B).
     * 
     * @param (in) order            matrix ordering
     * @param (in) element_size     element size
     * @param (in) A                specifies the source matrix on the host
     * @param (in) offA         specifies the offset of matrix A in 
     *                  elements
     * @param (in) ldA          specifies the leading dimension of 
     *                  matrix A in elements
     * @param (in) nrA          specifies the number of rows of A 
     *                  in elements
     * @param (in) ncA          specifies the number of columns of A 
     *                  in elements
     * @param (in) xA           specifies the top-left x position to 
     *                  copy from A
     * @param (in) yA           specifies the top-left y position to 
     *                  copy from A
     * @param (in) B                specifies the destination matrix on the 
     *                  device
     * @param (in) offB         specifies the offset of matrix B in 
     *                  elements
     * @param (in) ldB          specifies the leading dimension of 
     *                  matrix B in bytes
     * @param (in) nrB          specifies the number of rows of B 
     *                  in elements
     * @param (in) ncB          specifies the number of columns of B 
     *                  in elements
     * @param (in) xB           specifies the top-left x position to 
     *                  copy from B
     * @param (in) yB           specifies the top-left y position to 
     *                  copy from B
     * @param (in) nx           specifies the number of elements to 
     *                  copy according to the x dimension (rows)
     * @param (in) ny           specifies the number of elements to 
     *                  copy according to the y dimension 
     *                  (columns)
     * @param (in) command_queue        specifies the OpenCL queue
     * @param (in) numEventsInWaitList  specifies the number of OpenCL events 
     *                          to wait for
     * @param (in) eventWaitList        specifies the list of OpenCL events to 
     *                  wait for
     *
     * @return
     *   - \b clblasSuccess for success
     *   - \b clblasInvalidValue if:
     *  - \b xA + \b offA + \b nx is superior to number of columns of A
     *      - \b xB + \b offB + \b nx is superior to number of columns of B
     *      - \b yA + \b ny is superior to number of rows of A
     *      - \b yB + \b ny is superior to number of rows of B
     * </pre>
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
     * <pre>
     * Copies asynchronously a sub-matrix from host (A) to device (B). 
     *    See \b clblasWriteSubMatrix().
     *
     * @param (out) event   Event objects per each command queue that identify a 
     *          particular kernel execution instance.
     * </pre>
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
        cl_event event)
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
        cl_event event);


    /**
     * <pre>
     * Copies a sub-matrix from device (A) to host (B). 
     *    See \b clblasWriteSubMatrix().
     * 
     * @param (in) A        specifies the source matrix on the device
     * @param (in) B        specifies the destination matrix on the host
     *
     * @return
     *   - see \b clblasWriteSubMatrix()
     * </pre>
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
     * <pre>
     * Copies asynchronously a sub-matrix from device (A) to host (B). 
     *    See \b clblasReadSubMatrix() and \b clblasWriteSubMatrixAsync().
     * </pre>
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
        cl_event event)
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
        cl_event event);


    /**
     * <pre>
     * Copies a sub-matrix from device (A) to device (B). 
     *    See \b clblasWriteSubMatrix().
     * 
     * @param (in) A        specifies the source matrix on the device
     * @param (in) B        specifies the destination matrix on the device
     *
     * @return
     *   - see \b clblasWriteSubMatrix()
     * </pre>
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
     * <pre>
     * Copies asynchronously a sub-matrix from device (A) to device (B). 
     *        See \b clblasCopySubMatrix() and \b clblasWriteSubMatrixAsync().
     * </pre>
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
        cl_event event)
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
        cl_event event);


    /**
     * <pre>
     * Copies synchronously a vector from host (A) to device (B). 
     *    See \b clblasWriteSubMatrix().
     * 
     * @param (in) A        specifies the source vector on the host
     * @param (in) B        specifies the destination vector on the device
     *
     * @return
     *   - see \b clblasWriteSubMatrix()
     * </pre>
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
     * <pre>
     * Copies asynchronously a vector from host (A) to device (B). 
     *    See \b clblasWriteVector() and \b clblasWriteSubMatrixAsync().
     * </pre>
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
     * <pre>
     * Copies synchronously a vector from device (A) to host (B). 
     *    See \b clblasReadSubMatrix().
     * 
     * @param (in) A        specifies the source vector on the device
     * @param (in) B        specifies the destination vector on the host
     *
     * @return
     *   - see \b clblasReadSubMatrix()
     * </pre>
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
     * <pre>
     * Copies asynchronously a vector from device (A) to host (B). 
     *    See \b clblasReadVector() and \b clblasWriteSubMatrixAsync().
     * </pre>
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
     * <pre>
     * Copies synchronously a vector from device (A) to device (B). 
     *    See \b clblasCopySubMatrix().
     * 
     * @param (in) A        specifies the source vector on the device
     * @param (in) B        specifies the destination vector on the device
     *
     * @return
     *   - see \b clblasCopySubMatrix()
     * </pre>
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
     * <pre>
     * Copies asynchronously a vector from device (A) to device (B). 
     *    See \b clblasCopyVector() and \b clblasWriteSubMatrixAsync().
     * </pre>
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
     * <pre>
     * Copies synchronously a whole matrix from host (A) to device (B). 
     *        See \b clblasWriteSubMatrix().
     * 
     * @param (in) A        specifies the source matrix on the host
     * @param (in) B        specifies the destination matrix on the device
     *
     * @return
     *   - see \b clblasWriteSubMatrix()
     * </pre>
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
     * <pre>
     * Copies asynchronously a vector from host (A) to device (B). 
     *        See \b clblasWriteMatrix() and \b clblasWriteSubMatrixAsync().
     * </pre>
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
     * <pre>
     * Copies synchronously a whole matrix from device (A) to host (B). 
     *    See \b clblasReadSubMatrix().
     * 
     * @param (in) A        specifies the source vector on the device
     * @param (in) B        specifies the destination vector on the host
     *
     * @return
     *   - see \b clblasReadSubMatrix()
     * </pre>
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
     * <pre>
     * Copies asynchronously a vector from device (A) to host (B). 
     *        See \b clblasReadMatrix() and \b clblasWriteSubMatrixAsync().
     * </pre>
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
     * <pre>
     * Copies synchronously a whole matrix from device (A) to device (B). 
     *    See \b clblasCopySubMatrix().
     * 
     * @param (in) A        specifies the source matrix on the device
     * @param (in) B        specifies the destination matrix on the device
     *
     * @return
     *   - see \b clblasCopySubMatrix()
     * </pre>
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
     * <pre>
     * Copies asynchronously a vector from device (A) to device (B). 
     *        See \b clblasCopyMatrix() and \b clblasWriteSubMatrixAsync().
     * </pre>
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
     * <pre>
     * Fill synchronously a vector with a pattern of a size element_size_bytes
     * 
     * @param (in) nb_elem             specifies the number of element in buffer A
     * @param (in) element_size        specifies the size of one element of A. Supported sizes correspond 
     *                                element size used in clBLAS (1,2,4,8,16)
     * @param (in) A                  specifies the source vector on the device
     * @param (in) offA                specifies the offset of matrix A in 
     *                elements
     * @param (in) pattern             specifies the host address of the pattern to fill with (element_size_bytes)
     * @param (in) command_queue      specifies the OpenCL queue
     * @param (in) numEventsInWaitList specifies the number of OpenCL events 
     *                    to wait for
     * @param (in) eventWaitList      specifies the list of OpenCL events to 
     *                wait for
     * @return
     *   - see \b clblasWriteSubMatrix()
     * </pre>
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
     * <pre>
     * Fill asynchronously a vector with a pattern of a size element_size_bytes
     *    See \b clblasFillVector().
     * </pre>
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
        cl_event event)
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
        cl_event event);


    /**
     * <pre>
     * Fill synchronously a matrix with a pattern of a size element_size_bytes
     * 
     * @param (in) order               specifies the matrix order
     * @param (in) element_size        specifies the size of one element of A. Supported sizes correspond 
     *                                element size used in clBLAS (1,2,4,8,16)
     * @param (in) A                  specifies the source vector on the device
     * @param (in) offA                specifies the offset of matrix A in 
     * @param (in) ldA                 specifies the leading dimension of A
     * @param (in) nrA                 specifies the number of row in A
     * @param (in) ncA                 specifies the number of column in A
     * @param (in) pattern             specifies the host address of the pattern to fill with (element_size_bytes)
     * @param (in) command_queue      specifies the OpenCL queue
     * @param (in) numEventsInWaitList specifies the number of OpenCL events to wait for
     * @param (in) eventWaitList      specifies the list of OpenCL events to wait for
     * @return
     *   - see \b clblasWriteSubMatrix()
     * </pre>
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
     * <pre>
     * Partially fill a sub-matrix with a pattern of a size element_size_bytes 
     *        
     * 
     * @param (in) order               specifies the matrix order
     * @param (in) element_size        specifies the size of one element of A. Supported values 
     *                                are to element sizes used in clBLAS - that is 1, 2, 4, 8 or 16 
     * @param (in) offA                specifies the offset of matrix A in elements
     * @param (in) ldA                 specifies the leading dimension of A in elements
     * @param (in) nrA        specifies the number of rows of A 
     *                in elements
     * @param (in) ncA        specifies the number of columns of A 
     *                in elements
     * @param (in) xA         specifies the top-left x position to 
     *                copy from A
     * @param (in) yA         specifies the top-left y position to 
     *                copy from A
     * @param (in) nx         specifies the number of elements to 
     *                copy according to the x dimension (rows)
     * @param (in) ny         specifies the number of elements to 
     *                copy according to the y dimension 
     *                (columns)
     * @param (in) pattern             specifies the host address of the pattern to fill with (element_size_bytes)
     * @param (in) command_queue      specifies the OpenCL queue
     * @param (in) numEventsInWaitList specifies the number of OpenCL events to wait for
     * @param (in) eventWaitList      specifies the list of OpenCL events to wait for
     * @return
     *   - see \b clblasWriteSubMatrix()
     * </pre>
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
     * <pre>
     * Asynchronous asynchronously fill a sub-matrix with a pattern of a size element_size_bytes  
     *    See \b clblasFillSubMatrix().
     * </pre>
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
        cl_event event)
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
        cl_event event);


    /**
     * Private constructor to prevent instantiation
     */
    private CLBLAS()
    {
        // Private constructor to prevent instantiation
    }
}

