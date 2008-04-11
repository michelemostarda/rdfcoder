package com.asemantics.sourceparse;

import com.asemantics.CoderUtils;
import com.asemantics.model.CodeHandler;

import java.io.*;
import java.util.*;

/**
 * The Javadoc file parser.
 * //TODO: HIGH - TB tested.
 */
public class JavadocFileParser extends FileParser {

    static class JDToken {

        private String value;

        JDToken(String v) {
            value = v;
        }

        public String getValue() {
            return value;
        }
    }

    static class JDEof extends JDToken {
        JDEof() {
            super(null);
        }
    }
    static final JDEof JD_EOF = new JDEof();

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

    static class JDCloseComment extends JDToken {
        JDCloseComment() {
            super(null);
        }
    }
    static final JDCloseComment JD_CLOSE_COMMENT = new JDCloseComment();

    class JDAttribute extends JDToken {
        JDAttribute(String v) {
            super(v);
        }
    }

    class JDWord extends JDToken {
        JDWord(String v) {
            super(v);
        }
    }

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

    static class JDOpenClassOrMethod extends JDToken {
        JDOpenClassOrMethod() {
            super(null);
        }
    }
    static final JDOpenClassOrMethod JD_OPEN_CLASS_OR_METHOD = new JDOpenClassOrMethod();

   static class JDCloseClassOrMethod extends JDToken {
        JDCloseClassOrMethod() {
            super(null);
        }
    }
    static final JDCloseClassOrMethod JD_CLOSE_CLASS_OR_METHOD = new JDCloseClassOrMethod();

    static class JDOpenParameters extends JDToken {
        JDOpenParameters() {
            super(null);
        }
    }
    static final JDOpenParameters  JD_OPEN_PARAMETERS = new JDOpenParameters();

    static class JDCloseParameters extends JDToken {
        JDCloseParameters() {
            super(null);
        }
    }
    static final JDCloseParameters JD_CLOSE_PARAMETERS = new JDCloseParameters();

