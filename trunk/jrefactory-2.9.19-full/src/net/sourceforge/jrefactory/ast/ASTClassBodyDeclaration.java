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
 *  Each class body is made up of a number of class body declarations.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTClassBodyDeclaration extends SimpleNode {
   /**
    *  Constructor for the ASTClassBodyDeclaration node.
    *
    * @param  identifier  The id of this node (JJTCLASSBODYDECLARATION).
    */
   public ASTClassBodyDeclaration(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTClassBodyDeclaration node.
    *
    * @param  parser      The JavaParser that created this ASTClassBodyDeclaration node.
    * @param  identifier  The id of this node (JJTCLASSBODYDECLARATION).
    */
   public ASTClassBodyDeclaration(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Gets the anonymousInnerClass attribute of the ASTClassBodyDeclaration node.
    *
    * @return    The anonymousInnerClass value
    */
   public boolean isAnonymousInnerClass() {
      return jjtGetParent() instanceof ASTClassBody && jjtGetParent().jjtGetParent() instanceof ASTAllocationExpression;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTClassBodyDeclaration node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

