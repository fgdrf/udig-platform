package org.locationtech.udig.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;
import org.locationtech.udig.UdigPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public class CopyRightSplashHandler extends AbstractSplashHandler {
    private Composite composite = null;

    private final Set<Resource> resources = new HashSet<Resource>();

    private static String udigVersion;

    private static String udigCopyrightText;

    public CopyRightSplashHandler() {
    }

    public void init(final Shell splash) {
        Bundle bundle = UdigPlugin.getDefault();
        if (bundle == null) {
            return;
        }
        final Version version = bundle.getVersion();

        // TODO read year from about.mappings file        
        udigCopyrightText = Messages.splash_copyrightWithYear;
        udigVersion = version.toString();
        
        // Store the shell
        super.init(splash);

        // Configure the shell layout
        configureUISplash();
        // Create UI
        createUI();
        // Force the UI to layout
        splash.layout(true);

        doEventLoop();
    }

    private void doEventLoop() {
        try {
            Display.getCurrent().update();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // nothing
        }
    }

    private void createUI() {

        Shell splash = getSplash();
        Display d = splash.getDisplay();
        // set background for Label text so it is transparent
        splash.setBackgroundMode(SWT.INHERIT_DEFAULT);

        final Color labelColor = new Color(d, 102, 102, 102);
        resources.add(labelColor);
        final Color copyrightColor = new Color(d, 248, 248, 255);
        resources.add(copyrightColor);
        final Font titleFont = new Font(d, "Gill Sans", 21, SWT.NORMAL); //$NON-NLS-1$
        resources.add(copyrightColor);
        final Font copyrightFont = new Font(d, "Gill Sans", 12, SWT.NORMAL); //$NON-NLS-1$
        resources.add(copyrightColor);

        composite = new Composite(splash, SWT.NONE);
        Label copyRightLabel = new Label(composite, SWT.NONE);
        copyRightLabel.setBounds(10, 292, 342, 18);
        copyRightLabel.setText(udigCopyrightText);
        copyRightLabel.setFont(copyrightFont);
        copyRightLabel.setForeground(copyrightColor);

        Label udigLongLabel = new Label(composite, SWT.NONE);
        udigLongLabel.setBounds(118, 10, 362, 30);
        udigLongLabel.setText(Messages.splash_udigFullName);
        udigLongLabel.setFont(titleFont);
        udigLongLabel.setAlignment(SWT.CENTER);
        udigLongLabel.setForeground(labelColor);

        Label lblVersion = new Label(composite, SWT.NONE);
        lblVersion.setBounds(189, 46, 291, 14);
        lblVersion.setAlignment(SWT.RIGHT);
        udigLongLabel.setForeground(labelColor);

        lblVersion.setText(udigVersion);

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
        for (Resource resource : resources) {
            if (resource != null && !resource.isDisposed()) {
                resource.dispose();
            }
        }
        composite.dispose();
    }
}
