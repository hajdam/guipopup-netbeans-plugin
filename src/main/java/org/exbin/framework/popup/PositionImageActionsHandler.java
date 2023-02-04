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
package org.exbin.framework.popup;

import java.awt.Point;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for image handler for visual component / context menu.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface PositionImageActionsHandler {

    /**
     * Performs copy image on given relative position to clipboard operation.
     *
     * @param locationOnScreen location on screen
     */
    void performCopyImage(Point locationOnScreen);

    /**
     * Returns if true if image is selected on given relative position.
     *
     * @param locationOnScreen location on screen
     * @return true if image is selected
     */
    boolean isImageSelected(Point locationOnScreen);
}
