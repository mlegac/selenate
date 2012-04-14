package net.selenate


/** A `SelenateException` is thrown whenever an error occurs within `Selenate` code. */
class SelenateException(message: String, cause: Throwable)
  extends Exception(message, cause) {

  def this(message: String)  = this(message, null)
  def this(cause: Throwable) = this(null, cause)
  def this()                 = this(null, null)
}
