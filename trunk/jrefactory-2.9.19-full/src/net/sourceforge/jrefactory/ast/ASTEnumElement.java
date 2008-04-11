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
 *  This holds an element of an enumeration. Enumerations are new to JDK 1.5.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTEnumElement extends ASTLiteral {
   /**
    *  Constructor for the ASTEnumElement node.
    *
    * @param  identifier  The id of this node (JJTENUMELEMENT).
    */
   public ASTEnumElement(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTEnumElement node.
    *
    * @param  parser      The JavaParser that created this ASTEnumElement node.
    * @param  identifier  The id of this node (JJTENUMELEMENT).
    */
   public ASTEnumElement(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor. *
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTEnumElement node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

