package com.asemantics.model;

import com.asemantics.sourceparse.ObjectsTable;
import com.asemantics.sourceparse.JavadocEntry;
import com.asemantics.model.BackTrackingSupport;

/**
 * This interface represents a code handler.
 *
 * An handler is domething able to receive code analysis
 * events and use it to perform some analysis.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public interface CodeHandler extends BackTrackingSupport {

    /**
     * Expected format of the library datetime.
     */
    public static final String LIBRARY_DATETIME_FORMAT = "yyyy_MM_dd__HH_mm_ss";

    /**
     * The package separator character.
     */
    public static final String PACKAGE_SEPARATOR = ".";

    /**
     * Notifies the beginning of parsing process.
     *
     * @param libraryName the unique name of the parsed library.
     * @param location the location of the parsed library.
     */
    public void startParsing(String libraryName, String location);

    /**
     * Notifies the end of parsing process.
     */
    public void endParsing();

    /**
     * Notifies the begin of a compilation unit (A class or interface or enumeration of first level).
     */
    public void startCompilationUnit(String identifier);

    /**
     * Notifies the end of a compilation unit.
     */
    public void endCompilationUnit();

    /**
     * Nofifies the begin of a package parsing.
     * The package is expected to be FULLY QUALIFIED.
     *
     * @param pathToPackage the abosoute path to the package.
     */
    public void startPackage(String pathToPackage);

    /**
     * Notifies the end of a package parsing.
     */
    public void endPackage();

    /**
     * Notifies the begin of an interface definition.
     *
     * @param pathToInterface the pathToInterface of the interface.
     * @param extendedInterfaces the list of extended interfaces.
     */
    public void startInterface(String pathToInterface, String[] extendedInterfaces);

    /**
     * Notifies the end of an interface.
     */
    public void endInterface();

    /**
     * Notifies the begin of a class parsing.
     * The class is expected to be FULLY QUALIFIED.
     *
     * @param modifiers the class modifiers.
     * @param visibility the class visibility level.
     * @param pathToClass the fully qualified path to the class.
     * @param extendedClass the fully qualified pathToClass of the class extended by the starting class.
     * @param implementedInterfaces the fully qualified names for the interfaces implemented by the starting class.
     */
    public void startClass(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            String pathToClass,
            String extendedClass,
            String[] implementedInterfaces
    );

    /**
     * Notifies the end of a class parsing.
     */
    public void endClass();

    /**
     * Notifies the begin of an enumeration class parsing.
     *
     * The enumeration is expected to be FULLY QUALIFIED.
     *
     * @param modifiers the enumeration modifiers.
     * @param visibility the visibility level of the enumeration.
     * @param pathToEnumeration the fully qualified path to the enumeration.
     * @param elements the list of the elements of the enumeration.
     */
    public void startEnumeration(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            String pathToEnumeration,
            String[] elements
    );

    /**
     * Notifies the end of the enumeration class.
     */
    public void endEnumeration();

    /**
     * Notifies the parsing of an attribute.
     *
     * The attribute is expected to be FULLY QUALIFIED.
     *
     * @param modifiers the attribute modifiers.
     * @param visibility the attribute visibility level.
     * @param pathToAttribute the absolute path to the attribute.
     * @param type the type of the attribute.
     * @param value the init value for the attribute. 
     */
    public void attribute(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            String pathToAttribute,
            CodeModel.JType type,
            String value
    );

    /**
     * Notifies the parsing of a constructor.
     *
     * The length of paramterSize and parameterNames is the same,
     * the association between these elements is done positionally.
     *
     * @param modifiers the constructor modifiers.
     * @param visibility the visibility level for the constructor.
     * @param overloadIndex the overload index for the constructor.
     * @param parameterNames the array of parameter names.
     * @param parameterTypes the array of parameter types.
     * @param exceptions exceptions raised by the contructor.
     *
     */
    public void constructor(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            int overloadIndex,
            String[] parameterNames,
            CodeModel.JType[] parameterTypes,
            CodeModel.ExceptionType[] exceptions
    );

    /**
     * Notifies the parsing of a method.
     *
     * The method is expected to be FULLY QUALIFIED.
     *
     * The length of paramterSize and parameterNames is the same,
     * the association between these elements is done positionally.
     *
     * @param modifiers the method modifiers.
     * @param visibility the method visibility level.
     * @param pathToMethod the fully qualified path to the method.
     * @param overloadIndex the index of the current overload for the method.
     * @param parameterNames the array of parameter names.
     * @param parameterTypes the array of parameter types.
     * @param returnType the return type of the method.
     * @param exceptions exceptions raised by the method.
     *
     */
    public void method(
            CodeModel.JModifier[] modifiers,
            CodeModel.JVisibility visibility,
            String pathToMethod,
            int overloadIndex,
            String[] parameterNames,
            CodeModel.JType[] parameterTypes,
            CodeModel.JType returnType,
            CodeModel.ExceptionType[] exceptions
    );

    /**
     * Notifies an error occurred during parsing.
     *
     * @param location the location of the compilation unit raising the error.
     * @param description the error description.
     * TODO: MED - refactor it.
     */
    public void parseError(String location, String description);

    /**
     * Collects the unresolvedTypes remained unresolved at the end of the parsing.
     *
     * @param unresolvedTypes
     */
    public void unresolvedTypes(String[] unresolvedTypes);

    /**
     * Preloads the given Objects Table with the objects present into the underlying model.
     *
     * @param objectsTable
     */
    public void preloadObjectsFromModel(ObjectsTable objectsTable);

    /**
     * Serializes the unresolved entities of the object table to make them persistent into the model.
     *
     * @param objectTable
     */
    public void serializeUnresolvedTypeEntries(ObjectsTable objectTable);

    /**
     * Deserializes the unresolved entities from the model and populates with them the objects table.
     *
     * @param objectTable
     */
    public void deserializeUnresolvedTypeEntries(ObjectsTable objectTable);

    /**
     * Adds an error listener to the code handler.
     * @param errorListener
     */
    public void addErrorListener(ErrorListener errorListener);

    /**
     * Removes an error listener from the code handler.
     * @param errorListener
     */
    public void removeErrorListener(ErrorListener errorListener);

    /* Javadoc handling. */ //TODO: MED - refactor it.

    /**
     * Raised wether a Javadoc comment is found.
     *
     * @param entry
     */
    public void parsedEntry(JavadocEntry entry);

    /**
     * Raised when the parsed entry refers to a class.
     *
     * @param entry
     * @param pathToClass
     */
    public void classJavadoc(JavadocEntry entry, String pathToClass);

    /**
     * Raised when the parsed entry refers to a method.
     *
     * @param entry
     * @param pathToMethod
     * @param signature
     */
    public void methodJavadoc(JavadocEntry entry, String pathToMethod, String[] signature);

}
