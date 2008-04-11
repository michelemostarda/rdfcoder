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
 *  An expression that takes only a single argument that is not + or minus.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTUnaryExpressionNotPlusMinus extends NamedNode {
   //private String name = "";


   /**
    *  Constructor for the ASTUnaryExpressionNotPlusMinus node.
    *
    * @param  identifier  The id of this node (JJTUNARYEXPRESSIONNOTPLUSMINUSEXPRESSION).
    */
   public ASTUnaryExpressionNotPlusMinus(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTUnaryExpressionNotPlusMinus node.
    *
    * @param  parser      The JavaParser that created this ASTUnaryExpressionNotPlusMinus node.
    * @param  identifier  The id of this node (JJTUNARYEXPRESSIONNOTPLUSMINUSEXPRESSION).
    */
   public ASTUnaryExpressionNotPlusMinus(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the expressions's name
    *
    * @param  newName  the new name (should be "~" or "!)
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Get the expressions's name
    *
    * @return    the name (should be "~" or "!) or null if the expression were a cast or postfix expression.
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTUnaryExpressionNotPlusMinus node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

