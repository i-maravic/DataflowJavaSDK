/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.dataflow.sdk.util.state;

/**
 * A namespace used for scoping state stored with {@link StateInternals}.
 *
 * <p> Instances of {@code StateNamespace} are guaranteed to have a {@link #hashCode} and
 * {@link #equals} that uniquely identify the namespace.
 */
public interface StateNamespace {

  /**
   * Return a {@link String} representation of the key. It is guaranteed that this
   * {@code String} will uniquely identify the key.
   *
   * <p> This will encode the actual namespace as a {@code String}. It is
   * preferable to use the {@code StateNamespace} object when possible.
   */
  String stringKey();
}
