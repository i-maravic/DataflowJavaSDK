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

package com.google.cloud.dataflow.sdk.options;

import com.google.cloud.dataflow.sdk.annotations.Experimental;

/**
 * Options for controlling Cloud Debugger.
 */
@Description("[Experimental] Used to configure the Cloud Debugger")
@Experimental
public interface CloudDebuggerOptions {

  /**
   * Whether to enable the Cloud Debugger snapshot agent for the current job.
   */
  @Description("Whether to enable the Cloud Debugger snapshot agent for the current job.")
  boolean getEnableCloudDebugger();
  void setEnableCloudDebugger(boolean enabled);
}

