/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import net.sourceforge.jrefactory.parser.JavaParser;


/**
 *  Declares node with a name.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public abstract class NamedNode extends SimpleNode {
   protected String name ="";

   /**
    *  Constructor for the Named node.
    *
    * @param  identifier  The id of this node.
    */
   protected NamedNode(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the Named node.
    *
    * @param  parser      The JavaParser that created this Named node.
    * @param  identifier  The id of this node.
    */
   protected NamedNode(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set the object's name
    *
    * @param  newName  the new name
    */
   public void setName(String newName) {
      name = newName.intern();
   }


   /**
    *  Get the object's name
    *
    * @return    the name
    */
   public String getName() {
      return name;
   }

}

