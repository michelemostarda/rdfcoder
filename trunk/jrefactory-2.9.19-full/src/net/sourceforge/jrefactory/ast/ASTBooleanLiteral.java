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
 *  Holds either "true" or "false"
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTBooleanLiteral extends NamedNode {
   //private String name;


   /**
    *  Constructor for the ASTBooleanLiteral node.
    *
    * @param  identifier  The id of this node (JJTBOOLEANLITERAL).
    */
   public ASTBooleanLiteral(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTBooleanLiteral node.
    *
    * @param  parser      The JavaParser that created this ASTBooleanLiteral node.
    * @param  identifier  The id of this node (JJTBOOLEANLITERAL).
    */
   public ASTBooleanLiteral(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the boolean literal's name.
    *
    * @param  newName  the new name (should be "true" or "false").
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Get the boolean literal's name.
    *
    * @return    the name (should be "true" or "false").
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTBooleanLiteral node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

