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
 *  Declares a local variable in a method
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTLocalVariableDeclaration extends SimpleNode {
   private boolean usingFinal;


   /**
    *  Constructor for the ASTLocalVariableDeclaration node.
    *
    * @param  identifier  The id of this node (JJTLOCALVARIABLEDECLARATION).
    */
   public ASTLocalVariableDeclaration(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTLocalVariableDeclaration node.
    *
    * @param  parser      The JavaParser that created this ASTLocalVariableDeclaration node.
    * @param  identifier  The id of this node (JJTLOCALVARIABLEDECLARATION).
    */
   public ASTLocalVariableDeclaration(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Sets whether we are using the final modifier
    *
    * @param  way  the way we are setting
    */
   public void setUsingFinal(boolean way) {
      usingFinal = way;
   }


   /**
    *  Return true if we used the final modifier
    *
    * @return    true if we used final
    */
   public boolean isUsingFinal() {
      return usingFinal;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTLocalVariableDeclaration node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }

   public int skipAnnotations() {
      int childNo = 0;
      Node child = jjtGetFirstChild();
      while (child instanceof ASTAnnotation) {
         child = jjtGetChild(++childNo);
      }
      return childNo;
   }

}

