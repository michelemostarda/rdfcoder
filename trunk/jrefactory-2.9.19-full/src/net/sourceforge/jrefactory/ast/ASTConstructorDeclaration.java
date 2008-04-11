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
 *  Description of the Class
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTConstructorDeclaration extends AccessNode {
   private String name;


   /**
    *  Constructor for the ASTConstructorDeclaration node.
    *
    * @param  identifier  The id of this node (JJTCONSTRUCTORDECLARATION).
    */
   public ASTConstructorDeclaration(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTConstructorDeclaration node.
    *
    * @param  parser      The JavaParser that created this ASTConstructorDeclaration node.
    * @param  identifier  The id of this node (JJTCONSTRUCTORDECLARATION).
    */
   public ASTConstructorDeclaration(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the constructor's name
    *
    * @param  newName  the new name
    */
   public void setName(String newName) {
      name = newName.intern();
   }


   /**
    *  Get the constructor's name
    *
    * @return    the name
    */
   public String getName() {
      return name;
   }


   /**
    *  Gets the parameterCount attribute of the ASTConstructorDeclaration node.
    *
    * @return    The parameterCount value
    */
   public int getParameterCount() {
      return ((ASTFormalParameters)jjtGetFirstChild()).getParameterCount();
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTConstructorDeclaration node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }

}

