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

package ead.editor.control;

import ead.editor.control.change.ChangeNotifier;

/**
 * Interface for the controller of the system. This controller is in charge of
 * keeping the project in scope.
 */
public interface ProjectController extends ChangeNotifier {

	/**
	 * Load a project from an URL
	 *
	 * @param projectURL The URL for the project
	 */
	void load(String projectURL);

	/**
	 * Load a project from an URL
	 *
	 * @param sourceURL The URL for the old project
	 * @param projectURL The URL for the new project
	 */
	void doImport(String sourceURL, String projectURL);

    /**
	 * Save the project
	 */
	void save();

	/**
	 * Save the project in a new location
	 *
	 * @param projectURL The new URL location for the project
	 */
	void saveAs(String projectURL);

	/**
	 * Create a new project
	 */
	void newProject();

	/**
	 * Set the actual super-controller.
	 * @param controller the main controller, providing access to model, views,
	 * and more
	 */
	void setController(Controller controller);
	
	/**
	 * Launches a game
	 */
	void doRun();
}
