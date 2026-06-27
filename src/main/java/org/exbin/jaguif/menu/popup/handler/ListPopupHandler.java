/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.menu.popup.handler;

import java.awt.datatransfer.StringSelection;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import org.exbin.jaguif.utils.ClipboardUtils;
import org.exbin.jaguif.action.api.clipboard.TextClipboardOperationController;

/**
 * Popup handler for JList.
 */
@NullMarked
public class ListPopupHandler implements TextClipboardOperationController {

    private final JList<?> listComp;

    public ListPopupHandler(JList<?> listComp) {
        this.listComp = listComp;
    }

    @Override
    public void performCut() {
        throw new IllegalStateException();
    }

    @Override
    public void performCopy() {
        StringBuilder builder = new StringBuilder();
        List<?> rows = listComp.getSelectedValuesList();
        boolean empty = true;
        for (Object row : rows) {
            builder.append(empty ? row.toString() : System.getProperty("line.separator") + row);

            if (empty) {
                empty = false;
            }
        }

        ClipboardUtils.getClipboard().setContents(new StringSelection(builder.toString()), null);
    }

    @Override
    public void performPaste() {
        throw new IllegalStateException();
    }

    @Override
    public void performDelete() {
        throw new IllegalStateException();
    }

    @Override
    public void performSelectAll() {
        if (listComp.getModel().getSize() > 0) {
            listComp.setSelectionInterval(0, listComp.getModel().getSize() - 1);
        }
    }

    @Override
    public boolean hasSelection() {
        return listComp.isEnabled() && !listComp.isSelectionEmpty();
    }

    @Override
    public boolean hasDataToCopy() {
        return hasSelection();
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean canSelectAll() {
        return listComp.isEnabled() && listComp.getSelectionMode() != DefaultListSelectionModel.SINGLE_SELECTION && (listComp.getModel().getSize() > 0);
    }

    @Override
    public boolean isValidForPaste() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return true;
    }
}
