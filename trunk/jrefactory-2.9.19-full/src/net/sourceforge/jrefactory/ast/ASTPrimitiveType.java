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
 *  Contains a primitive type such as "int" or "boolean"
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTPrimitiveType extends NamedNode {
   //private String name;


   /**
    *  Constructor for the ASTPrimitiveType node.
    *
    * @param  identifier  The id of this node (JJTPRIMITIVETYPE).
    */
   public ASTPrimitiveType(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTPrimitiveType node.
    *
    * @param  parser      The JavaParser that created this ASTPrimitiveType node.
    * @param  identifier  The id of this node (JJTPRIMITIVETYPE).
    */
   public ASTPrimitiveType(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the primitive type's name
    *
    * @param  newName  the new name (should be "boolean", "char", "byte", "short", "int", "long", "float", "double").
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Get the primitive type's name
    *
    * 
    * @return    the name (should be "boolean", "char", "byte", "short", "int", "long", "float", "double").
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTPrimitiveType node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

