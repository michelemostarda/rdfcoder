package com.asemantics.rdfcoder.sourceparse.javadoc;

import com.asemantics.rdfcoder.model.java.JavadocHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class to serialize / deserialize {@link com.asemantics.rdfcoder.model.java.JavadocHandler}
 * messages.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class JavadocHandlerSerializer implements Serializable {

    private List<Invocation> invocations;

    public JavadocHandlerSerializer() {
        invocations = new ArrayList<Invocation>();
    }

    public void clear() {
        invocations.clear();
    }

    public JavadocHandler getHandler() {
        return (JavadocHandler) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{JavadocHandler.class}, 
                new InvocationHandler(){
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(! (proxy instanceof JavadocHandler) ) {
                    throw new IllegalArgumentException("Unexpected object proxy.");
                }
                invocations.add(new Invocation(method.getName(), args));
                return null;
            }
        });
    }

    public void serialize(OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(invocations);
        oos.close();
    }

    public void serialize(File f) throws JavadocHandlerSerializerException {
        try {
            final FileOutputStream fos = new FileOutputStream(f);
            serialize(fos);
        } catch (IOException ioe) {
            throw new JavadocHandlerSerializerException("An error occurred during serialization.", ioe);
        }
    }

    @SuppressWarnings("unchecked")
    public void deserialize(InputStream is, JavadocHandler jh) throws JavadocHandlerSerializerException {
        try {
            final ObjectInputStream ois = new ObjectInputStream(is);
            final List<Invocation> invocations = (List<Invocation>) ois.readObject();
            final Class<JavadocHandler> javadocHandlerClass = JavadocHandler.class;
            Method method;
            for (Invocation invocation : invocations) {
                method = javadocHandlerClass.getMethod(invocation.methodName, toClasses(invocation.args));
                method.invoke(jh, invocation.args);
            }
        } catch (Exception e) {
            throw new JavadocHandlerSerializerException("An error occurred during deserialization.", e);
        }
    }

    public void deserialize(File f, JavadocHandler jh) throws JavadocHandlerSerializerException {
        try {
            final FileInputStream fis = new FileInputStream(f);
            deserialize(fis, jh);
        } catch (Exception e) {
            throw new JavadocHandlerSerializerException("An error occurred during deserialization.", e);
        }
    }

    private Class[] toClasses(Object[] objects) {
        if(objects == null) {
            return null;
        }
        Class[] classes = new Class[objects.length];
        for(int i=0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }

    private class Invocation implements Serializable {
        private final String methodName;
        private final Object[] args;

        public Invocation(String methodName, Object[] args) {
            this.methodName = methodName;
            this.args   = args;
        }
        @Override
         public String toString() {
            return String.format("%s %s (%s)", this.getClass().getSimpleName(), methodName, Arrays.toString(args) );
        }
    }

}
