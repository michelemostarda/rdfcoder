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
 *  Stores a list of formal parameters
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTFormalParameters extends SimpleNode {
   /**
    *  Constructor for the ASTFormalParameters node.
    *
    * @param  identifier  The id of this node (JJTFORMALPARAMETERS).
    */
   public ASTFormalParameters(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTFormalParameters node.
    *
    * @param  parser      The JavaParser that created this ASTFormalParameters node.
    * @param  identifier  The id of this node (JJTFORMALPARAMETERS).
    */
   public ASTFormalParameters(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Gets the parameterCount attribute of the ASTFormalParameters node.
    *
    * @return    The parameterCount value
    */
   public int getParameterCount() {
      return jjtGetNumChildren();
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTFormalParameters node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

