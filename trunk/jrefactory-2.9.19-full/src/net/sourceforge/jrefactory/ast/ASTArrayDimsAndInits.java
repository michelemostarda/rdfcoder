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
 *  Holds the array dimensions and the initialization for the array.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTArrayDimsAndInits extends SimpleNode {
   private int arrayCount = 0;


   /**
    *  Constructor for the ASTArrayDimsAndInits node.
    *
    * @param  identifier  The id of this node (JJTAARRAYDIMSANDINITS).
    */
   public ASTArrayDimsAndInits(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTArrayDimsAndInits node.
    *
    * @param  parser      The JavaParser that created this ASTArrayDimsAndInits node.
    * @param  identifier  The id of this node (JJTAARRAYDIMSANDINITS).
    */
   public ASTArrayDimsAndInits(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the number of dimensions for the array
    *
    * @param  count  the count of the number of array dimensions.
    */
   public void setArrayCount(int count) {
      arrayCount = count;
   }


   /**
    *  Get the number of dimensions for the array
    *
    * @return    the count of the number of array dimensions.
    */
   public int getArrayCount() {
      return arrayCount;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTArrayDimsAndInits node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

