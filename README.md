
# RDF Coder README

the java source code RDF modeler.

Author: Michele Mostarda ( me@michelemostarda.com )


## About

RDF Coder is a CLI and a [flexible](architecture.md) library designed to generate RDF [models] (model.md) from Java code, 
both source and bytecode.

RDF Coder is written in Java and can be used to perform multi level code inspection, create code dependency graphs, generate custom documentation.

Currently RDF Coder supports only the Java language.

## Use cases

- Code inspection: navigate large package structures, classes, methods, signatures and dependencies among them.
- Code Refactoring: find package, classe and method relationships, planify refactoring.
- Code issue: troubleshouting large project dependencies, find conflicts.
- Custom Documentation: generate custom documentation, on compiled or incomplete code.

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

## Project Resources

RDF Coder is mantained on [Github] (https://github.com/michelemostarda/rdfcoder)

## Issue tracker

Please refer to the internal [issue tracker] (https://github.com/michelemostarda/rdfcoder/projects/1).

## Getting involved

I'm always looking for feedback and collaboration.
Please feel free to write [me] (me@michelemostarda.com) and [fork](https://github.com/michelemostarda/rdfcoder#fork-destination-box) this project to improve it.
