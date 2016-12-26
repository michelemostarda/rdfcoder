
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

```{bash}
.~default> help
Usage: <command> <parameters>

        available commands:
        cd                              Change the current directory
        debug                           Show/set the debug flag
        setmodel                        Set a new model
        removemodel                     Remove the current model
        ls                              List the content of the current directory
        clearmodel                      Clear the current model
        loadclasspath                   Load a classpath on the current model
        list                            List the loaded models
        savemodel                       Save the current model on filesystem
        help                            Print this help
                                        to obtain more information about a specific command type: help <command>
        newmodel                        Create a new model
        loadmodel                       Load a model from filesystem
        inspect                         Inspect the active model
        describe                        Describe the object referenced by the given path
        pwd                             Print the current directory
        querymodel                      Query the current model

.~default> 

```

Let's start creating a new model called _test_:

```{bash}
.~default> newmodel test
Model 'test' created.
```
Now let's check that _test_ model has been created:

```{bash}
.~default> list
Models:
        default[X]
        test
```

We see two models, _default_ (the active one is marked with _[X]_) and _test_.

## Project Resources

RDF Coder is mantained on [Github] (https://github.com/michelemostarda/rdfcoder)

## Issue tracker

Please refer to the internal [issue tracker] (https://github.com/michelemostarda/rdfcoder/projects/1).

## Getting involved

I'm always looking for feedback and collaboration.
Please feel free to write [me] (me@michelemostarda.com) and [fork](https://github.com/michelemostarda/rdfcoder#fork-destination-box) this project to improve it.
