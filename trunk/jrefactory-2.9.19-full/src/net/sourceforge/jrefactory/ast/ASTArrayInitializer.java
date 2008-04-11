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
 *  Holds the array initializer
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTArrayInitializer extends SimpleNode {
   private boolean finalComma = false;


   /**
    *  Constructor for the ASTArrayInitializer node.
    *
    * @param  identifier  The id of this node (JJTARRAYINITIALIZER).
    */
   public ASTArrayInitializer(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTArrayInitializer node.
    *
    * @param  parser      The JavaParser that created this ASTArrayInitializer node.
    * @param  identifier  The id of this node (JJTARRAYINITIALIZER).
    */
   public ASTArrayInitializer(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the final comma.
    *
    * Final comma's are optional in array initializers.
    *
    * @param  way  <code>true</code> if there is a final comma.
    */
   public void setFinalComma(boolean way) {
      finalComma = way;
   }


   /**
    *  Return <code>true</code> if the construct included a final comma.
    *
    * @return    <code>true</code> if there was a final comma.
    */
   public boolean isFinalComma() {
      return finalComma;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTArrayInitializer node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

