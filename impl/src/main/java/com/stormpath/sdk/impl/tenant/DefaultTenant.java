/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.tenant;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.EmailVerificationToken;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.CreateApplicationAndDirectoryRequest;
import com.stormpath.sdk.application.CreateApplicationRequest;
import com.stormpath.sdk.application.CreateApplicationRequestVisitor;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.tenant.Tenant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.1
 */
public class DefaultTenant extends AbstractInstanceResource implements Tenant {

    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String APPLICATIONS = "applications";
    private static final String DIRECTORIES = "directories";

    public DefaultTenant(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultTenant(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getName() {
        return getStringProperty(NAME);
    }

    @Override
    public String getKey() {
        return getStringProperty(KEY);
    }

    @Override
    public Application createApplication(Application application) {
        CreateApplicationRequest request = CreateApplicationRequest.with(application).build();
        return createApplication(request);
    }

    @Override
    public Application createApplication(CreateApplicationRequest request) {

        final Application application = request.getApplication();
        final String[] href = new String[]{"/" + APPLICATIONS };

        request.accept(new CreateApplicationRequestVisitor() {
            @Override
            public void visit(CreateApplicationRequest ignored) {
            }
            @Override
            public void visit(CreateApplicationAndDirectoryRequest request) {
                String name = request.getDirectoryName();
                if (name == null) {
                    name = "true"; //boolean true means 'auto name the directory'
                }
                href[0] += "?createDirectory=" + name;
            }
        });

        return getDataStore().create(href[0], application);
    }

    @Override
    public ApplicationList getApplications() {
        return getResourceProperty(APPLICATIONS, ApplicationList.class);
    }

    @Override
    public ApplicationList getApplications(Map<String,Object> queryParams) {
        ApplicationList proxy = getApplications(); //just a proxy - does not execute a query until iteration occurs
        return getDataStore().getResource(proxy.getHref(), ApplicationList.class, queryParams);
    }

    @Override
    public DirectoryList getDirectories() {
        return getResourceProperty(DIRECTORIES, DirectoryList.class);
    }

    @Override
    public DirectoryList getDirectories(Map<String, Object> queryParams) {
        DirectoryList proxy = getDirectories();
        return getDataStore().getResource(proxy.getHref(), DirectoryList.class, queryParams);
    }

    @Override
    public ApplicationList list(ApplicationCriteria criteria) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Account verifyAccountEmail(String token) {

        //TODO enable auto discovery via Tenant resource (should be just /emailVerificationTokens
        String href = "/accounts/emailVerificationTokens/" + token;

        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put(HREF_PROP_NAME, href);

        EmailVerificationToken evToken = getDataStore().instantiate(EmailVerificationToken.class, props);

        //execute a POST (should clean this up / make it more obvious)
        return getDataStore().save(evToken, Account.class);
    }
}
