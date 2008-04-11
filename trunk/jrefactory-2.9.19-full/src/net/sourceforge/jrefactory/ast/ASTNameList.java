/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import java.util.Arrays;
import java.util.Comparator;

import net.sourceforge.jrefactory.parser.JavaParserVisitor;
import net.sourceforge.jrefactory.parser.JavaParser;


/**
 *  Contains a list of names contained in the children, for instance for the interfaces that a class implements.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTNameList extends SimpleNode {
   /**
    *  Constructor for the ASTNameList node.
    *
    * @param  identifier  The id of this node (JJTNAMELIST).
    */
   public ASTNameList(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTNameList node.
    *
    * @param  parser      The JavaParser that created this ASTNameList node.
    * @param  identifier  The id of this node (JJTNAMELIST).
    */
   public ASTNameList(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Sorts the arrays
    *
    * @param  order  the order of items
    */
   public void sort(Comparator order) {
      if (children != null) {
         Arrays.sort(children, order);
      }
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTNameList node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

