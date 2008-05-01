package com.asemantics.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents every entity able to contain other entities.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public abstract class JContainer extends JModifiable {

    protected JContainer(QueryModel qm, String[] sections)
    throws QueryModelException {
        super(qm, sections);
    }

    protected JContainer(QueryModel qm, String pathToContainer)
    throws QueryModelException {
        super(qm, pathToContainer);
    }

    /**
     * Returns the parent hierarchy.
     * @return
     */
    public JContainer[] getPath() {
        List list = new ArrayList();
        list.add(0, this);
        JContainer current = parent;
        while(current != null) {
            list.add(0, current);
            current = current.parent;
        }
        return (JContainer[]) list.toArray( new JContainer[list.size()] );
    }

    /**
     * Generates the debug path of a container path.
     * @param path
     * @return
     */
    public static String getDebugPath(JContainer[] path) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < path.length; i++) {
            sb.append(path[i].getName()).append(":").append(path[i].getHyerarchyElemType());
            if(i < path.length -1) {
                sb.append(CodeHandler.PACKAGE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    /**
     * Generates the debug path of this container.
     * @return
     */
    public String getDebugPath() {
        return getDebugPath(getPath());
    }

    /**
     * Joins the container hierarchy to a path string.
     * @return
     */
    public static String getPathAsString(JContainer[] path) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < path.length; i++) {
            sb.append( path[i].getName() );
            if(i < path.length -1) {
                sb.append(CodeHandler.PACKAGE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    public String getPathAsString() {
        return getPathAsString(getPath());
    }

    public String toString() {
        return getDebugPath();
    }

}
