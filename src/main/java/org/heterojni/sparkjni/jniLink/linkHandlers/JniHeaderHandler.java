/**
 * Copyright 2016 Tudor Alexandru Voicu and Zaid Al-Ars, TUDelft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.heterojni.sparkjni.jniLink.linkHandlers;

import org.heterojni.sparkjni.exceptions.HardSparkJniException;
import org.heterojni.sparkjni.exceptions.SoftSparkJniException;
import org.heterojni.sparkjni.exceptions.Messages;
import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Container which parses the javah-generated jni function header files and retrieves JNI prototypes.
 */
public class JniHeaderHandler {
    private File jniHeaderFile;
    private String fullyQualifiedJavaClassName = null;
    private List<FunctionSignatureMapper> jniFunctions = null;

    public JniHeaderHandler(File jniHeaderFile) {
        this.jniHeaderFile = jniHeaderFile;
    }

    public String getFullyQualifiedJavaClassName() {
        if(fullyQualifiedJavaClassName == null)
            parse();
        return fullyQualifiedJavaClassName;
    }

    public List<FunctionSignatureMapper> getJniFunctions() {
        if(jniFunctions == null)
            parse();
        return jniFunctions;
    }

    private void parse(){
        String fn = jniHeaderFile.getName();
        fullyQualifiedJavaClassName = fn.replaceAll("_",".");
        fullyQualifiedJavaClassName = fullyQualifiedJavaClassName.substring(0, fullyQualifiedJavaClassName.length() - 2);
        try {
            registerJniFunctions();
        } catch(Exception exception){
            exception.printStackTrace();
        }
    }

    private void registerJniFunctions() throws Exception{
        jniFunctions = new ArrayList<>();
        List<String> linesInFile = Files.readAllLines(jniHeaderFile.toPath(), Charset.defaultCharset());
        for(int lineIdx = 0; lineIdx < linesInFile.size(); lineIdx++)
        {
            String currentLine = linesInFile.get(lineIdx);
            if(currentLine.contains("JNIEXPORT")){
                String[] tokens = currentLine.split("\\s");
                try {
                    jniFunctions.add(
                            ImmutableFunctionSignatureMapperProvider.builder()
                            .fullyQualifiedJavaClass(fullyQualifiedJavaClassName)
                            .parametersLine(linesInFile.get(++lineIdx))
                            .tokens(tokens)
                            .build()
                            .buildFunctionSignatureMapper()
                    );
                } catch(IndexOutOfBoundsException e){
                    throw new HardSparkJniException(String.format(Messages.ERR_INVALID_FORMATTING_FOR_FILE_AT_LINE, jniHeaderFile.getName(), currentLine));
                } catch (SoftSparkJniException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public boolean removeHeaderFile(){
        if(jniHeaderFile.exists())
            return jniHeaderFile.delete();
        else
            return false;
    }
}
