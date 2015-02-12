package org.locationtech.udig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
//        try {
//            Display.getCurrent().update();
////            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            // nothing
//        }
    }
 
    private void createUI() {
 
        Shell splash = getSplash();
        System.out.println(splash.getSize());
        Display d = splash.getDisplay();
        // set background for Label text so it is transparent
        splash.setBackgroundMode(SWT.INHERIT_DEFAULT);
 
        // create a text panel positioned at the bottom center
        // of the splash screen graphic.
        
        
        splash.setLayout(new FormLayout());
        
        composite = new Composite(splash, SWT.NONE);
        FormData fd_composite = new FormData();
        fd_composite.bottom = new FormAttachment(0, 290);
        fd_composite.right = new FormAttachment(0, 440);
        fd_composite.top = new FormAttachment(0, 10);
        fd_composite.left = new FormAttachment(0, 10);
        composite.setLayoutData(fd_composite);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        Composite composite_1 = new Composite(composite, SWT.NONE);
        
        Label udigLicenseLabel = new Label(composite_1, SWT.NONE);
        udigLicenseLabel.setAlignment(SWT.CENTER);
        udigLicenseLabel.setFont(new Font(d, "Lucida Grande", 10, SWT.ITALIC));
        udigLicenseLabel.setBounds(0, 232, 430, 38);
        udigLicenseLabel.setText("Dies ist freie Software, verfügbar unter der Eclipse Public License (EPL) und der\n"
                + "Refractions BSD License.Dieses Produkt enthält Software der Eclipse Foundation, der\n"
                + "Open Source Geospatial Foundation und anderer.");
        
        Label udigLongLabel = new Label(composite_1, SWT.NONE);
        udigLongLabel.setText("Nutzerfreundliches Desktop-Internet GIS");
        udigLongLabel.setFont(new Font(d, "Lucida Grande", 14, SWT.NORMAL));
        udigLongLabel.setAlignment(SWT.CENTER);
        udigLongLabel.setBounds(130, 172, 290, 24);
        
        Label copyRightLabel = new Label(composite_1, SWT.NONE);
        copyRightLabel.setText("© Copyright Refractions Research und andere, 2015");
        copyRightLabel.setFont(new Font(d, "Lucida Grande", 11, SWT.NORMAL));
        copyRightLabel.setAlignment(SWT.CENTER);
        copyRightLabel.setBounds(130, 196, 290, 12);
        
        Label udigAbbrLabel = new Label(composite_1, SWT.NONE);
        udigAbbrLabel.setAlignment(SWT.RIGHT);
        udigAbbrLabel.setFont(new Font(d,"Lucida Grande", 38, SWT.NORMAL));
        udigAbbrLabel.setBounds(10, 173, 114, 53);
        udigAbbrLabel.setText("uDig");
        
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
