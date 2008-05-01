package com.asemantics.sourceparse;

import com.asemantics.model.CodeHandler;
import com.asemantics.model.CodeModel;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The Java bytecode parser.
 */
public class JavaBytecodeFileParser extends CodeParser {

    /**
     * Name that bcel uses to describe constructor methods.
     */
    protected static final String INIT_METHOD = "<init>";

    /**
     * Inner classes stack.
     */
    private Stack<JavaClass> containersStack;

    protected JavaBytecodeFileParser() {
        containersStack = new Stack();
    }

    public void parse(JavaBytecodeClassLoader classloader, InputStream inputStream, String fileName) throws IOException {
        containersStack.clear();

        // Parse bytecode.
        ClassParser classParser = new ClassParser( inputStream, fileName );
        JavaClass javaClass = classParser.parse();

        CodeHandler codeHandler   = getCodeHandler();
        ObjectsTable objectsTable = getObjectsTable();

        // Package.
        codeHandler.startPackage( javaClass.getPackageName() );
        try {

            // Objects table update.
            objectsTable.addObject( javaClass.getClassName() );

            // Recursive processing.
            Set processed = new HashSet();
            processClass(codeHandler, classloader, processed, javaClass);

        } finally {
            // Close package.
            codeHandler.endPackage();
        }
    }

    public void parse(JavaBytecodeClassLoader classloader, File file) throws IOException {
        if(file == null) {
            throw new NullPointerException();
        }
        if( ! file.exists() ) {
            throw new IllegalArgumentException("cannot find file: " + file.getAbsolutePath());
        }

        InputStream inputStream = new FileInputStream(file);
        parse(classloader, inputStream, file.getAbsolutePath());
        inputStream.close();
    }

    public void parse( File file) throws IOException {
        parse(null, file);
    }

