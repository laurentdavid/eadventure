package es.eucm.eadventure.editor.view.swing;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.WindowConstants;

import es.eucm.eadventure.editor.view.ComponentProvider;
import es.eucm.eadventure.editor.view.generics.impl.TextOption;
import es.eucm.eadventure.gui.EAdFrame;

public class FactoryProviderTest extends EAdFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new FactoryProviderTest();
	}
	
    public FactoryProviderTest() {
        setSize( 400,400 );
        
        setLayout(new FlowLayout());
        
        
        TextOption option = new TextOption("name", "toolTip");
        SwingProviderFactory swingProviderFactory = new SwingProviderFactory();
        ComponentProvider<TextOption, JComponent> componentProvider = swingProviderFactory.getProvider(option);
        componentProvider.setElement(option);
        add(componentProvider.getComponent());
        
        setVisible( true );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
	
}