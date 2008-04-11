/*
 *  Author: Mike Atkinson
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
 *  Description of the Class
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.4, created November 09, 2003
 */
public class ASTConstantDeclaration extends AccessNode {

   /**
    *  Constructor for the ASTConstantDeclaration node.
    *
    * @param  identifier  The id of this node (JJTCONSTANTDECLARATION).
    */
   public ASTConstantDeclaration(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTConstantDeclaration node.
    *
    * @param  parser      The JavaParser that created this ASTConstantDeclaration node.
    * @param  identifier  The id of this node (JJTCONSTANTDECLARATION).
    */
   public ASTConstantDeclaration(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor. *
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTConstantDeclaration node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }

}

