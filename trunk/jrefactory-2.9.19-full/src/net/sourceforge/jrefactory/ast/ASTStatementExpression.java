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
 *  A statement that is also an expression
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTStatementExpression extends NamedNode {
   //private String name = "";


   /**
    *  Constructor for the ASTStatementExpression node.
    *
    * @param  identifier  The id of this node (JJTSTATEMENTEXPRESSION).
    */
   public ASTStatementExpression(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTStatementExpression node.
    *
    * @param  parser      The JavaParser that created this ASTStatementExpression node.
    * @param  identifier  The id of this node (JJTSTATEMENTEXPRESSION).
    */
   public ASTStatementExpression(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the object's name
    *
    * @param  newName  the new name
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Get the object's name
    *
    * @return    the name
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTStatementExpression node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

