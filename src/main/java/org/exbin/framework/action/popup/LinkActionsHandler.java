/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.action.popup;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for link handler for visual component / context menu.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface LinkActionsHandler {

    /**
     * Performs copy link to clipboard operation.
     */
    void performCopyLink();

    /**
     * Opens link using default browser.
     */
    void performOpenLink();

    /**
     * Returns if true if link is selected.
     *
     * @return true if link is selected
     */
    boolean isLinkSelected();
}
