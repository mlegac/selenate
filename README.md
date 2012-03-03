Selenate
========

A native Scala framework for [_Selenium 2 / WebDriver_](http://seleniumhq.org/docs/03_webdriver.html).


About
-----

_Selenate_ is a small framework for _Selenium 2 / WebDriver_ written in [_Scala_](http://www.scala-lang.org/).

It is being developed to:

  * Simplify working with real-life sites which are often complicated and messy.
  * Avoid uncountable lines of spaghetti scraping code.
  * Provide a declarative, robust and failsafe way of specifying a scraping
    process.



Use Cases
---------

Some use-cases for which _Selenate_ is specifically designed include:

  * **Sites completely out of scraper's control.**
    * Sites belonging to somebody else.
    * No ability to change or influence any aspect of the site.
    *  No control of when or how the site might change.
    *  No knowledge of what the site's server-side inner-workings are.
    *  No knowledge of all possible states (that is, unexpected things happen
       all the time).

  * **Scrape-unfriendly sites.**
    * Total disregard for any sort of specifications or standards.
    * Broken HTML.
    * Duplicate or missing HTML element IDs.

  * **Sites with dynamic content.**
    * Sites that seem to defy or break _Selenium's_ built-in page reload
      detection.
    * Sites with lots of JavaScript.
    * AJAX and Comet rich sites.

  * **Sites with frames.**

  * **Sites with embedded Java Applets.**



Environment
-----------

_Selenate_ provides support for applications that must be able to run many
scrapes at the same time on the same machine. That rules out `java.awt.Robot`,
or any other technology that assumes a focused window at a known position.

That is why _Selenate_ provides a native, browser-based access to all of its
features.


Features
--------

A list of major features supported by _Selenate_:

  * **Extended element selection functionality**:
    Selectors in _Selenate_ are based on _Selenium's_ selector system as
    provided by the [`By`](http://selenium.googlecode.com/svn/trunk/docs/api/java/index.html?org/openqa/selenium/By.html)
    class. They provide the following extensions:
      * A human-readable name for logging and error-reporting purposes.
      * Multiple `By` values for one selector for added redundancy.
      * If frames are involved, a full path (frame-wise) from the top _frameset_
        to the element for automated frame handling.

  * **Mechanism for saving scrape sessions**: If the scraping process has to be
    paused, its complete state may be saved so that it may be resumed later.

  * **Extensible system for capturing of browser state**: Capturing is an act
    of recording the current browser state: cookies, list of frames,
    screenshot of all frames and current HTML content of all frames. _Selenate_
    provides basic functionality for capturing which can be extended by specific
    _capturers_. These _capturers_ take the browser state data and usually
    output or write it somewhere (console, database, hard drive...).

  * **A robust error-handling functionality**: _Selenate_ provides a `failsafe`
    function that logs all appropriate information in case of any error.

  * **Page reload detection system**: _Selenate_ provides an alternative to
    _Selenium's_ `wait` mechanism.

  * **Tools for communication with embedded Java Applets**: _Selenate_ provides
    a toolbox for communication with Java Applets embedded in the page using
    JavaScript that natively runs within the browser.

  * **Automated frame handling**: When referencing an element using _Selenate's_
    selectors, _Selenate_ automatically takes care of switching to an
    appropriate frame.


License
-------

_Selenate_ is licensed under the 3-clause BSD License.
