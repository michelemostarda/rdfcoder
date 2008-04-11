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
 *  Contains a while statement
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTWhileStatement extends SimpleNode {
   /**
    *  Constructor for the ASTWhileStatement node.
    *
    * @param  identifier  The id of this node (JJTWHILESTATEMENT).
    */
   public ASTWhileStatement(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTWhileStatement node.
    *
    * @param  parser      The JavaParser that created this ASTWhileStatement node.
    * @param  identifier  The id of this node (JJTWHILESTATEMENT).
    */
   public ASTWhileStatement(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTWhileStatement node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

