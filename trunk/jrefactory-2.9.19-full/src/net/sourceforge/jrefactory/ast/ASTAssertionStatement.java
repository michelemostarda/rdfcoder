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
 *  Allows assertions to be thrown. This was added in JDK 1.4
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTAssertionStatement extends SimpleNode {
   /**
    *  Constructor for the ASTAssertionStatement node.
    *
    * @param  identifier  The id of this node (JJTASSERTIONSTATEMENT).
    */
   public ASTAssertionStatement(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTAssertionStatement node.
    *
    * @param  parser      The JavaParser that created this ASTAssertionStatement node.
    * @param  identifier  The id of this node (JJTASSERTIONSTATEMENT).
    */
   public ASTAssertionStatement(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor. *
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTAssertionStatement node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

