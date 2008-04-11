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
 *  Stores the class body. This has the ability to sort the children nodes. The order depends on the order set in the
 *  pretty.settings file.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTClassBody extends SimpleNode {
   /**
    *  Constructor for the ASTClassBody node.
    *
    * @param  identifier  The id of this node (JJTCLASSBODY).
    */
   public ASTClassBody(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTClassBody node.
    *
    * @param  parser      The JavaParser that created this ASTClassBody node.
    * @param  identifier  The id of this node (JJTCLASSBODY).
    */
   public ASTClassBody(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Sorts the arrays
    *
    * @param  order                  the order of items
    * @param  fixupFinalStaticOrder  ensure that final statics are defined before they are used.
    */
   public void sort(Comparator order, Comparator fixupFinalStaticOrder) {
      if (children != null) {
         Arrays.sort(children, order);
         // ensure that static final constants are defined in the correct order
         Arrays.sort(children, fixupFinalStaticOrder);
      }
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTClassBody node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

