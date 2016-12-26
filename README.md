
# RDF Coder README

the java source code RDF modeler.

Author: Michele Mostarda ( me@michelemostarda.com )


## About

RDF Coder is a CLI and a [flexible](architecture.md) library designed to generate RDF [models] (model.md) from Java code, 
both source and bytecode.

RDF Coder is written in Java and can be used to perform multi level code inspection, create code dependency graphs, generate custom documentation.

Currently RDF Coder supports only the Java language.

## Use cases

- Code Analysis of large package structures, classes, signatures and dependencies.
- Code Refactoring: find library issues and relationships.
- Custom Documentation: generate custom documentation.

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


