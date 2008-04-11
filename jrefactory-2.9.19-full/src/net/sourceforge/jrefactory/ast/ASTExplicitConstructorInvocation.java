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
 *  Holds an invocation of super() or this() in a constructor.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTExplicitConstructorInvocation extends NamedNode {
   //private String name;

   private String thisOrSuper;


   /**
    *  Constructor for the ASTExplicitConstructorInvocation node.
    *
    * @param  identifier  The id of this node (JJTEXPLICITCONSTRUCTORINVOCATION).
    */
   public ASTExplicitConstructorInvocation(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTExplicitConstructorInvocation node.
    *
    * @param  parser      The JavaParser that created this ASTExplicitConstructorInvocation node.
    * @param  identifier  The id of this node (JJTEXPLICITCONSTRUCTORINVOCATION).
    */
   public ASTExplicitConstructorInvocation(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the name of the constructor we are invoking.
    *
    * @param  name  the constructor's name
    */
   //public void setName(String name) {
   //   this.name = name.intern();
   //}


   /**  Sets the isThis attribute of the ASTExplicitConstructorInvocation node. */
   public void setIsThis() {
      this.thisOrSuper = "this";
   }


   /**  Sets the isSuper attribute of the ASTExplicitConstructorInvocation node. */
   public void setIsSuper() {
      this.thisOrSuper = "super";
   }


   /**
    *  Return the name we are invoking
    *
    * @return    the constructor's name
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Gets the argumentCount attribute of the ASTExplicitConstructorInvocation node.
    *
    * @return    The number of arguments to the constructor.
    */
   public int getArgumentCount() {
      return ((ASTArguments)this.jjtGetFirstChild()).getArgumentCount();
   }


   /**
    *  Gets the this attribute of the ASTExplicitConstructorInvocation node.
    *
    * @return    <code>true</code> if we are invoking the constructor on <code>this</code>.
    */
   public boolean isThis() {
      return thisOrSuper != null && thisOrSuper.equals("this");
   }


   /**
    *  Gets the super attribute of the ASTExplicitConstructorInvocation node.
    *
    * @return    <code>true</code> if we are invoking the constructor on <code>super</code>.
    */
   public boolean isSuper() {
      return thisOrSuper != null && thisOrSuper.equals("super");
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTExplicitConstructorInvocation node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