    static class JDParametersSeparator extends JDToken {
        JDParametersSeparator() {
            super(null);
        }
    }
    static final JDParametersSeparator JD_PARAMETERS_SEPARATOR = new JDParametersSeparator();

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
    }

    private class JDParameter {
        String type;
        String name;

        JDParameter(String t, String n) {
            type = t;
            name = n;
        }
    }

    private static final int BACKTRACK_BUFFER_SIZE = 3; 

    private class TokensPipe {

        private Queue<JDToken> queue;

        private List<JDToken> backTrackBuffer = new ArrayList<JDToken>();

        TokensPipe() {
            queue = new LinkedList<JDToken>();
        }

        void addToken(JDToken token) {
            queue.add(token);
        }

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

    private TokensPipe tokensPipe;

    private StringBuffer tokenBuffer;

    private Stack<JDClassData> classes;

    private String methodName;

    private boolean classExpected;

    private JDOpenComment lastOpenComment;

    private boolean insideComment;

    private StringBuffer entryText; 

    private String attribute;

    private StringBuffer attributeText;

    private Map<String,List<String>> attributesMap;

    private JavadocEntry lastEntry;

    private List<JDParameter> parameters;

    private int row;

    private int col;

    public JavadocFileParser() {
        tokensPipe    = new TokensPipe();
        classes       = new Stack<JDClassData>();
        tokenBuffer   = new StringBuffer();
        entryText     = new StringBuffer();
        attributeText = new StringBuffer();
        attributesMap = new HashMap<String,List<String>>();
        parameters    = new ArrayList<JDParameter>();
    }

    public void parse(InputStream inputStream, String compilationUnitPath) throws ParserException {

        getCodeHandler().startCompilationUnit(compilationUnitPath);

        tokensPipe.clear();
        classes.clear();
        methodName = null;
        attribute = null;
        entryText    .delete(0, entryText.length());
        attributeText.delete(0, attributeText.length());
        attributesMap.clear();
        parameters   .clear();
        lastEntry = null;

        JDToken token;
        try {
            while( true ) {
                nextTokens(inputStream, tokensPipe);
                if( (token = tokensPipe.nextToken() ) == JD_EOF) {
                    break;
                }

                if(insideComment) {
                    if(token == JD_CLOSE_COMMENT) {
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
                        JavadocEntry entry = new JavadocEntry(
                                shortComment,
                                longComment,
                                new HashMap<String,List<String>>(attributesMap),
                                lastOpenComment.getRow(),
                                lastOpenComment.getCol()
                        );
                        lastEntry = entry;
                        try {
                            // A Javadoc entry has been found.
                            getCodeHandler().parsedEntry(entry);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                    } else if( token instanceof JDAttribute) {

                        // Storing previous attribute.
                        if(attribute != null) {
                            String key = attribute.trim();
                            System.out.println("key:" + key);
                            List<String> list = attributesMap.get(key);
                            if(list == null) {
                                list = new ArrayList<String>();
                                System.out.println("PUTTING " + key + "::" + list);
                                attributesMap.put(key, list);
                            }
                            list.add(attributeText.toString().trim());
                        }

                        attribute = token.getValue();
                        attributeText.delete(0, attributeText.length());
                    } else if( attribute != null && token instanceof JDWord ) {
                        attributeText.append(token.getValue());
                    } else if( token instanceof JDWord ) {
                        entryText.append(token.getValue());
                    }
                } else { // Outside comment.
                    if( token instanceof JDOpenComment ) {
                        lastOpenComment = (JDOpenComment) token;
                        insideComment = true;
                        entryText.delete(0, entryText.length());
                        attributesMap.clear();
                        attributeText.delete(0, attributeText.length());
                    } else if(token instanceof JDClassDeclarator) {
                        classExpected = true;
                    } else if(classExpected && token instanceof JDWord) {
                        classes.push( new JDClassData( token.getValue() ) );
                        if(lastEntry != null) {
                            getCodeHandler().classJavadoc(lastEntry, classes.toString());
                            lastEntry = null;
                        }
                    } else if( ! classes.isEmpty() && token == JD_OPEN_CLASS_OR_METHOD) {
                        classes.peek().increment();
                    } else if(  ! classes.isEmpty() && token == JD_CLOSE_CLASS_OR_METHOD) {
                        classes.peek().decrement();
                        if(classes.peek().endOfClass()) {
                            classes.pop();
                        }
                    } else if(token == JD_OPEN_PARAMETERS) {
                        parameters.clear();
                        methodName = tokensPipe.backtrack(1).getValue();
                    } else if(token == JD_CLOSE_PARAMETERS) {
                        parameters.add( new JDParameter( tokensPipe.backtrack(1).getValue(), tokensPipe.backtrack(2).getValue() ) );
                        if(lastEntry != null) {
                            String containerPath = getContainerPath();
                            getCodeHandler().methodJavadoc(
                                    lastEntry,
                                    (containerPath.equals("") ? "" : containerPath + CodeHandler.PACKAGE_SEPARATOR) + methodName, 
                                    toSignature(parameters)
                            );
                            methodName = null;
                            lastEntry  = null;
                        }
                    } else if(token == JD_PARAMETERS_SEPARATOR) {
                        parameters.add( new JDParameter( tokensPipe.backtrack(1).getValue(), tokensPipe.backtrack(2).getValue() ) );
                    }
                    classExpected = false;
                }
            }
        } catch(IOException ioe) {
            throw new ParserException(ioe, compilationUnitPath);
        }

        getCodeHandler().endCompilationUnit();
    }

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

    private void nextTokens(InputStream inputStream, TokensPipe tokensPipe) throws IOException {
        int intc;
        char c;

        row = col = 0;
        tokenBuffer.delete(0, tokenBuffer.length());
        while( (intc = inputStream.read()) != -1 ) {
            c = (char) intc;

            if(c == '\n') {
                row = 0;
                col++;
            } else {
                row++;
            }

            if(c == ' ' || c == '\n' || c == '{' || c == '}' || c == '(' || c == ')' || c == ',') {

                // Tokens.

                String bufferContent = tokenBuffer.toString();
                if("*".equals(bufferContent)) {
                    tokenBuffer.delete(0, tokenBuffer.length());
                    continue;
                } 

                if("/**".equals(bufferContent)) {
                    tokensPipe.addToken( new JDOpenComment(row, col) );
                    return;
                }

                if("*/".equals(bufferContent)) {
                    tokensPipe.addToken( JD_CLOSE_COMMENT );
                    return;
                }

                if("class".equals(bufferContent) ) {
                    tokensPipe.addToken( new JDClassDeclarator(row, col) );
                    return;
                }

                if(bufferContent.length() > 1 && bufferContent.charAt(0) == '@') {
                    tokensPipe.addToken( new JDAttribute(bufferContent) );
                    return;
                }

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

    StringBuffer getContainerPath_sb = new StringBuffer();
        
    private String getContainerPath() {
        return CoderUtils.join(classes, CoderUtils.PACKAGE_SEPARATOR);
    }

    private String[] toSignature(List<JDParameter> parameters) {
        String[] signature = new String[parameters.size()];
        for(int i = 0; i < parameters.size(); i++) {
            signature[i] = parameters.get(i).name;
        }
        return signature;
    }

}
