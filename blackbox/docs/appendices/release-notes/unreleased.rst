==================
Unreleased Changes
==================

.. NOTE::

    These changes have not yet been released.

    If you are viewing this document on the Crate.io website, these changes
    reflect what exists on `the master branch`_ in Git. This is where we
    collect changes before they are ready for release.

.. WARNING::

    Unreleased changes may not be ready for general use and could lead to data
    corruption or data loss. You should `back up your data`_ before
    experimenting with unreleased changes.

.. _the master branch: https://github.com/crate/crate
.. _back up your data: https://crate.io/a/backing-up-and-restoring-crate/

.. DEVELOPER README
.. ================

.. Changes should be recorded here as you are developing CrateDB. When a new
.. release is being cut, changes will be moved to the appropriate release notes
.. file.

.. When resetting this file during a release, leave the headers in place, but
.. add a single paragraph to each section with the word "None".

.. rubric:: Table of Contents

.. contents::
   :local:

Breaking Changes
================

Changes
=======

- Expand the ``search_path`` setting to accept a list of schemas that will be
  searched when a relation (table, view or user defined function) is referenced
  without specifying a schema. The system ``pg_catalog`` schema is implicitly
  included as the first one in the path.

- Improved the handling of function expressions inside subscripts used on
  object columns. This allows expressions like ``obj['x' || 'x']`` to be used.

- The ``= ANY`` operator now also supports operations on nested arrays. This
  enables queries like ``WHERE ['foo', 'bar'] =
  ANY(object_array(string_array))``.

- Added support for the ``array(subquery)`` expression.

- ``<object_column> = <object_literal>`` comparisons now try to utilize the
  index for the objects contents and can therefore run much faster.

- Values of byte-size and time based configuration setting do not require a unit
  suffix anymore. Without a unit time values are treat as milliseconds since
  epoch and byte size values are treat as bytes.

- Added support of using units inside byte-size or time bases statement
  parameters values. E.g. '1mb' for 1 MegaByte or '1s' for 1 Second.

- Added support for using generated columns inside object columns.

Fixes
=====

- Calling an unknown user defined function now results in an appropriate error
  message instead of a ``NullPointerException``.

- Fixed processing of the ``endpoint``, ``protocol`` and ``max_retries`` S3
  repository parameters.
