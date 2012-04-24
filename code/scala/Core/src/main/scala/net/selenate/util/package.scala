package net.selenate

import scala.annotation.tailrec
import org.slf4j.LoggerFactory


/** Provides general utility functions for the entire project. */
package object util {
  /** Repeatedly evaluates specified predicate, and reports weather it
    * eventually becomes ''true''.
    *
    * `predicate` is evaluated with a specified pause between evaluations until
    * one of the following happens:
    * - `predicate` becomes ''true'': function returns ''true''
    * - a timeout occurs: function returns ''false''
    *
    * @param timeout maximum execution duration in milliseconds
    * @param resolution pause in milliseconds between repeated evaluations of this predicate
    * @param predicate value that will repeatedly be evaluated
    * @return ''true'' if predicate eventually evaluates to ''true''; ''false'' if timeout occurs
    */
  def waitFor(timeout: Long, resolution: Long):
      (=> Boolean) => Boolean = (predicate) => {
    // This function does the actual work. waitFor is just a wrapper.
    //   end: an absolute execution end-time, as a UNIX timestamp
    //   resolution: same as in waitFor
    //   predicate: same as in waitFor
    //   returns: same as in waitFor
    @tailrec
    def waitForDoit(end: Long, resolution: Long, predicate: => Boolean): Boolean = {
      val current = System.currentTimeMillis
      val remaining = end - current

      if (remaining < 0) {
        false  // Timeout
      } else {
        if (predicate) {
          true  // Predicate evaluated to true
        } else {
          // Do not oversleep.
          val sleep = scala.math.min(resolution, remaining)
          Thread.sleep(sleep)
          waitForDoit(end, resolution, predicate)
        }
      }
    }

    val end = System.currentTimeMillis + timeout
    waitForDoit(end, resolution, predicate)
  }


  /** Repeatedly evaluates specified predicate, and throws an exception if if
    * never becomes ''true''.
    *
    * `predicate` is evaluated with a specified pause between evaluations until
    * one of the following happens:
    * - `predicate` becomes ''true'': function returns
    * - a timeout occurs: function throws an exception
    *
    * @param timeout maximum execution duration in milliseconds
    * @param resolution pause in milliseconds between repeated evaluations of this predicate
    * @param predicateName used in exception message to provide a clue about what exactly happened
    * @param predicate value that will repeatedly be evaluated
    * @throws SelenateException if this predicate never becomes ''true''
    */
  def waitOrFail(timeout: Long, resolution: Long): String => (=> Boolean) => Unit =
      (predicateName) => (predicate) => {
    val r = waitFor(timeout, resolution)(predicate)
    if (!r)
      throw new SelenateException("An error occured while waiting for %s!".format(predicateName))
  }
}