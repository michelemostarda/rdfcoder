# RDF Coder #

Author: _Michele Mostarda ( michele.mostarda@gmail.com )_

## Introduction ##

_RDF Coder_ is a tool able to generate [RDF](http://www.w3.org/RDF) models of
code libraries, entirely written in Java. _RDF Coder_ can be used to perform multi
level code inspection, create code dependency graphs and generate custom
documentation. Currently is supported the **Java** language only at version _1.5_.

## Audience ##

This library can be used by Java programmers as a tool to deal with huge classpaths,
to find relationships across classes and objects. _RDFCoder_ can be also used to develop
more complex analysis tools leveraging on the flexibility of the RDF related technologies.

## Key Concepts ##

Before reading the examples, it is suggested to look at [Key Concepts](KeyConcepts.md) section.

## Tutorials ##

Use the _RDFCoder_ [commandline interface](http://rdfcoder.googlecode.com/svn/trunk/doc/htmldoc/quickstart.html).

Use _RDFCoder_ [programmatically](http://rdfcoder.googlecode.com/svn/trunk/doc/htmldoc/api-sample.html).

## Download ##

The _RDF Coder_ source trunk can be browsed [here](http://code.google.com/p/rdfcoder/source/browse/trunk).

To download:

```
   svn co http://code.google.com/p/rdfcoder/source/browse/trunk rdfcoder
```

## Distribution ##

```
export RDFCODER_HOME=path/to/rdfcoder/
```

ls $RDFCODER\_HOME

|_Folder_|_Description_|_Generated_|
|:-------|:------------|:----------|
|build.xml|Ant build file.|No         |
|src     |The source code|No         |
|classes |The compiled classes.|Yes        |
|bin     |Some bash scripts|No         |
|lib     |Project libraries.|No         |
|test    |Unit test classes.|No         |
|target\_test|Contains some classes and source code used to test parsers.|No         |
|reports |Contains the test reports.|Yes        |
|doc     | Static documentation|No         |
|dist    |Contains the distribution executables.|Yes        |
|jrefactory-2.9.19-full|Subproject dependecy.|No         |


## Documentation ##

See the _RDF Coder_ [documentation](Documentation.md)

or

$RDFCODER\_HOME/doc

## Build ##

Build _RDF Coder_ requires [Ant 1.7.0 (>)](http://ant.apache.org)

```
cd $RDFCODER_HOME

ant clean dist
```


## Javadoc ##

```
cd $RDFCODER_HOME

ant clean javadoc
```


## License ##

```
Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ). 
All Rights Reserved.
  
  Licensed under the Apache License, Version 2.0 (the 'License');
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
      http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an 'AS IS' BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```