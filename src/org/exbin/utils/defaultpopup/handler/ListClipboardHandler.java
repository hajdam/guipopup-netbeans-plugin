/*
 * Copyright (C) ExBin Project
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
package org.exbin.utils.defaultpopup.handler;

import java.awt.datatransfer.StringSelection;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import org.exbin.utils.defaultpopup.ClipboardActionsHandler;
import org.exbin.utils.defaultpopup.ClipboardUtils;

/**
 * Clipboard handler for JList.
 *
 * @version 0.1.0 2019/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class ListClipboardHandler implements ClipboardActionsHandler {
    
    private final JList listComp;

    public ListClipboardHandler(JList listComp) {
        this.listComp = listComp;
    }

    @Override
    public void performCut() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void performCopy() {
        StringBuilder builder = new StringBuilder();
        List rows = listComp.getSelectedValuesList();
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void performDelete() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void performSelectAll() {
        if (listComp.getModel().getSize() > 0) {
            listComp.setSelectionInterval(0, listComp.getModel().getSize() - 1);
        }
    }

    @Override
    public boolean isSelection() {
        return listComp.isEnabled() && !listComp.isSelectionEmpty();
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean canSelectAll() {
        return listComp.isEnabled() && listComp.getSelectionMode() != DefaultListSelectionModel.SINGLE_SELECTION;
    }

    @Override
    public boolean canPaste() {
        return true;
    }
    
}
