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

package es.eucm.ead.editor.control.commands;

import static org.mockito.Mockito.when;

import es.eucm.ead.engine.desktop.platform.DesktopModule;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Injector;

import es.eucm.ead.model.elements.EAdElement;
import es.eucm.ead.model.elements.extra.EAdList;
import es.eucm.ead.editor.EditorGuiceModule;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.editor.model.nodes.DependencyNode;
import es.eucm.ead.editor.model.nodes.EngineNode;
import ead.importer.BaseImporterModule;
import es.eucm.ead.tools.java.JavaToolsModule;
import es.eucm.ead.tools.reflection.ReflectionClassLoader;

/**
 * Class for testing the right functionality of generic Commands to modify the lists of EAdElement instances in the game model.
 */
public class ElementCommandsTest extends TestCase {

	/**
	 * The mock object list in which the elements will be modified
	 */
	@Mock
	private EAdList<EAdElement> mockList;
	/**
	 * The mock object element to be modified
	 */
	@Mock
	private EAdElement mockElement;

	/**
	 * The list in which the elements will be modified
	 */
	private EAdList<EAdElement> elemList;
	/**
	 * The parent element to create EAdElement instances
	 */
	@Mock
	private EAdElement pelement;
	/**
	 * The element that will be modified
	 */
	@Mock
	private EAdElement element;

	@Mock
	private Controller controller;

	private EditorModel editorModel;

	/**
	 * Must be called after mocks initialized
	 */
	public void prepareControllerAndModel() {
		Injector injector = Guice.createInjector(new BaseImporterModule(),
				new DesktopModule(), new EditorGuiceModule(),
				new JavaToolsModule());

		// init reflection
		ReflectionClassLoader.init(injector
				.getInstance(ReflectionClassLoader.class));

		editorModel = injector.getInstance(EditorModel.class);

		when(controller.getModel()).thenReturn(editorModel);
	}

	/**
	 * The Command to add elements to a list
	 */
	private ListCommand.AddToList<EAdElement> addComm, mockAdd;
	/**
	 * The Command to remove elements from a list
	 */
	private ListCommand.RemoveFromList<EAdElement> removeComm, mockRemove;
	/**
	 * The Command to move elements in a list
	 */
	private ListCommand.ReorderInList<EAdElement> moveComm, mockMove;

	/**
	 * Method that initiates both the mock objects and the regular objects of the class, works similar to a constructor.
	 */
	@Before
	@Override
	public void setUp() {

		DependencyNode node1 = new EngineNode<String>(1, "test1");

		MockitoAnnotations.initMocks(this);

		elemList = new EAdList<EAdElement>();
		elemList.add(pelement);

		addComm = new ListCommand.AddToList<EAdElement>(elemList, element,
				node1);
		removeComm = new ListCommand.RemoveFromList<EAdElement>(elemList,
				element, node1);
		moveComm = new ListCommand.ReorderInList<EAdElement>(elemList, element,
				0, node1);

		mockAdd = new ListCommand.AddToList<EAdElement>(mockList, mockElement,
				node1);
		mockRemove = new ListCommand.RemoveFromList<EAdElement>(mockList,
				mockElement, node1);
		mockMove = new ListCommand.ReorderInList<EAdElement>(mockList,
				mockElement, 0, node1);

		prepareControllerAndModel();
	}

	/**
	 * Testing the correct functionality of the method for performing commands.
	 */
	@Test
	public void testPerformCommands() {

		assertEquals(0, elemList.indexOf(pelement));
		assertEquals(false, elemList.contains(element));
		addComm.performCommand(editorModel);
		assertEquals(true, elemList.contains(element));
		assertEquals(1, elemList.indexOf(element));

		removeComm.performCommand(editorModel);
		assertEquals(false, elemList.contains(element));
		addComm.performCommand(editorModel);
		assertEquals(true, elemList.contains(element));
		assertEquals(1, elemList.indexOf(element));

		moveComm.performCommand(editorModel);
		assertEquals(0, elemList.indexOf(element));
		assertEquals(1, elemList.indexOf(pelement));
	}

	/**
	 * Testing the correct functionality of the method for undoing commands.
	 */
	@Test
	public void testUndoCommands() {

		addComm.performCommand(editorModel);
		assertEquals(true, elemList.contains(element));
		addComm.undoCommand(editorModel);
		assertEquals(false, elemList.contains(element));

		addComm.performCommand(editorModel);
		assertEquals(true, elemList.contains(element));
		removeComm.performCommand(editorModel);
		assertEquals(false, elemList.contains(element));
		removeComm.undoCommand(editorModel);
		assertEquals(true, elemList.contains(element));

		moveComm.performCommand(editorModel);
		assertEquals(0, elemList.indexOf(element));
		assertEquals(1, elemList.indexOf(pelement));
		moveComm.undoCommand(editorModel);
		assertEquals(1, elemList.indexOf(element));
		assertEquals(0, elemList.indexOf(pelement));
	}
}
