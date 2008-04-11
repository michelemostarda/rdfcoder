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
 *  The primary suffix of the expression
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTPrimarySuffix extends NamedNode {
   //private String name = "";
   private boolean isArguments;


   /**
    *  Constructor for the ASTPrimarySuffix node.
    *
    * @param  identifier  The id of this node (JJTPRIMARYSUFFIX).
    */
   public ASTPrimarySuffix(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTPrimarySuffix node.
    *
    * @param  parser      The JavaParser that created this ASTPrimarySuffix node.
    * @param  identifier  The id of this node (JJTPRIMARYSUFFIX).
    */
   public ASTPrimarySuffix(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the object's name
    *
    * @param  newName  the new name (should be "this" or "super" or &lt;identifier&gt;)
    */
   //public void setName(String newName) {
   //   name = newName.intern();
   //}


   /**
    *  Set the object's name
    *
    * @param  newName  the new name (should be "this" or "super" or &lt;identifier&gt;)
    */
   public void setImage(String newName) {
      setName(newName);
   }


   /**  Sets the isArguments attribute of the ASTPrimarySuffix node. */
   public void setIsArguments() {
      this.isArguments = true;
   }


   /**
    *  Get the object's name
    *
    * @return    the name (should be "this" or "super" or &lt;identifier&gt;) or null.
    */
   //public String getName() {
   //   return name;
   //}


   /**
    *  Gets the arguments attribute of the ASTPrimarySuffix node.
    *
    * @return    The arguments value
    */
   public boolean isArguments() {
      return this.isArguments;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTPrimarySuffix node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }

}

