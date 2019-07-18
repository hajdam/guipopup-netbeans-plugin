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
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import org.exbin.utils.defaultpopup.ClipboardActionsHandler;
import org.exbin.utils.defaultpopup.ClipboardUtils;

/**
 * Clipboard handler for JTable.
 *
 * @version 0.1.0 2019/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class TableClipboardHandler implements ClipboardActionsHandler {
    
    private final JTable tableComp;

    public TableClipboardHandler(JTable tableComp) {
        this.tableComp = tableComp;
    }

    @Override
    public void performCut() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void performCopy() {
        StringBuilder builder = new StringBuilder();
        int[] rows = tableComp.getSelectedRows();
        int[] columns;
        if (tableComp.getSelectionModel().getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
            columns = new int[tableComp.getColumnCount()];
            for (int i = 0; i < tableComp.getColumnCount(); i++) {
                columns[i] = i;
            }
        } else {
            columns = tableComp.getSelectedColumns();
        }
        boolean empty = true;
        for (int rowIndex : rows) {
            if (!empty) {
                builder.append(System.getProperty("line.separator"));
            } else {
                empty = false;
            }
            boolean columnEmpty = true;
            for (int columnIndex : columns) {
                if (!columnEmpty) {
                    builder.append("\t");
                } else {
                    columnEmpty = false;
                }
                Object value = tableComp.getModel().getValueAt(rowIndex, columnIndex);
                if (value != null) {
                    builder.append(value.toString());
                }
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
        tableComp.selectAll();
    }

    @Override
    public boolean isSelection() {
        return tableComp.isEnabled() && (tableComp.getSelectedColumn() >= 0 || tableComp.getSelectedRow() >= 0);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean canSelectAll() {
        return tableComp.isEnabled() && tableComp.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_SELECTION;
    }

    @Override
    public boolean canPaste() {
        return true;
    }
    
}
