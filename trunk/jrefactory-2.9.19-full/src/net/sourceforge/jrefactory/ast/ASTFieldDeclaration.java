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
 *  Holds a field declaration. The two components that this structure store are the modifiers to the field and any
 *  javadoc comments associated with the field.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTFieldDeclaration extends AccessNode {

   /**
    *  Constructor for the ASTFieldDeclaration node.
    *
    * @param  identifier  The id of this node (JJTFIELDDECLARATION).
    */
   public ASTFieldDeclaration(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTFieldDeclaration node.
    *
    * @param  parser      The JavaParser that created this ASTFieldDeclaration node.
    * @param  identifier  The id of this node (JJTFIELDDECLARATION).
    */
   public ASTFieldDeclaration(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTFieldDeclaration node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

