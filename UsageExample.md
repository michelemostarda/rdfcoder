### This page shows how to use _RDFCoder_ programmatically ###

```

// Creates an RDFCoder facade instance on a specified repository.
RDFCoder coder = new RDFCoder("target_test/hla_repo");

// Enable debug mode.
coder.setDebug(true);

// Register the Java profile.
coder.registerProfile("java", "com.asemantics.JavaProfile");

// Create a model.
Model model = coder.createModel("test_model");

// Enable the model validation.
model.setValidating(true);

// Retrieve the Java profile on test_model.
JavaProfile jprofile = (JavaProfile) model.getProfile("java");

// Initialize the JRE model if not yet done.
final File JRE = new File("/path/to/current/JRE" );
if ( ! jprofile.checkJREInit(JRE)) {
    JREReport jreReport = jprofile.initJRE(JRE);
    System.out.println(jreReport);
}
        
// Retrieve the Java profile ontology in OWL format.
jprofile.printOntologyOWL( System.out );

// Parse Java sources.
JStatistics s1 = jprofile.loadSources("src_lib"  , "path/to/source/files");
System.out.println(s1);

// Parse Java compiled classes.
JStatistics s2 = jprofile.loadClasses("class_lib", "path/to/bytecode/files");
System.out.println(s2);

// Parse a classpath.
JStatistics s3 = jprofile.loadJar    ("jar_lib"  , "path/to/file.jar");
System.out.println(s3);

// Retrieve the query model.
JavaQueryModel jquery = jprofile.getQueryModel();

// Retrive the attributes of java.lang.String class.
JAttribute[] attributes = jquery.getAttributesInto("java.lang.String");

// Perform a SPARQL query.
if( model.supportsSparqlQuery() ) {
    QueryResult result = model.sparqlQuery("<SPARQL query>");
    System.out.println(result);
}

// Persist the model data.
model.save();

// Remove all data inside the model.
model.clear();

// Load the data stored in old_model inside the current model.
model.load("old_model");

```