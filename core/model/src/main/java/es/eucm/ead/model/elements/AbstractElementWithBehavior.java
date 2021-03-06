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

package es.eucm.ead.model.elements;

import es.eucm.ead.model.interfaces.Param;
import es.eucm.ead.model.interfaces.features.Evented;
import es.eucm.ead.model.interfaces.features.WithBehavior;
import es.eucm.ead.model.elements.behaviors.Behavior;
import es.eucm.ead.model.elements.events.SceneElementEv;
import es.eucm.ead.model.elements.events.enums.SceneElementEvType;
import es.eucm.ead.model.elements.extra.EAdList;
import es.eucm.ead.model.params.guievents.EAdGUIEvent;

/**
 * An abstract element with behavior, resources and events
 * 
 */
public abstract class AbstractElementWithBehavior extends BasicElement
		implements Evented, WithBehavior {

	@Param
	protected EAdBehavior behavior;

	/**
	 * Events associated with this element
	 */
	@Param
	protected es.eucm.ead.model.elements.extra.EAdList<EAdEvent> events;

	/**
	 * Initial event for this element. This attribute WILL NOT BE SERIALIZED by
	 * the any writer. It is a transient attribute
	 */
	private transient SceneElementEv initEvent;

	public AbstractElementWithBehavior() {
		super();
		this.behavior = new Behavior();
		this.events = new EAdList<EAdEvent>();
	}

	public EAdList<EAdEffect> getEffects(EAdGUIEvent event) {
		return behavior.getEffects(event);
	}

	/**
	 * Adds a behavior to this actor attached to the given event
	 * 
	 * @param event
	 *            the event
	 * @param effect
	 *            the effect
	 */
	public void addBehavior(EAdGUIEvent event, EAdEffect effect) {
		behavior.addBehavior(event, effect);
	}

	/**
	 * Adds the given effects to the list of the events that will be executed
	 * when the given event is processed by this element
	 * 
	 * @param event the GUI event
	 * @param effects the list of effects
	 */
	public void addBehavior(EAdGUIEvent event, EAdList<EAdEffect> effects) {
		behavior.addBehavior(event, effects);
	}

	/**
	 * Sets the behavior for this actor
	 * 
	 * @param behavior
	 *            the behavior
	 */
	public void setBehavior(EAdBehavior behavior) {
		this.behavior = behavior;
	}

	/**
	 * Returns the behavior for this actor
	 * 
	 * @return the behavior for this actor
	 */
	public EAdBehavior getBehavior() {
		return behavior;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.eucm.eadventure.common.model.EAdElement#getEvents()
	 */
	@Override
	public EAdList<EAdEvent> getEvents() {
		return events;
	}

	public void setEvents(EAdList<EAdEvent> events) {
		this.events = events;
	}

	public void addAddedEffect(EAdEffect e) {
		if (initEvent == null) {
			initEvent = new SceneElementEv();
			this.getEvents().add(initEvent);
		}
		initEvent.addEffect(SceneElementEvType.ADDED, e);
	}

	/**
	 * Adds an effect that will be executed only once when this scene element
	 * appears for the first time in the game
	 *
	 * @param e
	 *            the effect
	 */
	public void addInitEffect(EAdEffect e) {
		if (initEvent == null) {
			initEvent = new SceneElementEv();
			this.getEvents().add(initEvent);
		}
		initEvent.addEffect(SceneElementEvType.INIT, e);
	}
}
