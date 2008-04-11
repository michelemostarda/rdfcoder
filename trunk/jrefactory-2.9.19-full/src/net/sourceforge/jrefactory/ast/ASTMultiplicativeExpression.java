/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.jrefactory.parser.JavaParserVisitor;
import net.sourceforge.jrefactory.parser.JavaParser;


/**
 *  Expression consisting of a list of UnaryExpression (contained in the children) separated by
 *  operators ("*", "/" or "%") contained in the list of names.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTMultiplicativeExpression extends SimpleNode {
   private Vector names;


   /**
    *  Constructor for the ASTMultiplicativeExpression node.
    *
    * @param  identifier  The id of this node (JJTMULTIPLICATIVEEXPRESSION).
    */
   public ASTMultiplicativeExpression(int identifier) {
      super(identifier);
      names = new Vector();
   }


   /**
    *  Constructor for the ASTMultiplicativeExpression node.
    *
    * @param  parser      The JavaParser that created this ASTMultiplicativeExpression node.
    * @param  identifier  The id of this node (JJTMULTIPLICATIVEEXPRESSION).
    */
   public ASTMultiplicativeExpression(JavaParser parser, int identifier) {
      super(parser, identifier);
      names = new Vector();
   }


   /**
    *  Get the object's names
    *
    * @return    the names in an enumeration
    */
   public Enumeration getNames() {
      return names.elements();
   }


   /**
    *  Set the object's name
    *
    * @param  newName  the new name (should be one of "*", "/" or "%")
    */
   public void addName(String newName) {
      if (newName != null) {
         names.addElement(newName.intern());
      }
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTMultiplicativeExpression node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

