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
 *  Contains a TypeParameterList as its only child. Generics were indroduced by JDK 1.5.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTTypeParameters extends SimpleNode {
   /**
    *  Constructor for the ASTTypeParameters node.
    *
    * @param  identifier  The id of this node (JJTTYPEPARAMETERS).
    */
   public ASTTypeParameters(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTTypeParameters node.
    *
    * @param  parser      The JavaParser that created this ASTTypeParameters node.
    * @param  identifier  The id of this node (JJTTYPEPARAMETERS).
    */
   public ASTTypeParameters(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor. *
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTTypeParameters node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

