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
 *  Holds the package declaration at the beginning of the java file
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTPackageDeclaration extends SimpleNode {

   /**
    *  Constructor for the ASTPackageDeclaration node.
    *
    * @param  identifier  The id of this node (JJTPACKAGEDECLARATION).
    */
   public ASTPackageDeclaration(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTPackageDeclaration node.
    *
    * @param  parser      The JavaParser that created this ASTPackageDeclaration node.
    * @param  identifier  The id of this node (JJTPACKAGEDECLARATION).
    */
   public ASTPackageDeclaration(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTPackageDeclaration node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }

}

