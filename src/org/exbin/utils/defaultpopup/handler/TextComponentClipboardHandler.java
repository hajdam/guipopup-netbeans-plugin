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

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import org.exbin.utils.defaultpopup.ActionUtils;
import org.exbin.utils.defaultpopup.ClipboardActionsHandler;

/**
 * Clipboard handler for JTextComponent.
 *
 * @version 0.1.0 2019/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class TextComponentClipboardHandler implements ClipboardActionsHandler {
    
    private final JTextComponent txtComp;

    public TextComponentClipboardHandler(JTextComponent txtComp) {
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
        txtComp.requestFocus();
        txtComp.selectAll();
    }

    @Override
    public boolean isSelection() {
        return txtComp.isEnabled() && txtComp.getSelectionStart() != txtComp.getSelectionEnd();
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
    public boolean canPaste() {
        return true;
    }
    
}
