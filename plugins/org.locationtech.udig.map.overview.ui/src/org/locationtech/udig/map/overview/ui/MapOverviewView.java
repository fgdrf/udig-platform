/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.map.overview.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.IMapListener;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.MapEvent;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * A navigation view.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class MapOverviewView extends ViewPart implements SelectionListener, IMapListener, IMapCompositionListener {

    private Image worldImage;
    private Canvas canvas;
    private Color color;

    public MapOverviewView() {
        ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
                "org.locationtech.udig.map.overview.ui",
                "icons/worldoverview2.png");
        worldImage = imageDescriptor.createImage();

        color = Display.getDefault().getSystemColor(SWT.COLOR_RED);

    }

    @Override
    public void createPartControl( Composite theparent ) {

        ScrolledComposite scrolledComposite = new ScrolledComposite(theparent, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setMinSize(360, 180);

        Composite parent = new Composite(scrolledComposite, SWT.NONE);
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parent.setLayout(new GridLayout(1, true));

        org.eclipse.swt.graphics.Point point = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        parent.setSize(point);
        scrolledComposite.setContent(parent);

        Group overviewGroups = new Group(parent, SWT.NONE);
        GridData gd1 = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        overviewGroups.setLayoutData(gd1);
        overviewGroups.setLayout(new GridLayout(1, false));
        overviewGroups.setText("Overview");

        canvas = new Canvas(overviewGroups, SWT.NONE);
        GridData gd2 = new GridData(SWT.FILL, SWT.FILL, false, false);
        gd2.widthHint = 250;
        gd2.heightHint = 121;
        canvas.setLayoutData(gd2);
        canvas.addPaintListener(new PaintListener(){

            @Override
            public void paintControl( PaintEvent e ) {
                Rectangle canvasBounds = canvas.getBounds();
                Rectangle imageBounds = worldImage.getBounds();
                int h = (int) ((float) canvasBounds.width * (float) imageBounds.height / imageBounds.width);
                e.gc.drawImage(worldImage, 0, 0, imageBounds.width, imageBounds.height, 0, 0, canvasBounds.width, h);

                IMap activeMap = ApplicationGIS.getActiveMap();
                ViewportModel viewportModel = (ViewportModel) activeMap.getViewportModel();
                ReferencedEnvelope bounds = viewportModel.getBounds();

                CoordinateReferenceSystem mapCrs = viewportModel.getCRS();
                CoordinateReferenceSystem imageCrs = DefaultGeographicCRS.WGS84;
                try {
                    MathTransform transform = CRS.findMathTransform(mapCrs, imageCrs);
                    Envelope targetEnv = JTS.transform(bounds, transform);
                    double west = targetEnv.getMinX();
                    double north = targetEnv.getMaxY();
                    double east = targetEnv.getMaxX();
                    double south = targetEnv.getMinY();

                    if (west < -180) {
                        west = -180;
                    }
                    if (west > 180) {
                        west = 180;
                    }
                    if (north < -90) {
                        north = -90;
                    }
                    if (north > 90) {
                        north = 90;
                    }
                    if (east < -180) {
                        east = -180;
                    }
                    if (east > 180) {
                        east = 180;
                    }
                    if (south < -90) {
                        south = -90;
                    }
                    if (south > 90) {
                        south = 90;
                    }
                    west = 180.0 + west;
                    north = 90.0 + north;
                    east = 180.0 + east;
                    south = 90.0 + south;
                    double width = east - west;
                    double height = north - south;
                    if (width < 1) {
                        width = 1;
                    }
                    if (height < 1) {
                        height = 1;
                    }

                    int x = (int) (canvasBounds.width * west / 360.0);
                    int y = (int) (h * north / 180.0);
                    int fw = (int) (canvasBounds.width * width / 360.0);
                    if (fw <= 1)
                        fw = 2;
                    int fh = (int) (h * height / 180.0);
                    if (fh <= 1)
                        fh = 2;
                    int newy = h - y;
                    e.gc.setForeground(color);
                    e.gc.setBackground(color);
                    e.gc.setAlpha(80);
                    e.gc.fillRectangle(x, newy, fw, fh);

                    e.gc.drawLine(x + fw / 2, 0, x + fw / 2, newy);
                    // e.gc.drawLine(x + fw / 2, newy + fh, x + fw / 2, h);
                    //
                    e.gc.drawLine(0, newy + fh / 2, x, newy + fh / 2);
                    e.gc.drawLine(x + fw, newy + fh / 2, canvasBounds.width, newy + fh / 2);

                } catch (FactoryException e1) {
                    e1.printStackTrace();
                } catch (TransformException e1) {
                    e1.printStackTrace();
                }

            }
        });

        updateData();

    }

    private void updateData() {
        Display.getDefault().asyncExec(new Runnable(){
            @Override
            public void run() {
                IMap activeMap = ApplicationGIS.getActiveMap();
                activeMap.addMapListener(MapOverviewView.this);
                activeMap.addMapCompositionListener(MapOverviewView.this);
                if (canvas != null && !canvas.isDisposed()) {
                    canvas.redraw();
                }
            }
        });
    }

    @Override
    public void widgetSelected( SelectionEvent e ) {
        // ViewportModel viewportModel = (ViewportModel)
        // ApplicationGIS.getActiveMap().getViewportModel();
        // TODO get click position and center map there
        updateData();
    }

    @Override
    public void changed( MapEvent event ) {
        updateData();
    }

    @Override
    public void changed( MapCompositionEvent event ) {
        updateData();
    }

    @Override
    public void setFocus() {
        updateData();
    }

    @Override
    public void widgetDefaultSelected( SelectionEvent e ) {
    }
}
