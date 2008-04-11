package net.sourceforge.jrefactory.ast;


/**
 *  Provides methods which all scopes must implement. See JLS 6.3 for a description of scopes
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public interface Scope {
   /**
    *  Points this scope to its parent
    *
    * @param  parent  The new parent value
    */
   void setParent(Scope parent);


   /**
    *  Retrieves this scope's parent
    *
    * @return    The parent value
    */
   Scope getParent();

}

