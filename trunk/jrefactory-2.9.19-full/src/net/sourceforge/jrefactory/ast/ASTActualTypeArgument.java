/*
 *  Author: Mike Atkinson
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
 *  Stores an Actual Type Parameter (new to JDK 1.5). Created by the parser.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTActualTypeArgument extends SimpleNode {
   private boolean hasWildcard;
   private boolean hasSuper;
   private boolean hasExtends;


   /**
    *  Constructor for the ASTActualTypeArgument node.
    *
    * @param  identifier  The id of this node (JJTACTUALTYPEARGUMENT).
    */
   public ASTActualTypeArgument(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTActualTypeArgument node.
    *
    * @param  parser      The JavaParser that created this ASTActualTypeArgument node.
    * @param  identifier  The id of this node (JJTACTUALTYPEARGUMENT)
    */
   public ASTActualTypeArgument(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**  Sets the wildcard attribute of the ASTActualTypeArgument node. */
   public void setWildcard() {
      hasWildcard = true;
   }


   /**  Sets the super attribute of the ASTActualTypeArgument node. */
   public void setSuper() {
      hasSuper = true;
   }


   /**  Sets the extends attribute of the ASTActualTypeArgument node. */
   public void setExtends() {
      hasExtends = true;
   }


   /**
    *  Does the actual type argument take the form &lt;?,&gt; or &lt;? extends ReferenceType&gt; or &lt? super
    *  ReferenceType&gt;? If not it is a ReferenceType.
    *
    * @return    <code>true</code> if the type argument is a wildcard ("?").
    */
   public boolean hasWildcard() {
      return hasWildcard;
   }


   /**
    *  Does the actual type argument take the form &lt;? super ReferenceType&gt;?
    *
    * @return    <code>true</code> if the type argument has a <code>super</code> part.
    */
   public boolean hasSuper() {
      return hasSuper;
   }


   /**
    *  Does the actual type argument take the form &lt;? extends ReferenceType&gt;?
    *
    * @return    <code>true</code> if the type argument has an <code>extends</code> part.
    */
   public boolean hasExtends() {
      return hasExtends;
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTActualTypeArgument node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

