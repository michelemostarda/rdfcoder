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
 *  A statement that has been labeled.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTLabeledStatement extends NamedNode {
   //private String name;


   /**
    *  Constructor for the ASTLabeledStatement node.
    *
    * @param  identifier  The id of this node (JJTLABELEDSTATEMENT).
    */
   public ASTLabeledStatement(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTLabeledStatement node.
    *
    * @param  parser      The JavaParser that created this ASTLabeledStatement node.
    * @param  identifier  The id of this node (JJTLABELEDSTATEMENT).
    */
   public ASTLabeledStatement(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the label's name
    *
    * @param  newName  the new name for the label
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Get the label's name
    *
    * @return    the name of the label.
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTLabeledStatement node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

