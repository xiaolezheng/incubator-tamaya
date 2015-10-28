/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.integration.osgi.general;

import java.io.IOException;
import java.util.*;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.BiPredicate;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.mutableconfig.ConfigChangeProvider;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * Tamaya based implementation of an OSGI {@link Configuration}.
 */
public class TamayaConfigurationImpl implements Configuration {
    private final String pid;
    private final String factoryPid;
    private Map<String, String> properties = new HashMap<>();
    private org.apache.tamaya.Configuration config;

    public TamayaConfigurationImpl(String confPid, String factoryPid, ConfigurationAdmin parent) {
        this.pid = confPid;
        this.factoryPid = factoryPid;
        if(parent!=null){
            try {
                Dictionary<String, Object> conf = parent.getConfiguration(confPid, factoryPid).getProperties();
                Enumeration<String> keys = conf.keys();
                while(keys.hasMoreElements()){
                    String key = keys.nextElement();
                    this.properties.put(key, conf.get(key).toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = ConfigurationProvider.getConfiguration();
        this.properties.putAll(
                config.with(ConfigurationFunctions.filter(new BiPredicate<String, String>() {
                    @Override
                    public boolean test(String key, String value) {
                        // TODO define name space / SPI
                        if (key.startsWith("bundle." + pid)) {
                            return true;
                        }
                        return false;
            }
        })).getProperties());
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public Dictionary<String, Object> getProperties() {
        return new Hashtable<String, Object>(properties);
    }

    @Override
    public void update(Dictionary<String, ?> properties) throws IOException {
                                                                                                                                                                                                                                                                                                                                                                                                                         ConfigChangeProvider.createChangeRequest(this.config)
    }

    @Override
    public void delete() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFactoryPid() {
        return factoryPid;
    }

    @Override
    public void update() throws IOException {
        this.config = ConfigurationProvider.getConfiguration();
        this.properties = config.with(ConfigurationFunctions.filter(new BiPredicate<String, String>() {
            @Override
            public boolean test(String key, String value) {
// TODO define name space / SPI
                return false;
            }
        })).getProperties();
    }

    @Override
    public void setBundleLocation(String location) {
    }

    @Override
    public String getBundleLocation() {
        return null;
    }

    @Override
    public long getChangeCount() {
        return 0;
    }

}