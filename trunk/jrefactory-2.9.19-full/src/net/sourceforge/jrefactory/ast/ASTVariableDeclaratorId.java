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
 *  Declares a variable. Contains the name of the variable and the number of [] afterwards.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTVariableDeclaratorId extends SimpleNode {
   private String name;
   private int arrayCount = 0;


   /**
    *  Constructor for the ASTVariableDeclaratorId node.
    *
    * @param  identifier  The id of this node (JJTVARIABLEDECLARATORID).
    */
   public ASTVariableDeclaratorId(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTVariableDeclaratorId node.
    *
    * @param  parser      The JavaParser that created this ASTVariableDeclaratorId node.
    * @param  identifier  The id of this node (JJTVARIABLEDECLARATORID).
    */
   public ASTVariableDeclaratorId(JavaParser parser, int identifier) {
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
    *  Set the object's name
    *
    * @param  newName  the new name
    */
   public void setImage(String newName) {
      name = newName.intern();
   }


   /**
    *  Set the number of indirection for the array
    *
    * @param  count  the count
    */
   public void setArrayCount(int count) {
      arrayCount = count;
   }


   /**
    *  Get the object's name
    *
    * @return    the name
    */
   public String getName() {
      return name;
   }


   /**
    *  Get the number of indirection for the array
    *
    * @return    the count
    */
   public int getArrayCount() {
      return arrayCount;
   }


   /**
    *  Gets the exceptionBlockParameter attribute of the ASTVariableDeclaratorId node.
    *
    * @return    The exceptionBlockParameter value
    */
   public boolean isExceptionBlockParameter() {
      return jjtGetParent().jjtGetParent() instanceof ASTTryStatement;
   }


   /**
    *  Gets the typeNameNode attribute of the ASTVariableDeclaratorId node.
    *
    * @return    The typeNameNode value
    */
   public SimpleNode getTypeNameNode() {
      if (jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration) {
         return findTypeNameNode(jjtGetParent().jjtGetParent());
      } else if (jjtGetParent() instanceof ASTFormalParameter) {
         return findTypeNameNode(jjtGetParent());
      } else if (jjtGetParent().jjtGetParent() instanceof ASTFieldDeclaration) {
         return findTypeNameNode(jjtGetParent().jjtGetParent());
      }
      throw new RuntimeException("Don't know how to get the type for anything other than a ASTLocalVariableDeclaration/ASTFormalParameterASTFieldDeclaration");
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTVariableDeclaratorId node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }


   /**
    *  Description of the Method
    *
    * @param  node  Description of Parameter
    * @return       Description of the Returned Value
    */
   private SimpleNode findTypeNameNode(Node node) {
      ASTType typeNode = (ASTType)node.jjtGetFirstChild();
      SimpleNode refNode = (SimpleNode)typeNode.jjtGetFirstChild();
      if (refNode instanceof ASTReferenceType) {
         return (SimpleNode)refNode.jjtGetFirstChild();
      }
      return (SimpleNode)typeNode.jjtGetFirstChild();
   }

}

