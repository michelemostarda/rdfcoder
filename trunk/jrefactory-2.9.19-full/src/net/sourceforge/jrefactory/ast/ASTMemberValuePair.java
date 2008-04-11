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
 * @since     jRefactory 2.9.4, created November 07, 2003
 */
public class ASTMemberValuePair extends SimpleNode {
   /**
    *  Constructor for the ASTMemberValuePair node.
    *
    * @param  identifier  The id of this node (JJTMEMBERVALUEPAIR).
    */
   public ASTMemberValuePair(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTMemberValuePair node.
    *
    * @param  parser      The JavaParser that created this ASTMemberValuePair node.
    * @param  identifier  The id of this node (JJTMEMBERVALUEPAIR).
    */
   public ASTMemberValuePair(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Accept the visitor. *
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTMemberValuePair node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

