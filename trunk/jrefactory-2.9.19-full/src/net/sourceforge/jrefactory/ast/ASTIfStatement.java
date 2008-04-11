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
 *  Stores an if statement. This statement can contain an else, and that just depends on the number of children nodes to
 *  this statement.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTIfStatement extends SimpleNode {
   private boolean hasElse;


   /**
    *  Constructor for the ASTIfStatement node.
    *
    * @param  identifier  The id of this node (JJTELSESTATEMENT).
    */
   public ASTIfStatement(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTIfStatement node.
    *
    * @param  parser      The JavaParser that created this ASTIfStatement node.
    * @param  identifier  The id of this node (JJTELSESTATEMENT).
    */
   public ASTIfStatement(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**  Sets the hasElse attribute of the ASTIfStatement node. */
   public void setHasElse() {
      this.hasElse = true;
   }


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    */
   public boolean hasElse() {
      return this.hasElse;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTIfStatement node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

