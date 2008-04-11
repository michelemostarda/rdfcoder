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
 *  Contains an expression with instanceof operator
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTInstanceOfExpression extends SimpleNode {
   /**
    *  Constructor for the ASTInstanceOfExpression node.
    *
    * @param  identifier  The id of this node (JJTINSTANCEOFEXPRESSION).
    */
   public ASTInstanceOfExpression(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTInstanceOfExpression node.
    *
    * @param  parser      The JavaParser that created this ASTInstanceOfExpression node.
    * @param  identifier  The id of this node (JJTINSTANCEOFEXPRESSION).
    */
   public ASTInstanceOfExpression(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTInstanceOfExpression node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

