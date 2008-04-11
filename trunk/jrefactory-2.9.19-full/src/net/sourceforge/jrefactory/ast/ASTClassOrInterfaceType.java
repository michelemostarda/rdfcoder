/*
 *  Author: Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import net.sourceforge.jrefactory.parser.JavaParserTreeConstants;
import net.sourceforge.jrefactory.parser.JavaParserVisitor;
import net.sourceforge.jrefactory.parser.JavaParser;


/**
 *  Description of the Class
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTClassOrInterfaceType extends ASTName {
   /**
    *  Constructor for the ASTClassOrInterfaceType node.
    *
    * @param  identifier  The id of this node (JJTCLASSORINTERFACETYPE).
    */
   public ASTClassOrInterfaceType(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTClassOrInterfaceType node.
    *
    * @param  parser      The JavaParser that created this ASTClassOrInterfaceType node.
    * @param  identifier  The id of this node (JJTCLASSORINTERFACETYPE).
    */
   public ASTClassOrInterfaceType(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Constructor for the ASTClassOrInterfaceType node.
    *
    * @param  name  The name of the class or interface.
    */
   public ASTClassOrInterfaceType(ASTName name) {
      super(JavaParserTreeConstants.JJTCLASSORINTERFACETYPE);
      children = name.children;
   }



   /**
    *  Accept the visitor. *
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTClassOrInterfaceType node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }



   /**
    *  Determines if two names start with the same series of items
    *
    * @param  otherName  Description of Parameter
    * @return            Description of the Returned Value
    */
   public boolean startsWith(ASTClassOrInterfaceType otherName) {
      //  To start with the other name, the other name must be less than or equal in parts
      if (otherName.getNameSize() > getNameSize()) {
         return false;
      }

      //  Look for the point where they are different
      int last = Math.min(otherName.getNameSize(), getNameSize());
      for (int ndx = 0; ndx < last; ndx++) {
         if (!getNamePart(ndx).equals(otherName.getNamePart(ndx))) {
            return false;
         }
      }

      //  They must be the same
      return true;
   }



   /**
    *  Change starting part. Presumes that otherName is less than the length of the current name.
    *
    * @param  oldBase  Description of Parameter
    * @param  newBase  Description of Parameter
    * @return          Description of the Returned Value
    */
   public ASTClassOrInterfaceType changeStartingPart(ASTClassOrInterfaceType oldBase, ASTClassOrInterfaceType newBase) {
      ASTClassOrInterfaceType result = new ASTClassOrInterfaceType(JavaParserTreeConstants.JJTCLASSORINTERFACETYPE);

      int last = newBase.getNameSize();
      for (int ndx = 0; ndx < last; ndx++) {
         result.addNamePart(newBase.getNamePart(ndx));
      }

      int end = getNameSize();
      int start = oldBase.getNameSize();
      for (int ndx = start; ndx < end; ndx++) {
         result.addNamePart(getNamePart(ndx));
      }
      result.setLineAndColumnInfo(getBeginLine(), getBeginColumn(), getEndLine(), getEndColumn());

      return result;
   }


   /**
    *  Change starting part. Presumes that otherName is less than the length of the current name.
    *
    * @param  oldBase  Description of Parameter
    * @param  newBase  Description of Parameter
    * @return          Description of the Returned Value
    */
   public ASTClassOrInterfaceType changeStartingPart(ASTClassOrInterfaceType oldBase, ASTName newBase) {
      ASTClassOrInterfaceType result = new ASTClassOrInterfaceType(JavaParserTreeConstants.JJTCLASSORINTERFACETYPE);

      int last = newBase.getNameSize();
      for (int ndx = 0; ndx < last; ndx++) {
         result.addNamePart(newBase.getNamePart(ndx));
      }

      int end = getNameSize();
      int start = oldBase.getNameSize();
      for (int ndx = start; ndx < end; ndx++) {
         result.addNamePart(getNamePart(ndx));
      }
      result.setLineAndColumnInfo(getBeginLine(), getBeginColumn(), getEndLine(), getEndColumn());

      return result;
   }
}

