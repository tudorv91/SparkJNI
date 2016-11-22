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
package sparkjni.jniLink.linkContainers;

import sparkjni.dataLink.CppBean;
import org.immutables.value.Value;

@Value.Immutable
public abstract class TypeMapper {
    public abstract Class javaType();
    public abstract CppBean cppType();
    public abstract String jniType();

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(!(o instanceof TypeMapper))
            return false;
        TypeMapper other = (TypeMapper) o;

        return this.javaType().equals(other.javaType())
                && this.cppType().equals(other.cppType())
                && this.jniType().equals(other.jniType());
    }
}
