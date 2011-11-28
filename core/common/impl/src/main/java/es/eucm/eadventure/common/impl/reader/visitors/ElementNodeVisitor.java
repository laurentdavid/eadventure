package es.eucm.eadventure.common.impl.reader.visitors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import es.eucm.eadventure.common.model.DOMTags;
import es.eucm.eadventure.common.impl.reader.extra.ObjectFactory;
import es.eucm.eadventure.common.model.EAdElement;

/**
 * Visitor for the element. The element should be {@code <element id="ID"
 *  type="ENGINE_TYPE"
 *  class="EDITOR_TYPE"></element>}.
 */
public class ElementNodeVisitor extends NodeVisitor<EAdElement> {
	
	protected static final Logger logger = Logger.getLogger("ElementNodeVisitor");

	@Override
	public EAdElement visit(Node node, Field field, Object parent, Class<?> listClass) {
		EAdElement element = null;
		if (node.getChildNodes().getLength() == 1 && !node.getChildNodes().item(0).hasChildNodes()) {
			element = (EAdElement) ObjectFactory.getObject(node.getTextContent(), EAdElement.class);
			if (element != null) {
				setValue(field, parent, element);
				return element;
			}
		}
		
		Node n = node.getAttributes().getNamedItem(DOMTags.UNIQUE_ID_AT);
		String uniqueId = n != null ? n.getNodeValue() : null;
		n = node.getAttributes().getNamedItem(DOMTags.ID_AT);
		String id = n != null ? n.getNodeValue() : null;

		n = node.getAttributes().getNamedItem(loaderType);
		String clazz = null;
		if (n != null) {
			clazz = n.getNodeValue();
			clazz = translateClass(clazz);
		} else {
			logger.info("Null element for: " + (parent != null ? parent.getClass() : node.getNodeName()));
		}

		Class<?> c = null;
		
		if (clazz != null) {
			Constructor<?> con;
			try {
				c = ClassLoader.getSystemClassLoader().loadClass(clazz);
				con = c.getConstructor();
				element = (EAdElement) con.newInstance();
				element.setId(id);
			} catch (NoSuchMethodException e1) {
				logger.info("No constructor for :" + c);
			} catch (Exception e1) {
				logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		
		if (element != null)
			ObjectFactory.addElement(uniqueId, element);

		setValue(field, parent, element);

		if (element != null)
			readFields(element, node);
		
		return element;
	}

	@Override
	public String getNodeType() {
		return DOMTags.ELEMENT_AT;
	}

}
