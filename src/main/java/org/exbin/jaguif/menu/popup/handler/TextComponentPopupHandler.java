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

import org.jspecify.annotations.NullMarked;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import org.exbin.jaguif.utils.ActionUtils;
import org.exbin.jaguif.action.api.clipboard.TextClipboardOperationController;

/**
 * Popup handler for text component.
 */
@NullMarked
public class TextComponentPopupHandler implements TextClipboardOperationController {

    private final JTextComponent txtComp;

    public TextComponentPopupHandler(JTextComponent txtComp) {
        this.txtComp = txtComp;
    }

    @Override
    public void performCut() {
        txtComp.cut();
    }

    @Override
    public void performCopy() {
        txtComp.copy();
    }

    @Override
    public void performPaste() {
        txtComp.paste();
    }

    @Override
    public void performDelete() {
        ActionUtils.invokeTextAction(txtComp, DefaultEditorKit.deleteNextCharAction);
    }

    @Override
    public void performSelectAll() {
        SwingUtilities.invokeLater(() -> {
            txtComp.requestFocus();
            ActionUtils.invokeTextAction(txtComp, DefaultEditorKit.selectAllAction);
            int docLength = txtComp.getDocument().getLength();
            if (txtComp.getSelectionStart() > 0 || txtComp.getSelectionEnd() != docLength) {
                txtComp.selectAll();
            }
        });
    }

    @Override
    public boolean hasSelection() {
        return txtComp.isEnabled() && txtComp.getSelectionStart() != txtComp.getSelectionEnd();
    }

    @Override
    public boolean hasDataToCopy() {
        return hasSelection();
    }

    @Override
    public boolean isEditable() {
        return txtComp.isEnabled() && txtComp.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return txtComp.isEnabled() && !txtComp.getText().isEmpty();
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
