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

package es.eucm.ead.model.assets.drawable.filters;

import es.eucm.ead.model.interfaces.Element;
import es.eucm.ead.model.interfaces.Param;
import es.eucm.ead.model.elements.BasicElement;
import es.eucm.ead.model.params.util.Matrix;

@Element
public class MatrixFilter extends BasicElement implements EAdDrawableFilter {

	@Param
	private Matrix matrix;

	@Param
	private float originX;

	@Param
	private float originY;

	public MatrixFilter() {
	}

	/**
	 *
	 * @param m
	 *            the matrix
	 * @param originX
	 *            the origin, a value between 0 and 1, 0 meaning 0 as x
	 *            coordinate, and 1 meaning drawable width as x coordinate
	 * @param originY
	 * the origin, a value between 0 and 1, 0 meaning 0 as y
	 *            coordinate, and 1 meaning drawable height as y coordinate
	 */
	public MatrixFilter(Matrix m, float originX, float originY) {
		this.matrix = m;
		this.originX = originX;
		this.originY = originY;
	}

	public MatrixFilter(Matrix m) {
		this(m, 0.5f, 0.5f);
	}

	public float getOriginX() {
		return originX;
	}

	public float getOriginY() {
		return originY;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public void setOriginX(float originX) {
		this.originX = originX;
	}

	public void setOriginY(float originY) {
		this.originY = originY;
	}

}
