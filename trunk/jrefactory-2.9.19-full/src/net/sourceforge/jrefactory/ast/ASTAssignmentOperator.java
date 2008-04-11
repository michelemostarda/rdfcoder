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
 *  This node contains some type of assignment operator for instance = or +=.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTAssignmentOperator extends NamedNode {
   //private String name;


   /**
    *  Constructor for the ASTAssignmentOperator node.
    *
    * @param  identifier  The id of this node (JJTASSIGNMENTOPERATOR).
    */
   public ASTAssignmentOperator(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTAssignmentOperator node.
    *
    * @param  parser      The JavaParser that created this ASTAssignmentOperator node.
    * @param  identifier  The id of this node (JJTASSIGNMENTOPERATOR).
    */
   public ASTAssignmentOperator(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the operators's name
    *
    * @param  newName  the new name (should be "=", "*=", "/=", "%=", "+=", "-=", "<<=", ">>=", ">>>=", "&=", "^=" or "|=" )
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Get the operators's name
    *
    * @return    the name (should be "=", "*=", "/=", "%=", "+=", "-=", "<<=", ">>=", ">>>=", "&=", "^=" or "|=" ).
    */
   //public String getName() {
   //   return name;
   //}



   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTAssignmentOperator node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

