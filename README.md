BabelTrace
==========

Trace validation is the process of evaluating a formal specification over a log
of recorded events produced by a system. In addition to the numerous techniques
developed specifically for that purpose over the years, a range of peripheral
tools such as model checkers and database engines can also be used as *bona
fide* trace validators. BabelTrace is an evolvable software environment that
provides a large set of transducers which, when piped into an appropriate chain,
can transform a trace and a formal specification into a suitable input problem
for a variety of available tools.

## Supported Tools

Currently BabelTrace supports translations from LTL-FO+ and XML to the
following software:

- [BeepBeep](http://beepbeep.sourceforge.net/)
- A trace analyzer based on filter logic (to be published)
- [MonPoly](https://projects.developer.nokia.com/MonPoly/wiki)
- [MySQL](http://www.mysql.com/)
- [NuSMV](http://nusmv.fbk.eu/)
- [Saxon](http://saxonica.com/welcome/welcome.xml)
- [Spin](http://www.spinroot.com/)

## References

For more information about BabelTrace, please refer to the following
publication:

- A. Mrad, S. Hallé, E. Beaudet. (2012).  A Collection of Transducers For
  Trace Validation.  Proc. 3rd International Conference on Runtime
  Verification, Istanbul, Turkey, September 2012. Springer: Lecture Notes in
  Computer Science, to appear in 2013.
