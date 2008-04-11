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



/**
 *  All AST nodes must implement this interface. It provides basic machinery for constructing the parent and child
 *  relationships between nodes.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public interface Node {
   /**
    *  Gets the beginLine attribute of the Node.
    *
    * @return    The beginLine value
    * @since     v 1.0
    */
   int getBeginLine();


   /**
    *  Gets the beginColumn attribute of the Node.
    *
    * @return    The beginColumn value
    * @since     v 1.0
    */
   int getBeginColumn();


   /**
    *  Gets the endLine attribute of the Node.
    *
    * @return    The endLine value
    * @since     v 1.0
    */
   int getEndLine();


   /**
    *  Gets the endColumn attribute of the Node.
    *
    * @return    The endColumn value
    * @since     v 1.0
    */
   int getEndColumn();


   /**
    *  This method is called after the node has been made the current node. It indicates that child nodes can now be
    *  added to it.
    *
    * @since    v 1.0
    */
   void jjtOpen();


   /**
    *  This method is called after all the child nodes have been added.
    *
    * @since    v 1.0
    */
   void jjtClose();


   /**
    *  This pair of methods are used to inform the node of its parent.
    *
    * @param  parentNode  Description of Parameter
    * @since              v 1.0
    */
   void jjtSetParent(Node parentNode);


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    * @since     v 1.0
    */
   Node jjtGetParent();


   /**
    *  This method tells the node to add its argument to the node's list of children.
    *
    * @param  childNode  Description of Parameter
    * @param  atIndex    Description of Parameter
    * @since             v 1.0
    */
   void jjtAddChild(Node childNode, int atIndex);


   /**
    *  This method tells the node to add its argument to the node's list of children.<p>
    *
    *  Same as jjtAddChild(n, 0);
    *
    * @param  childNode  Description of Parameter
    * @since             v 1.0
    */
   void jjtAddFirstChild(Node childNode);


   /**
    *  Description of the Method
    *
    * @param  childNode  Description of Parameter
    * @param  atIndex    Description of Parameter
    * @since             v 1.0
    */
   void jjtInsertChild(Node childNode, int atIndex);


   /**
    *  Description of the Method
    *
    * @param  atIndex  Description of Parameter
    * @since           v 1.0
    */
   void jjtDeleteChild(int atIndex);


   /**
    *  This method returns a child node. The children are numbered from zero, left to right.
    *
    * @param  atIndex  Description of Parameter
    * @return          Description of the Returned Value
    * @since           v 1.0
    */
   Node jjtGetChild(int atIndex);


   /**
    *  This method returns a child node. The children are numbered from zero, left to right.<p>
    *
    *  Same as jjtGetFirstChild();
    *
    * @return    Description of the Returned Value
    * @since     v 1.0
    */
   Node jjtGetFirstChild();


   /**
    *  Return the number of children the node has.
    *
    * @return    Description of the Returned Value
    * @since     v 1.0
    */
   int jjtGetNumChildren();


   /**
    *  Accept the visitor.
    *
    * @param  visitor  Description of Parameter
    * @param  data     Description of Parameter
    * @return          Description of the Returned Value
    * @since           v 1.0
    */
   Object jjtAccept(JavaParserVisitor visitor, Object data);
}

