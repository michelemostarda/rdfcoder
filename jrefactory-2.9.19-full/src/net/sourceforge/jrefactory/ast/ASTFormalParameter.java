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
 *  Stores an individual formal parameter in a method declaration
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTFormalParameter extends SimpleNode {
   private boolean usingFinal;
   private boolean varArg;


   /**
    *  Constructor for the ASTFormalParameter node.
    *
    * @param  identifier  The id of this node (JJTFORMALPARAMETER).
    */
   public ASTFormalParameter(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTFormalParameter node.
    *
    * @param  parser      The JavaParser that created this ASTFormalParameter node.
    * @param  identifier  The id of this node (JJTFORMALPARAMETER).
    */
   public ASTFormalParameter(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Mark that this formal parameter using the final modifier
    *
    * @param  way  the way to set the final modifier
    */
   public void setUsingFinal(boolean way) {
      usingFinal = way;
   }


   /**
    *  Mark that this formal parameter as having a variable argument.
    *
    *  VarArg is new to JDK 1.5
    *
    * @param  varArg  is this Formal Parameter a VarArg
    */
   public void setVarArg(boolean varArg) {
      this.varArg = varArg;
   }


   /**
    *  Does this formal parameter have a variable argument.
    *
    *  VarArg is new to JDK 1.5
    *
    * @return  <code>true</code> if this Formal Parameter a VarArg
    */
   public boolean hasVarArg() {
      return varArg;
   }


   /**
    *  Return whether this formal parameter using the final modifier
    *
    * @return    if the formal modifier is used
    */
   public boolean isUsingFinal() {
      return usingFinal;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTFormalParameter node.
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

