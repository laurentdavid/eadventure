/**
 * eAdventure (formerly <e-Adventure> and <e-Game>) is a research project of the
 *    <e-UCM> research group.
 *
 *    Copyright 2005-2010 <e-UCM> research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    <e-UCM> is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure, version 2.0
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */

package ead.editor.view.menu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import ead.editor.R;
import ead.editor.control.Controller;
import ead.editor.control.EditorConfig;
import ead.editor.control.EditorConfig.EditorConf;
import ead.editor.control.change.ChangeListener;
import ead.editor.model.EditorModel.ModelProgressListener;
import ead.engine.core.game.GameLoaderImpl;
import ead.gui.EAdMenuItem;
import ead.utils.FileUtils;
import ead.utils.swing.SwingUtilities;

/**
 * Default file menu implementation
 */
public class RunMenu extends AbstractEditorMenu {

    private static final Logger logger = LoggerFactory.getLogger("RunMenu");

    @Inject
    public RunMenu(Controller controller) {
        super(controller, Messages.run_menu);
    }

    /**
     * Initialize the file menu
     */
	@Override
    public void initialize() {
        AbstractEditorAction[] as = new AbstractEditorAction[]{
            new RunDesktopAction(Messages.run_menu_run_desktop,
                KeyEvent.VK_R, 0),
            new RunBrowserAction(Messages.run_menu_run_browser,
                KeyEvent.VK_B, 0)
        };

        for (AbstractEditorAction a : as) {
			registerAction(a);
			controller.getProjectController().addChangeListener(a);
        }
    }

    /**
     * File menu actions have access to the containing class, and are subscribed
     * to change events from the ProjectController
     */
    public abstract class RunMenuAction extends AbstractEditorAction implements ChangeListener {

        public RunMenuAction(String name, int gkey, int gmask) {
            super(name, gkey, gmask);
        }

        /**
         * Ask for confirmation before saving current edits (if any).
         * @param message to show in confirmation dialogue
         * @return
         */
        public boolean allowChanceToSave(String message) {
            if (controller.getModel().initialized()
                    && controller.getCommandManager().isChanged()) {
                int rc = JOptionPane.showConfirmDialog(null, message,
                        Messages.run_menu_confirm_save_before_run,
                        JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
                if (rc == JOptionPane.CANCEL_OPTION) {
                    return false;
                } else if (rc == JOptionPane.OK_OPTION) {
                    new MenuProgressListener(controller, new Runnable() {
                        @Override
                        public void run() {
                            controller.getProjectController().save();
                        }
                    }).runInEDT();
                    return true;
                }
            }
            return true;
        }

        @Override
        public void processChange(Object event) {
            // default is to do nothing
        }
    }

    public class RunDesktopAction extends RunMenuAction {

        public RunDesktopAction(String name, int gkey, int gmask) {
            super(name, gkey, gmask);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            allowChanceToSave(Messages.file_menu_open_confirm_destructive);
			controller.getProjectController().doRun();			
        }
    }

    public class RunBrowserAction extends RunMenuAction {

        public RunBrowserAction(String name, int gkey, int gmask) {
            super(name, gkey, gmask);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            allowChanceToSave(Messages.file_menu_open_confirm_destructive);
			logger.warn("Not implemented: run in browser");
        }
    }

    public static void main(String args[]) {
//        ProgressListener pl = new ProgressListener(null, null);
//        JFrame jf = new JFrame();
//        jf.add(pl);
//        jf.pack();
//        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jf.setVisible(true);
    }
}
