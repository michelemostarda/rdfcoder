/*
 *Author: Mike Atkinson
 *
 *This software has been developed under the copyleft
 *rules of the GNU General Public License.  Please
 *consult the GNU General Public License for more
 *details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import net.sourceforge.jrefactory.parser.JavaParserVisitor;
import net.sourceforge.jrefactory.parser.JavaParser;


/**
 *  Description of the Class
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTReferenceType extends ASTType {
   private int arrayCount;


   /**
    *  Constructor for the ASTReferenceType node.
    *
    * @param  identifier  The id of this node (JJTREFERENCETYPE).
    */
   public ASTReferenceType(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTReferenceType node.
    *
    * @param  parser      The JavaParser that created this ASTReferenceType node.
    * @param  identifier  The id of this node (JJTREFERENCETYPE).
    */
   public ASTReferenceType(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the number of indirection for the array
    *
    * @param  count  The new arrayCount value
    */
   public void setArrayCount(int count) {
      arrayCount = count;
   }


   /**
    *  Get the number of indirection for the array
    *
    * @return    the count
    */
   public int getArrayCount() {
      return arrayCount;
   }


   /*
    public String getImage() {
        if (jjtGetNumChildren()>0) {
            String x = ((SimpleNode)jjtGetFirstChild()).getName();
            for (int i=0; i<arrayCount; i++) {
                x = x + "[]";
            }
            return x;
        } else {
            return "";
        }
    }
*/
   /**
    *  Accept the visitor. *
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTReferenceType node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

