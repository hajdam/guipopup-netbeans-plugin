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
package org.exbin.framework.action.api.clipboard;

/**
 * Interface for document clipboard actions controller.
 *
 * @author ExBin Project (https://exbin.org)
 */
public interface ClipboardController {

    /**
     * Performs cut to clipboard operation.
     */
    void performCut();

    /**
     * Performs copy to clipboard operation.
     */
    void performCopy();

    /**
     * Performs paste from clipboard operation.
     */
    void performPaste();

    /**
     * Returns whether it is possible to copy or cut content to the clipboard.
     *
     * @return true if can perform copy or cut
     */
    boolean hasDataToCopy();

    /**
     * Returns whether it is possible to paste current content of the clipboard.
     * <p>
     * TODO: Replace with "clipboard contains valid content for paste"
     *
     * @return true if can perform paste
     */
    boolean canPaste();
}
