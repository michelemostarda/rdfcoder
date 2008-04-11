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
 *  Declares an expression that takes only one argument.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTUnaryExpression extends NamedNode {
   //private String name = "";


   /**
    *  Constructor for the ASTUnaryExpression node.
    *
    * @param  identifier  The id of this node (JJTUNARYEXPRESSION).
    */
   public ASTUnaryExpression(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTUnaryExpression node.
    *
    * @param  parser      The JavaParser that created this ASTUnaryExpression node.
    * @param  identifier  The id of this node (JJTUNARYEXPRESSION).
    */
   public ASTUnaryExpression(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the unary expression's name
    *
    * @param  newName  the new name (should be either "+" or "-").
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Get the unary expression's name
    *
    * @return    the name (should be either "+" or "-"), or null if the expression is a preincrement, predecrement or non +/- unary expression.
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTUnaryExpression node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

