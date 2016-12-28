package tk.eabin.events.ui;

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 00:12
 */

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapPageResponse;

public class MyBootstrapListener implements com.vaadin.server.BootstrapListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        response.getDocument().head().prependElement("meta").attr("name", "viewport").attr("content", "width=device-width");
    }

    @Override
    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
        // Wrap the fragment in a custom div element
        /*Element myDiv = new Element(Tag.valueOf("div"), "");
        List<Node> nodes = response.getFragmentNodes();
        for (Node node : nodes) {
            myDiv.appendChild(node);
        }
        nodes.clear();
        nodes.add(myDiv);*/
    }

}
