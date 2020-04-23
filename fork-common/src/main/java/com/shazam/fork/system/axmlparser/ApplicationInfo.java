/*
 * Copyright 2019 Apple Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.fork.system.axmlparser;

import com.google.common.collect.ImmutableList;
import com.shazam.fork.model.Permission;

import java.util.List;

import javax.annotation.Nonnull;

public class ApplicationInfo {

    private final List<Permission> permissions;

    public ApplicationInfo(@Nonnull List<Permission> permissions) {
        this.permissions = ImmutableList.copyOf(permissions);
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
