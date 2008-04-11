/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import net.sourceforge.jrefactory.parser.JavaParserVisitor;
import net.sourceforge.jrefactory.parser.JavaParser;


/**
 *  The prefix expression. This is the name of the method or the variable, but at this level it has no knowledge of
 *  whether it is a variable or a method invocation.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTPrimaryPrefix extends NamedNode {
   //private String name = "";
   private int count = 0;
   private boolean usesThisModifier;
   private boolean usesSuperModifier;


   /**
    *  Constructor for the ASTPrimaryPrefix node.
    *
    * @param  identifier  The id of this node (JJTPRIMARYPREFIX).
    */
   public ASTPrimaryPrefix(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTPrimaryPrefix node.
    *
    * @param  parser      The JavaParser that created this ASTPrimaryPrefix node.
    * @param  identifier  The id of this node (JJTPRIMARYPREFIX).
    */
   public ASTPrimaryPrefix(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the object's name
    *
    * @param  newName  the new name (should be "this" or (&lt;identifier&gt; ".")* "super" ".").
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Sets the count attribute of the ASTPrimaryPrefix node.
    *
    * @param  value  The new count value
    */
   public void setCount(int value) {
      count = value;
   }


   /**  Sets the usesThisModifier attribute of the ASTPrimaryPrefix node. */
   public void setUsesThisModifier() {
      usesThisModifier = true;
   }


   /**  Sets the usesSuperModifier attribute of the ASTPrimaryPrefix node. */
   public void setUsesSuperModifier() {
      usesSuperModifier = true;
   }


   /**
    *  Get the object's name
    *
    * @return    the name (should be "this" or (&lt;identifier&gt; ".")* "super" ".") or null.
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Gets the count attribute of the ASTPrimaryPrefix node.
    *
    * @return    The count value
    */
   public int getCount() {
      return count;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTPrimaryPrefix node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    */
   public boolean usesThisModifier() {
      return this.usesThisModifier;
   }


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    */
   public boolean usesSuperModifier() {
      return this.usesSuperModifier;
   }
}

