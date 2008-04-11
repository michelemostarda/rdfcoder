package com.asemantics.model;

/**
 * Represents an enumeration.
 */
public class JEnumeration extends JObject {

    public static boolean exists(QueryModel qm, String pathToEnumeration) {
        return qm.enumerationExists(pathToEnumeration);
    }

    /**
      * Constructor by sections.
      * @param queryModel
      * @param sections
      * @throws CodeModelException
      */
     protected JEnumeration(QueryModel queryModel, String[] sections)
             throws QueryModelException {
         super(queryModel, sections);
     }

     /**
      * Constructor by path.
      * @param queryModel
      * @param pathToMethod
      * @throws CodeModelException
      */
     protected JEnumeration(QueryModel queryModel, String pathToMethod)
             throws QueryModelException {
         super(queryModel, pathToMethod);
     }

     private static final JEnumeration[] EMPTY_ENUMERATION = new JEnumeration[0];

    /**
     *  No enumerations can be defined inside an enumeration.
     * @return
     * @throws QueryModelException
     */
     public JEnumeration[] getEnumerations() throws QueryModelException {
        return EMPTY_ENUMERATION;
     }

    /**
     * Returns the elements defined into this enumeration.
     * @return
     */
     public String[] getElements() {
         return getQueryModel().getElements( getFullName() );   
     }

     public boolean exists( String[] name, int index) {
         return exists(getQueryModel(), concatenate(name, index));
     }

     protected String getHyerarchyElemType() {
         return this.getClass().getSimpleName();
     }

}
