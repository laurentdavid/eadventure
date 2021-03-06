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

package es.eucm.ead.techdemo.elementfactories.scenes.scenes;

import es.eucm.ead.model.assets.drawable.basics.Image;
import es.eucm.ead.model.assets.drawable.filters.EAdFilteredDrawable;
import es.eucm.ead.model.assets.drawable.filters.FilteredDrawable;
import es.eucm.ead.model.assets.drawable.filters.MatrixFilter;
import es.eucm.ead.model.assets.drawable.filters.ShaderFilter;
import es.eucm.ead.model.elements.scenes.SceneElement;
import es.eucm.ead.model.params.util.Matrix;
import es.eucm.ead.model.params.util.Position.Corner;

public class FiltersDemo extends EmptyScene {

	public FiltersDemo() {
		this.setId("FiltersDemo");
		Matrix m = new Matrix();
		m.scale(-1.0f, 1.0f, true);
		Image i = new Image("@drawable/ng_key.png");
		EAdFilteredDrawable d = new FilteredDrawable(i, new MatrixFilter(m,
				1.0f, 0.0f));
		SceneElement e = new SceneElement(d);
		e.setInitialScale(0.8f);
		e.setPosition(Corner.CENTER, 400, 300);

		EAdFilteredDrawable d2 = new FilteredDrawable(i, new ShaderFilter(
				"@binary/shaders/normal.vert", "@binary/shaders/reder.frag"));
		SceneElement e3 = new SceneElement(d2);
		e3.setInitialScale(0.8f);
		e3.setPosition(Corner.CENTER, 400, 200);
		add(e3);

		SceneElement e2 = new SceneElement(i);
		e2.setPosition(Corner.CENTER, 400, 400);
		e2.setInitialScale(0.8f);
		getSceneElements().add(e2);
		getSceneElements().add(e);
	}

}
