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
 *  Stores a static or dynamic initializer that is contained within the class.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTInitializer extends SimpleNode {
   private boolean usingStatic;


   /**
    *  Constructor for the ASTInitializer node.
    *
    * @param  identifier  The id of this node (JJTINITIALIZER).
    */
   public ASTInitializer(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTInitializer node.
    *
    * @param  parser      The JavaParser that created this ASTInitializer node.
    * @param  identifier  The id of this node (JJTINITIALIZER).
    */
   public ASTInitializer(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Sets whether we are using the static modifier
    *
    * @param  isStatic  <code>true</code> if <code>static</code> being used.
    */
   public void setUsingStatic(boolean isStatic) {
      usingStatic = isStatic;
   }


   /**
    *  Return true if we used the static modifier
    *
    * @return    <code>true</code> if we used <code>static</code>.
    */
   public boolean isUsingStatic() {
      return usingStatic;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTInitializer node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

