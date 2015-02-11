package org.locationtech.udig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

public class CopyRightSplashHandler extends AbstractSplashHandler {
    private Composite composite = null;
    
    public CopyRightSplashHandler() {
          // nothing
        System.out.println("Splash alarm");
    }
 
    private String getSplashText() {
        String splashText = "(c) Copyright Refractions Research ond others";
 System.out.println(splashText);
        return splashText;
    }
 
    public void init(final Shell splash) {
        // Store the shell
        super.init(splash);
 
        // Configure the shell layout
        configureUISplash();
        // Create UI
        createUI();
        // Force the UI to layout
        splash.layout(true);
 
        // Keep the splash screen visible and prevent the RCP
        // application from loading until the close button is
                // clicked.
        doEventLoop();
    }
 
    private void doEventLoop() {
        // pause for 3 seconds so the text displays
        try {
            Display.getCurrent().update();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // nothing
        }
    }
 
    private void createUI() {
 
        Shell splash = getSplash();
        System.out.println(splash.getSize());
        Display d = splash.getDisplay();
        // set background for Label text so it is transparent
        splash.setBackgroundMode(SWT.INHERIT_DEFAULT);
 
        // create a text panel positioned at the bottom center
        // of the splash screen graphic.
        composite = new Composite(splash, SWT.NONE);
        composite.setBackground(d.getSystemColor(SWT.COLOR_YELLOW));
        
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
                   true,true));
 
        composite.setLayout(new GridLayout(1, true));
 
        Composite innerComposite = new Composite(composite, SWT.NONE);
        innerComposite.setBackground(d.getSystemColor(SWT.COLOR_RED));
        
        innerComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
                   true,false));

        Label l = new Label(innerComposite, SWT.NONE);
        l.setText(getSplashText());
        l.setForeground(d.getSystemColor(SWT.COLOR_DARK_GRAY));
        
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER, GridData.VERTICAL_ALIGN_CENTER,
                true,true);
        l.setLayoutData(gridData);
        
    }
 
    private void configureUISplash() {
        GridLayout layout = new GridLayout(1, true);
        getSplash().setLayout(layout);
    }
 
    /**
     * @see org.eclipse.ui.splash.AbstractSplashHandler#dispose()
     */
    @Override
    public void dispose() {
        composite.dispose();
 
        super.dispose();
    }
}
