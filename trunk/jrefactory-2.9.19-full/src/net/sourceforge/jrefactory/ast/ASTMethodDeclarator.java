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
 *  Constains the name and the parameters associated with this method
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTMethodDeclarator extends NamedNode {
   private int arrayCount;


   /**
    *  Constructor for the ASTMethodDeclarator node.
    *
    * @param  identifier  The id of this node (JJTMETHODDECLARATOR).
    */
   public ASTMethodDeclarator(int identifier) {
      super(identifier);
      arrayCount = 0;
   }


   /**
    *  Constructor for the ASTMethodDeclarator node.
    *
    * @param  parser      The JavaParser that created this ASTMethodDeclarator node
    * @param  identifier  The id of this node (JJTMETHODDECLARATOR).
    */
   public ASTMethodDeclarator(JavaParser parser, int identifier) {
      super(parser, identifier);
      arrayCount = 0;
   }


   /**
    *  Set the number of levels of array indirection
    *
    * @param  count  the number of []
    */
   public void setArrayCount(int count) {
      arrayCount = count;
   }


   /**
    *  Return the number of array brackets
    *
    * @return    the levels of indirection
    */
   public int getArrayCount() {
      return arrayCount;
   }


   /**
    *  Gets the parameterCount attribute of the ASTMethodDeclarator node.
    *
    * @return    The parameterCount value
    */
   public int getParameterCount() {
      return this.jjtGetFirstChild().jjtGetNumChildren();
   }


   /**
    *  Makes sure all the java doc components are present. For methods and constructors we need to do more work -
    *  checking parameters, return types, and exceptions.
    */
   public void finish() { }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTMethodDeclarator node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

