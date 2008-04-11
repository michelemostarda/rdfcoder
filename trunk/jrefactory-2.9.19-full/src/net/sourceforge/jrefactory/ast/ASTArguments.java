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
 *  Holds the argument list that contains the specific arguments of a method invocation.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTArguments extends SimpleNode {
   /**
    *  Constructor for the ASTArguments node.
    *
    * @param  identifier  The id of this node (JJTARGUMENTS).
    */
   public ASTArguments(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTArguments node.
    *
    * @param  parser      The JavaParser that created this ASTArguments node.
    * @param  identifier  The id of this node (JJTARGUMENTS).
    */
   public ASTArguments(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Gets the number of arguments for the ASTArguments node.
    *
    * This is the number of children of the first child.
    *
    * @return    The number of arguments
    */
   public int getArgumentCount() {
      if (this.jjtGetNumChildren() == 0) {
         return 0;
      }
      return this.jjtGetFirstChild().jjtGetNumChildren();
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTArguments node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

