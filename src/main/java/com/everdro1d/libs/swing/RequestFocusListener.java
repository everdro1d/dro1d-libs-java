// dro1dDev - created: 2024-03-03

package com.everdro1d.libs.swing;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *  Convenience class to request focus on a component.
 *  <p>
 *  When the component is added to a realized Window then component will
 *  request focus immediately, since the ancestorAdded event is fired
 *  immediately.
 *  <p>
 *  When the component is added to a non realized Window, then the focus
 *  request will be made once the window is realized, since the
 *  ancestorAdded event will not be fired until then.
 *  <p>
 *  Using the default constructor will cause the listener to be removed
 *  from the component once the AncestorEvent is generated. A second constructor
 *  allows you to specify a boolean value of false to prevent the
 *  AncestorListener from being removed when the event is generated. This will
 *  allow you to reuse the listener each time the event is generated.
 */
public class RequestFocusListener implements AncestorListener
{
    private final boolean removeListener;

    /**
     *  Convenience constructor. The listener is only used once, then it is
     *  removed from the component.
     *  <p><strong>Example:</strong></p>
     *  {@code button1.addAncestorListener(new RequestFocusListener());}
     */
    public RequestFocusListener()
    {
        this(true);
    }

    /**
     *  Constructor that controls whether this listen can be used once or
     *  multiple times.
     *
     *  @param removeListener when true this listener is only invoked once
     *                        otherwise it can be invoked multiple times.
     */
    public RequestFocusListener(boolean removeListener)
    {
        this.removeListener = removeListener;
    }

    @Override
    public void ancestorAdded(AncestorEvent e)
    {
        JComponent component = e.getComponent();
        component.requestFocusInWindow();

        if (removeListener) component.removeAncestorListener( this );
    }

    @Override
    public void ancestorMoved(AncestorEvent e) {}

    @Override
    public void ancestorRemoved(AncestorEvent e) {}
}
