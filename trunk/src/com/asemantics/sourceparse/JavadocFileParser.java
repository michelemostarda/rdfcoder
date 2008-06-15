/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.sourceparse;

import com.asemantics.CoderUtils;
import com.asemantics.model.CodeHandler;
import com.asemantics.model.JavadocHandler;

import java.io.*;
import java.util.*;

/**
 * The Javadoc file parser.
 */
public class JavadocFileParser extends FileParser {

    /**
     * Defines any token of a javadoc.
     */
    static class JDToken {

        private String value;

        JDToken(String v) {
            value = v;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Defines an End of File token.
     */
    static class JDEof extends JDToken {
        JDEof() {
            super(null);
        }
    }
    static final JDEof JD_EOF = new JDEof();

    /**
     * Defines an open comment.
     */
    static class JDOpenComment extends JDToken {

        private int row;
        private int col;

        JDOpenComment(int r, int c) {
            super(null);
            row = r;
            col = c;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    /**
     * Defines a close comment.
     */
    static class JDCloseComment extends JDToken {
        JDCloseComment() {
            super(null);
        }
    }
    static final JDCloseComment JD_CLOSE_COMMENT = new JDCloseComment();

    /**
     * Defines a javadoc attribute.
     */
    class JDAttribute extends JDToken {
        JDAttribute(String v) {
            super(v);
        }
    }

    /**
     * Defines any javadoc word.
     */
    class JDWord extends JDToken {
        JDWord(String v) {
            super(v);
        }
    }

    /**
     * Defines a javadoc class declarator.
     */
    class JDClassDeclarator extends JDToken {

        private int row;
        private int col;

        JDClassDeclarator(int r, int c) {
            super(null);
            row = r;
            col = c;
        }

        public int getCol() {
            return col;
        }

        public int getRow() {
            return row;
        }
    }

    /**
     * Defines the opening of a class or method.
     */
    static class JDOpenClassOrMethod extends JDToken {
        JDOpenClassOrMethod() {
            super(null);
        }
    }
    static final JDOpenClassOrMethod JD_OPEN_CLASS_OR_METHOD = new JDOpenClassOrMethod();

    /**
     * Defines the closure of a class or method.
     */
   static class JDCloseClassOrMethod extends JDToken {
        JDCloseClassOrMethod() {
            super(null);
        }
    }
    static final JDCloseClassOrMethod JD_CLOSE_CLASS_OR_METHOD = new JDCloseClassOrMethod();

    /**
     * Defines the begin of a parameter.
     */
    static class JDOpenParameters extends JDToken {
        JDOpenParameters() {
            super(null);
        }
    }
    static final JDOpenParameters  JD_OPEN_PARAMETERS = new JDOpenParameters();

    /**
     * Defines the end of a parameter.
     */
    static class JDCloseParameters extends JDToken {
        JDCloseParameters() {
            super(null);
        }
    }
    static final JDCloseParameters JD_CLOSE_PARAMETERS = new JDCloseParameters();

    /**
     * Defines a parameter separator.
     */
    static class JDParametersSeparator extends JDToken {
        JDParametersSeparator() {
            super(null);
        }
    }
    static final JDParametersSeparator JD_PARAMETERS_SEPARATOR = new JDParametersSeparator();

    /**
     * Contains all data relative the parsing of a class.
     */
    private class JDClassData {

        String classIdentifier;

        int parentesisCounter = 0;

        JDClassData(String ci) {
            classIdentifier = ci;
        }

        void increment() {
            parentesisCounter++;
        }

        void decrement() {
            parentesisCounter--;
        }

        boolean endOfClass() {
            return parentesisCounter == 0;
        }

        public String toString() {
            return classIdentifier;
        }
    }

    private class JDParameter {
        String type;
        String name;

        JDParameter(String t, String n) {
            type = t;
            name = n;
        }
    }

    /**
     * The back tracking buffer size.
     */
    private static final int BACKTRACK_BUFFER_SIZE = 3;

    /**
     * Contains the list of tokens to be processed.
     */
    private class TokensPipe {

        /**
         * Internal queue.
         */
        private Queue<JDToken> queue;

        /**
         * Back tracking buffer.
         */
        private List<JDToken> backTrackBuffer = new ArrayList<JDToken>();

        /**
         * Constructor.
         */
        TokensPipe() {
            queue = new LinkedList<JDToken>();
        }

        /**
         * Adds a token to the pipe.
         *
         * @param token
         */
        void addToken(JDToken token) {
            queue.add(token);
        }

        /**
         * Returns the next token to be processed.
         *
         * @return
         */
        public JDToken nextToken() {
            JDToken headToken = queue.poll();
            if(headToken != null) {
                if(backTrackBuffer.size() == BACKTRACK_BUFFER_SIZE) {
                    backTrackBuffer.remove(0);
                }
                backTrackBuffer.add(headToken);
            }
            return headToken;
        }

        /**
         * Returns the token with backtrack <i>b</i>.
         * @param b
         * @return
         */
        public JDToken backtrack(int b) {
            if(b <= 0) {
                throw new IllegalArgumentException();
            }
            if(b > backTrackBuffer.size()) {
                return null;
            }
            return backTrackBuffer.get(b - 1);
        }

        public void clear() {
            queue.clear();
            backTrackBuffer.clear();
        }
    }

    /**
     * Tokens pipe instance.
     */
    private TokensPipe tokensPipe;

    /**
     * Tokens buffer instance.
     */
    private StringBuilder tokenBuffer;

    /**
     * Classes stack.
     */
    private Stack<JDClassData> classes;

    /**
     * Current method name.
     */
    private String methodName;

    /**
     * Last open comment.
     */
    private JDOpenComment lastOpenComment;

    /**
     * Status flag.
     */
    private boolean classExpected;

    /**
     * Inside comment flag.
     */
    private boolean insideComment;

    /**
     * Text buffer for entry text.
     */
    private StringBuilder entryText;

    /**
     * Current attribute.
     */
    private String attribute;

    /**
     * Attribute text.
     */
    private StringBuilder attributeText;

    /**
     * Attributes map.
     */
    private Map<String,List<String>> attributesMap;

    /**
     * Current javadoc entry.
     */
    private JavadocEntry lastEntry;

    /**
     * Javadoc parameters.
     */
    private List<JDParameter> parameters;

    /**
     * Current row.
     */
    private int row;

    /**
     * Current column.
     */
    private int col;

    /**
     * Constructor with initialization.
     */
    public JavadocFileParser() {
        row = col = 0;
        tokensPipe    = new TokensPipe();
        classes       = new Stack<JDClassData>();
        tokenBuffer   = new StringBuilder();
        entryText     = new StringBuilder();
        attributeText = new StringBuilder();
        attributesMap = new HashMap<String,List<String>>();
        parameters    = new ArrayList<JDParameter>();
    }

    /**
     * Parses a compilation unit.
     * 
     * @param inputStream
     * @param compilationUnitPath
     * @throws ParserException
     */
    public void parse(InputStream inputStream, String compilationUnitPath) throws ParserException {

        final JavadocHandler javadocHandler = (JavadocHandler) getParseHandler();

        tokensPipe.clear();
        classes.clear();
        methodName = null;
        attribute = null;
        entryText    .delete(0, entryText.length());
        attributeText.delete(0, attributeText.length());
        attributesMap.clear();
        parameters   .clear();
        lastEntry = null;

        try {
            getParseHandler().startCompilationUnit(compilationUnitPath);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        JDToken token;
        try {

            // Main cycle.
            while( true ) {

                // Extract tokens from imput stream.
                extractTokens(inputStream, tokensPipe);

                // Check if end of file has been reached.
                if( (token = tokensPipe.nextToken() ) == JD_EOF) {
                    break;
                }

                // Tokens to be processed when inside Javadoc block.
                if(insideComment) {

                    // Closing Javadoc, the entity is emitted.
                    if(token == JD_CLOSE_COMMENT) {

                        // Stores the last attribute if any.
                        storeCurrentAttribute();

                        insideComment = false;
                        String entryText = this.entryText.toString();
                        int splitPoint = entryText.indexOf(".");
                        String shortComment, longComment;
                        if(splitPoint != -1) {
                            shortComment = entryText.substring(0, splitPoint).trim();
                            longComment = entryText.substring(splitPoint + 1).trim();
                        } else {
                            shortComment = entryText;
                            longComment  = "";
                        }

                        // Creates last entry data.
                        JavadocEntry entry = new JavadocEntry(
                                shortComment,
                                longComment,
                                new HashMap<String,List<String>>(attributesMap),
                                lastOpenComment.getRow(),
                                lastOpenComment.getCol()
                        );
                        lastEntry = entry;

                        // Notifies last entry to listener.
                        try {
                            // A Javadoc entry has been found.
                            javadocHandler.parsedEntry(entry);
                        } catch(Throwable t) {
                            t.printStackTrace();
                        }

                    // Process attribute.
                    } else if( token instanceof JDAttribute) {

                        // Storing previous attribute.
                        storeCurrentAttribute();

                        attribute = token.getValue();
                        attributeText.delete(0, attributeText.length());

                    // Process attribute words.
                    } else if( attribute != null && token instanceof JDWord ) {

                        attributeText.append(token.getValue());

                    // Process entry comment words.
                    } else if( token instanceof JDWord ) {

                        entryText.append(token.getValue());

                    }

                // Tokens to be processed when outside javadoc blocks.
                } else {

                    // Javadoc commend opening.
                    if( token instanceof JDOpenComment ) {

                        //System.out.println("OPEN COMMENT!!");

                        lastOpenComment = (JDOpenComment) token;
                        insideComment = true;
                        entryText.delete(0, entryText.length());
                        attributesMap.clear();
                        attributeText.delete(0, attributeText.length());

                    // Class declaration beginning.
                    } else if(token instanceof JDClassDeclarator) {

                        classExpected = true;

                    // Class signature handling.
                    } else if(classExpected && token instanceof JDWord) {


                        classExpected = false;
                        classes.push( new JDClassData( token.getValue() ) );
                        if(lastEntry != null) {
                            String containerPath = getContainerPath();
                            try {
                                javadocHandler.classJavadoc( lastEntry, containerPath );
                            } catch(Throwable t) {
                                t.printStackTrace();
                            }
                            lastEntry = null;
                        }

                    // Opening class or method.
                    } else if( ! classes.isEmpty() && token == JD_OPEN_CLASS_OR_METHOD) {

                        classes.peek().increment();

                    // Closing class or method.
                    } else if(  ! classes.isEmpty() && token == JD_CLOSE_CLASS_OR_METHOD) {

                        classes.peek().decrement();
                        if(classes.peek().endOfClass()) {
                            classes.pop();
                        }

                    // Begin parameters list.
                    } else if(token == JD_OPEN_PARAMETERS) {

                        parameters.clear();
                        methodName = tokensPipe.backtrack(2).getValue();

                    // Ends parameters list.
                    } else if(token == JD_CLOSE_PARAMETERS) {

                        // Adds last parameter.
                        parameters.add( new JDParameter( tokensPipe.backtrack(1).getValue(), tokensPipe.backtrack(2).getValue() ) );

                        // Notifies to the listener the method javadoc recognition.
                        if(lastEntry != null) {
                            String containerPath = getContainerPath();
                            String pathToMethod    = (containerPath.equals("") ? "" : containerPath + CodeHandler.PACKAGE_SEPARATOR) + methodName;
                            String[] signature   = toSignature(parameters);
                            try {
                                javadocHandler.methodJavadoc(lastEntry, pathToMethod, signature);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                            methodName = null;
                            lastEntry  = null;
                        }

                    // Parameters separator.
                    } else if(token == JD_PARAMETERS_SEPARATOR) {

                        parameters.add( new JDParameter( tokensPipe.backtrack(1).getValue(), tokensPipe.backtrack(2).getValue() ) );

                    }

                }
            }

            for(JDClassData clazz : classes) {
                System.out.println(clazz);
            }

        } catch(IOException ioe) {
            throw new ParserException(ioe, compilationUnitPath);
        }

        try {
            getParseHandler().endCompilationUnit();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Parses a file.
     * @param file
     * @throws ParserException
     */
    public void parse(File file) throws ParserException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            parse(fileInputStream, file.getAbsolutePath());
            fileInputStream.close();
        } catch (FileNotFoundException fnfe) {
            throw new ParserException(fnfe, file.getAbsolutePath());
        } catch (IOException ioe) {
            throw new ParserException(ioe, file.getAbsolutePath());
        }
    }

    /**
     *
     * Tokenizer: extracts token from the given <i>char stream</i>.
     *
     * @param inputStream
     * @param tokensPipe
     * @throws IOException
     */
    private void extractTokens(InputStream inputStream, TokensPipe tokensPipe) throws IOException {
        int intc;
        char c;

        tokenBuffer.delete(0, tokenBuffer.length());
        while( (intc = inputStream.read()) != -1 ) {
            c = (char) intc;

            // Manage row / col position.
            if(c == '\n') {
                col = 0;
                row++;
            } else {
                col++;
            }

            // Manage special characters.
            if(c == ' ' || c == '\n' || c == '{' || c == '}' || c == '(' || c == ')' || c == ',') {

                // Tokens.

                String bufferContent = tokenBuffer.toString();
                //System.out.println("BUFFER CONTENT: '" + bufferContent + "'");

                // Handles inside <i>*</i> comment.
                if("*".equals(bufferContent)) {
                    tokenBuffer.delete(0, tokenBuffer.length());
                    continue;
                } 

                // Handles begin comment.
                if("/**".equals(bufferContent.trim())) {  //TODO: improve this check by removing trim() !
                    tokensPipe.addToken( new JDOpenComment(row, col) );
                    //System.out.println("OPEN COMMENT TOKEN");
                    return;
                }

                // Handles end comment.
                if("*/".equals(bufferContent)) {
                    tokensPipe.addToken( JD_CLOSE_COMMENT );
                    return;
                }

                // Handles class key.
                if("class".equals(bufferContent) ) {
                    tokensPipe.addToken( new JDClassDeclarator(row, col) );
                    return;
                }

                // Handles attribute.
                if(bufferContent.length() > 1 && bufferContent.charAt(0) == '@') {
                    tokensPipe.addToken( new JDAttribute(bufferContent) );
                    return;
                }

                // Handles word separators.
                //System.out.println("ADDTOKEN: '" + bufferContent + " '");
                tokensPipe.addToken( new JDWord(bufferContent + " ") );

                if( c == '{' ) {
                    tokensPipe.addToken(JD_OPEN_CLASS_OR_METHOD);
                } else if(c == '}') {
                    tokensPipe.addToken(JD_CLOSE_CLASS_OR_METHOD);
                } else if(c == '(') {
                    tokensPipe.addToken(JD_OPEN_PARAMETERS);
                } else if(c == ')') {
                    tokensPipe.addToken(JD_CLOSE_PARAMETERS);
                } else if(c == ',') {
                    tokensPipe.addToken(JD_PARAMETERS_SEPARATOR);
                }
                return;

            } else {
                tokenBuffer.append(c);
            }
        }
        tokensPipe.addToken( JD_EOF );
        return;
    }

    /**
     * Stores the current attribute.
     */
    private void storeCurrentAttribute() {
        if(attribute != null) {
            String key = attribute.trim();
            //System.out.println("key:" + key);
            List<String> list = attributesMap.get(key);
            if(list == null) {
                list = new ArrayList<String>();
                attributesMap.put(key, list);
            }
            //System.out.println("list " + key + "::" + list);
            list.add(attributeText.toString().trim());
        }
    }

    /**
     * Returns the container path.
     *
     * @return
     */
    private String getContainerPath() {
        return CoderUtils.join(classes, CoderUtils.PACKAGE_SEPARATOR);
    }

    /**
     * Converts the parameters list to a method signature.
     *
     * @param parameters
     * @return
     */
    private String[] toSignature(List<JDParameter> parameters) {
        String[] signature = new String[parameters.size()];
        for(int i = 0; i < parameters.size(); i++) {
            signature[i] = parameters.get(i).name;
        }
        return signature;
    }

}
