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
 *  Contains a break statement. The break statement can contain a label. The label is referred to the name of the
 *  statement.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTBreakStatement extends NamedNode {
   //private String name = "";


   /**
    *  Constructor for the ASTBreakStatement node.
    *
    * @param  identifier  identifier  The id of this node (JJTBREAKSTATEMENT).
    */
   public ASTBreakStatement(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTBreakStatement node.
    *
    * @param  parser      The JavaParser that created this ASTBreakStatement node.
    * @param  identifier  The id of this node (JJTBREAKSTATEMENT).
    */
   public ASTBreakStatement(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the break statements's target name.
    *
    *  This is the optional label that follows the <code>break</code>.
    *
    * @param  newName  the new name.
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Get the break statements's target name.
    *
    * @return    the name (may be null if there was no optional label).
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTBreakStatement node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

