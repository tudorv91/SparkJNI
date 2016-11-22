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
package sparkjni.jniLink.linkHandlers;

import sparkjni.utils.exceptions.HardSparkJniException;
import sparkjni.utils.exceptions.Messages;
import sparkjni.utils.exceptions.SoftSparkJniException;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import sparkjni.jniLink.linkContainers.ImmutableJniHeader;
import sparkjni.jniLink.linkContainers.JniHeader;
import org.immutables.value.Value;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Value.Immutable
public abstract class JniHeaderProvider {
    abstract File jniHeaderFile();
    String fullyQualifiedJavaClassName;

    public JniHeader buildJniHeader(){
        generateFullyQualifiedJavaClassName();
        try {
            return  ImmutableJniHeader.builder()
                    .jniHeaderFile(jniHeaderFile())
                    .fileName(jniHeaderFile().getName())
                    .fullyQualifiedJavaClassName(fullyQualifiedJavaClassName)
                    .jniFunctions(registerJniFunctions())
                    .build();
        } catch(Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    private void generateFullyQualifiedJavaClassName(){
        String aux = jniHeaderFile().getName().replaceAll("_",".");
        fullyQualifiedJavaClassName = aux.substring(0, aux.length() - 2);
    }

    private List<FunctionSignatureMapper> registerJniFunctions() throws Exception{
        List<FunctionSignatureMapper> jniFunctions = new ArrayList<>();
        List<String> linesInFile = Files.readAllLines(jniHeaderFile().toPath(), Charset.defaultCharset());

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
                    throw new HardSparkJniException(String.format(Messages.ERR_INVALID_FORMATTING_FOR_FILE_AT_LINE, jniHeaderFile().getName(), currentLine));
                } catch (SoftSparkJniException e){
                    System.out.println(e.getMessage());
                }
            }
        }
        return jniFunctions;
    }
}
