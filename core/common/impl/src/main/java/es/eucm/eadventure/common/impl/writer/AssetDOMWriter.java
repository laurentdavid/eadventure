package es.eucm.eadventure.common.impl.writer;

import org.w3c.dom.Element;

import es.eucm.eadventure.common.resources.assets.AssetDescriptor;

public class AssetDOMWriter extends FieldParamWriter<AssetDescriptor> {

	public static final String TAG = "asset";

	@Override
	public Element buildNode(AssetDescriptor assetDescriptor) {
		Element node = doc.createElement(TAG);

		// Check if asset is new
		int index = mappedAsset.indexOf(assetDescriptor);
		if (index != -1) {
			node.setTextContent("" + index);
			return node;
		}

		// Set unique id and class
		mappedAsset.add(assetDescriptor);
		node.setAttribute(UNIQUE_ID_AT, mappedAsset.size() + "");
		node.setAttribute(CLASS_AT, assetDescriptor.getClass().getName());

		// Process Param fields
		super.processParams(node, assetDescriptor);

		return node;
	}

}