    protected void processClass(CodeHandler codeHandler, JavaBytecodeClassLoader classloader, Set processed, JavaClass javaClass ) {

        containersStack.push( javaClass );

        codeHandler.startCompilationUnit(javaClass.toString());

        // The stack is currectly handled despite errors.
        try {

            Field[] fields = javaClass.getFields();
            List<String> enumElements = new ArrayList<String>();

            // Extracts class modifiers.
            CodeModel.JModifier[] modifiers = extractModifiers(javaClass);

            if( javaClass.isEnum() ) { // Enumeration.
                for(int i = 0; i < fields.length - 1; i++) {   // Skip last element containing $VALUE
                    if( fields[i].getType().toString().equals( javaClass.getClassName() ) ) { // Excluding real enumElements.
                        enumElements.add( fields[i].getName() );
                    }
                }
                codeHandler.startEnumeration(
                        modifiers,
                        toVisibility( javaClass ),
                        toQualifiedClassName( javaClass ),
                        enumElements.toArray( new String[ enumElements.size() ] )
                );
            } else if( javaClass.isClass() ) { // Class.
                codeHandler.startClass(
                   modifiers,
                   toVisibility(javaClass),
                   toQualifiedClassName(javaClass),
                   toQualifiedSuperClassName(javaClass),
                   toQualifiedInterfaces(javaClass)
                );
            } else if( javaClass.isInterface() ) { // Interface.
                codeHandler.startInterface(
                   toQualifiedClassName(javaClass),
                   toQualifiedInterfaces(javaClass)
                );
            } else {
                throw new IllegalStateException();
            }

            // Fields.
            for(int i = 0; i < fields.length; i++) {
                if( enumElements.contains( fields[i].getName() ) ) {
                    continue;
                }
                codeHandler.attribute(
                        extractModifiers(fields[i]),
                        toVisibility(fields[i]),
                        toQualifiedAttribute(fields[i]),
                        bcelTypeToJType( fields[i].getType() ),
                        toConstantValue( fields[i].getConstantValue() )
                );
            }
            enumElements.clear();


            // Methods.
            Method[] methods = javaClass.getMethods();
            for(int i = 0; i < methods.length; i++) {

                if( INIT_METHOD.equals(methods[i].getName()) ) { // Constructors.
                    codeHandler.constructor(
                        extractModifiers(methods[i]),
                        toVisibility( methods[i] ),
                        i,
                        toParameterNames( methods[i].getArgumentTypes()  ),
                        toParameterTypes( methods[i].getArgumentTypes()  ),
                        toExceptionTypes( methods[i].getExceptionTable() )
                    );
                }

                codeHandler.method(
                        extractModifiers(methods[i]),
                        toVisibility( methods[i] ),
                        toQualifiedMethod( methods[i] ),
                        i,
                        toParameterNames( methods[i].getArgumentTypes() ),
                        toParameterTypes( methods[i].getArgumentTypes() ),
                        bcelTypeToJType ( methods[i].getReturnType()    ),
                        toExceptionTypes( methods[i].getExceptionTable())
                );
            }

            // Inner classes. If class loader is not defined the inner classes are ignored.
            if(classloader != null) {
                String innerClassName = null;
                InnerClass[] innerClasses = innerClasses(javaClass);
                ConstantPool constantPool = javaClass.getConstantPool();
                for(int i = 0; i < innerClasses.length; i++) {
                    innerClassName = constantPool.getConstantString(
                        innerClasses[i].getInnerClassIndex(),
                        Constants.CONSTANT_Class
                    );
                    if( processed.contains(innerClassName) ) {
                        continue;
                    } else {
                        processed.add(innerClassName);
                    }
                    System.out.println("INNER CLASS: " + innerClassName);
                    innerClassName = Utility.compactClassName(innerClassName);
                    JavaClass innerClass = null;
                    try {
                        innerClass = classloader.loadClass(innerClassName);
                        if(innerClass == null) {
                            System.out.println("CANNOT LOAD class: " + innerClassName);
                            continue;
                        }
                    } catch (IOException ioe) {
                        codeHandler.parseError(javaClass.getClassName(), "[" + ioe.getClass().getName() + "]: " + ioe.getMessage());
                        ioe.printStackTrace();
                        continue;
                    }
                }
            }

         } finally {

            // End container.
            if( javaClass.isClass() ) {
                codeHandler.endClass();
            } else if( javaClass.isInterface() ) {
                codeHandler.endInterface();
            } else if(javaClass.isEnum()) {
                codeHandler.endEnumeration();
            } else {
                throw new IllegalStateException();
            }

            containersStack.pop();
            codeHandler.endCompilationUnit();
        }
    }

    private InnerClass[] innerClasses(JavaClass javaClass) {
        Attribute[] attributes = javaClass.getAttributes();
        for(int i = 0; i < attributes.length; i++) {
            if( attributes[i] instanceof InnerClasses ) {
                InnerClasses innerClasses = (InnerClasses) attributes[i];
                return innerClasses.getInnerClasses();
            }
        }
        return new InnerClass[0];
    }

    private CodeModel.JVisibility toVisibility(AccessFlags af) {
        if( af.isPublic() ) {
            return CodeModel.JVisibility.PUBLIC;
        } else if( af.isProtected() ) {
            return CodeModel.JVisibility.PROTECTED;
        } else if( af.isPrivate() ) {
            return CodeModel.JVisibility.PRIVATE;
        } else {
            return CodeModel.JVisibility.DEFAULT;
        }
    }

    private String getContainerPath() {
        if( containersStack.size() == 0 ) {
            throw new IllegalStateException();
        }
        if( containersStack.size() == 1 ) {
            return containersStack.peek().getPackageName();  
        }
        StringBuffer pack = new StringBuffer();
        pack.append( containersStack.get(0).getPackageName() );
        for(int i = 1; i < containersStack.size(); i++) {
            pack.append( containersStack.get(i).getClassName() );
        }
        return pack.toString();
    }

