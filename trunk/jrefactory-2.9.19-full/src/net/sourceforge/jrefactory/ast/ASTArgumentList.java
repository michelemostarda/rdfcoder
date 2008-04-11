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
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTArgumentList extends SimpleNode {
   /**
    *  Constructor for the ASTArgumentList node.
    *
    * @param  identifier  Description of Parameter
    */
   public ASTArgumentList(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTArgumentList node.
    *
    * @param  p           Description of Parameter
    * @param  identifier  Description of Parameter
    */
   public ASTArgumentList(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor. *
    *
    * @param  visitor  Description of Parameter
    * @param  data     Description of Parameter
    * @return          Description of the Returned Value
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

