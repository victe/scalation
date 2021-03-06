
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.4
 *  @date    Mon Apr 24 21:28:06 EDT 2017
 *  @see     LICENSE (MIT style license file).
 *
 *  @author Simon Lucey 2012 (MatLab version)
 *  @see www.simonlucey.com/lasso-using-admm/
 *  Coverted to Scala with change of variables
 */

package scalation.minima

import scala.math.{abs, max, min}

import scalation.linalgebra.{MatrixD, VectorD}
import scalation.math.{double_exp, sign}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `LassoAdmm` class performs LASSO regression using Alternating Direction
 *  Method of Multipliers (ADMM).  Minimize the following objective function to
 *  find an optimal solutions for 'x'.
 *  <p>
 *      argmin_x (1/2)||Ax − b||_2^2 + λ||x||_1
 *
 *      A = data matrix
 *      b = response vector
 *      λ = weighting on the l_1 penalty
 *      x = solution (coefficient vector)
 *  <p>
 *  @see euler.stat.yale.edu/~tba3/stat612/lectures/lec23/lecture23.pdf
 *  @param a  the data matrix
 *  @param b  the response vector
 *  @param λ  the regularization l_1 penalty weight
 */
class LassoAdmm (a: MatrixD, b: VectorD, λ: Double = 0.01)
{
    private val DEBUG   = true                               // debug flag
    private val maxIter = 100                                // maximum number of iterations
    private val maxRho  = 5.0                                // maximum value for ρ
    private var ρ       = 1E-4                               // set initial value for ρ to be quite low

    private val (m, n) = (a.dim1, a.dim2)                    // # rows, # columns in data matrix
    private val ata    = a.t * a                             // a transpose times a
    private val ρI     = new MatrixD (n, n)                  // to hold ρ * I

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Solve for 'x' using ADMM.
     */
    def solve (): VectorD =
    {
        var x: VectorD = null                                // the solution (coefficient vector)
        var z = new VectorD (n)                              // the ? vector - FIX - may need to randomize
        val l = new VectorD (n)                              // the Lagrangian vector

        for (k <- 0 until maxIter) {                         // FIX - break loop if no progress

            ρI.setDiag (ρ)
            x  = (ata + ρI).inverse * (a.t * b + z * ρ - l)  // solve sub-problem for x 
            z  = fast_sthresh (x + l/ρ, λ/ρ)                 // solve sub-problem for z
            l += (x - z) * ρ                                 // update the Lagrangian l
            ρ  = min (maxRho, ρ * 1.1)                       // increase ρ slowly

            if (DEBUG) println (s"on iteration $k: x = $x")
        } // for
        x
    } // solve

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the fast soft thresholding function.
     *  @param v    the vector to threshold
     *  @param thr  the threshold
     */
    def fast_sthresh (v: VectorD, thr: Double): VectorD =
    {
        VectorD (for (i <- v.range) yield sign (max (abs (v(i)) - thr, 0.0), v(i)))
    } // fast_sthresh

} // LassoAdmm class


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `LassoAdmmTest` object tests `LassoAdmm` class using the following
 *  regression equation.
 *  <p>
 *      y  =  b dot x  =  b_0 + b_1*x_1 + b_2*x_2.
 *  <p>
 *  @see statmaster.sdu.dk/courses/st111/module03/index.html
 *  > run-main scalation.minima.LassoAdmmTest
 */
object LassoAdmmTest extends App
{
    val a = new MatrixD ((5, 3), 1.0, 36.0,  66.0,               // 5-by-3 data matrix
                                 1.0, 37.0,  68.0,
                                 1.0, 47.0,  64.0,
                                 1.0, 32.0,  53.0,
                                 1.0,  1.0, 101.0)
    val b = VectorD (745.0, 895.0, 442.0, 440.0, 1598.0)         // response vector

    val admm = new LassoAdmm (a, b)
    val x    = admm.solve ()                                     // optimal coefficient vector
    val e    = b - a * x                                         // error vector
    val sse  = e dot e                                           // sum of squared errors
    val sst  = (b dot b) - b.sum~^2.0 / b.dim.toDouble           // total sum of squares
    val ssr  = sst - sse                                         // regression sum of squares
    val rSquared = ssr / sst                                     // coefficient of determination

    println (s"x        = $x")
    println (s"e        = $x")
    println (s"sse      = $sse")
    println (s"rSquared = $rSquared")

} // LassoAdmmTest object