    private String toQualifiedClassName(JavaClass javaClass) {
        return getContainerPath() + CodeHandler.PACKAGE_SEPARATOR + javaClass.getClassName();
    }

    private String toQualifiedSuperClassName(JavaClass javaClass) {
        JavaClass superClass;
        try {
            superClass = javaClass.getSuperClass();
        } catch (ClassNotFoundException e) {
            return null;
        }

        return superClass.getPackageName() + CodeHandler.PACKAGE_SEPARATOR + superClass.getClassName();
    }

    private String[] toQualifiedInterfaces(JavaClass javaClass) {
        JavaClass[] interfaces;
        try {
            interfaces = javaClass.getAllInterfaces();
        } catch (ClassNotFoundException e) {
            return null;
        }
        String[] result = new String[interfaces.length];
        for(int i = 0; i < interfaces.length; i++) {
            result[i] = interfaces[i].getPackageName() + CodeHandler.PACKAGE_SEPARATOR + interfaces[i].getClassName();
        }
        return result;
    }

    private String toQualifiedAttribute(Field field) {
        return getContainerPath() + CodeHandler.PACKAGE_SEPARATOR + field.getName();
    }

    private String toQualifiedMethod(Method method) {
        return getContainerPath() + CodeHandler.PACKAGE_SEPARATOR + method.getName();
    }

    private String toConstantValue(ConstantValue cv) {
        if(cv == null) {
            return null;
        }
        ConstantPool cp =  cv.getConstantPool();
        Constant c = cp.getConstant( cv.getConstantValueIndex() );
        return c.toString();
    }

    private CodeModel.JType bcelTypeToJType(org.apache.bcel.generic.Type type) {

        // Convert basic types.
        if(type == org.apache.bcel.generic.Type.VOID ) {
             return CodeModel.VOID;
        } else if( type == org.apache.bcel.generic.Type.BOOLEAN ) {
            return CodeModel.BOOL;
        } else if( type == org.apache.bcel.generic.Type.INT ) {
            return CodeModel.INT;
        } else if( type == org.apache.bcel.generic.Type.SHORT ) {
            return CodeModel.SHORT;
        } else if( type == org.apache.bcel.generic.Type.BYTE ) {
            return CodeModel.BYTE;
        } else if( type == org.apache.bcel.generic.Type.LONG ) {
            return CodeModel.LONG;
        } else if( type == org.apache.bcel.generic.Type.DOUBLE) {
            return CodeModel.DOUBLE;
        } else if( type == org.apache.bcel.generic.Type.FLOAT ) {
            return CodeModel.FLOAT;
        } else if( type == org.apache.bcel.generic.Type.CHAR ) {
            return CodeModel.CHAR;
        }

        // Convert object types.
        if( type instanceof org.apache.bcel.generic.ObjectType) {
            org.apache.bcel.generic.ObjectType bcelOT = (org.apache.bcel.generic.ObjectType) type;
            return new CodeModel.ObjectType( bcelOT.getClassName() );
        }

        // Array types.
        if ( type instanceof org.apache.bcel.generic.ArrayType ) {
            org.apache.bcel.generic.ArrayType arrayType = (org.apache.bcel.generic.ArrayType) type;
            return new CodeModel.ArrayType(
                    bcelTypeToJType(arrayType.getBasicType() ),
                    arrayType.getDimensions()
            );
        }

        throw new IllegalArgumentException("cannot convert " + type);

    }

