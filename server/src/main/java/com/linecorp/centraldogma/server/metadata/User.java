/*
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.linecorp.centraldogma.server.metadata;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import com.linecorp.centraldogma.internal.Util;

/**
 * A user.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class User implements Identifiable, Serializable {

    private static final long serialVersionUID = -5429782019985526549L;

    private static final String LEVEL_USER_STR = "LEVEL_USER";
    private static final String LEVEL_SYSTEM_ADMIN_STR = "LEVEL_SYSTEM_ADMIN";

    // System-wide roles for a user. It is different from the role in a project.
    public static final List<String> LEVEL_USER = ImmutableList.of(LEVEL_USER_STR);
    public static final List<String> LEVEL_SYSTEM_ADMIN =
            ImmutableList.of(LEVEL_SYSTEM_ADMIN_STR, LEVEL_USER_STR);

    public static final User DEFAULT = new User("user@localhost.localdomain", LEVEL_USER);
    public static final User SYSTEM_ADMIN = new User("admin@localhost.localdomain", LEVEL_SYSTEM_ADMIN);
    public static final User SYSTEM = new User("system@localhost.localdomain", LEVEL_SYSTEM_ADMIN);

    private final String login;
    private final String name;
    private final String email;
    private final List<String> roles;

    private final boolean isSystemAdmin;

    /**
     * Creates a new instance.
     */
    @JsonCreator
    public User(@JsonProperty("login") String login,
                @JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("roles") List<String> roles) {
        this.login = requireNonNull(login, "login");
        this.name = requireNonNull(name, "name");
        this.email = requireNonNull(email, "email");
        this.roles = ImmutableList.copyOf(requireNonNull(roles, "roles"));
        isSystemAdmin = roles.stream().anyMatch(LEVEL_SYSTEM_ADMIN_STR::equals);
    }

    /**
     * Creates a new instance.
     */
    public User(String login) {
        this(login, LEVEL_USER);
    }

    /**
     * Creates a new instance.
     */
    public User(String login, List<String> roles) {
        if (Strings.isNullOrEmpty(login)) {
            throw new IllegalArgumentException("login");
        }
        requireNonNull(roles, "roles");

        this.login = login;
        email = Util.toEmailAddress(login, "login");
        name = Util.emailToUsername(email, "login");

        this.roles = ImmutableList.copyOf(roles);
        isSystemAdmin = roles.stream().anyMatch(LEVEL_SYSTEM_ADMIN_STR::equals);
    }

    /**
     * Returns the login ID of the user.
     */
    @JsonProperty
    public String login() {
        return login;
    }

    /**
     * Returns the human friendly name of the user.
     */
    @JsonProperty
    public String name() {
        return name;
    }

    /**
     * Returns the e-mail address of the user.
     */
    @JsonProperty
    public String email() {
        return email;
    }

    /**
     * Returns the roles of the user.
     */
    @JsonProperty
    public List<String> roles() {
        return roles;
    }

    @Override
    public String id() {
        return email();
    }

    /**
     * Returns {@code true} if this user has system administrative privileges.
     */
    public boolean isSystemAdmin() {
        return isSystemAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final User user = (User) o;
        return login.equals(user.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("login", login())
                          .add("name", name())
                          .add("email", email())
                          .add("roles", roles())
                          .add("isSystemAdmin", isSystemAdmin())
                          .toString();
    }
}
