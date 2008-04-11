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
 *  Blocks are made up of BlockStatements. This contains some sort of statement or variable declaration.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTBlockStatement extends SimpleNode {
   private boolean isFinal;


   /**
    *  Constructor for the ASTBlockStatement node.
    *
    * @param  identifier  The id of this node (JJTBLOCKSTATEMENT).
    */
   public ASTBlockStatement(int identifier) {
      super(identifier);
      isFinal = false;
   }


   /**
    *  Constructor for the ASTBlockStatement node.
    *
    * @param  parser      The JavaParser that created this ASTBlockStatement node.
    * @param  identifier  The id of this node (JJTBLOCKSTATEMENT).
    */
   public ASTBlockStatement(JavaParser parser, int identifier) {
      super(parser, identifier);
      isFinal = false;
   }


   /**
    *  Sets the final attribute of the ASTBlockStatementnode.
    *
    * @param  value  The new final value
    */
   public void setFinal(boolean value) {
      isFinal = value;
   }


   /**
    *  Gets the final attribute of the ASTBlockStatement node.
    *
    * @return    <code>true</code> if the block is final.
    */
   public boolean isFinal() {
      return isFinal;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTBlockStatement node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

