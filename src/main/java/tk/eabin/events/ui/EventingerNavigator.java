package tk.eabin.events.ui;

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 21:44
 */

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import tk.eabin.events.event.PostViewChangeEvent;
import tk.eabin.events.event.UIEventBus;

@SuppressWarnings("serial")
public class EventingerNavigator extends Navigator {

    private static final EventingerViews ERROR_VIEW = EventingerViews.EVENTS;
    private ViewProvider errorViewProvider;

    public EventingerNavigator(final ComponentContainer container) {
        super(UI.getCurrent(), container);

        String host = getUI().getPage().getLocation().getHost();
        initViewChangeListener();
        initViewProviders();

    }

    private void initViewChangeListener() {
        addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(final ViewChangeEvent event) {
                // Since there's no conditions in switching between the views
                // we can always return true.
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
                System.out.println("Change view to: " + event.getViewName());
                UIEventBus.INSTANCE.postEvent(new PostViewChangeEvent(EventingerViews.Companion.find(event.getViewName())));
            }
        });
    }

    private void initViewProviders() {
        // A dedicated view provider is added for each separate view type
        for (final EventingerViews viewType : EventingerViews.values()) {
            ViewProvider viewProvider = new ClassBasedViewProvider(
                    viewType.getViewName(), viewType.getViewClass()) {

                // This field caches an already initialized view instance if the
                // view should be cached (stateful views).
                private View cachedInstance;

                @Override
                public View getView(final String viewName) {
                    View result = null;
                    if (viewType.getViewName().equals(viewName)) {
                        if (viewType.isStateful()) {
                            // Stateful views get lazily instantiated
                            if (cachedInstance == null) {
                                cachedInstance = super.getView(viewType
                                        .getViewName());
                            }
                            result = cachedInstance;
                        } else {
                            // Non-stateful views get instantiated every time
                            // they're navigated to
                            result = super.getView(viewType.getViewName());
                        }
                    }
                    return result;
                }
            };

            if (viewType == ERROR_VIEW) {
                errorViewProvider = viewProvider;
            }

            addProvider(viewProvider);
        }

        setErrorProvider(new ViewProvider() {
            @Override
            public String getViewName(final String viewAndParameters) {
                return ERROR_VIEW.getViewName();
            }

            @Override
            public View getView(final String viewName) {
                return errorViewProvider.getView(ERROR_VIEW.getViewName());
            }
        });
    }
}
