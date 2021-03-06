
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.4
 *  @date    Mon Sep  2 16:24:38 EDT 2013
 *  @see     LICENSE (MIT style license file).
 */

package scalation.analytics

import scalation.linalgebra.MatrixD

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `Reducer` trait provides a common framework for several data reduction
 *  algorithms.
 */
trait Reducer
{
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Given the original data matrix, produce a lower dimensionality matrix
     *  that maintains most of the descriptive power of the original matrix.
     */
    def reduce (): MatrixD

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Approximately recover the original matrix.  The new matrix will have
     *  the same dimensionality, but will have some lose of information.
     */
    def recover (): MatrixD

} // Reducer trait

