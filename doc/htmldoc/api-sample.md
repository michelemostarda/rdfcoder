# API Sample

This code snippet shows how to use the RDFCoder API to analyze the content of the current JVM classpath.

```{java}
// Creates an RDFCoder instance on a specified repository.
RDFCoder coder = new RDFCoder("target_test/hla_repo");

// Enables debug mode.
coder.setDebug(true);

// Registers Java profile.
coder.registerProfile("java", "com.asemantics.JavaProfile");

// Creates a model, i.e. a set of libraries.
Model model = coder.createModel("test_model");

// Enables model validation over profile ontologies.
model.setValidating(true);

// Retrieves the Java profile on test_model.
JavaProfile jprofile = (JavaProfile) model.getProfile("java");

// Initializes the JRE model if not yet done.
final File JRE = new File("/path/to/current/JRE" );
if ( ! jprofile.checkJREInit(JRE)) {
    JREReport jreReport = jprofile.initJRE(JRE);
    System.out.println(jreReport);

}

// Retrieves the Java profile ontology.
jprofile.printOntologyOWL( System.out );

// Processes Java libraries.
JStatistics s1 = jprofile.loadSources("src_lib"  , "path/to/source/files");
System.out.println(s1);

JStatistics s2 = jprofile.loadClasses("class_lib", "path/to/bytecode/files");
System.out.println(s2);

JStatistics s3 = jprofile.loadJar    ("jar_lib"  , "path/to/file.jar");
System.out.println(s3);

// Querying java model.
JavaQueryModel jquery = jprofile.getQueryModel();

// Retrives the attributes of java.lang.String class.
JAttribute[] attributes = jquery.getAttributesInto("java.lang.String");

// Low level querying.
if( model.supportsSparqlQuery() ) {
    QueryResult result = model.sparqlQuery("<SPARQL query>");
    System.out.println(result);
}

// Saves model data.
model.save();

// Resets the model content.
model.clear();

// Loads existing model data into the current model.
model.load("new_model_name");
```