    private String toParameterName(org.apache.bcel.generic.Type type ) {
        // Generate name for basic type.
        if( type == org.apache.bcel.generic.Type.BOOLEAN ) {
            return "b";
        } else if( type == org.apache.bcel.generic.Type.INT ) {
            return "i";
        } else if( type == org.apache.bcel.generic.Type.SHORT ) {
            return "s";
        } else if( type == org.apache.bcel.generic.Type.BYTE ) {
            return "e";
        } else if( type == org.apache.bcel.generic.Type.LONG ) {
            return "l";
        } else if( type == org.apache.bcel.generic.Type.DOUBLE) {
            return "d";
        } else if( type == org.apache.bcel.generic.Type.FLOAT ) {
            return "f";
        } else if( type == org.apache.bcel.generic.Type.CHAR ) {
            return "c";
        }

        // Generate name for object type.
        if( type instanceof org.apache.bcel.generic.ObjectType) {
            org.apache.bcel.generic.ObjectType bcelOT = (org.apache.bcel.generic.ObjectType) type;
            String cn = bcelOT.getClassName();
            int i = cn.lastIndexOf(".");
            String result = cn.substring(i + 1);
            if("String".equals(result)) {
                return "s";
            } else if("Object".equals(result)) {
                return "o";
            } else {
                return result;
            }
        }

        // Generate name for array type.
        if ( type instanceof org.apache.bcel.generic.ArrayType ) {
            org.apache.bcel.generic.ArrayType arrayType = (org.apache.bcel.generic.ArrayType) type;
            String typeName = toParameterName( arrayType.getBasicType() );
            return typeName + "s";
        }

        throw new IllegalArgumentException("cannot generate name for " + type);
    }


    private String[] toParameterNames( org.apache.bcel.generic.Type[] type ) {
        HashMap<String,Integer> namesMap = new HashMap();
        String[] names = new String[type.length];
        String designedName;
        for(int i = 0; i < type.length; i++) {
            designedName = toParameterName( type[i] );
            Integer occurrences = namesMap.get(designedName);
            if(occurrences == null) { occurrences = new Integer(0); }
            occurrences++;
            namesMap.put(designedName, occurrences);
            names[i] = designedName + ( occurrences == 1 ? "" : occurrences );
        }
        namesMap.clear();
        return names;
    }

    private CodeModel.JType[] toParameterTypes( org.apache.bcel.generic.Type[] types ) {
        CodeModel.JType[] result = new CodeModel.JType[types.length];
        for(int i = 0; i < types.length; i++) {
            result[i] = bcelTypeToJType(types[i]);
        }
        return result;
    }

    private CodeModel.ExceptionType[] toExceptionTypes(ExceptionTable exceptionTable) {
        if(exceptionTable == null) {
            return null;
        }
        String[] exceptionNames = exceptionTable.getExceptionNames();
        CodeModel.ExceptionType[] exceptions = new CodeModel.ExceptionType[exceptionNames.length];
        for(int i = 0; i < exceptionNames.length; i++) {
            exceptions[i] = new CodeModel.ExceptionType(exceptionNames[i]);
        }
        return exceptions;
    }

    /**
     * Extracts the list of modifiers associated to the given entity.
     * @param accessFlags
     * @return
     */
    private CodeModel.JModifier[] extractModifiers(AccessFlags accessFlags) {
        List<CodeModel.JModifier> modifiers = new ArrayList<CodeModel.JModifier>(CodeModel.JModifier.values().length);
        if( accessFlags.isAbstract() ) {
            modifiers.add(CodeModel.JModifier.ABSTRACT);
        }
        if( accessFlags.isFinal() ) {
            modifiers.add(CodeModel.JModifier.FINAL);
        }
        if( accessFlags.isStatic() ) {
            modifiers.add(CodeModel.JModifier.STATIC);
        }
        if( accessFlags.isVolatile() ) {
            modifiers.add(CodeModel.JModifier.VOLATILE);
        }
        if( accessFlags.isNative() ) {
            modifiers.add(CodeModel.JModifier.NATIVE);
        }
        if( accessFlags.isTransient() ) {
            modifiers.add(CodeModel.JModifier.TRANSIENT);
        }
        if( accessFlags.isSynchronized() ) {
            modifiers.add(CodeModel.JModifier.SYNCHRONIZED);
        }
        return modifiers.toArray(new CodeModel.JModifier[modifiers.size()]);
    }

}
