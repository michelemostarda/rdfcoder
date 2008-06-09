package net.sourceforge.jrefactory.ast;

import net.sourceforge.jrefactory.parser.JavaParser;


/**
 *  This class is the base for all Nodes that contain modifiers ("public", "native", etc.).
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class AccessNode extends SimpleNode implements ModifierHolder {

   /**  Description of the Field  */
   protected int modifiers = 0;
   /**  Description of the Field */
   public final static int ALPHABETICAL_ORDER = 1;
   /**  Description of the Field */
   public final static int STANDARD_ORDER = 2;


   /**
    *  Constructor for the AccessNode object.
    *
    *  Access nodes are not designed to be constructed directly.
    *
    * @param  parser      Description of Parameter
    * @param  identifier  Description of Parameter
    */
   public AccessNode(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Constructor for the AccessNode object
    *
    *  Access nodes are not designed to be constructed directly.
    *
    * @param  identifier  Description of Parameter
    */
   public AccessNode(int identifier) {
      super(identifier);
   }



   /**
    *  Sets the private bit in the modifiers
    *
    * @param  value  <code>true</code> if we are setting the private modifier
    */
   public void setPrivate(boolean value) {
      setCode(value, PRIVATE);
   }


   /**  Sets the private bit (to <code>true</code>) in the modifiers  */
   public void setPrivate() {
      modifiers = modifiers | PRIVATE;
   }


   /**
    *  Sets the protected bit in the modifiers
    *
    * @param  value  <code>true</code> if we are setting the protected modifier
    */
   public void setProtected(boolean value) {
      setCode(value, PROTECTED);
   }


   /**  Sets the protected bit (to <code>true</code>) in the modifiers  */
   public void setProtected() {
      modifiers = modifiers | PROTECTED;
   }


   /**
    *  Sets the public bit in the modifiers
    *
    * @param  value  <code>true</code> if we are setting the public modifier
    */
   public void setPublic(boolean value) {
      setCode(value, PUBLIC);
   }


   /**  Sets the public bit (to <code>true</code>) in the modifiers  */
   public void setPublic() {
      modifiers = modifiers | PUBLIC;
   }


   /**
    *  Sets the abstract bit in the modifiers
    *
    * @param  value  <code>true</code> if we are setting the modifier
    */
   public void setAbstract(boolean value) {
      setCode(value, ABSTRACT);
   }


   /**  Sets the abstract bit (to <code>true</code>) in the modifiers  */
   public void setAbstract() {
      modifiers = modifiers | ABSTRACT;
   }


   /**
    *  Sets the Synchronized bit in the modifiers
    *
    * @param  value  The new Synchronized value
    */
   public void setSynchronized(boolean value) {
      setCode(value, SYNCHRONIZED);
   }


   /**  Sets the Synchronized bit (to <code>true</code>) in the modifiers  */
   public void setSynchronized() {
      modifiers = modifiers | SYNCHRONIZED;
   }


   /**
    *  Sets the Static bit in the modifiers
    *
    * @param  value  The new Static value
    */
   public void setStatic(boolean value) {
      setCode(value, STATIC);
   }


   /**  Sets the Static bit (to <code>true</code>) in the modifiers  */
   public void setStatic() {
      modifiers = modifiers | STATIC;
   }


   /**  Sets the Final bit (to <code>true</code>) of the in the modifiers  */
   public void setFinal() {
      modifiers = modifiers | FINAL;
   }


   /**  Sets the Final bit (to <code>true</code>) of the in the modifiers  */
   public void setVolatile() {
      modifiers = modifiers | VOLATILE;
   }


   /**  Sets the Final bit (to <code>true</code>) of the in the modifiers  */
   public void setTransient() {
      modifiers = modifiers | TRANSIENT;
   }


   /**  Sets the Final bit (to <code>true</code>) of the in the modifiers  */
   public void setNative() {
      modifiers = modifiers | NATIVE;
   }


   /**  Sets the Final bit (to <code>true</code>) of the in the modifiers  */
   public void setInterface() {
      modifiers = modifiers | INTERFACE;
   }


   /**  Sets the StrictFP bit (to <code>true</code>) of the in the modifiers  */
   public void setStrict() {
      modifiers = modifiers | STRICTFP;
   }


   /**
    *  Sets the modifier bits
    *
    * @param  modifiers  the modifier bits
    */
   public void setModifiers(int modifiers) {
      this.modifiers = modifiers;
   }


   /**
    *  Determine if the node is abstract
    *
    * @return    <code>true</code> if this stores an ABSTRACT flag
    */
   public boolean isAbstract() {
      return ((modifiers & ABSTRACT) != 0);
   }


   /**
    *  Determine if the node is explicit
    *
    * @return    <code>true</code> if this stores an EXPLICIT flag
    */
   public boolean isExplicit() {
      return ((modifiers & EXPLICIT) != 0);
   }


   /**
    *  Determine if the node is final
    *
    * @return    <code>true</code> if this stores an FINAL flag
    */
   public boolean isFinal() {
      return ((modifiers & FINAL) != 0);
   }


   /**
    *  Determine if the node is interface
    *
    * @return    <code>true</code> if this stores an INTERFACE flag
    */
   public boolean isInterface() {
      return ((modifiers & INTERFACE) != 0);
   }


   /**
    *  Determine if the node is native
    *
    * @return    <code>true</code> if this stores an NATIVE flag
    */
   public boolean isNative() {
      return ((modifiers & NATIVE) != 0);
   }


   /**
    *  Determine if the node is private
    *
    * @return    <code>true</code> if this stores an PRIVATE flag
    */
   public boolean isPrivate() {
      return ((modifiers & PRIVATE) != 0);
   }


   /**
    *  Determine if the node is protected
    *
    * @return    <code>true</code> if this stores an PROTECTED flag
    */
   public boolean isProtected() {
      return ((modifiers & PROTECTED) != 0);
   }


   /**
    *  Determine if the node is public
    *
    * @return    <code>true</code> if this stores an PUBLIC flag
    */
   public boolean isPublic() {
      return ((modifiers & PUBLIC) != 0);
   }


   /**
    *  Determine if the node is static
    *
    * @return    <code>true</code> if this stores an static flag
    */
   public boolean isStatic() {
      return ((modifiers & STATIC) != 0);
   }


   ///**
   // *  Determine if the node is strict
   // *
   // *@return    <code>true</code> if this stores an STRICT flag
   // */
   //public boolean isStrict() {
   //    return ((modifiers & STRICT) != 0);
   //}


   /**
    *  Determine if the node is strictFP
    *
    * @return    <code>true</code> if this stores an STRICTFP flag
    */
   public boolean isStrictFP() {
      return ((modifiers & STRICTFP) != 0);
   }


   /**
    *  Determine if the node is synchronized
    *
    * @return    <code>true</code> if this stores an SYNCHRONIZED flag
    */
   public boolean isSynchronized() {
      return ((modifiers & SYNCHRONIZED) != 0);
   }


   /**
    *  Determine if the node is transient
    *
    * @return    <code>true</code> if this stores an TRANSIENT flag
    */
   public boolean isTransient() {
      return ((modifiers & TRANSIENT) != 0);
   }


   /**
    *  Determine if the node is volatile
    *
    * @return    <code>true</code> if this stores an VOLATILE flag
    */
   public boolean isVolatile() {
      return ((modifiers & VOLATILE) != 0);
   }


   /**
    *  Determines if this has package scope
    *
    * @return    <code>true</code> if this has package scope
    */
   public boolean isPackage() {
      return !isPublic() && !isProtected() && !isPrivate();
   }


   /**
    *  Gets the modifier bits
    *
    * @return    the modifier bits
    */
   public int getModifiers() {
      return modifiers;
   }


   /**
    *  Returns a string containing all the modifiers
    *
    * @param  code  the code used to determine the order of the modifiers
    * @return       the string representationof the order
    */
   public String getModifiersString(int code) {
      if (code == ALPHABETICAL_ORDER) {
         return toStringAlphabetical();
      } else {
         return toStandardOrderString();
      }
   }


   /**
    *  Add a modifier
    *
    * @param  mod  the new modifier
    */
   public void addModifier(String mod) {
      if ((mod == null) || (mod.length() == 0)) {
         //  Nothing to add
         return;
      } else if (mod.equalsIgnoreCase(names[0])) {
         modifiers = modifiers | ABSTRACT;
      } else if (mod.equalsIgnoreCase(names[1])) {
         modifiers = modifiers | EXPLICIT;
      } else if (mod.equalsIgnoreCase(names[2])) {
         modifiers = modifiers | FINAL;
      } else if (mod.equalsIgnoreCase(names[3])) {
         modifiers = modifiers | INTERFACE;
      } else if (mod.equalsIgnoreCase(names[4])) {
         modifiers = modifiers | NATIVE;
      } else if (mod.equalsIgnoreCase(names[5])) {
         modifiers = modifiers | PRIVATE;
      } else if (mod.equalsIgnoreCase(names[6])) {
         modifiers = modifiers | PROTECTED;
      } else if (mod.equalsIgnoreCase(names[7])) {
         modifiers = modifiers | PUBLIC;
      } else if (mod.equalsIgnoreCase(names[8])) {
         modifiers = modifiers | STATIC;
         //} else if (mod.equalsIgnoreCase(names[9])) {
         //    modifiers = modifiers | STRICT;
      } else if (mod.equalsIgnoreCase(names[10])) {
         modifiers = modifiers | STRICTFP;
      } else if (mod.equalsIgnoreCase(names[11])) {
         modifiers = modifiers | SYNCHRONIZED;
      } else if (mod.equalsIgnoreCase(names[12])) {
         modifiers = modifiers | TRANSIENT;
      } else if (mod.equalsIgnoreCase(names[13])) {
         modifiers = modifiers | VOLATILE;
      }
   }


   /**
    *  Convert the node to a string
    *
    * @return    a string describing the modifiers
    */
   public String toStringAlphabetical() {
      //  Local Variables
      StringBuilder buf = new StringBuilder();

      //  Protection first
      if (isPrivate()) {
         buf.append(names[5]);
         buf.append(" ");
      }
      if (isProtected()) {
         buf.append(names[6]);
         buf.append(" ");
      }
      if (isPublic()) {
         buf.append(names[7]);
         buf.append(" ");
      }

      //  Others next
      if (isAbstract()) {
         buf.append(names[0]);
         buf.append(" ");
      }
      if (isExplicit()) {
         buf.append(names[1]);
         buf.append(" ");
      }
      if (isFinal()) {
         buf.append(names[2]);
         buf.append(" ");
      }
      if (isInterface()) {
         buf.append(names[3]);
         buf.append(" ");
      }
      if (isNative()) {
         buf.append(names[4]);
         buf.append(" ");
      }
      if (isStatic()) {
         buf.append(names[8]);
         buf.append(" ");
      }
      //if (isStrict()) {
      //    buf.append(names[9]);
      //    buf.append(" ");
      //}
      if (isStrictFP()) {
         buf.append(names[10]);
         buf.append(" ");
      }
      if (isSynchronized()) {
         buf.append(names[11]);
         buf.append(" ");
      }
      if (isTransient()) {
         buf.append(names[12]);
         buf.append(" ");
      }
      if (isVolatile()) {
         buf.append(names[13]);
         buf.append(" ");
      }

      return buf.toString();
   }


   /**
    *  Convert the node to a string
    *
    * @return    a string describing the modifiers
    */
   public String toStandardOrderString() {
      //  Local Variables
      StringBuilder buf = new StringBuilder();

      //  Protection first
      if (isPrivate()) {
         buf.append(names[5]);
         buf.append(" ");
      }
      if (isProtected()) {
         buf.append(names[6]);
         buf.append(" ");
      }
      if (isPublic()) {
         buf.append(names[7]);
         buf.append(" ");
      }

      //  Others next
      if (isAbstract()) {
         buf.append(names[0]);
         buf.append(" ");
      }
      if (isExplicit()) {
         buf.append(names[1]);
         buf.append(" ");
      }
      if (isInterface()) {
         buf.append(names[3]);
         buf.append(" ");
      }
      if (isStatic()) {
         buf.append(names[8]);
         buf.append(" ");
      }
      //if (isStrict()) {
      //    buf.append(names[9]);
      //    buf.append(" ");
      //}
      if (isFinal()) {
         buf.append(names[2]);
         buf.append(" ");
      }
      if (isSynchronized()) {
         buf.append(names[11]);
         buf.append(" ");
      }
      if (isTransient()) {
         buf.append(names[12]);
         buf.append(" ");
      }
      if (isVolatile()) {
         buf.append(names[13]);
         buf.append(" ");
      }
      if (isNative()) {
         buf.append(names[4]);
         buf.append(" ");
      }
      if (isStrictFP()) {
         buf.append(names[10]);
         buf.append(" ");
      }

      return buf.toString();
   }


   /**
    *  Copies the modifiers from another source
    *
    * @param  source  the source
    */
   public void copyModifiers(ModifierHolder source) {
      modifiers = source.getModifiers();
   }


   /**
    *  Compare the modifiers of these two nodes.
    *
    * @param  obj  the other node.
    * @return      <code>true</code> if they are the same
    */
   public boolean equalModifiers(Object obj) {
      if (obj instanceof ModifierHolder) {
         ModifierHolder other = (ModifierHolder)obj;
         return other.getModifiers() == modifiers;
      }
      return false;
   }


   /**
    *  Sets or resets a single bit in the modifiers
    *
    * @param  value  <code>true</code> if we are setting the bit
    * @param  code   The new Code value
    */
   protected void setCode(boolean value, int code) {
      if (value) {
         modifiers = modifiers | code;
      } else {
         modifiers = modifiers & (~code);
      }
   }


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    */
   protected String printModifiers() {
      return (modifiers == 0) ? "" : (" (" + toStandardOrderString() + ")");
   }
   
   public int skipAnnotations() {
      int childNo = 0;
      Node child = jjtGetFirstChild();
      while (child instanceof ASTAnnotation) {
         child = jjtGetChild(++childNo);
      }
      return childNo;
   }

   public int skipAnnotationsAndTypeParameters() {
      int childNo = 0;
      Node child = jjtGetFirstChild();
      while (child instanceof ASTAnnotation) {
         child = jjtGetChild(++childNo);
      }
      if (child instanceof ASTTypeParameters) {
         childNo++;// skip possible type parameters
      }
      return childNo;
   }

}

