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


package com.asemantics.rdfcoder.storage;

import com.asemantics.model.CodeModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Defines a persistent sotrage for a <code>CodeModel</code>.
 */
public abstract class CodeStorage {

    public static final String STORAGE_FS  = "fs";
    public static final String FS_FILENAME = "filename";

    public static final String STORAGE_DB  = "db";
    public static final String DB_SERVER   = "server";
    public static final String DB_PORT     = "port";
    public static final String DB_NAME     = "name";
    public static final String DB_USERNAME = "username";
    public static final String DB_PASSWORD = "password";

    /**
     * Loads a model from an {@link InputStream}.
     *
     * @param codeModel
     * @param inputStream
     */
    public abstract void loadModel(CodeModel codeModel, InputStream inputStream) throws CodeStorageException;

    /**
     * Saves a model to an {@link java.io.OutputStream}.
     * 
     * @param codeModel
     * @param outputStream
     */
    public abstract void saveModel(CodeModel codeModel, OutputStream outputStream) throws CodeStorageException;

    /**
     * Save a model to a storage specified by given parameters.
     *
     * @param codeModel
     * @param parameters
     */
    public abstract void saveModel(CodeModel codeModel, Map<String,String> parameters) throws CodeStorageException;

    /**
     * Loads a model from a storage by using given parameters.
     * @param codeModel
     * @param parameters
     */
    public abstract void loadModel(CodeModel codeModel, Map parameters) throws CodeStorageException;

    /**
     * Returns true if database storage is supported.
     * @return <code>true</code> if database is supported.
     */
    public abstract boolean supportsDatabase();

    /**
     * Returns true if filesystem storage is supported.
     * @return <code>true</code> if filesystem is supported.
     */
    public abstract boolean supportsFile();

    protected FileInputStream openFileInputStream(Map<String,String> parameters) {
        if(parameters == null) {
            throw new NullPointerException("parameters cannot be null");
        }

        String fileName = parameters.get(FS_FILENAME);
        if(fileName == null) {
            throw new IllegalArgumentException("Cannot find FS_FILENAME parameter into parameters.");
        }

        try {
            FileInputStream fis = new FileInputStream(fileName);
            return fis;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Cannot find file: " + fileName);
        }
    }

    protected FileOutputStream openFileOutputStream(Map<String,String> parameters) {
        if(parameters == null) {
            throw new NullPointerException("parameters cannot be null");
        }

        String fileName = parameters.get(FS_FILENAME);
        if(fileName == null) {
            throw new IllegalArgumentException("Cannot find FS_FILENAME parameter into parameters.");
        }

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            return fos;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Cannot find file: " + fileName);
        }
    }

}
