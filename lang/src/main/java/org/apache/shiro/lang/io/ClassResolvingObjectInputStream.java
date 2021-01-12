/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.lang.io;

import org.apache.shiro.lang.util.ClassUtils;
import org.apache.shiro.lang.util.UnknownClassException;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Enables correct ClassLoader lookup in various environments (e.g. JEE Servers, etc).
 *
 * @since 1.2
 * @see <a href="https://issues.apache.org/jira/browse/SHIRO-334">SHIRO-334</a>
 */
public class ClassResolvingObjectInputStream extends ObjectInputStream {

    public ClassResolvingObjectInputStream(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    /** black listing the files causing the vurnability in deserielzation*/
     

     final List<String> isBlacklisted = Arrays.asList("org.apache.commons.collections.functors.ChainedTransformer.transform",
                                                     "org.apache.commons.collections.functors.InvokerTransformer",
                                                     "org.apache.commons.collections.functors.InstantiateTransformer",
                                                     "org.apache.commons.collections4.functors.InvokerTransformer",
                                                     "org.apache.commons.collections4.functors.InstantiateTransformer",
                                                     "org.codehaus.groovy.runtime.ConvertedClosure",
                                                     "org.codehaus.groovy.runtime.MethodClosure",
                                                     " org.springframework.beans.factory.ObjectFactory",
                                                     "xalan.internal.xsltc.trax.TemplatesImpl");

    
    /**
     * Resolves an {@link ObjectStreamClass} by delegating to Shiro's 
     * {@link ClassUtils#forName(String)} utility method, which is known to work in all ClassLoader environments.
     * 
     * @param osc the ObjectStreamClass to resolve the class name.
     * @return the discovered class
     * @throws IOException never - declaration retained for subclass consistency
     * @throws ClassNotFoundException if the class could not be found in any known ClassLoader
     */
    @Override
    protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
         String name = osc.getName();
        try {
            if(isBlacklisted.contains(name)){
                throw new IOException("Cannot be Deseialized");
            }
            return ClassUtils.forName(name);
        } catch (UnknownClassException e) {
            throw new ClassNotFoundException("Unable to load ObjectStreamClass [" + osc + "]: ", e);
        }
    }
}
