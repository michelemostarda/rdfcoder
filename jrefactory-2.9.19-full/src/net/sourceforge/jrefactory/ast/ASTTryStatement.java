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
 *  Statement that catches exceptions
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTTryStatement extends SimpleNode {
   private boolean hasCatch;
   private boolean hasFinally;


   /**
    *  Constructor for the ASTTryStatement node.
    *
    * @param  identifier  The id of this node (JJTTRYSTATEMENT).
    */
   public ASTTryStatement(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTTryStatement node.
    *
    * @param  parser      The JavaParser that created this ASTTryStatement node.
    * @param  identifier  The id of this node (JJTTRYSTATEMENT).
    */
   public ASTTryStatement(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**  This try statement has <code>catch</code> block. */
   public void setHasCatch() {
      hasCatch = true;
   }


   /**  This try statement has <code>finally</code> block. */
   public void setHasFinally() {
      hasFinally = true;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTTryStatement node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }


   /**
    *  Does the try statement have a <code>catch</code> block.
    *
    * @return    <code>true</code> if the try statement has a <code>catch</code> block.
    */
   public boolean hasCatch() {
      return hasCatch;
   }


   /**
    *  Does the try statement have a <code>finally</code> block.
    *
    * @return    <code>true</code> if the try statement has a <code>finally</code> block.
    */
   public boolean hasFinally() {
      return hasFinally;
   }
}

