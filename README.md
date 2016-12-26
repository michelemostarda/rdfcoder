
# RDF Coder README

the java source code RDF modeler.

Author: Michele Mostarda ( me@michelemostarda.com )


## About

RDF Coder is a CLI and a [flexible](architecture.md) library designed to generate RDF [models] (model.md) from Java code, 
both source and bytecode.

RDF Coder is written in Java and can be used to perform multi level code inspection, create code dependency graphs, generate custom documentation.

Currently RDF Coder supports only the Java language.

## Use cases

- *Code inspection*: navigate large package structures, classes, methods, signatures and dependencies among them.
- *Code Refactoring*: find package, classe and method relationships, planify refactoring.
- *Code issue*: troubleshouting large project dependencies, find conflicts.
- *Custom Documentation*: generate custom documentation, on compiled or incomplete code.

## Requirements
- JDK  +1.5
- Apache Ant +1.7.0

## Installation

Clone or download the latest version on [Github] (https://github.com/michelemostarda/rdfcoder), build and use it.

```{bash}
git clone https://github.com/michelemostarda/rdfcoder.git

cd rdfcoder

ant clean dist

bin/rdfcoder
```
## Quick tutorial (5 minutes)

### Run the CLI

Assuming that you have downloaded and compiled RDFCoder successfully, now you can run the CLI

```{bash}
bin/rdfcoder
```
You'll see a message confirming that the JRE model has been loaded into the _default model_.

```
Initializing JRE ...
JRE model loaded.
RDFCoder command line console [version 0.5]
.~default>
```

A _model_ is a container of RDF _statements_.
RDFCoder provides a default model containing the statements representing the JRE (Java Runtime Environment).
Using the CLI you can:
- get help;
- list all the existing models;
- create new models;
- delete existing models;
- load statements into the current model;
- clear the content of the model returning it to the original state;
- inspect the structure of data stored in a model;
- query a model;
- dump the content of a model on the file system.

To start let's invoke the help command:

```
.~default> help
Usage: <command> <parameters>

        available commands:
        cd                              Change the current directory
        debug                           Show/set the debug flag
        setmodel                        Set a new model
        removemodel                     Remove the current model
        ls                              List content of current directory
        clearmodel                      Cleanup the current model
        loadclasspath                   Load a classpath on the active model
        list                            List the loaded models
        savemodel                       Save the current model on file system
        help                            Print this help
                                        to obtain more information about a command type: help <command>
        newmodel                        Create a new model
        loadmodel                       Load a model from file system
        inspect                         Inspect the active model
        describe                        Describe the object referenced by the given path
        pwd                             Print the current directory
        querymodel                      Query the active model

```

Let's start creating a new model called _test_:

```
.~default> newmodel test
Model 'test' created.
```
Now let's check that _test_ model has been created:

```
.~default> list
Models:
        default[X]
        test
```

We see two models, _default_ (the active one is marked with _[X]_) and _test_.

Let's change now the active model to _test_:

```
.~default> setmodel test
Model set to 'test'
.~test> 
```
we can see the new model into the prompt.

It's time now to load statements into the active model. RDFCoder supports two sources of data:
- JAR files;
- source code directories.
To parse data from any source we always use the command _loadclasspath_.

The _loadclasspath_ command accepts both relative and absolue paths, to use relative path the RDF CLI must be avare
of a current directory.

You can check your current directory with _pwd_:

```
.~test> pwd
/Users/hardest/repository/RDFCoder/rdfcoder-github/.
```
You can also list the content of the current directory with _ls_:

```
.~test> ls 
/Users/hardest/repository/RDFCoder/rdfcoder-github/.
d       bin                     rwed    170
-       build.xml               rwed    9938
d       classes                 rwed    102
d       dist                    rwed    102
d       doc                     rwed    102
d       javadoc                 rwed    612
d       lib                     rwed    782
[...]
```

For this tutorial we can use the content of the _lib_ directory:

```
.~test> ls lib
/Users/hardest/repository/RDFCoder/rdfcoder-github/./lib

-       antlr-2.7.5.jar                 rwed    435563
-       arq.jar                         rwed    818802
-       bcel-6.0.jar                    rwed    670734
-       commons-logging.jar             rwed    38015
-       concurrent.jar                  rwed    126474
[...]
.~test> 
```

Let's choose the _arq.jar_ for the test.

```
rdfcoder-github~test> loadclasspath arq jar:lib/arq.jar
loading /Users/hardest/repository/RDFCoder/rdfcoder-github/lib/arq.jar ... done
parsing time (secs):0.0
parsed files: 543
parsed classes: 494
parsed interfaces: 49
parsed attributes: 1853
parsed constructors: 768
parsed methods: 3748
parsed enumerations: 0
javadoc entries: 0
classes javadoc: 0
fields javadoc: 0
constructors javadoc: 0
methods javadoc: 0
generated temporary identifiers: 0
replaced entries: 0
unresolved [0] {
}
parse errors[0] {
}
```

The command output shows the data extraction statistics in term of detected entities.

The loadclasspath command accepts an identifier for the library and classpath resolutor, expressed with syntax ```<format>:<path>```.
Read the command documentation with:

```
rdfcoder-github~test> help loadclasspath
Load a classpath on the active model
syntax: loadclasspath <library_name> <library_location> [<library_name> <library_location>]+
where <library_location> can be expressed as
    a jar    file:  jar:/path/to/jarfile.jar
    a source  dir:  src:/path/to/src
    a javadoc dir:  javadoc:/path/to/src
    a class   dir:  class:/path/to/class
Performs a parsing of the given set of resources
and loads extracted data within the current model
```

## Project Resources

RDF Coder is mantained on [Github] (https://github.com/michelemostarda/rdfcoder)

## Issue tracker

Please refer to the internal [issue tracker] (https://github.com/michelemostarda/rdfcoder/projects/1).

## Getting involved

I'm always looking for feedback and collaboration.
Please feel free to write [me] (me@michelemostarda.com) and [fork](https://github.com/michelemostarda/rdfcoder#fork-destination-box) this project to improve it.
