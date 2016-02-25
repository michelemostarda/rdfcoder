This section is meant to provide the basic knowledge required to understand
the concepts behind _RDFCoder_.

## RDF Coder Basics ##

An Object Oriented language like Java is based on the definition of Objects and relationship
among these objects. Any object is constituted of methods and attributes.

A method modifies the attributes of the same object and invokes methods of other objects.
These objects are arranged in logical packages. These packages are grouped inside  physical archives (jars).

All the relationships among archives, packages, classes, attributes methods, and method calls can be
represented as a graph, where nodes are resources (classes, methods attributes, and so on)

and links are the relationship among a couple of resources.

In general a couple of resources `r1`, `r2` and the link `l` connecting them defines a triple expressed as
> `(r1, l, r2)`. In order to disambiguate triples, every resource is usually prefixed with a specific _qualifier_,
describing the type of the resource (package, class, attribute etc).

In a basic example, if we have a jar containing the class `a.b.MyClass`, that defines the methods
> `MyClass.m1()` and the `MyClass.m2(String p1)`, we could express the following triples:

  * `(package a, contains package, package b)`
  * `(package b, contains class, class MyClass)`
  * `(class MyClass, contains method, method m1)`
  * `(class MyClass, contains method, method m2)`
  * `(method m2, defines parameter, parameter p2)`

NOTE: the example above introduces a simplification on the representation of entities and relationships.

## RDF Introduction ##

_RDFCoder_ is based on the [W3C RDF specification](http://www.w3.org/RDF/).

RDF is a framework for expressing and manipulating such kind of graphs. Despite
this technology has been developed for the Web, it is enough flexible to be used
is different domains.

The main concepts behind _RDFCoder_ are:

  * Graph: the graph is a mathematical concept for representing nodes and edges. Read [more](http://en.wikipedia.org/wiki/Graph_(mathematics)).
  * RDF: that is a framework for representing graphs.
  * RDFS: that is a schema language for data  represented in RDF. Read [more](http://www.obitko.com/tutorials/ontologies-semantic-web/rdf-schema-rdfs.html).
  * SPARQL: that is a query language for data represented in RDF. Read [more](http://www.obitko.com/tutorials/ontologies-semantic-web/rdf-query-language-sparql.html).

An independent introduction on these topics can be found [here](http://www.obitko.com/tutorials/ontologies-semantic-web/resource-description-framework.html).

## The Kabbalah Model ##

After having acquired a good confidence with the RDF related topics, you can read more
specific details about RDFCoder and its [model organization](http://rdfcoder.googlecode.com/svn/trunk/doc/htmldoc/model.html).