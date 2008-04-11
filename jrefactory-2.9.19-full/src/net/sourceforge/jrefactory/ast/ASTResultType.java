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
 *  Contains void, a primitive type, or a named type and can be used as a return value from a method.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTResultType extends SimpleNode {
   /**
    *  Constructor for the ASTResultType node.
    *
    * @param  identifier  The id of this node (JJTRESULTTYPE).
    */
   public ASTResultType(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTResultType node.
    *
    * @param  parser      The JavaParser that created this ASTResultType node.
    * @param  identifier  The id of this node (JJTRESULTTYPE).
    */
   public ASTResultType(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Gets the void attribute of the ASTResultType node.
    *
    * @return    The void value
    */
   public boolean isVoid() {
      return jjtGetNumChildren() == 0;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTResultType node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

