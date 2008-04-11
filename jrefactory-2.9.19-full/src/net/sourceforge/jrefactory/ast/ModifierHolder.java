/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;




/**
 *  Holds a description of the modifiers for a field or a class
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public interface ModifierHolder {

   /**
    *  The <code>int</code> value representing the <code>public</code> modifier.
    *
    * @since    v 1.0
    */
   final static int PUBLIC = 0x00000001;

   /**
    *  The <code>int</code> value representing the <code>private</code> modifier.
    *
    * @since    v 1.0
    */
   final static int PRIVATE = 0x00000002;

   /**
    *  The <code>int</code> value representing the <code>protected</code> modifier.
    *
    * @since    v 1.0
    */
   final static int PROTECTED = 0x00000004;

   /**
    *  The <code>int</code> value representing the <code>static</code> modifier.
    *
    * @since    v 1.0
    */
   final static int STATIC = 0x00000008;

   /**
    *  The <code>int</code> value representing the <code>final</code> modifier.
    *
    * @since    v 1.0
    */
   final static int FINAL = 0x00000010;

   /**
    *  The <code>int</code> value representing the <code>synchronized</code> modifier.
    *
    * @since    v 1.0
    */
   final static int SYNCHRONIZED = 0x00000020;

   /**
    *  The <code>int</code> value representing the <code>volatile</code> modifier.
    *
    * @since    v 1.0
    */
   final static int VOLATILE = 0x00000040;

   /**
    *  The <code>int</code> value representing the <code>transient</code> modifier.
    *
    * @since    v 1.0
    */
   final static int TRANSIENT = 0x00000080;

   /**
    *  The <code>int</code> value representing the <code>native</code> modifier.
    *
    * @since    v 1.0
    */
   final static int NATIVE = 0x00000100;

   /**
    *  The <code>int</code> value representing the <code>interface</code> modifier.
    *
    * @since    v 1.0
    */
   final static int INTERFACE = 0x00000200;

   /**
    *  The <code>int</code> value representing the <code>abstract</code> modifier.
    *
    * @since    v 1.0
    */
   final static int ABSTRACT = 0x00000400;

   /**
    *  The <code>int</code> value representing the <code>strictfp</code> modifier.
    *
    * @since    v 1.0
    */
   final static int STRICTFP = 0x00000800;

   /**
    *  Description of the Field
    *
    * @since    v 1.0
    */
   final static int EXPLICIT = 0x2000;


   /**
    *  Description of the Field
    *
    * @since    v 1.0
    */
   final static String[] names =
            {
            "abstract",
            "explicit",
            "final",
            "interface",
            "native",
            "private",
            "protected",
            "public",
            "static",
            "strict",
            "strictfp",
            "synchronized",
            "transient",
            "volatile"
            };


   /**
    *  Gets the modifier bits
    *
    * @return    the modifier bits
    * @since     v 1.0
    */
   int getModifiers();


   /**
    *  Sets the modifier bits
    *
    * @param  modifiers  the modifier bits
    * @since             v 1.0
    */
   void setModifiers(int modifiers);


   /**
    *  Sets the private bit in the modifiers
    *
    * @param  value  true if we are setting the private modifier
    * @since         v 1.0
    */
   void setPrivate(boolean value);


   /**
    *  Sets the private bit (to true) in the modifiers
    *
    * @since    v 1.0
    */
   void setPrivate();


   /**
    *  Sets the protected bit in the modifiers
    *
    * @param  value  true if we are setting the protected modifier
    * @since         v 1.0
    */
   void setProtected(boolean value);


   /**
    *  Sets the protected bit (to true) in the modifiers
    *
    * @since    v 1.0
    */
   void setProtected();


   /**
    *  Sets the public bit in the modifiers
    *
    * @param  value  true if we are setting the public modifier
    * @since         v 1.0
    */
   void setPublic(boolean value);


   /**
    *  Sets the public bit (to true) in the modifiers
    *
    * @since    v 1.0
    */
   void setPublic();


   /**
    *  Sets the abstract bit in the modifiers
    *
    * @param  value  true if we are setting the modifier
    * @since         v 1.0
    */
   void setAbstract(boolean value);


   /**
    *  Sets the abstract bit (to true) in the modifiers
    *
    * @since    v 1.0
    */
   void setAbstract();


   /**
    *  Sets the Synchronized bit of the in the modifiers
    *
    * @param  value  The new Synchronized value
    * @since         v 1.0
    */
   void setSynchronized(boolean value);


   /**
    *  Sets the Synchronized bit (to true) in the modifiers
    *
    * @since    v 1.0
    */
   void setSynchronized();


   /**
    *  Sets the Static bit of the in the modifiers
    *
    * @param  value  The new Static value
    * @since         v 1.0
    */
   void setStatic(boolean value);


   /**
    *  Sets the Static bit (to true) of the in the modifiers
    *
    * @since    v 1.0
    */
   void setStatic();


   /**
    *  Sets the Final bit (to true) of the in the modifiers
    *
    * @since    v 1.0
    */
   void setFinal();


   /**
    *  Sets the StrictFP bit (to true) of the in the modifiers
    *
    * @since    v 1.0
    */
   void setStrict();


   /**
    *  Determine if the node is abstract
    *
    * @return    true if this stores an ABSTRACT flag
    * @since     v 1.0
    */
   boolean isAbstract();


   /**
    *  Determine if the node is explicit
    *
    * @return    true if this stores an EXPLICIT flag
    * @since     v 1.0
    */
   boolean isExplicit();


   /**
    *  Determine if the node is final
    *
    * @return    true if this stores an FINAL flag
    * @since     v 1.0
    */
   boolean isFinal();


   /**
    *  Determine if the node is interface
    *
    * @return    true if this stores an INTERFACE flag
    * @since     v 1.0
    */
   boolean isInterface();


   /**
    *  Determine if the node is native
    *
    * @return    true if this stores an NATIVE flag
    * @since     v 1.0
    */
   boolean isNative();


   /**
    *  Determine if the node is private
    *
    * @return    true if this stores an PRIVATE flag
    * @since     v 1.0
    */
   boolean isPrivate();


   /**
    *  Determine if the node is protected
    *
    * @return    true if this stores an PROTECTED flag
    * @since     v 1.0
    */
   boolean isProtected();


   /**
    *  Determine if the node is public
    *
    * @return    true if this stores an PUBLIC flag
    * @since     v 1.0
    */
   boolean isPublic();


   /**
    *  Determine if the node is static
    *
    * @return    true if this stores an static flag
    * @since     v 1.0
    */
   boolean isStatic();


   /**
    *  Determine if the node is strictFP
    *
    * @return    true if this stores an STRICTFP flag
    * @since     v 1.0
    */
   boolean isStrictFP();


   /**
    *  Determine if the node is synchronized
    *
    * @return    true if this stores an SYNCHRONIZED flag
    * @since     v 1.0
    */
   boolean isSynchronized();


   /**
    *  Determine if the node is transient
    *
    * @return    true if this stores an TRANSIENT flag
    * @since     v 1.0
    */
   boolean isTransient();


   /**
    *  Determine if the node is volatile
    *
    * @return    true if this stores an VOLATILE flag
    * @since     v 1.0
    */
   boolean isVolatile();


   /**
    *  Determines if this has package scope
    *
    * @return    true if this has package scope
    * @since     v 1.0
    */
   boolean isPackage();


   /**
    *  Add a modifier
    *
    * @param  mod  the new modifier
    * @since       v 1.0
    */
   void addModifier(String mod);


   /**
    *  Description of the Method
    *
    * @param  source  Description of Parameter
    * @since          v 1.0
    */
   void copyModifiers(ModifierHolder source);
}

